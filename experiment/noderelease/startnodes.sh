#!/bin/bash
#echo "use: startnodes.sh <NUM_NODES> <STARTING_PORT> <BOOTSTRAP> <ID> <STORAGE>""
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
NUM_NODES=$1
STARTING_PORT=$2
BOOTSTRAP=$3
ID=$4
STORAGE=$5
echo $NUM_NODES nodes
echo starting at port $STARTING_PORT
echo bootstrapping from $BOOTSTRAP
echo identifying this set of startups as $ID
underscore="_"

i=0
while [ $i -lt $NUM_NODES ]; do
	port=$(($STARTING_PORT+$i))
	logfile="$i$underscore$ID.log"
	mkdir -p $STORAGE/$port/
	dh256exe=(bin/dh256.jar join $BOOTSTRAP -p $port --log log/$logfile -d $STORAGE/$port/)
	nohup java -jar ${dh256exe[*]} </dev/null &> log/run.log 2>&1 & disown
	i=$((i+1))
done
