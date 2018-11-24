package FromJavaToScala

import FromJavaToScala.Currency.Currency

object Currency extends Enumeration {

  type Currency = Value
  val CNY, GBP, INR, JPY, NOK, PLN, SEK, USD = Value

  def main(args: Array[String]): Unit = {
    Currency.values.foreach{
      currency => println(currency)
    }
  }

}

class Money(val amount: Int, val currency: Currency) {
  override def toString: String = s"$amount $currency"
}
