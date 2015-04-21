#!/bin/bash
exec >/home/cube/run.dh256/kill.out 2>&1

#echo use: "stopnodes.sh <NUM_NODES> <STARTING_PORT>"

NUM_NODES=$1
STARTING_PORT=$2
echo $NUM_NODES nodes
echo starting at port $STARTING_PORT

i=0
while [ $i -lt $NUM_NODES ]; do
	port=$(($STARTING_PORT+$i))
	dh256exe=(bin/dh256.jar shutdown -p $port)
	nohup java -jar ${dh256exe[*]} </dev/null &> log/shutdown.log 2>&1 & disown
	i=$((i+1))
done
