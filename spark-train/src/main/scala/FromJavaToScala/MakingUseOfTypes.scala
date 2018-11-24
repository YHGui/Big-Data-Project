package FromJavaToScala

import java.time.LocalDate

object MakingUseOfTypes {

  def main(args: Array[String]): Unit = {
    for (input <- Set("test", "hack")) {
      val comment = commentOfPractice(input)
      val commentDisplay = comment.getOrElse("Found no comments")
      println(s"input: $input comment: $commentDisplay")
    }

    /*displayResult(compute(4))
    displayResult(compute(-4))*/

    /*val dogs = Array(new Dog("Rover"), new Dog("Comet"))
    workWithPets(dogs)*/
    /*implicit def convertInt2DateHelper(offset: Int): DateHelper = new DateHelper(offset)

    val ago = "ago"
    val from_now = "from_now"

    val past = 2 days ago
    val appointment = 5 days from_now

    println(past)
    println(appointment)*/

  }

  def commentOfPractice(input: String) = {
    if (input == "test") Some("good") else None
  }

  def compute(input: Int) = {
    if (input > 0)
      Right(math.sqrt(input))
    else
      Left("Error computing, invalid input")
  }

  def displayResult(result: Either[String, Double]): Unit = {
    println(s"Raw: $result")
    result match {
      case Right(value) => println(s"result $value")
      case Left(err) => println(s"Error: $err")
    }
  }

  class Pet(val name: String) {
    override def toString: String = name
  }

  class Dog(override val name: String) extends Pet(name)

  def workWithPets[T <: Pet](pets: Array[T]): Unit = {
    println("Playing with pets: " + pets.mkString(", "))
  }



}

class DateHelper(offset: Int) {
  def days(when: String) = {
    val today = LocalDate.now
    when match {
      case "ago" => today.minusDays(offset)
      case "from_now" => today.plusDays(offset)
      case _ => today
    }
  }
}

object DateHelper {
  val ago = "ago"
  val from_now = "from_now"
  implicit def convertInt2DateHelper(offset: Int) = {
    new DateHelper(offset)
  }
}
