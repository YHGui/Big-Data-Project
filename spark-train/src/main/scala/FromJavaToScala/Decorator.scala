package FromJavaToScala

object Decorator {
  def main(args: Array[String]): Unit = {
    val apartmentApplication = new Check with CreditCheck with CriminalRecordCheck

    println(apartmentApplication.check)

    val employmentApplication = new Check with CriminalRecordCheck with EmploymentCheck

    println(employmentApplication.check)
  }
}

abstract class Check {
  def check: String = "Checked Application Details..."
}

trait CreditCheck extends Check {
  override def check: String = s"Checked Credit... ${super.check}"
}

trait EmploymentCheck extends Check {
  override def check: String = s"Checked Employment... ${super.check}"
}

trait CriminalRecordCheck extends Check {
  override def check: String = s"Check Criminal Records...${super.check}"
}


