#!/bin/sh
if [ ! $# == 1 ]; then
  echo usage: $0 application
  exit
fi
java -cp lib/mckoidb.jar com.mckoi.tools.DBConglomerateRepairTool -path applications/$1/db/data
