package com.imooc.bigdata;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.topology.base.BaseRichBolt;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.utils.Utils;

import java.util.Map;

/**
 * 使用Storm实现累计求和的操作
 */
public class LocalSumStormTopology {

    private SpoutOutputCollector collector;

    /**
     * Spout需要继承BaseRichSpout
     * 数据源需要产生数据并发射
     */
    public static class DataSourceSpout extends BaseRichSpout{

        private SpoutOutputCollector collector;

        /**
         * 初始化方法，只会调用一次
         * @param conf 配置参数
         * @param context 上下文
         * @param collector 数据发射器
         */
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            this.collector = collector;
        }

        int number = 0;

        /**
         * 产生数据，生产环境是从消息队列中获取数据
         * 不断执行
         */
        public void nextTuple() {
            this.collector.emit(new Values(++number));

            System.out.println("Spout: " + number);

            //防止数据产生太快
            Utils.sleep(1000);
        }

        /**
         * 声明输出字段
         * @param declarer
         */
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("num"));
        }
    }


    /**
     * 数据的累计求和Bolt：接收数据并处理
     */
    public static class SumBolt extends BaseRichBolt {

        int sum = 0;

        /**
         * 初始化方法，只执行一次
         * @param stormConf
         * @param context
         * @param collector
         */
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

        }

        /**
         * 获取Spout发送的数据
         * @param input
         */
        public void execute(Tuple input) {

            //建议根据filed名称获取值
            Integer value = input.getIntegerByField("num");
            sum += value;

            System.out.println("Bolt: sum = [" + sum + "]");
        }

        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }
    }

    public static void main(String[] args) {

        //TopologyBuilder根据Spout和Bolt来构建出Topology
        //Storm中任何一个作业都是通过Topology的方式来进行提交
        //Topology中需要指定Spout和Bolt的执行顺序
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("DataSourceSpout", new DataSourceSpout());
        builder.setBolt("SumBolt", new SumBolt()).shuffleGrouping("DataSourceSpout");

        //创建一个本地Storm集群：本地模式运行，不需要搭建Storm集群
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("LocalSumStormTopology", new Config(),
                               builder.createTopology());
    }

}
