package scrapers

import java.net.URL

import org.joda.time.DateTime

/**
 * Created by fabiofumarola on 13/08/16.
 */
object Utils {

  /**
    *
    * @param absolute
    * @param relative
    * @return the absolute url obtained from the relative one
    */
  def makeUrlAbsolute(absolute: String, relative: String): String = {
      val base = new URL(absolute)
      new URL(base, relative).toExternalForm
  }

  def nowToString() = DateTime.now.toString("YYYY-MM-dd HH:mm::ss")
}
