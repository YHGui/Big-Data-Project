package com.imooc.spark.offset

import kafka.common.TopicAndPartition
import kafka.message.MessageAndMetadata
import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.{HasOffsetRanges, KafkaUtils}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 1、创建StreamingContext
  * 2、从kafka中获取数据 <== offset获取
  * 3、根据业务逻辑进行处理
  * 4、将处理结果写入到外部存储中去 <==offset保存
  * 5、启动程序，等待程序终止
  */

object Offset03App {

  def main(args: Array[String]): Unit = {

    val sparkConf = new SparkConf()
      .setAppName("Offset03App")
      .setMaster("local[2]")
    val ssc = new StreamingContext(sparkConf, Seconds(10))

    val kafkaParams = Map[String, String](
      "metadata.broker.list" -> "hadoop000:9092",
      "auto.offset.reset" -> "smallest"
    )
    val topics = "imooc_pk_offset".split(",").toSet

    /**
      * 获取偏移量
      *
      * MySql/ZK/NoSql
      */
    val fromOffsets = Map[TopicAndPartition, Long]()


    val messages = if(fromOffsets.size == 0) {//从头开始消费
      KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](
        ssc, kafkaParams, topics)
    } else {//从指定偏移量开始消费
      val messageHandler = (mm:MessageAndMetadata[String, String]) => (mm.key(), mm.message())
      KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder, (String, String)](ssc, kafkaParams, fromOffsets, messageHandler)
    }

    messages.foreachRDD(rdd => {
      if(!rdd.isEmpty()) {
        println("慕课PK哥：" + rdd.count)
      }


      /**
        *将offset提交
        */
      val offsetRanges = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
      offsetRanges.foreach(o => {

        //提交如下信息到外部存储：ZK/MySql/NoSql
        println(s"${o.topic} ${o.partition} ${o.fromOffset} ${o.untilOffset}")
      })
    })

    ssc.start()
    ssc.awaitTermination()
  }

}
