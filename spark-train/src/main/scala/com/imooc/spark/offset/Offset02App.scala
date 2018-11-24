package com.imooc.spark.offset

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Duration, Seconds, StreamingContext}

object Offset02App {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
      .setAppName("Offset02App")
      .setMaster("local[2]")

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> "hadoop000:9092",
      "auto.offset.reset" -> "smallest"
    )
    val topics = "imooc_pk_offset".split(",").toSet

    val checkpointDirectory = "hdfs://hadoop000:8020/offset"
    // Function to create and setup a new StreamingContext
    def functionToCreateContext(): StreamingContext = {
      val ssc = new StreamingContext(sparkConf, Seconds(10))   // new context
      val messages = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
        ssc, kafkaParams, topics
      )

      ssc.checkpoint(checkpointDirectory)   // set checkpoint directory
      messages.checkpoint(Duration(10 * 1000))

      messages.foreachRDD(rdd => {
        if(!rdd.isEmpty()) {
          println("慕课PK哥：" + rdd.count)
        }
      })

      ssc
    }


    val context = StreamingContext.getOrCreate(checkpointDirectory, functionToCreateContext _)


    context.start()
    context.awaitTermination()
  }

}
