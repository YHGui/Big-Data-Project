# Recommender System
- A project about movie recommender system based on Item CF algorithm with the aid of Hadoop ecosystem.
There are five map reduce job in this mini project.
- It works on hadoop cluster built in Docker.
- Data about movie rating from Netflix after cleaning.
## Algorithms used for recommender system
- 基于物品的协同过滤算法(Item Collaborative Filtering)
根据物品的相似性进行推荐：简单来说，就是基于大量用户对不同电影的评分来进行得到电影的相似性（比如一千个用户对所有的电影进行评分，如果有90%以上的用户对某几部电影有较高的评分，那么可以认为这几部电影具有相似性），再根据某个用户的看过的电影实际情况来得到推荐电影，因为在这里我使用基于物品的协同过滤算法，理由如下：
1. 电影推荐系统中，用户远远多于电影数量，如果使用基于用户的协同过滤算法，同一数据集会导致模型复杂，计算过程就更复杂了；
因此当product的数量远远高于user的时候，比如新闻应用，才使用基于用户的协同过滤算法。
2. 物体本身变化不大，电影评分也不会变化太快。
3. 基于用户自身历史数据来推荐也更具有说服力，因为如果基于用户相似性的话就是说根据某个用户对电影的喜好，来得到另一个相似的用户。
- 基于用户的协同过滤算法(User Collaborative Filtering)
根据用户的相似性进行推荐：简单讲，如果用户A,B,C对多部电影观看过后打了分，如果两个用户对几部电影打分的pattern相似，我们可以
说这两个用户具有相似性，那么可以根据一个用户的打分来决定是否推荐给另一个相似的用户。
## 实现过程
计算co-occurrence矩阵 -> 计算评分矩阵 -> 矩阵相乘得到推荐结果
1. 描述物品之间的关系／相似性
根据用户的评分记录来建立物品之间的关系，用户认真看了，并打了分，当然不排除水军，即使给了低分，但是用户是真正看了，然后觉得不满意，
最后打了低分，但却能说明用户对这个电影是有兴趣的。
co-occurrence矩阵，表示同时看了两部电影的人数，对两部电影来说，仅仅是说明用户对这两部电影有兴趣，但不能说明喜恶，因此需要区分出用户
是否喜欢电影。
2. rating matrix（表示当前用户对所有电影的的打分）建立，通过用户对各个电影的打分建立rating matrix。
其中做了的优化是如果用户没有看过此电影，那么需要给一个平均分，不能给0，否则会影响结果，因为不同的用户打分习惯不一样，有人
习惯性给高分，也有人习惯性给低分。
3. normalization（归一化）
因为co-occurrence矩阵不能通过绝对值来对比，需要考虑基数的影响，归一化就是将基数变为1，消除基数的影响，从而得到两个电影之间的相关性。 
4. co-occurrence矩阵和rating矩阵相乘得到推荐结果
相乘的意义：用户的喜好（分数）* 电影之间相关性 
不能将稀疏矩阵数据直接存入HDFS，浪费空间，而且不方便更改，存入的数据格式是：用户Id，电影Id，评分。
5. 如何测试？预期的结果和真实的数据相比
## install Docker
- visit https://docs.docker.com/docker-for-mac/ for docker for Mac
- PS: because of gfw, speed up with the mirror of Aliyun(https://cr.console.aliyun.com/#/accelerator) 
advanced → registry mirrors.(Daocloud:https://www.daocloud.io/mirror)
## build hadoop cluster
- pull hadoop cluster from dockerhub
- sudo ./start-container.sh
- sudo ./start-hadoop.sh
## Mapper-Reducer
five map reduce job in code
1. 用户看过多少电影，以及评分，map：用户区分，以用户Id为key，reduce：merge阶段，统计用户看过多少电影。input user,movie,rating ==> key = user value=movie1:rating, movie2:rating...
2. 建立co-occurrence矩阵，同时看过一部电影的人数。
3. 归一化处理，不能根据绝对值来进行计算
4. co-occurrence矩阵和rating矩阵相乘
这里的优化在于不能将两个矩阵存入内存中然后相乘，一来太慢，而且可能会出现oom的后果，可以理解矩阵相乘和线性代数的另一种形式进行计算，每次读入矩阵的一个
单元，或者说是列向量，然后相乘，就是对应列向量的线性组合，
5. 各个单元的积求和得到结果。
## run
hadoop com.sun.tools.javac.Main *.java

jar cf recommender.jar *.class

hadoop jar recommender.jar Driver /input /dataDividedByUser /coOccurrenceMatrix /Normalize /Multiplication /Sum

PS:output directory is not allowed to exist. 