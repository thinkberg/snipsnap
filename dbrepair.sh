#!/bin/sh
if [  ! $# == 1 ]; then
  echo usage: $0 application
  exit
else
  if [ "$1" == "-all" ]; then
    APPS=`cd applications; ls -d *`
  else 
    APPS=$1
  fi
fi

for app in $APPS
do 
  echo Repairing $app
  java -cp lib/mckoidb.jar com.mckoi.tools.DBConglomerateRepairTool \
	-path applications/$app/WEB-INF/db/data
done
