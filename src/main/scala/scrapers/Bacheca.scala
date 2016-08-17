package scrapers

import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.model.Element
import scrapers.models._


object Bacheca {
  val urlTransformer = (page: String) =>
    s"http://milano.bakeca.it/annunci/vendita-case/luogo/Milano/mqcaseinvendita/80-0/page/$page/nope/true/"
}

/**
  * Created by fabiofumarola on 17/08/16.
  */
class Bacheca(val baseUrl: String => String = Bacheca.urlTransformer,
              val maxCount: Int = 100) extends HomeScraper {

  override def extractRecords(html: String): List[HomeRecord] = {
    val doc = browser.parseString(html)
    val elements = doc >> elementList("div.bk-annuncio-item")
    elements.map(extractRecord)
  }

  override def extractRecord(e: Element): HomeRecord = {
    HomeRecord(
      id = e >?> attr("data-id")("div"),
      src = "Bacheca",
      title = e >?> text("h3.bk-annuncio-title"),
      url = e >?> attr("href")("h3.bk-annuncio-title > a"),
      price = e >?> text("span.bk-annuncio-prezzo"),
      category = Option("appartamenti"),
      dateTime = Option(Utils.nowToString()),
      location = e >?> text("p.bk-annuncio-breadcrumb"),
      phone = None
    )
  }

  override def extractDetails(html: String, hr: HomeRecord): HomeRecord = {
    val doc = browser.parseString(html)

    val mapDetails = (doc >> elementList("table.bk-dett-meta > tbody > tr > td"))
      .filter(_.children.nonEmpty)
      .map {e =>

        val key = e >> text("td > strong")
        val value = e >> text("td > span")
        key -> value
      }.toMap

    hr.copy(
      mapDetails = mapDetails,
      description = doc >?> text("div.bk-dett-block-content"),
      publisher = doc >?> text("div.bk-inserzionista-box-title"),
      imagesUrl = doc >> attrs("src")("div.swiper-slide.no-padding-bottom > img") toList
    )
  }
}
