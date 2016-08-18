package scrapers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import org.joda.time.DateTime
import scrapers.models._


object Immobiliare {
  val urlTransformer = (page: String) =>
    s"http://www.immobiliare.it/Milano/vendita_appartamenti-Milano.html?criterio=rilevanza&superficieMinima=80&pag=$page"
}

/**
 * Created by fabiofumarola on 17/08/16.
 */
class Immobiliare(
  val baseUrl: String => String = Immobiliare.urlTransformer,
  val maxCount: Int = 30) extends HomeScraper {

  override def extractRecords(html: String): List[RawHomeRecord] = {
    val doc = browser.parseString(html)
    val elements = doc >> elementList("div.wrapper_riga_annuncio")
    elements.map(extractRecord)
  }

  override def extractRecord(e: Element): RawHomeRecord = {
    RawHomeRecord(
    id = e >?> attr("id")("div.box_centrali_bianchi.riga_annuncio"),
      src = "Immobiliare",
      title = e >?> text("div.annuncio_title"),
      url = e >?> attr("href")("div.annuncio_title > strong > a"),
      price = e >?> text("span.price").map(_.replace("Prezzo:","")),
      category = e >?> text("div.titolo_annuncio"),
      dateTime = Option(Utils.nowToString()),
      location = e >?> attr("title")("div.luogo"),
      phone = None,
      publisher = e >?> attr("alt")("div.agenzia_logo.in_annuncio > img")
    )
  }

  override def extractDetails(html: String, hr: RawHomeRecord): RawHomeRecord = {
    val doc = browser.parseString(html)

    val keys = doc >> texts("dl > dt")
    val values = doc >> texts("dl > dd")

    hr.copy(
      description = doc >?> text("div.description-text"),
      mapDetails = keys.zip(values).toMap
    )
  }
}
