package FromJavaToScala

object Factorial {

  def main(args: Array[String]): Unit = {
    println(factorial(5))
    println(factorial2(1, 1000))
  }

  def factorial(number: Int): BigInt = {
    if (number == 0) {
      1
    } else {
      number * factorial(number - 1)
    }
  }

  @scala.annotation.tailrec
  def factorial2(fact: BigInt, number: Int):BigInt = {
    if (number == 0)
      fact
    else
      factorial2(fact * number, number - 1)
  }

}
