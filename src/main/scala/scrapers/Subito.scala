package scrapers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import scrapers.models._

object Subito {
  val urlTransformer = (value: String) =>
    s"http://www.subito.it/annunci-lombardia/vendita/appartamenti/milano/milano/?sqs=4&o=$value"
}

/**
 * Created by fabiofumarola on 24/07/16.
 */
class Subito(
  val baseUrl: String => String = Subito.urlTransformer,
  val maxCount: Int = 100) extends HomeScraper {

  private val absoluteUrl = baseUrl("1")

  /**
   *
   * @param html a string containing html elements
   * @return the record in the list page html
   */
  override def extractRecords(html: String): List[HomeRecord] = {
    val doc = browser.parseString(html)
    val elements = doc >> elementList("article")
    elements.map(extractRecord)
  }

  /**
   *
   * @param e
   * @return parse Home Record
   */
  override def extractRecord(e: Element): HomeRecord = {
    val cssSelector = "div.item_list_section.item_description > h2 > a"

    HomeRecord(
      id = e >?> attr("name")(cssSelector),
      src = "subito",
      title = e >?> attr("title")(cssSelector),
      url = e >?> attr("href")(cssSelector)
        .map(r => Utils.makeUrlAbsolute(absoluteUrl, r)),
      price = e >?> text("span.item_price"),
      category = e >?> text("span.item_category"),
      dateTime = e >?> attr("datetime")("time"),
      location = e >?> text("span.item_location"),
      phone = Option.empty)
  }

  /**
   *
   * @param html
   * @param hr
   * @return extract the elements from a detail page
   */
  override def extractDetails(html: String, hr: HomeRecord): HomeRecord = {
    val doc = browser.parseString(html)

    val table = doc >> elementList("div.summary > table > tbody > tr")

    val details = table.map(_ >> texts("td"))
      .filter(_.size == 2)
      .map {
        case List(key, value) => key -> value
      }.toMap

    val description = doc >?> text("div.description")
    val publisher = doc >?> text("div.data > strong.author")

    hr.copy(
      mapDetails = details,
      description = description,
      publisher = publisher)
  }

}
