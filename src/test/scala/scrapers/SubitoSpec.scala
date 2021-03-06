package scrapers

import scalaj.http.Http

/**
  * Created by fabiofumarola on 12/08/16.
  */
class SubitoSpec extends UnitSpec {

  val subito = new Subito()

  "A Subito scraper" should "generate the correct number of urls" in {

    val urls = subito.collectionPagesIterator.toList
    urls should have length subito.maxCount
  }


  it should "extract records " in {
    val url = subito.collectionPagesIterator.next()
    val html = Http(url).asString.body

    val records = subito.extractRecords(html)

//    records.foreach(pprint.log(_))

    records.size should be > 0
  }

  it should "extract details for a record" in {
    val url = subito.collectionPagesIterator.next()
    val html = Http(url).asString.body
    val record = subito.extractRecords(html).head

    val recHtml = Http(record.url.get).asString.body

    val detailedRecord = subito.extractDetails(recHtml,record)

    pprint.log(detailedRecord)

    detailedRecord.mapDetails.size should be >= 3
    detailedRecord.description shouldBe defined
    detailedRecord.publisher shouldBe defined

  }
}
