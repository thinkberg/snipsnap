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
  shift
  echo executing: $JAVA -jar lib/snipsnap-utils.jar $*
  $JAVA -jar lib/snipsnap-utils.jar $*
  exit
fi
case "`uname`" in
  IRIX*)
    JAVA_OPTS="$JAVA_OPTS"
    ;;
  *)
    JAVA_OPTS="$JAVA_OPTS -server"
    ;;
esac

JAVA_OPTS="$JAVA_OPTS -DentityExpansionLimit=1000000 -Djava.awt.headless=true -Xmx512m"
# uncomment if you have a lot of memory and want optimizations
#JAVA_OPTS="$JAVA_OPTS -Xsqnopause -XX:+UseLWPSynchronization -Xms512m -Xmx1024m -Xss256k -XX:MaxNewSize=96m -XX:MaxPermSize=512m"

$JAVA $JAVA_OPTS $DBG -jar lib/snipsnap.jar $*
