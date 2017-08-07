# Project about big data 
### Hadoop
在这里面我将总结自己学习Hadoop ecosystem相关的一些知识，包括Google的“三架马车”的初略阅读，以及总结大众点评CANAAN平台和原生MapReduce的优缺点。
整个搜索引擎分为三层，文件层（file，GFS），data model（BigTable），计算层（algorithm，MapReduce），缺少了底层的操作系统层。但是MapReduce会访问底层文件（GFS），为什么会操作访问底层文件呢？是为了提高性能。
首先是GFS，是为了解决一个问题：如何保存一个文件？->如何保存一个大文件？
- 改变一个block（1024Byte）的大小，改为一个chunk（64MB）的大小，当然这里存储小文件的话会浪费空间
- 如果文件是超大文件呢？一个master server加上许多个chunk server，这时候master server和chunk server之间的沟通成本太大，因此master只能存chunk在哪个chunk server，而不存在chunk server的offset，那么就需要在chunk server存每个chunk的offset，这也是符合内聚合，外解耦。
- 我们可以看出这个过程却是不是很难，需要抓住重点。
- 随着chunk的增多，必然会出现chunk的在硬盘上的broken，因此如何验证chunk是否broken呢？可以使用checksum来检验，读硬盘的时候可以对比它的checksum来验证是否broken。
- 随着server的增多，如何避免chunkserver宕机后出现数据丢失呢？因此需要做热备，大多数系统总共是三备份，为了追求性能系统一般就是二备份，往往会将其中的两个放到一个数据中心，另外放到另外的数据中心，这是为了防止一个数据中心出事故所有的数据都丢失。
- 如果一个备份宕机了，需要向master通知，并从master获取其他备份的数据，遵守就近原则，开启修复进程，进行数据恢复。
- 如何确定server是否挂掉，server通过发送heartbeat来确定其是否存活，如果master挂掉，那么master的另一个影子备份会顶上，master和备份之间会同步log，往往在设计过程中，需要将master server的功能尽可能减小。
- 如何避免热点问题？
- 开启一个进程来记录访问频率，如果发现经常被访问，就将其复制到多个服务器上。
- 读文件的过程：client向master请求查找read某个文件，master会去目录结构查找，并找到对应的服务器，然后带上chunk handle去那个服务器read文件，读出相关文件，当然会根据多个文件位置就近寻找。
- 写文件过程：先向master发出写请求，并确定三个中的的primary（选primary的作用是是为了写的顺序正确，统一），然后client会找最近的服务器开始往里面写，随后pipeline传输，达到速度最快，传完后会往回传，表示传输成功，但是这个时候只存于内存中，并没有写入硬盘中，需要约定好之后按照primary的定的统一顺序来进行读写。
第二个是BigTable，解决支持范围查询，就是通过对key进行排序，key是table最重要的概念。如果要保存一个大表，则需要将一个大表存成许多小表，并有一个metadata，存有各个小表的地址等信息。如果需要存一个超大表，将小表再拆成小小表。
- 如何往表里写数据？如果往表里写了一个数据，会往内存表里写一个数据，缓存住，如果内存表过大，就需要往硬盘上写。为了防止断电导致缓存丢失，则会每一次读写都写入log，log直接append在后面，因此速度也比较快，因此一个小表由缓存表和一系列小小表以及log组成。小小表是由对应了GFS的一个chunk的大小。
- 如何读数据？由于小小表仅在内部有序，而在外部无序，因此为了读取数据的时候需要查找所有的小小表和内存表才能解决，为了加速数据读取，则需要加入index，那么就可以定位了，然后遍历小小表。为了读取速度更快，需要加入bloomfilter加速检索过程。bloomfilter本质就是多个hash，降低单个hash误判的可能性。
- Bloom
filter是一个数据结构，用来判断某个元素是否在集合内，具有运行快，内存占用小的特点。而高效插入和查询的代价就是，Bloom filter是一个基于概率的数据结构，只能告诉我们一个元素绝对不在集合内或可能在集合内，它的时间复杂度为O(k)
- 逻辑层结构 -> 物理层结构：把非数据层的column综合作为key，数据层的值作为value，逻辑表就变成了物理表。
第三个是MapReduce，MapReduce能够代表所有计算的原因是因为其核心是分治法，先拆解再组合，大部分问题都可以归结为这种计算范式。
### Spark
### zookeeper
### kafka
借鉴网上一张图表示一个big data pipeline，在远景智能实习期间做的与数据相关的项目中，平台团队开发的EnOS能源物联网平台在获取的时候是通过Kafka和Spark Streaming将各种能源相关设备（目前包括风机、电厂、智能硬件等灯硬件设备）按照规约接入之后的数据进行采集，而EnOS平台做的事提供了MapReduce算子平台，Spark平台，实时监控平台，对能源进行管理。因此在大部分的IoT都是按照这种方式接入的，EnOS还用了在后续还结合使用了Flume工具，在学习使用Spark Streaming和Kafka进行采集数据的时候，之前准备在网上找实时的金融时间序列数据，后来直接想按照相应的格式来进行simulation，后续基于采集的数据结合Spring Boot框架做相应的后台工作以及前端展示。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/iot-architecture.png)

