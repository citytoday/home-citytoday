package extractors

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.model.DateTime
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.kafka.scaladsl._
import akka.stream.{ActorMaterializer, Attributes}
import akka.stream.ThrottleMode.Shaping
import akka.stream.scaladsl._
import com.typesafe.config.ConfigFactory
import eu.citytoday.messages.RawHomeRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.ByteArraySerializer
import scrapers._

import scala.concurrent.Future
import scala.util.Random
import scala.concurrent.duration._
import net.ceedubs.ficus.Ficus._

/**
  * Created by fabiofumarola on 22/08/16.
  */
object HomeListsExtractor extends App {

  private val config = ConfigFactory.load("local")

  implicit val system = ActorSystem("HomeListsExtractor")
  implicit val materializer = ActorMaterializer()
  implicit val exec = materializer.system.dispatcher
  implicit val log = Logging(system, "HomeListsExtractor")

  val boostrapServers = config.as[String]("citytoday.kafka.brokers")
  val rawTopic = config.as[String]("citytoday.kafka.raw_topic")
  val producerSettings = ProducerSettings(system, new ByteArraySerializer, new ByteArraySerializer)
    .withBootstrapServers(boostrapServers)

  val sources = List(new Bacheca(), new Casa(), new Idealista(), new Immobiliare(), new Subito())

  scheduledGraph().runWith(Sink.ignore)


  def scheduledGraph() =
    Source.tick(1 seconds, 24.hours, "start")
      .map { msg =>
        log.info(s"started extraction at ${DateTime.now}")
        val extractors: List[Future[Done]] = sources.map(e => graph(e).run())
        extractors.map(e => terminateWhenAllDone(e, extractors))
      }

  /**
    *
    * @tparam T
    * @return a Flow that throttle the stream to the desired rate
    */
  def throttler[T] = {
    var last = System.currentTimeMillis()
    Flow[T]
      .throttle(1, 5.seconds, 2, x => Random.nextInt(10), Shaping)
      .log("Throttler", t => {
        val value = s"emitted element to downstream after ${System.currentTimeMillis() - last} milliseconds"
        last = System.currentTimeMillis()
        value
      }).withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
  }

  def recordFlow(scraper: HomeScraper) =
    Flow[String]
      .map(MyHttp(_).asString.body)
      .map(scraper.extractRecords)
      .mapConcat(identity)
      .filter { r =>
        if (r.url.isEmpty) log.error(s"cannot extract url for record ${r.src}")
        r.url.isDefined
      }

  def detailFlow(scraper: HomeScraper) =
    Flow[RawHomeRecord]
      .map(r => MyHttp(r.url.get).asString.body -> r)
      .map(data => scraper.extractDetails(data._1, data._2))
      .map { r =>
        if (r.description.isEmpty) log.error(s"cannot extract details for record ${r.url.get} ${r.src}")
        r
      }

  def kafkaFlow() =
    Flow[RawHomeRecord]
      .map(e =>
        ProducerMessage.Message(new ProducerRecord[Array[Byte], Array[Byte]](
          rawTopic, 0, DateTime.now.clicks, e.url.get.getBytes, e.toByteArray), e.url))
      .via(Producer.flow(producerSettings))
      .log("KafkaSaver", result => {
        val record = result.message.record
        s"saved listing ${DateTime(record.timestamp()).toIsoDateTimeString()} ${record.topic}/${record.partition} ${result.offset}:${result.message.passThrough.get}"
      }).withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))

  def kafkaSink() =
    Flow[RawHomeRecord]
      .map(e =>
        new ProducerRecord[Array[Byte], Array[Byte]](rawTopic, e.url.get.getBytes, e.toByteArray))
      .toMat(Producer.plainSink(producerSettings))(Keep.right)

  def graph(scraper: HomeScraper): RunnableGraph[Future[Done]] =
    Source.fromIterator(() => scraper.collectionPagesIterator)
      .log("Scraper", u => s"extracting listings from $u")
      .withAttributes(Attributes.logLevels(onElement = Logging.InfoLevel))
      .via(recordFlow(scraper))
      .via(detailFlow(scraper))
      .via(throttler)
      .via(kafkaFlow())
      .toMat(Sink.ignore)(Keep.right)

  def terminateWhenAllDone(result: Future[Done], list: List[Future[Done]]): Unit = {
    result.onFailure {
      case e: Throwable =>
        system.log.error(e, e.getMessage)

    }
    result.onSuccess {
      case _ =>
        if (list.forall(_.isCompleted))
          system.terminate()
    }
  }
}


