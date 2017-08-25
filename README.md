# Project about big data 
<!-- ### redis学习
- learn by doing，learn by teaching.之前和同学交流的时候，突然发现自己向别人讲授redis的时候竟然完全不知道如何讲，因此决定做一些总结。
- 什么是redis？redis是一个内存数据结构存储，可以用于数据库，缓存，也可以作为消息传输中介。redis本质是内存中的存储，redis极为火爆。数据结构存储：redis的value可以是Strings，Hashes，Lists，Set，Sorted set。redis可以在数据结构内部进行操作，比如在对list的value进行append，避免了进行序列化和反序列化的操作。redis使用了更好用的结构，打通了数据库和实际程序的屏障（序列化）。
- redis 使用情景：1. timeline，比如微信朋友圈，userID作为key对应一个list，作为缓存，redis支持很多复杂的数据结构，我们可以知道在database支持多种数据结构是很少见的。
- redis实现：单线程的event loop，事件驱动。一个redis机器包含listening port，同时有内部周期性的事件，比如过期删除和定期persist，event loop则用来协调他们的工作。event loop：硬件，epoll注册事件，比如将一个端口注册到epoll，监听事件，查询的时候就知道已经连接好了。
- 数据如何存储在redis中？内部不同情况用不同的数据结构存储，即使对外还是简单的一种数据结构。 -->
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
- 什么是Spark？Spark已经成为了具有调度、具有管理能力、监控功能的通用分布式计算引擎。相对于传统的MapReduce，Spark砍掉了将中间的数据存回HDFS的资源消耗，并且通过记录计算过程的方式又砍掉了复制多份数据的消耗，因此取得了10-100倍的提速（传输数据速度如下，内存：10GB/s，硬盘：100MB/s，SSD：600MB/s，同机架网络传输：125MB/s，跨机架网络传输：12.5MB/s）。
运算处理时间为：（读取数据时间+处理时间+任务调度时间）再除以并行度。由于记录了运算的过程，因此不需要进行备份，只要在数据丢失了的时候就去重新计算一遍即可。
- 什么是RDD？Resilient Distributed Dataset,弹性的分布式数据集，RDD是lazy evaluation，说白了RDD是Spark操作的数据集的逻辑视图，而这个数据集在物理上会分布在各个机器上，我们甚至可以把RDD简单理解为一个分布式的list，它本身是不可修改的，immutable的数据可知，状态不会改变，可预知，immutable数据不可变，那么就可以将数据分离拆解，各干各的。不同的数据来源是不同的RDD。一方面Spark能够处理更大的数据，同时Spark也能够并发处理这个数据集，提高速度。执行过程，每一个会单独产生task，task执行逻辑是一样的。
- 如何创建RDD？
  - 生成变量一样，int a = 5；
  - 从外部读数据，RDD就是Spark中的变量。 
- RDD进过过滤之后，可以进行重新分区，这是需要通过网络进行混洗的，然后创建出新的分区，当然代价比较大，优化的版本是coalesce()，能够确保合并到比现在分区数更少的分区中，但是这个过程Spark是不进行运行计算的，了解了全局就能进行各种优化，只有在用户需要获取这些数据的时候，才会开始执行，在每个stage中是可以并行流水化的，stage之间就是传输数据，每个stage要在一起进行优化。
- RDD基本特征：
  - A list of partitions
  - A function for computing each split
  - A list of dependencies on other RDDs（窄依赖，被一个子分区使用；宽依赖，被多个子分区使用）
  - Optionally, a Partitioner for key-value RDDs (e.g. to say that the RDD is hash-partitioned)，基于hash来进行partition
  - Optionally, a list of preferred locations to compute each split on (e.g. block locations for an HDFS file)，倾向于数据在哪就在那进行计算
  - 比如从Hadoop系读取的HadoopRDD来说，每个hdfs的block可以当作一个partition，没有其他依赖，直接从hdfs对于的block读取数据，优先也在该机器上计算。
  - filteredRDD，和父母的partition一致，依赖就是和父母一一对应，将数据和父母在同一个位置计算，这样仍然是处于本地。
  - joinRDD则不一样，它的数据来源于每一个父亲，每个reduce的task的数据拿过来进行运算，因此较为优化的计算地址就没有了，可以通过hash的方法来进行partition。
  - spark中运算前能过滤好，尽量先过滤。
- lifecycle of a Spark program：1. Create some input RDDs from external data or parallelize a collection in your driver program. 2. Lazily transform them to define new RDDs using transformations like filter() or map(). 3. Ask Spark to cache() any intermediate RDDs that will need to be reused. 4. Launch actions such as count() and collect() to kick off a parallel computation, which is then optimized and executed by Spark.
- Spark的local运行模式：
  - 启动一个JVM，包括Driver和Executor进程，启动spark的local模式，同时有多个任务在执行，local模式下，根据内部的线程来执行任务。
