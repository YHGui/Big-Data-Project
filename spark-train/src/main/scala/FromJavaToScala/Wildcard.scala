package FromJavaToScala

object Wildcard {

  def main(args: Array[String]): Unit = {
    /*activity(DayOfWeek.SATURDAY)
    activity(DayOfWeek.MONDAY)*/

    /*processCoordinates((39, -104))
    processCoordinates((39.3, -104.0))
    processCoordinates("done")*/

    /*processItems(List("apple", "ibm"))
    processItems(List("red", "blue", "green"))
    processItems(List("red", "blue", "white"))
    processItems(List("apples", "orange", "grapes", "dates"))*/

    /*process((34.2, -159.3))
    process(0)
    process(1000001)
    process(2.2)*/

    val sample = new Sample
    try {
      sample.process(0)
    } catch {
      case ex: Throwable => println(ex)
    }
    sample.process(100)
  }

  object DayOfWeek extends Enumeration {

    val SUNDAY: DayOfWeek.Value = Value("Sunday")
    val MONDAY: DayOfWeek.Value = Value("Monday")
    val TUESDAY: DayOfWeek.Value = Value("Tuesday")
    val WEDNESDAY: DayOfWeek.Value = Value("Wednesday")
    val THURSDAY: DayOfWeek.Value = Value("Thursday")
    val FRIDAY: DayOfWeek.Value = Value("Friday")
    val SATURDAY: DayOfWeek.Value = Value("Saturday")

  }

  def activity(day: DayOfWeek.Value): Unit = {
    day match {
      case DayOfWeek.SUNDAY => println("Eat, sleep, repeat...")
      case DayOfWeek.SATURDAY => println("Hang out with friends")
      case _ => println("...code for fun...")
    }
  }

  def processCoordinates(input: Any): Unit = {
    input match {
      case (lat, lon) => printf("Processing (%d, %d)...", lat, lon)
      case "done" => println("done")
      case _ => println("invalid input")
    }
  }

  def processItems(items: List[String]): Unit = {
    items match {
      case List("apple", "ibm") => println("Apple and IBMs")
      case List("red", "blue", "white") => println("Stars and Stripes...")
      case List("red", "blue", _*) => println("colors red, blue,... ")
      case List("apples", "orange", otherFruits @ _*) => println("apples, oranges, and " + otherFruits)
    }
  }

  def process(input: Any): Unit = {
    input match {
      case (_: Int, _: Int) => println("Processing (int, int)...")
      case (_: Double, _: Double) => println("Processing (double, double)...")
      case msg: Int if msg > 1000000 => println("Processing int > 1000000...")
      case _: Int  => println("Processing int...")
      case _: String  => println("Processing string...")
      case _  => println("Can't handle $input... ")
    }
  }
}

class Sample {
  val MAX = 100

  def process(input: Int): Unit = {
    input match {
      case MAX => println(s"You matched max $MAX")
    }
  }
}
