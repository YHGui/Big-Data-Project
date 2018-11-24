package com.imooc.spark.offset

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

object Offset01App {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
      .setAppName("Offset01App")
      .setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> "hadoop000:9092",
      "auto.offset.reset" -> "smallest"
    )
    val topics = "imooc_pk_offset".split(",").toSet

    val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
      ssc, kafkaParams, topics
    )

    messages.foreachRDD(rdd => {
      if(!rdd.isEmpty()) {
        println("慕课PK哥：" + rdd.count)
      }
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