- standalone模式
  - 自己管理自己的多台机器，首先如果有台机器，会有一个master来管理，每台机器上会有一个worker，worker向master注；册沟通，使得master能调度里面的资源，每台机器包含RDD的不同分片，也有可能有复本的情况，	然后每台机器的执行器会有多个运行的任务task，运行后将结果返回给driver，通过会痛自身的块管理器为用户程序中要求缓存的RDD提供内存式存储。
  - 为了实现HA高可用性，可以在woker上面加载多个spark master，他们之间通过zookeeper来管理。
  - 一个worker也能对应多个executor，一般一个worker给一个driver只会开一个executor，因此要在一个worker给同样的driver开多个executor，就可以在一台机器上开多个worker，这些都是Spark的配置。Spark默认都是FIFO的模式提交driver，可以配置spark的core数量以及executor执行器的内存配置。
- 运行Spark应用的详细过程：
  - 用户通过Spark-submit脚本提交应用。
  - spark-submit脚本启动驱动器程序，调用用户自定义的main()方法。
  - 驱动器程序与集群管理器通信，申请资源以启动执行器节点。
  - 驱动器进程执行用户应用中的操作，根据程序中所定义的RDD的转化操作和行动操作，驱动器节点把工作以任务的形式发送执行器进程。
  - 任务在执行器程序中进行计算保存结果
  - 如果驱动器程序的main方法退出，或者调用了SparkContext.stop(),驱动器程序会终止执行器进程，并且通过集群管理器释放资源。
- YARN(Yet Another Resource Negotiator)：简而言之，资源管理器，可以让多种数据框架运行在同一个共享的资源池中。Resource Manager（master，管理）和Node Manager（slave，运行任务），（slave）Node会向Master发送心跳和自己所拥有的资源信息。client向Resource Manager提交应用申请资源，得到app master的资源，紧接着resource manager会找一个node manager给它使用，创建app master，然后app master就相当于一个用户代理，然后再向resource manager申请资源（container），resource manager就会查询node manager状态，并提供相关凭证给app master去找node manager创建container去运行，client只需要和resource manager进行交互，而且连接时间短，协调少，然后就会由一个app master作为client的代理进行交互，解绑了client。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/YARN-Spark.png)
当然在多个client的情况下，在resource manager中会有一个apps master管理所有的client对应的app master，如果有app master挂掉，则会重启，解决了HA的问题，如果resource manager挂了，需要加入zookeeper实现HA。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/YARN-Spark-2.png)
Spark在YARN运行有两种运行方法，一种是client模式，在client里面有driver，运行过程中client需要一直存活，如果断网或者关机就结束了，这是交互运行的情形，client还会和申请到的container进行交互。另一种模式是cluster模式，driver在app master里面协调运行，提交后就可以做其他事情，不能交互式运行，直到运行结束。
yarn可以配置executors和executor的内存和executor的cpu core数，源码是ExecutorAllocationManager.scala中，可以看出YARN会借助计时器动态调整executor的数目，少则不断添加，直到资源上限，借助计时器也能检查executor的工作状态，空闲则减少executor数目，到最少的资源。
- 数据如何存？一般来说，75%的内存用于Spark，其余给操作系统使用，每个executor的heap最小为8G，最大就依赖于GC策略，可能40G左右，和存储方式以及序列化的方式有关。存储数据有两种方式：内存以及内存+硬盘，内存足则放内存，否则将object序列化之后存在硬盘上，用时间来换空间。为了防止数据丢失带来影响，可以为数据制备复本，内存硬盘ssd可以产生多种组合，得到一个折衷的组合。在Spark中接入了Tachyon，它是一个缓存系统，它是独立的存储系统，如果一个Spark节点挂了，在Tachyon中有缓存的RDD数据，Tachyon是序列化的数据，那么可以跨语言跨平台支持的，支持统一访问，Tachyon已经改名为Alluxio，Tachyon专职序列化，所以效率也比较高。Spark存数据的原则是尽量放在内存中，内存不足将数据序列化后存放，尽量不在硬盘中读写，一般情况下不要做备份，除非非常极端的情况下。Executor中JVM的默认的内存分配是6:2:2，分别是RDD，shuffle的内存（中间数据）和用户程序等内存，因此在调节的时候一般调后面两个，比如运行到shuffle就挂掉，可能就是shuffle内存不足。
- 数据何时会进行序列化：
  - 网络之间传递数据
  - 往硬盘写数据的时候
  - 为了减少空间在内存中进行序列化的时候
  - 广播变量
  - Java传统序列化通用性高，但效率一般，为了提高序列化的效率可以使用KRYO来代替Java的序列化。
