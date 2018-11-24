package com.imooc.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object StatefulWordCount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StatefulWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))

    //如果使用了stateful的算子，必须要设置checkpoint
    //生产环境下一般放在hdfs中
    ssc.checkpoint(".")

    val lines = ssc.socketTextStream("localhost", 6789)
    val result = lines.flatMap(_.split("")).map((_, 1))
    val state = result.updateStateByKey(updateFunction _)
    state.print()


    ssc.start()
    ssc.awaitTermination()
  }

  /**
    * 把当前的数据去更新已有的或者是老的数据
    * @param currentValues
    * @param preValues
    * @return
    */
  def updateFunction(currentValues: Seq[Int], preValues: Option[Int]): Option[Int] = {
    val current = currentValues.sum
    val pre = preValues.getOrElse(0)
    Some(current + pre)
  }

}
