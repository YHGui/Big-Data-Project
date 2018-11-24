package com.imooc.spark.dao

import com.imooc.spark.domain.{CourseClickCount, CourseSearchClickCount}
import com.imooc.spark.project.utils.HBaseUtils
import org.apache.hadoop.hbase.client.Get
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

/**
  * 从搜索过来的实战课程点击数-数据访问层
  */
object CourseSearchClickCountDao {

  val tableName = "imooc_course_search_clickcount"

  val cf = "info"

  val qualifier = "click_count"

  def save(list: ListBuffer[CourseSearchClickCount]): Unit ={
    val table = HBaseUtils.getInstance().getTable(tableName)

    for(ele <- list) {
      table.incrementColumnValue(Bytes.toBytes(ele.day_search_course),
        Bytes.toBytes(cf),
        Bytes.toBytes(qualifier),
        ele.click_count)
    }
    println("save success")
  }

  def count(day_search_course: String) : Long = {
    val table = HBaseUtils.getInstance().getTable(tableName)

    val get = new Get(Bytes.toBytes(day_search_course))
    val value = table.get(get).getValue(cf.getBytes(), qualifier.getBytes())

    if(null == value) {
      0l
    } else {
      Bytes.toLong(value)
    }
  }

  def main(args: Array[String]): Unit = {
    val list = new ListBuffer[CourseSearchClickCount]
    list.append(CourseSearchClickCount("20181111_www.baidu.com_19", 2))
    list.append(CourseSearchClickCount("20181111_www.yahoo.com_30", 12))
    list.append(CourseSearchClickCount("20181111_www.sogou.com_1", 112))

    save(list)
  }

}