- 优化（tuning）
  - gc策略未设置好，会出现数据缓存后又丢掉，然后得继续缓存
- Spark如何执行任务？
  - 调度过程，RDD建立过程
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/schedule-process.png)
  - task scheduler往右仅了解task。
  - executor是task线程，block manager管理具体数据。
  - 由于宽窄依赖不同，最后会导致不同的stage。
- 概念：
  - 广播变量(broadcast variables): 需要发给所有executor的变量，比如机器学习的feature vector，这很低效，因此可以传给所有的机器上cahe住，然后就可以本地读取了，但是如果机器特别多，需要广播的特别多，这时就引入bittorent（p2p）
  - 累加器：将工作中节点的值聚合到驱动器程序中的简单语法，只有驱动器能够读取到累加器的值，工作节点的executor是访问不到的。累加器在运行spark时，会出现错误，通过累加器可以计算错误次数，帮助debug，可以设置。
- 排序100TB的数据
  - 为什么sorting？排序有很多核心的算法，比如shuffle，也是各种machine learning的基础，能挑战系统。
  - zero copy：之前读取需要经历linux内核，java程序中，然后到网卡，继而传输，zero copy可以直接将数据发送到网卡。
  - 想将数据排序切分，然后合理的分给reducer
- 如何处理实时数据
  - DStream：
  - 输入源源不断的数据源通过map reduce mllib等Spark相关操作，然后存储到hdfs等相关存储介质。
  - 使用案例：用户访问数据存储到kafka的消息队列中，然后spark每个一段时间拿数据。
  - 先将数据变为RDD，然后进行后续的各种操作。
  - 使用“微批次”架构，把流式计算当作一系列连续的小规模批处理来对待，分别在微小间隔的时候采集数据然后转化为一个RDD，然后进行处理，需要注意的是处理RDD的时间不应该过长，导致RDD堆积。
  - 如果有多个流，则需要将得到的RDD给union或者join起来。
  - 时间窗口：每隔两秒统计三秒的用户量，不断的滑动，每次滑动出来的结果形成一个RDD就可以了，只是数据收集的区别。
- Spark 2.0（Unified engine across data workloads and platforms）
  - RDD是更为底层的数据结构
  - Tungsten Phase 2 speedups of 5-20x
  - Structured Streaming 
  - SQL 2003 & Unifying Datasets and DataFrames
  - Datases&DataFrame：结构化数据。Dataframe = Dataset[Row]，Dataset强制类型检查。
  - SparkSession is the SparkContext for Dataset/DataFrame，不仅能处理数据，还包含元数据。
  - 流处理：实时计算（批量计算，交互式查询）出问题：延迟，故障恢复，分布式读写。将底层流看成无限大的数据库，不需要管底层，Spark越来越智能。
  - Spark已经成为一个分布式编译器
### zookeeper
### kafka
借鉴网上一张图表示一个big data pipeline，在远景智能实习期间做的与数据相关的项目中，平台团队开发的EnOS能源物联网平台在获取的时候是通过Kafka和Spark Streaming将各种能源相关设备（目前包括风机、电厂、智能硬件等灯硬件设备）按照规约接入之后的数据进行采集，而EnOS平台做的事提供了MapReduce算子平台，Spark平台，实时监控平台，对能源进行管理。因此在大部分的IoT都是按照这种方式接入的，EnOS还用了在后续还结合使用了Flume工具，在学习使用Spark Streaming和Kafka进行采集数据的时候，个人想法是通过google finance或者yahoo finance获取股票的实时数据，并且都有相应的Python module，十分方便，因此pip install之后，测试了下发现google finance直接不能用，猜想是wall的原因，平时的代理也仅仅是在浏览器用一下google，后来转战yahoo finance，可用，后来却发现不能再用了，似乎已经不支持了，网上还有人声称yahoo is dead，最后向国内肯定有相关的module，找到chinesestockapi，分别写了producer和consumer，并存储在Cassandra中，期间也直接想按照相应的格式来进行simulation，后续基于采集的数据结合Spring Boot框架做相应的后台工作以及前端展示。
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/master/images/iot-architecture.png)
了解的公司内部的技术栈为：
![Alt text](https://github.com/YHGui/Big-Data-Project/blob/8181867acefe5e46e965344b31941e2afb667701/images/architecture.jpeg)

