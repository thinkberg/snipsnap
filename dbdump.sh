#!/bin/sh
java -cp lib/mckoidb.jar:cls/default/WEB-INF/classes/ org.snipsnap.util.DBDump $1 $2
