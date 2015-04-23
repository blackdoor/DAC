#!/bin/bash
#echo "use: collectlogs.sh <Storage name> <NAME FILE> <ANNEX NAME FILE>
DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )
storagefolder=$1
towernames=$2
annexnames=$3

while read -r line
do
	name=$line
	echo "Files grabbed from: $name"
	mkdir -p ../stored/$name
	scp -r cube@$name:~/run.dh256/$storagefolder/* ../stored/$name/ > ../stored/$name/grab.out &

done < "$towernames"

# i=1
# while read -r line
# do
# 	name=$line
# 	echo "Files grabbed from: annex$i"
# 	mkdir -p ../stored/annex$i
# 	scp -r $name:~/run.dh256/$storagefolder/* ../stored/annex$i/ > ../stored/annex$i/grab.out &
# 	i=$((i+1))
# done < "$annexnames"