#! /bin/sh
if [ "$1" = "-debug" ]; then
  DBG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5000"
  shift
fi
if [ "$1" = "-admin" ]; then
  DBG="-Dlauncher.errlog=admin.log"
fi
if [ "$JAVA_HOME" != "" ]; then
  JAVA=$JAVA_HOME/bin/java
else
  JAVA=java
fi
$JAVA -Xmx128m -server $DBG -jar lib/snipsnap.jar $*
