package extractors

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.ThrottleMode.Shaping
import akka.stream.scaladsl._
import org.joda.time.DateTime
import scrapers._
import scrapers.models.RawHomeRecord

import scala.concurrent.duration._
import scala.util.{Failure, Random, Success}

/**
  * Created by fabiofumarola on 18/08/16.
  */
object DataRecordsExtractor {

  //  val bacheca = new Bacheca()
  //  val casa = new Casa()
  //  val idealista = new Idealista()
  //  val immobiliare = new Immobiliare()
  //  val subito = new Subito()


  /**
    *
    * throttle a Stream
    *
    * @return
    */
  def throttler[T] = {
    var last = System.currentTimeMillis()
    Flow[T]
      .throttle(1, 3 seconds, 2, x => Random.nextInt(10), Shaping)
      .map { e =>
        println(s"emitted element after ${System.currentTimeMillis() - last} milliseconds")
        last = System.currentTimeMillis()
        e
      }
  }

  def source(extractor: HomeScraper): Source[RawHomeRecord, NotUsed] =
    Source.fromIterator(() => extractor.collectionPagesIterator)
      .map(CHttp(_).asString.body)
      .map(html => extractor.extractRecords(html))
      .mapConcat(identity)
      .filter(_.url.isDefined)
      .map(r => CHttp(r.url.get).asString.body -> r)
      .map {
        case (html, r) =>
          extractor.extractDetails(html, r)
      }

  //.via(DataRecordsExtractor.throttler)

}

object SubitoExtractor {

  val subito = new Subito()

  val source: Source[RawHomeRecord, NotUsed] =
    Source.fromIterator(() => subito.collectionPagesIterator)
      .map(CHttp(_).asString.body)
      .map(html => subito.extractRecords(html))
      .mapConcat(identity)
      .filter(_.url.isDefined)
      .map(r => CHttp(r.url.get).asString.body -> r)
      .map {
        case (html, r) =>
          subito.extractDetails(html, r)
      }.via(DataRecordsExtractor.throttler)


}

object Main extends App {

  implicit val system = ActorSystem("QuickStart")
  implicit val materializer = ActorMaterializer()

  implicit val exec = materializer.system.dispatcher


  //  extractor.subitoSource()
  //    .via(extractor.throttler)
  //    .runWith(Sink.foreach { u =>
  //      println(s"${DateTime.now().toString("hh:mm ss")} with url $u")
  //    })

}