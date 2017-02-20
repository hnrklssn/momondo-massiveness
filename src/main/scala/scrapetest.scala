/*import org.scalatest.selenium.HtmlUnit
import org.openqa.selenium.WebElement
import org.openqa.selenium.By._
import akka.actor.Actor
import java.time.LocalDate

class test extends HtmlUnit {
  //org.apache.log4j.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(org.apache.log4j.Level.FATAL);
java.util.logging.Logger.getLogger("com.gargoylesoftware.htmlunit").setLevel(java.util.logging.Level.OFF);
java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(java.util.logging.Level.OFF);
System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");
  def receive = {
    case m: MomondoModel => scrapeFlights(m.searchFlightUrl)
    case s: String => scrapeFlights(s)
  }
  
  def scrapeFlights(url: String) = {
    go to url //requires call 2 times because unsupported browser
    Thread.sleep(500)
    println(currentUrl)
    click on cssSelector("a.goon")
    Thread.sleep(500)
    println(currentUrl)
    go to url
    Thread.sleep(5000)
    println(currentUrl)
    println("""
      chrome.get(url)
      val html = chrome.findElementByTagName("html")
      val p = "klar".r
      p.findFirstIn(html.getText)
      
      
      printing elements
      
      chrome.get(url)
      val res = chrome.findElementByCssSelector("div.show-on-results")
      println(res.isDisplayed)
      println(res.isEnabled)
      
      """)
    val elements = cssSelector("div.result-box-inner").findAllElements
    println(s"No of elements: ${elements.size}")
    elements.foreach { ele =>
      println(ele)
    }
    println("done")
  }
}
  
  class test2 {
    import scala.collection.JavaConversions._
    System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe");
    def scrapeFlights(url: String) = {
        import org.openqa.selenium.chrome.ChromeDriver
        import org.openqa.selenium.WebElement
        import org.openqa.selenium.By._
        val chrome = new ChromeDriver()
        val p = "Sökningen är klar".r
        chrome.get(url)
        var m: Option[String] = None
        do {
          println("!!")
          Thread.sleep(1500)
          val html = chrome.findElementByTagName("html")
          m = p.findFirstIn(html.getText)
          println(m)
        }
        while (m.isEmpty)
        println("$$")
        val results = chrome.findElementsByCssSelector("div.result-box-inner")
        val flights = results.map(r => Flight(getTravelTime(r), getPrice(r)))
        println("()")
        chrome.quit
        flights
  }
  def getTravelTime(element: WebElement) = {
    val s = element.findElement(new ByCssSelector("div.travel-time")).getText
    val pattern = """(\d+)tim (\d+)min""".r
    val m = pattern.findAllIn(s)
    val mdata = m.matchData.next
    val hours = mdata.group(1).toInt
    val minutes = mdata.group(2).toInt
    60 * hours + minutes
  }
  
  def getPrice(element: WebElement) = {
    val s = element.findElement(new ByCssSelector("span.value")).getText
    s.replaceAll("""[\D]""", "").toInt
  }
}


  case class Flight(totalMinutes: Int, price: Int)//, flightDate: LocalDate)*/