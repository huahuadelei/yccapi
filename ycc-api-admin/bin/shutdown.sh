#!/bin/bash

source /etc/profile

pids=$(jps -m|grep "ycc-api:ycc-api-admin"|cut -d " " -f 1)

if [ ${#pids[@]} -eq 0 ]
then
 echo "没有需要关闭的程序"
 exit 1
fi

echo "检索到已启动的程序数量为:${#pids[@]},准备 kill !!"

for pid in $pids
do

kill -9 $pid
echo "killed pid $pid"

done

echo "操作完成,程序已关闭"
