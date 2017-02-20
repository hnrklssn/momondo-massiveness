import java.time.LocalDate
import scala.collection.immutable.IndexedSeq
import java.time.format.DateTimeFormatter

import MomondoModel._
case class MomondoModel(trip: Trip, adults: Int, children: Children, ticketClass: TicketType, directOnly: Boolean, nearbyAirports: Boolean) {
  private val baseUrl = "http://www.momondo.se/"
  val homePage = baseUrl
  
  lazy val searchFlightUrl =
    s"${baseUrl}flightsearch/?Search=true${trip.searchString}&AD=${adults}${ticketClass.searchString}&DO=${directOnly}&NA=${nearbyAirports}"
}

object MomondoModel {
  val dateStringFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
  
  trait SearchProperty { def searchString: String }
  trait Trip extends SearchProperty { 
    def code: Int
    def legs: IndexedSeq[TravelLeg]
    lazy val searchString = legs.foldLeft(s"&TripType=$code&SegNo=$code") {
      (s: String, leg: TravelLeg) => s + leg.searchString
    }
  }
  case class OneWay(outbound: TravelLeg) extends Trip {
    override def code = 1
    override def legs = IndexedSeq(outbound)
  }
  case class ReturnTrip(outbound: TravelLeg, returnFlight: TravelLeg) extends Trip {
    override def code = 2
    override def legs = IndexedSeq(outbound, returnFlight)
  }
  case class MultiTrip(legs: IndexedSeq[TravelLeg]) extends Trip { val code = 4 }
  
  trait TicketType extends SearchProperty { 
    def code: String
    val searchString = s"&TK=$code"
  }
  case object EconomyClass extends TicketType {
    override def code = "ECO"
    override def toString() = "Economy Class"
  }
  case object PremiumEconomyClass extends TicketType {
    override def code = "FLX"
    override def toString() = "Premium Economy Class"
  }
  case object BusinessClass extends TicketType {
    override def code = "BIZ"
    override def toString() = "Business Class"
  }
  case object FirstClass extends TicketType {
    override def code = "FST"
    override def toString() = "First Class"
  }
  
  case class TravelLeg(legNbr: Int, origin: Airport, destination: Airport, date: LocalDate) extends SearchProperty {
    private val dateString = date.format(dateStringFormatter)
    println(date)
    println(dateString)
    //example search string: &SO0=CPH&SD0=CHC&SDP0=04-08-2017
    def searchString = s"&SO$legNbr=$origin&SD$legNbr=$destination&SDP$legNbr=$dateString"
    def withDate(newDate: LocalDate) = TravelLeg(legNbr, origin, destination, newDate)
  }
  
  case class Airport(code: String) {
    private val pattern = """^[a-zA-Z]{3}$"""
    if(!code.matches(pattern)) {
      throw new IllegalArgumentException("Airport code must be 3 letters")
    }
    override def toString = code
  }
  
  case class Children(childAges: IndexedSeq[Int]) extends SearchProperty {
    def searchString = "&CA=" + childAges.map(c => c toString).mkString(",")
  }

}