package scrapers

import scalaj.http.Http

/**
  * Created by fabiofumarola on 12/08/16.
  */
class IdealistaSpec extends UnitSpec {

  val idealista = new Idealista()

  "A Idealista scraper" should "generate the correct number of urls" in {

    val urls = idealista.collectionPagesIterator.toList
    urls should have length idealista.maxCount

    println(urls.head)
  }


  it should "extract records " in {
    val url = idealista.collectionPagesIterator.next()
    val html = Http(url).asString.body

    val records = idealista.extractRecords(html)

//    records.foreach(pprint.log(_))

    records.size should be > 0
  }

  it should "extract details for a record" in {
    val url = idealista.collectionPagesIterator.next()
    val html = Http(url).asString.body
    val record = idealista.extractRecords(html).head

    val recHtml = Http(record.url.get).asString.body

    val detailedRecord = idealista.extractDetails(recHtml,record)

    pprint.log(detailedRecord)

    detailedRecord.mapDetails.size should be >= 3
    detailedRecord.description shouldBe defined
    detailedRecord.publisher shouldBe defined

  }
}
