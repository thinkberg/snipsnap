#! /bin/sh
base=`dirname $0`
jar=lib
if [ "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  echo "A Java SDK of at least version 1.4 is required!"
  exit
fi

info=`$JAVA_HOME/bin/java -version 2>&1`
version=${info:14:3}
if [ ! "$version" = "1.4" ]; then
  echo "Found Java version $version, but require at least 1.4"
  exit
fi

if [ ! -f $JAVA_HOME/lib/tools.jar ]; then
  echo "$JAVA_HOME/lib/tools.jar not found, cannot compile jsp files"
  echo "Make sure tools.jar from the Java SDK is in the classpath!"
  exit 
fi

if [ ! -f $base/$jar/SnipSnap.jar ]; then
  echo "$jar/SnipSnap.jar missing, please compile application first"
  exit
fi

if [ ! -x $base/db/data ]; then
  echo "No database found, creating one ..."
  $JAVA_HOME/bin/java -cp lib/SnipSnap.jar com.neotis.config.CreateDB
fi

# put classpath together
CLASSPATH=lib/jakarta.jar:lib/javax.servlet.jar:lib/mckoidb.jar:lib/org.apache.jasper.jar:lib/org.mortbay.jetty.jar:$JAVA_HOME/lib/tools.jar

# execute application server
$JAVA_HOME/bin/java -cp $CLASSPATH:lib/SnipSnap.jar com.neotis.net.AppServer 2> server.log
