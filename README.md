# Project about big data 
### Hadoop
在这里面我将总结自己学习Hadoop ecosystem相关的一些知识，包括Google的“三架马车”的初略阅读。
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
- 最近在看Sameer Farooqui的Advanced Apache Spark Training视频，pdf链接为：https://spark-summit.org/wp-content/uploads/2015/03/SparkSummitEast2015-AdvDevOps-StudentSlides.pdf
- 什么是Spark？Spark已经成为了具有调度和管理能力的通用分布式计算引擎。相对于传统的MapReduce，Spark砍掉了将中间的数据存回HDFS的消耗，并且通过记录计算过程的方式又砍掉了复制多份数据的消耗，因此取得了10-100倍的提速（传输数据速度如下，内存：10GB/s，硬盘：100MB/s，SSD：600MB/s，同机架网络传输：125MB/s，跨机架网络传输：12.5MB/s）。
- 什么是RDD？Resilient Distributed Dataset,弹性的分布式数据集，RDD是lazy evaluation，说白了RDD是Spark操作的数据集的逻辑视图，而这个数据集在物理上会分布在各个机器上，我们甚至可以把RDD简单理解为一个分布式的list，它本身是不可修改的，immutable的数据可知，状态不会改变，可预知，immutable数据不可变，那么就可以将数据分离拆解，各干各的。不同的数据来源是不同的RDD。一方面Spark能够处理更大的数据，同时Spark也能够并发处理这个数据集，提高速度。执行过程，每一个会单独产生task，task执行逻辑是一样的。
- lifecycle of a Spark program：1. Create some input RDDs from external data or parallelize a collection in your driver program. 2. Lazily transform them to define new RDDs using transformations like filter() or map(). 3. Ask Spark to cache() any intermediate RDDs that will need to be reused. 4. Launch actions such as count() and collect() to kick off a parallel computation, which is then optimized and executed by Spark.
- YARN(Yet Another Resource Negotiator)：简而言之，资源管理器。Resource Manager（master）和Node Manager（slave），Node会向Master发送心跳和自己所拥有的资源信息。client向Resource Manager提交应用申请资源，得到app master的资源，紧接着resource manager会找一个node给它使用，然后app master再向resource manager申请资源，resouce manager就会查询node状态，并提供相关凭证给app master去找node manager创建container
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/YARN-Spark.png)
当然在多个client的情况下，在resource manager中会有一个apps master管理所有的app master，如果有app master挂掉，则会重启，解决了HA的问题，如果resource manager挂了，需要加入zookeeper。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/YARN-Spark-2.png)
Spark在YARN运行有两种运行方法，一种是在client里面有driver，运行过程中client需要一直存活，这是交互运行的情形，另一种模式是cluster模式，driver在app master里面协调运行，提交后就可以做其他事情，直到运行结束。
yarn可以配置executors和executor的RAM和executor的core，YARN会借助计时器动态调整executor的数目，少则不断添加，同时计时器频率会变，直到上限，借助计时器也能检查executor的工作状态，空闲则减少executor数目。
- 数据如何存？一般来说，75%的内存用于Spark，其余给操作系统使用，每个executor的heap最小为8G，最大就依赖于GC，可能40G左右，和存储方式以及序列化的方式有关。存储数据有两种方式：内存以及内存+硬盘，内存足则放内存，否则将object序列化之后存在硬盘上。为了防止数据丢失带来影响，可以为数据制备复本，内存硬盘ssd可以产生多种组合，得到一个折衷的组合。在Spark中接入了Tychyon，它是一个缓存系统，它是独立的存储系统，如果一个Spark节点挂了，在Tychyon中有缓存的RDD数据，Tychyon是序列化的数据，那么可以跨语言跨平台支持的，支持统一访问，Tychyon已经改名为Alluxio。Spark存数据的原则是尽量放在内存中，内存不足将数据序列化后存放，尽量不在硬盘中读写，一般情况下不要做备份，除非非常极端的情况下。Executor中JVM的默认的内存分配是6:2:2，分别是RDD，shuffle的内存和用户程序等内存，因此在调节的时候一般调后面两个，比如运行到shuffle就挂掉，可能就是shuffle内存不足。为了提高序列化的效率可以使用KRYO来代替Java的序列化。
- Spark如何执行任务？
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/schedule-process.png)
概念：广播时引入bittorent（p2p）
### zookeeper
### kafka
借鉴网上一张图表示一个big data pipeline，在远景智能实习期间做的与数据相关的项目中，平台团队开发的EnOS能源物联网平台在获取的时候是通过Kafka和Spark Streaming将各种能源相关设备（目前包括风机、电厂、智能硬件等灯硬件设备）按照规约接入之后的数据进行采集，而EnOS平台做的事提供了MapReduce算子平台，Spark平台，实时监控平台，对能源进行管理。因此在大部分的IoT都是按照这种方式接入的，EnOS还用了在后续还结合使用了Flume工具，在学习使用Spark Streaming和Kafka进行采集数据的时候，个人想法是通过google finance或者yahoo finance获取股票的实时数据，并且都有相应的Python module，十分方便，因此pip install之后，测试了下发现google finance直接不能用，猜想是wall的原因，平时的代理也仅仅是在浏览器用一下google，后来转战yahoo finance，可用，后来却发现不能再用了，似乎已经不支持了，网上还有人声称yahoo is dead，最后向国内肯定有相关的module，找到chinesestockapi，分别写了producer和consumer，并存储在Cassandra中，期间也直接想按照相应的格式来进行simulation，后续基于采集的数据结合Spring Boot框架做相应的后台工作以及前端展示。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/iot-architecture.png)

