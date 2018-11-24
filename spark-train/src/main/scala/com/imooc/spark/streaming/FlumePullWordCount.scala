package com.imooc.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.flume.FlumeUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object FlumePullWordCount {

  def main(args: Array[String]): Unit = {

    if(args.length != 2) {
      System.err.println("Usage: FlumePullWordCount <hostname> <port>")
      System.exit(1)
    }

    val Array(hostname, port) = args

    val conf = new SparkConf()
                   //.setMaster("local[2]")
                   //.setAppName("FlumePullWordCount")
    val ssc = new StreamingContext(conf, Seconds(5))

    //TODO....如何使用Spark Streaming 整合 Flume
    val flumeStream = FlumeUtils.createPollingStream(ssc, hostname, port.toInt)
    flumeStream.map(x => new String(x.event.getBody.array()).trim)
        .flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _).print()


    ssc.start()
    ssc.awaitTermination()
  }

}
