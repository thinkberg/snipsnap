#! /bin/sh
base=`dirname $0`
jar=lib
if [ "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  echo "A Java SDK of at least version 1.4 is required!"
  exit
fi

#info=`$JAVA_HOME/bin/java -version 2>&1`
#version=${info:14:3}
#if [ ! "$version" = "1.4" ]; then
#  echo "Found Java version $version, but require at least 1.4"
#  exit
#fi

if [ ! -f $JAVA_HOME/lib/tools.jar ]; then
  echo "$JAVA_HOME/lib/tools.jar not found, cannot compile jsp files"
  echo "Make sure tools.jar from the Java SDK is in the classpath!"
  exit 
fi

if [ ! -f $base/$jar/snipsnap.jar ]; then
  echo "$jar/snipsnap.jar missing, please compile application first"
  exit
fi

# put classpath together
CLASSPATH=lib/xmlrpc-1.1.jar:lib/jakarta.jar:lib/javax.servlet.jar:lib/mckoidb.jar:lib/org.apache.jasper.jar:lib/org.mortbay.jetty.jar:lib/jdbcpool.jar:lib/lucene-1.2.jar:$JAVA_HOME/lib/tools.jar

if [ "$1" = "stop" ]; then
  cmdline='-admin shutdown'
fi

if [ "$1" = "index" ]; then
  $JAVA_HOME/bin/java -cp app/WEB-INF/lib/servlets.jar:$CLASSPATH com.neotis.config.Indexer
  exit 0
fi

# execute application server
$JAVA_HOME/bin/java -cp $CLASSPATH:lib/snipsnap.jar com.neotis.server.AppServer $cmdline 2> server.log

