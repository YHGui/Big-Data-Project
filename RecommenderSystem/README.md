# Recommender System
- A project about movie recommender system based on Item CF algorithm with the aid of Hadoop ecosystem.
There are five map reduce job in this mini project.
- It works on hadoop cluster built in Docker.
- Data about movie rating from Netflix after cleaning.
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
## run
hadoop com.sun.tools.javac.Main *.java

jar cf recommender.jar *.class

hadoop jar recommender.jar Driver /input /dataDividedByUser /coOccurrenceMatrix /Normalize /Multiplication /Sum

PS:output directory is not allowed to exist. 