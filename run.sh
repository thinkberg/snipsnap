#! /bin/sh
if [ "$1" = "-debug" ]; then
  DBG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5000"
  shift
fi
if [ "$JAVA_HOME" != "" ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
fi
if [ "$1" = "-admin" ]; then
  $JAVA -jar lib/snipsna-utils.jar $2 $3 $4 $5 $6 $7 $8
fi
$JAVA -Xmx128m -server $DBG -jar lib/snipsnap.jar $*
