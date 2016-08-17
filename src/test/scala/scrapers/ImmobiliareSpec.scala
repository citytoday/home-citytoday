package scrapers

import scalaj.http.Http

/**
  * Created by fabiofumarola on 12/08/16.
  */
class ImmobiliareSpec extends UnitSpec {

  val immobiliare = new Immobiliare()

  "A Immobiliare scraper" should "generate the correct number of urls" in {

    val urls = immobiliare.collectionPagesIterator.toList
    urls should have length immobiliare.maxCount
  }


  it should "extract records " in {
    val url = immobiliare.collectionPagesIterator.next()
    val html = Http(url).asString.body

    val records = immobiliare.extractRecords(html)

//    records.foreach(pprint.log(_))

    records.size should be > 0
  }

  it should "extract details for a record" in {
    val url = immobiliare.collectionPagesIterator.next()
    val html = Http(url).asString.body
    val record = immobiliare.extractRecords(html).head

    val recHtml = Http(record.url.get).asString.body

    val detailedRecord = immobiliare.extractDetails(recHtml,record)

    pprint.log2(detailedRecord)

    detailedRecord.mapDetails.size should be >= 3
    detailedRecord.description shouldBe defined
    detailedRecord.publisher shouldBe defined

  }
}
