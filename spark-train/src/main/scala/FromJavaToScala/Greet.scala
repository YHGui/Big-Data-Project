package FromJavaToScala

import scala.beans.BeanProperty

object Greet {
  def main(args: Array[String]): Unit = {
    /*for (i <- 1 to 3) {
      print(s"$i, ")
    }

    println("")

    for (i <- 1 until 3)
      print(s"$i, ")

    println("")

    1 to 3 foreach(i => print(s"$i, "))
    println("Scala Rocks")

    val(firstName, lastName, emailAddress) = getPersonInfo(1)
    println(s"First Name: $firstName")
    println(s"Last Name: $lastName")
    println(s"Email Address: $emailAddress")*/

    /*println(max(8, 2, 4))

    function(1, 2, 3)

    val numbers = Array(2, 3, 5, 7, 1, 6)
    max(numbers: _*)

    mail("Houston office", "Priority")
    mail("Boston office")
    mail()*/

    /*atOffice()
    atJoeHome()*/

    /*var price = 100
    val discount = 10
    val totalPrice = s"The amount of discount is $$${price * discount / 100}"
    println(totalPrice)

    price = 50
    println(s"The amount of discount is $$${price * discount / 100}")*/

    /*val product = "ticket"
    val price = 25.12
    val  discount = 10
    println(s"On $product $discount% saves $$${price * discount / 100.00}")
    println(f"On $product $discount%% saves $$${price * discount / 100.00}%2.2f")*/


    /*val microwave = new Microwave
    microwave.start()*/

    /*val john = new Person("John", "Smith", "Analyst")
    println(john)
    val bill = new Person("Bill", "Walker")
    println(bill)*/

    type Cop = PoliceOfficer

    val topCop = new Cop("Jack")
    println(topCop.getClass)
  }


  def getPersonInfo(primaryKey: Int)= {
    ("Venkat", "Subramaniam", "venkats@agiledeveloper.com")
  }

  def max(values: Int*) = values.foldLeft(values(0)) {Math.max}

  def function(input: Int*) = println(input.getClass)

  def mail(destination: String = "head office", mailClass: String = "first") = {
    println(s"sending to $destination by $mailClass")
  }

  class Wifi(name: String) {
    override def toString: String = name
  }

  def connectToNetwork(user: String)(implicit wifi: Wifi): Unit = {
    println(s"User: $user connected to WIFI $wifi")
  }

  def atOffice() = {
    println("--- at the office ---")
    implicit def officeNetwork: Wifi = new Wifi("office-network")
    val cafeterialNetwork = new Wifi("cafe-connect")

    connectToNetwork("guest")(cafeterialNetwork)
    connectToNetwork("Jill Coder")
    connectToNetwork("JOe Hacker")
  }

  def atJoeHome(): Unit = {
    println("--- at Joe's Home ---")
    implicit def homeNetwork: Wifi = new Wifi("home-network")

    connectToNetwork("guest")(homeNetwork)
    connectToNetwork("Joe Hacker")
  }


  class Microwave {
    def start(): Unit = {
      println("started")
    }

    def stop(): Unit = {
      println("stopped")
    }

    private def turnTable(): Unit = {
      println("turning table")
    }
  }

  class Vehicle {
    protected def checkEngine{}
  }

  class Car extends Vehicle {
    def start: Unit = {
      checkEngine
    }

    def tow(car: Car): Unit = {
      car.checkEngine
    }

    /*def tow2(vehicle: Vehicle): Unit = {
      vehicle.checkEngine
    }*/
  }

  /*class GasStation{
    def fillGas(vehicle: Vehicle): Unit = {
      vehicle.checkEngine()
    }
  }*/

  class CreditCar(val number: Int, var creditLimit: Int)

  class Construct(param: String) {
    println(s"Creating an instance of Construct with parameter $param")
  }

  class Person(val firstName: String, val lastName: String) {
    var position: String = _

    println(s"Creating $toString")

    def this(firstName: String, lastName: String, positionHeld: String) {
      this(firstName, lastName)
      position = positionHeld
    }

    override def toString: String = {
      s"$firstName $lastName holds $position position"
    }
  }

  class Dude(@BeanProperty val firstName: String, val lastName: String) {
    @BeanProperty var position: String = _
  }

  class PoliceOfficer(val name: String)
}
