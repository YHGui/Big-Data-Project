package FromJavaToScala

class NameExample {

}

class Name(val value: String) {
  override def toString: String = value
  def length: Int = value.length
}

object UseName extends App {
  def printName(name: Name) = {
    println(name)
  }

  val name = new Name("Snowy")
  println(name.length)
  printName(name)
}
