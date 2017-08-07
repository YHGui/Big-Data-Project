# Project about big data 
### Hadoop
### Spark
### zookeeper
### kafka
借鉴网上一张图表示一个big data pipeline，在远景智能实习期间做的与数据相关的项目中，平台团队开发的EnOS能源物联网平台在获取的时候是通过Kafka和Spark Streaming将各种能源相关设备（目前包括风机、电厂、智能硬件等灯硬件设备）按照规约接入之后的数据进行采集，而EnOS平台做的事提供了MapReduce算子平台，Spark平台，实时监控平台，对能源进行管理。因此在大部分的IoT都是按照这种方式接入的，EnOS还用了在后续还结合使用了Flume工具，在学习使用Spark Streaming和Kafka进行采集数据的时候，之前准备在网上找实时的金融事件序列数据，后来直接想按照相应的格式来进行simulation，后续基于采集的数据结合Spring Boot框架做相应的后台工作以及前端展示。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/iot-architecture.png)

