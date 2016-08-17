package scrapers

import scalaj.http.Http

/**
  * Created by fabiofumarola on 12/08/16.
  */
class CasaSpec extends UnitSpec {

  val casa = new Casa()

  "A Casa scraper" should "generate the correct number of urls" in {

    val urls = casa.collectionPagesIterator.toList
    urls should have length casa.maxCount
  }


  it should "extract records " in {
    val url = casa.collectionPagesIterator.next()
    val html = Http(url).asString.body

    val records = casa.extractRecords(html)

//    records.foreach(pprint.log(_))

    records.size should be > 0
  }

  it should "extract details for a record" in {
    val url = casa.collectionPagesIterator.next()
    val html = Http(url).asString.body
    val record = casa.extractRecords(html).head

    val recHtml = Http(record.url.get).asString.body

    val detailedRecord = casa.extractDetails(recHtml,record)

    pprint.log2(detailedRecord)

    detailedRecord.mapDetails.size should be >= 3
    detailedRecord.description shouldBe defined
    detailedRecord.publisher shouldBe defined

  }
}
