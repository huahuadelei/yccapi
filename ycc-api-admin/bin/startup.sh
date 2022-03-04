#!/bin/bash

source /etc/profile

port_index=-1
index=1


for item in $@
do
 
  case $item in
   "-port") port_index=`expr $index + 1`;;
  esac
 
 index=$(($index + 1)) 
done


index=1
for item in $@
do
  
  case $index in
    $port_index) set_port=$item;;
  esac 

 index=$(($index + 1)) 
done


# 变量
project_home=$(cd $(dirname $0)/.. && pwd)
java_script=$JAVA_HOME/bin/java
JAR_NAME="$project_home/target/ycc-api-admin.jar"
CONFIG_HOME="classpath:/,classpath:/config/,file:./,file:./config/,file:$project_home/config/"

# 判断jdk是否配置
if [ ! -e $java_script -o ! -x $java_script ];then
 echo "环境变量中没有找到Jdk8的运行命令" 
 exit 1 
fi


# jvm环境参数
VM_OPTIONS="-Xms512m -Xmx700m"
VM_OPTIONS="${VM_OPTIONS} -XX:+UseG1GC"
VM_OPTIONS="${VM_OPTIONS} -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+PrintGCApplicationStoppedTime  -Xloggc:$project_home/logs/gc.log"


# 项目环境参数
JAVA_OPTIONS="-Dproject.home=$project_home"
JAVA_OPTIONS="$JAVA_OPTIONS -Dfile.encoding=UTF-8"
JAVA_OPTIONS="$JAVA_OPTIONS -jar $JAR_NAME"
JAVA_OPTIONS="$JAVA_OPTIONS --spring.config.location=$CONFIG_HOME" 

if [ $set_port ];then
 JAVA_OPTIONS="$JAVA_OPTIONS --server.port=$set_port"
fi


JAVA_CMD="$java_script $VM_OPTIONS $JAVA_OPTIONS"

nohup $JAVA_CMD ycc-api:ycc-api-admin > $project_home/bin/console.out &

