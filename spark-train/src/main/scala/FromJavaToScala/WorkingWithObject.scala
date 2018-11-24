package FromJavaToScala


import scala.collection.mutable

object WorkingWithObject {

  def main(args: Array[String]): Unit = {
    /*val car = new Car(1, 2015, 100)
    println(car)
    echo(4, 5)
    echo[String]("hi", "hello")*/

    val message1: Message[String] = new Message("howdy")
    //val message1 = new Message("howdy")
    val message2 = new Message(42)

    println(message1)
    println(message1.is("howdy"))
    println(message1.is("hi"))
    println(message2.is(22))

    /*println(MarkerFactory getMarker "blue")
    println(MarkerFactory getMarker "blue")
    println(MarkerFactory getMarker "red")
    println(MarkerFactory getMarker "red")
    println(MarkerFactory getMarker "green")*/

    /*println(s"Supported colors are: ${Marker.supportedColors}")
    println(Marker("blue"))
    println(Marker("red"))*/

  }


  def echo[T](input1: T, input2: T): Unit = {
    println(s"got $input1 ${input1.getClass} $input2 ${input2.getClass}")
  }
  class Vehicle(val id: Int, val year: Int) {
    override def toString: String = s"ID: $id, Year: $year"
  }

  class Car(override val id: Int, override val year: Int, var fuelLevel: Int) extends Vehicle(id, year) {
    override def toString: String = s"${super.toString} Fuel Level: $fuelLevel"
  }

  class Message[T](val content: T) {
    override def toString: String = s"message content is $content"

    def is(value: T): Boolean = value == content
  }

  class Marker(val color: String) {
    //println(s"Creating ${this}")

    override def toString: String = s"marker color $color"
  }

  object Marker {
    private val markers = mutable.Map(
      "red" -> new Marker("red"),
    "blue" -> new Marker("blue"),
    "yellow" -> new Marker("yellow")
    )

    /*def getMarker(color: String) = {
      markers.getOrElseUpdate(color, new Marker(color))
    }*/

    def apply(color: String): Marker = markers.getOrElseUpdate(color, op = new Marker(color))

    def supportedColors: Iterable[String] = markers.keys
  }
}
