#!/bin/bash
CDR=$(pwd)
BASEDIR=$(dirname $0)

if [ $BASEDIR = '.' ]
then
BASEDIR="$CDR"
fi
echo "export PATH='$PATH:$BASEDIR'" >> ~/.bashrc
echo "export PATH='$PATH:$BASEDIR'" >> ~/.profile
source ~/.profile
source ~/.bashrc
chmod -R 755 $BASEDIR
