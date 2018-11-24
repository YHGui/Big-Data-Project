package FromJavaToScala

object DateUtil {

  val ago = "ago"
  val from_now = "from_now"

  implicit class DateHelper(val offset: Int) {
    import java.time.LocalDate
    def days(when: String) = {
      val  today = LocalDate.now
      when match {
        case "ago" => today.minusDays(offset)
        case "from_now" => today.plusDays(offset)
        case _ => today
      }
    }
  }

  def main(args: Array[String]): Unit = {
    val past = 2 days ago
    val appointment = 5 days ago

    println(past)
    println(appointment)
  }

}
