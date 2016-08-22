package scrapers
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import eu.citytoday.messages._


object Casa {
  val urlTransformer = (page: String) =>
    s"http://www.casa.it/vendita-residenziale/dimensione-80-qualsiasi-in-repubblica%2f+stazione+centrale%2c+milano%2c+mi%2c+lombardia%3b+buenos+aires%2f+indipendenza%2f+p.ta+venezia%2c+milano%2c+mi%2c+lombardia%3b+v+giornate%2f+xxii+marzo%2f+porta+romana%2c+milano%2c+mi%2c+lombardia%3b+bocconi%2f+c.so+italia%2f+ticinese%2c+milano%2c+mi%2c+lombardia%3b+p.ta+genova%2f+romolo%2f+solari%2c+milano%2c+mi%2c+lombardia%3b+de+angeli%2f+vercelli%2f+washinghton%2c+milano%2c+mi%2c+lombardia%3b+fiera%2f+firenze%2f+sempione%2f+paolo+sarpi%2farena%2c+milano%2c+mi%2c+lombardia%3b+garibaldi%2f+isola%2f+maciachini%2f+monumentale%2c+milano%2c+mi%2c+lombardia%3b/lista-$page"
}

/**
 * Created by fabiofumarola on 17/08/16.
 */
class Casa(val baseUrl: String => String = Casa.urlTransformer) extends HomeScraper {

  override def extractRecords(html: String): List[RawHomeRecord] = {
    val doc = browser.parseString(html)
    val elements = doc >> elementList("ul.listing-list > li")
    elements.map(extractRecord)
  }

  val absoluteUrl = baseUrl("1")

  override def extractRecord(e: Element): RawHomeRecord = {
    RawHomeRecord(
      id = e >?> attr("data-reactid")("li")
        .map(e => e.substring(e.indexOf("$") + 1)),
      src = "Casa",
      title = e >?> text("div.title > h2"),
      url = e >?> attr("href")("a")
        .map(Utils.makeUrlAbsolute(absoluteUrl,_)),
      price = e >?> text("ul > li.price"),
      category = e >?> text("span.optional-zone"),
      dateTime = Option(Utils.nowToString()),
      location = e >?> text("span.zone"),
      phone = None
    )
  }

  override def extractDetails(html: String, hr: RawHomeRecord): RawHomeRecord = {
    val doc = browser.parseString(html)

    val mapDetails = (doc >> elementList("div.characteristics > div > ul > li"))
      .map{ e =>
        val key = e >> text("li > b")
        val value = e >> text("li > span")
        key -> value
      }.toMap

    hr.copy(
      dateTime = doc >?> text("span.last-mod > span:nth-child(2)"),
      description = doc >?> text("div.description > p"),
      mapDetails = mapDetails,
      publisher = doc >?> text("div.agency-info > div > a"),
      imagesUrl = (doc >> attrs("src")("div.image-gallery-thumbnails-container > ul > li > img"))
        .map(_.replace("63x45","630x450")).toList
    )
  }
}
