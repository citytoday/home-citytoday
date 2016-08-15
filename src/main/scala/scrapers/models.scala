package scrapers

/**
 * Created by fabiofumarola on 24/07/16.
 */
object models {

  case class HomeRecord(
    id: Option[String],
    src: String,
    title: Option[String],
    url: Option[String],
    price: Option[String],
    category: Option[String],
    dateTime: Option[String],
    location: Option[String],
    phone: Option[String],
    mapDetails: Map[String,String] = Map.empty,
    description: Option[String] = None,
    publisher: Option[String] = None)
}
