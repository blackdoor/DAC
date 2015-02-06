#!/bin/bash
#echo "use: collectlogs.sh  <NAME FILE> <ANNEX NAME FILE>
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
towernames=$1
annexnames=$2

while read -r line
do
	name=$line
	echo "Logs grabbed from: $name"
	mkdir -p ../logs/$name
	scp -r cube@$name:~/run.dh256/log/* ../logs/$name/ &

done < "$towernames"

i=1
while read -r line
do
	name=$line
	echo "Logs grabbed from: $name"
	mkdir -p ../logs/annex$i
	scp -r $name:~/run.dh256/log/* ../logs/annex$i/ &
	i=$((i+1))
done < "$annexnames"