package FromJavaToScala

import java.io.{File, PrintWriter}

object FunctionValuesAndClosure {
  def main(args: Array[String]): Unit = {
    var result = 0
    val addIt = { value: Int => result += value}

    loopThrough(10) {elem => addIt(elem)}
    println(s"total of values from 1 to 10 is $result")

    result = 0
    loopThrough(5){ addIt }
    println(s"total of values from 1 to 5 is $result")

    var product = 1
    loopThrough(5) { product *= _}
    println(s"total of values from 1 to 5 is $product")

    Resource.use { resource =>
      resource.op1()
      resource.op2()
      resource.op3()
      resource.op1()
    }

    writeToFile("output.txt") { writer =>
      writer write "hello from scala"
    }
  }

  def loopThrough(number: Int)(closure: Int => Unit) = {
    for (i <- 1 to number) {closure(i)}
  }

  def writeToFile(filename: String)(codeBlock: PrintWriter => Unit) = {
    val writer = new PrintWriter(new File(filename))
    try { codeBlock(writer) } finally { writer.close() }
  }
}
