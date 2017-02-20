/*import com.bfil.scalescrape.actor.ScrapingActor
import com.bfil.scalescrape.data.ScrapingResponse
import java.time.LocalDate
import org.jsoup.nodes.Document

class MomondoScrapingActor extends ScrapingActor {
  def receive = {
    case m: MomondoModel => scrape { ctx =>
      get(m.searchFlightUrl) { response =>
        response.asHtml { html =>
          complete(scrapeFlights(html))
        }
      }
    }
  }
  
  def scrapeFlights(doc: Document) = {
     //for now
  }
}

object MomondoScrapingActor {
  case class Flight(totalMinutes: Int, price: Int, flightDate: LocalDate)
}*/