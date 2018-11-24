package com.imooc.spark.dao

import com.imooc.spark.domain.CourseClickCount
import com.imooc.spark.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * 实战课程点击数-数据访问层
  */
object CourseClickCountDao {

  val tableName = "imooc_course_clickcount"

  val cf = "info"

  val qualifier = "click_count"

  def save(list: ListBuffer[CourseClickCount]): Unit ={
    val table = HBaseUtils.getInstance().getTable(tableName)

    for(ele <- list) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifier),
        ele.click_count)
    }
    println("save success")
  }

  def count(day_course: String) : Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_course))
    val value = table.get(get).getValue(cf.getBytes(), qualifier.getBytes())

    if(null == value) {
      0l
    } else {
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseClickCount]
    list.append(CourseClickCount("20181111_19", 2))
    list.append(CourseClickCount("20181111_30", 12))
    list.append(CourseClickCount("20181111_1", 112))

    save(list)
  }

}
