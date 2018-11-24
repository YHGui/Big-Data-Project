package com.imooc.spark.streaming

import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf}

object FileWordCount {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setMaster("local").setAppName("FileWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.textFileStream("file:///Users/guiyonghui/Documents/big-data-project/spark-train/data/")
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    result.print()

    ssc.start()
    ssc.awaitTermination()


  }

}
