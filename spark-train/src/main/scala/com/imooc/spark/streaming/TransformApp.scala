package com.imooc.spark.streaming

import org.apache.spark.SparkConf
import org.apache.spark.streaming.{Seconds, StreamingContext}

object TransformApp {
  def main(args: Array[String]): Unit = {

    val conf = new SparkConf().setMaster("local[2]").setAppName("TransformApp")
    val scc = new StreamingContext(conf, Seconds(5))

    /**
      * 构建黑名单
      */
    val blacks = List("zs", "ls")
    val blacksRDD = scc.sparkContext.parallelize(blacks).map(x => (x, true))


    val lines = scc.socketTextStream("localhost", 6789)
    val clicklog = lines.map(x => (x.split(",")(1), x)).transform(rdd => {
      rdd.leftOuterJoin(blacksRDD)
         .filter(x => x._2._2.getOrElse(false) != true)
         .map(x => x._2._1)
    })

    clicklog.print()

    scc.start()
    scc.awaitTermination()
  }
}
