package com.imooc.spark.streaming

import java.sql.DriverManager

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * 使用Spark Streaming完成所有词频统计，并将结果写入到Mysql数据库中
  */
object ForeachRDDApp {

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("StatefulWordCount").setMaster("local[2]")
    val ssc = new StreamingContext(conf, Seconds(5))

    val lines = ssc.socketTextStream("localhost", 6789)
    val result = lines.flatMap(_.split(" ")).map((_, 1)).reduceByKey(_ + _)
    //result.print()//此处是将统计结果输出都控制台
    //TODO将结果写入到Mysql

    /*result.foreachRDD(rdd =>{
      val connection = createConnection()
      rdd.foreach{record =>
        val sql = "insert into wordcount(word, wordcount) values('" + record._1 + "'," + record._2 + ")"
        connection.createStatement().execute(sql)
      }
    })*/
    result.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
          val connection = createConnection()
          partitionOfRecords.foreach(record => {
            val sql = "insert into wordcount(word, wordcount) values('" + record._1 + "', " + record._2 + ")"
            connection.createStatement().execute(sql)
          })

          connection.close()
      })
    })


    ssc.start()
    ssc.awaitTermination()
  }

  def createConnection() = {
    Class.forName("com.mysql.jdbc.Driver")
    DriverManager.getConnection("jdbc:mysql://localhost:3306/imooc_spark", "root", "root")

  }

}
