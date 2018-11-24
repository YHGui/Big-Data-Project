package FromJavaToScala

object MatchLiterals {
  def main(args: Array[String]): Unit = {
    List("Monday", "Sunday", "Saturday").foreach(activity)
  }

  def activity(day: String): Unit = {
    day match {
      case "Sunday" => print("Eat, sleep, repeat... ")
      case "Saturday" => print("Hang out with friends... ")
      case "Monday" => print("...code for fun..")
      case "Friday" => print("...read a good book..")
    }
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
}
