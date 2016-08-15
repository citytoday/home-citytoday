package scrapers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import scrapers.models._

object Idealista {
  val urlTransformer = (value: String) => s"https://www.idealista.it/vendita-case/milano-milano/lista-$value.htm"
}

/**
 * Created by fabiofumarola on 13/08/16.
 */
class Idealista(
  val baseUrl: String => String = Idealista.urlTransformer,
  val maxCount: Int = 628) extends HomeScraper {

  override def collectionPagesIterator: Iterator[String] =
    Iterator.range(1, maxCount + 1)
      .map(i => baseUrl(i.toString))

  override def extractRecords(html: String): List[HomeRecord] = {
    val doc = browser.parseString(html)
    val elements = doc >> elementList("article")
    elements.map(extractRecord).filter(_.id.nonEmpty)
  }

  private val absoluteUrl = baseUrl("1")

  override def extractRecord(e: Element): HomeRecord = {
    HomeRecord(
      id = e >?> attr("data-adid")("div"),
      src = "idealista",
      title = e >?> text("div > div > div.item-info-container > a"),
      url = e >?> attr("href")("div > div > div.item-info-container > a")
        .map(r => Utils.makeUrlAbsolute(absoluteUrl, r)),
      price = e >?> text("div > div > div.item-info-container > div.row.price-row > span.item-price"),
      category = None,
      dateTime = None,
      location = None,
      phone = e >?> text("div > div > div.item-info-container > div.item-toolbar > div.item-toolbar-contact > span"))
  }

  override def extractDetails(html: String, hr: HomeRecord): HomeRecord = {
    val doc = browser.parseString(html)

    val elements = (doc >> elementList("#details > div")) ++
      (doc >> elementList("#addressPromo"))

    val details = elements.map { e =>
      val key = e >> text("h2")
      val values = e >> texts("ul > li")
      key -> values.mkString(" , ")
    }.toMap

    val description = details.get("Commento dell’inserzionista")
    val position = details.get("Posizione")
    val category = doc >?> text("#pager > div.back-to-listing > a > span")


    val publisher = doc >?> text("a.about-advertiser-name")

    hr.copy(
      mapDetails = details - "Commento dell’inserzionista" - "Posizione",
      description = description,
      publisher = publisher,
      location = position,
      category = category)
  }
}
