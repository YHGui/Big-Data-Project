package FromJavaToScala

class TradeStock {

}

trait Trade

case class Sell(stockSymbol: String, quantity: Int) extends Trade
case class Buy(stockSymbol: String, quantity: Int) extends Trade
case class Hedge(stockSymbol: String, quantity: Int) extends Trade
case class Apple()
case class Orange()
case class Book()

object TradeProcessor {
  def main(args: Array[String]): Unit = {
    processTransaction(Sell("GOOG", 500))
    processTransaction(Buy("GOOG", 700))
    processTransaction(Sell("GOOG", 1000))
    processTransaction(Buy("GOOG", 3000))
  }

  def processTransaction(request: Trade): Unit = {
    request match {
      case Sell(stock, 1000) => println(s"Selling 1000-unit of $stock")
      case Sell(stock, quantity) => println(s"Selling $quantity-unit of $stock")
      case Buy(stock, quantity) if quantity > 2000 => println(s"Buying $quantity (large) units of $stock")
      case Buy(stock, quantity) => println(s"Buying $quantity units of $stock")
    }
  }
}

object ThingsAcceptor {
  def main(args: Array[String]): Unit = {
    acceptStuff(Apple())
    acceptStuff(Book())
    acceptStuff(Apple)
  }
  def acceptStuff(thing: Any): Unit = {
    thing match {
      case Apple() => println("Thanks for the apple")
      case Orange() => println("Thanks for the orange")
      case Book() => println("Thanks for the book")
      case _ => println("Excuse me, why did you send me $thing")
    }
  }
}

object StockService {

  def main(args: Array[String]): Unit = {
    /*StockService process "GOOG"
    StockService process "GOOG:310.84"
    StockService process "GOOG:BUY"
    StockService process "ERR:12.21"*/

    /*val pattern = "(S|s)cala".r
    val str = "Scala is scalable and cool"
    println(pattern findFirstIn str)
    println((pattern findAllIn str).mkString(", "))
    println("cool".r replaceFirstIn (str, "awesome"))*/

    /*process2("GOOG:310.84")
    process2("GOOG:310")
    process2("IBM:84.01")*/

    process3("GOOG:310.84")
    process3("GE:15.96")
    process3("IBM:84.01")
  }

  def process(input: String): Unit = {
    input match {
      case Symbol() => println(s"Look up price for valid symbol $input")
      case ReceiveStockPrice(symbol @Symbol(), price) => println(s"Received price $$$price for symbol $symbol")
      case _ => println(s"Invalid input $input")
    }
  }

  def process2(input: String): Unit = {
    val  GoogStock = """^GOOG:(\d*\.\d+)""".r
    input match {
      case GoogStock(price) => println(s"Price of GOOG is $$$price")
      case _ => println(s"not processing $input")
    }
  }

  def process3(input: String): Unit = {
    val  MatchStock = """^(.+):(\d*\.\d+)""".r
    input match {
      case MatchStock("GOOG", price) => println(s"We got GOOG at $$$price")
      case MatchStock("IBM", price) => println(s"IBM's trading at $$$price")
      case MatchStock(symbol, price) => println(s"Price of $symbol is $$$price")
      case _ => println(s"not processing $input")
    }
  }
}

object Symbol {
  def unapply(symbol: String) = {
    symbol == "GOOG" || symbol == "IBM"
  }
}

object ReceiveStockPrice {
  def unapply(input: String): Option[(String, Double)] = {
    try {
      if (input contains ":") {
        val splitQuote = input split ":"
        Some((splitQuote(0), splitQuote(1).toDouble))
      } else {
        None
      }
    } catch {
      case _: NumberFormatException => None
    }
  }
}

object Tax {

  def main(args: Array[String]): Unit = {
    for (amount <- List(100.0, 0.009, -2.0, 10000001.0)) {
      try {
        println(s"Amount: $$$amount")
        println(s"Tax: $$${taxFor(amount)}")
      } catch {
        case ex: IllegalArgumentException => println(ex.getMessage)
        case ex: RuntimeException => println(s"Don't bother reporting...${ex.getMessage}")
      }
    }
  }
  def taxFor(amount: Double) = {
    if (amount < 0) throw new IllegalArgumentException("Amount must be greater than zero")

    if (amount < 0.01) throw new RuntimeException("Amount too small to be taxed")

    if (amount > 1000000) throw new Exception("Amount too large...")

    amount * 0.08
  }
}
