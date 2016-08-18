package scrapers

import scalaj.http.Http

/**
  * Created by fabiofumarola on 12/08/16.
  */
class BachecaSpec extends UnitSpec {

  val bacheca = new Bacheca()

  "A Bacheca scraper" should "generate the correct number of urls" in {

    val urls = bacheca.collectionPagesIterator.toList
    urls should have length bacheca.maxCount
  }

  it should "extract records " in {
    val url = bacheca.collectionPagesIterator.next()
    val html = Http(url).asString.body

    val records = bacheca.extractRecords(html)

//    records.foreach(pprint.log(_))

    records.size should be > 0
  }

  it should "extract details for a record" in {
    val url = bacheca.collectionPagesIterator.next()
    val html = Http(url).asString.body
    val record = bacheca.extractRecords(html).head

    val recHtml = Http(record.url.get).asString.body

    val detailedRecord = bacheca.extractDetails(recHtml,record)

    pprint.log(detailedRecord)

    detailedRecord.mapDetails.size should be > 0
    detailedRecord.description shouldBe defined
    detailedRecord.publisher shouldBe defined

  }
}
