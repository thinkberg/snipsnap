#! /bin/sh
if [ "$1" = "-debug" ]; then
  DBG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5000"
  shift
fi
if [ "$1" = "-admin" ]; then
  DBG="-Dlauncher.errlog=admin.log"
fi
java -server $DBG -jar lib/snipsnap.jar $*

