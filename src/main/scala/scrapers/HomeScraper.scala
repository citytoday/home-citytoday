package scrapers

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.model.Element
import scrapers.models.RawHomeRecord

/**
  * Created by fabiofumarola on 12/08/16.
  */
trait HomeScraper {

  /**
    *
    * @return a function that take a String as parameter and return a String
    */
  def baseUrl: String => String

  def maxCount: Int

  val browser = JsoupBrowser()

  def collectionPagesIterator: Iterator[String] =
    Iterator.range(1, maxCount + 1)
      .map(i => baseUrl(i.toString))

  def extractRecords(html: String): List[RawHomeRecord]

  def extractRecord(e: Element): RawHomeRecord

  def extractDetails(html: String, hr: RawHomeRecord): RawHomeRecord

}
