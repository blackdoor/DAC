#!/bin/bash
CDR=$(pwd)
BASEDIR=$(dirname $0)

if [ $BASEDIR = '.' ]
then
BASEDIR="$CDR"
fi
echo "export PATH='$PATH:$BASEDIR'" >> ~/.bash_profile
source ~/.bash_profile
chmod -R 755 $BASEDIR
