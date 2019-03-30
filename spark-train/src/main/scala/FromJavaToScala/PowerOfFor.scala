package FromJavaToScala

object PowerOfFor {
  def main(args: Array[String]): Unit = {
    for (_ <- 1 to 3) {print("ho ")}

    val result = for (i <- 1 to 10) yield i * 2
    println(result)

    val result2 = (1 to 10).map(_ * 2)
    println(result2)

    val doubleEven = for (i <- 1 to 10; if i % 2 == 0) yield i * 2
    println(doubleEven)

    val friends = List(Person("Brian", "Sletten"), Person("Neal", "Ford"),
      Person("Scott", "Davis"), Person("Stuart", "Halloway"))

    val lastNames = for (friend <- friends; lastName = friend.lastName) yield lastName
    println(lastNames)
  }

  class Person(val firstName: String, val lastName: String)
  object Person {
    def apply(firstName: String, lastName: String) = {
      new Person(firstName, lastName)
    }
  }

}
