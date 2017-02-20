import akka.actor.Actor
import java.time.LocalDate
import org.openqa.selenium.WebElement
import org.openqa.selenium.By._

class MomondoScrapingActor extends Actor {
  import MomondoScrapingActor._
  //org.apache.log4j.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(org.apache.log4j.Level.FATAL);
//java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF)
//java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(java.util.logging.Level.OFF)
//System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog")
  def receive = {
    case m: MomondoModel => sender ! scrapeFlights(m.searchFlightUrl, m.trip.legs(0).date)
    case s: String => sender ! scrapeFlights(s, LocalDate.now())
  }
  
  def scrapeFlights(url: String, date: LocalDate) = {
    import org.openqa.selenium.chrome.ChromeDriver
    import scala.collection.JavaConversions._
    val chrome = new ChromeDriver()
    val p = "Sökningen är klar".r
    chrome.get(url)
    var m: Option[String] = None
    do {
      Thread.sleep(4000)
      val html = chrome.findElementByTagName("html")
      m = p.findFirstIn(html.getText)
    } while (m.isEmpty)
    val results = chrome.findElementsByCssSelector("div.result-box-inner")
    val flights = results.map(r => Flight(getTravelTime(r), getPrice(r), date))
    chrome.quit
    flights
  }
  
  private def getTravelTime(element: WebElement) = {
    val s = element.findElement(new ByCssSelector("div.travel-time")).getText
    val pattern = """(\d+)tim (\d+)min""".r
    val m = pattern.findAllIn(s)
    val mdata = m.matchData.next
    val hours = mdata.group(1).toInt
    val minutes = mdata.group(2).toInt
    60 * hours + minutes
  }
  
  private def getPrice(element: WebElement) = {
    val s = element.findElement(new ByCssSelector("span.value")).getText
    s.replaceAll("""[\D]""", "").toInt
  }
}

object MomondoScrapingActor {
  case class Flight(totalMinutes: Int, price: Int, flightDate: LocalDate)
}