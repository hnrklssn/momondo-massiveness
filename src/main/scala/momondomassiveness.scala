import java.util.Scanner
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import MomondoModel._
import MomondoScrapingActor._
import akka.actor._
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{Failure, Success}
//http://www.momondo.se/flightsearch/?Search=true&TripType=1&SegNo=1&SO1=CPH&SD1=CHC&SDP1=03-08-2017&AD=1&TK=ECO&DO=false&NA=false
//http://www.momondo.se/flightsearch/?Search=true&TripType=1&SegNo=1&SO0=CPH&SD0=CHC&SDP0=03-08-2017&AD=1&TK=ECO&DO=false&NA=false
object Main extends App {

  System.setProperty("webdriver.chrome.driver", "C:/chromedriver/chromedriver.exe")
  val scan = new Scanner(System.in)
  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  
  println("""What is your start date to search (yyyy-mm-dd)?""")
  val startDateString = scan.nextLine
  var startDate = LocalDate.parse(startDateString, dateFormatter)
  
  println("""What is your end date to search (yyyy-mm-dd)?""")
  val endDateString = scan.nextLine
  var endDate = LocalDate.parse(endDateString, dateFormatter)
  
  if (startDate.isAfter(endDate)) {
    val temp = endDate
    endDate = startDate
    startDate = temp
  }

  val dates = dateRange(startDate, endDate)
  
  println("How many adult tickets do you want?")
  val adults = scan.nextInt
  
  println("How many child tickets do you want?")
  val children = scan.nextInt
  
  val ages = (1 to children).map { i: Int =>
    println("Write the age of child number " + i)
    scan.nextInt
  }

  println("""What ticket type do your want?
  1. Economy
  2. Premium Economy
  3. Business
  4. First Class""")
  val ticketType: TicketType = scan.nextInt match {
    case 1 => EconomyClass
    case 2 => PremiumEconomyClass
    case 3 => BusinessClass
    case 4 => FirstClass
    case _ => {println("Illegal input"); System.exit(0); null}
  }
  
  println("Do you want to allow stopovers? (true/false)")
  val directOnly = !scan.nextBoolean
  
  println("Do you want to search nearby airports as well? (true/false)")
  val nearbyAirports = scan.nextBoolean
  
  println("""What trip type do you want?
  1. One Way
  2. Return Trip
  3. Multileg Trip""")
  val tripType = scan.nextInt

  val trips: Stream[Trip] = tripType match {
    case 1 => {
      val (outbound, destination) = getAirports(scan)
      dates.map {date: LocalDate =>
        OneWay(TravelLeg(0, outbound, destination, date)) //scala.collection.immutable.Stream[MomondoModel.OneWay]
      }
    }
    case 2 => {
      val (outbound, destination) = getAirports(scan)
      println("How many days do you want to spend before returning?")
      val daysStaying = scan.nextInt
      dates.map {date: LocalDate =>
        ReturnTrip(TravelLeg(0, outbound, destination, date),
        TravelLeg(1, destination, outbound, date.plusDays(daysStaying)))
      } //scala.collection.immutable.Stream[MomondoModel.ReturnTrip]
    }
    case 3 => {
      println("How many legs are there on this trip?")
      val nbrOfLegs = scan.nextInt
      var legDay = startDate
      val legs = (0 until nbrOfLegs).map { i: Int =>
        var daysToAdd = 0;
        if (i > 1) {
          println("How many days do you want to spend before continuing to the next leg?")
          daysToAdd = scan.nextInt
        }
        legDay = legDay.plusDays(daysToAdd)
        println(s"Leg number: $i\n")
        val (outbound, destination) = getAirports(scan)
        TravelLeg(i, outbound, destination, legDay)
      }
      val collectionOfTrips = Stream.iterate(legs){legs => legs.map{leg => leg.withDate(leg.date.plusDays(1))}}
      collectionOfTrips.takeWhile(legs => endDate.isAfter(legs(0).date)).map { MultiTrip(_)} //scala.collection.immutable.Stream[MomondoModel.MultiTrip]
    }
    case _ => {println("Illegal input"); System.exit(0);null}
  }
  
  val props = Props[MomondoScrapingActor]
  val system = ActorSystem()

  implicit val timeout = Timeout(200 seconds)
  val futures = trips.force.toParArray.map { trip => 
    val actor = system.actorOf(props)
    val model = MomondoModel(trip, adults, Children(ages), ticketType, directOnly, nearbyAirports)
    actor ? model
  }
  while (futures.exists(!_.isCompleted)) {
    Thread.sleep(2000)
    System.out.println("Waiting for results...")
  }
  val results = futures.flatMap {_.value}
  results.seq.sortBy(_.isFailure).foreach(t => t match {
    case Success(s) => println(s)
    case f: Failure[Any] => println(f.exception)
  })
  results.filter(_.isSuccess)
    .map {_ match {case Success(s) => s match {case fs: Seq[Flight] => fs}}}
    .flatten
    .seq
    .groupBy{f: Flight => f.flightDate}
    .foreach{ case (date, flights) =>
      println(date)
      flights.sortBy{ f => sq(f.totalMinutes) + sq(f.price) }
      .foreach{println}
    }
  
  private def sq(x: Int) = x*x
  
  private def getAirports(scan: Scanner) = {
    println("What is the airport code for the first airport of this leg of the trip?")
    val outbound = scan.next
    println("What is the airport code for the second airport of this leg of the trip?")
    val destination = scan.next
    (Airport(outbound), Airport(destination))
  }
  
  private def dateRange(start: LocalDate, end: LocalDate): Stream[LocalDate] = {
    Stream.iterate(start){_.plusDays(1)}.takeWhile{end.isAfter(_)}
  }
      
}  