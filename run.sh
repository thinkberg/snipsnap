#! /bin/sh
echo "APPNAME ..."
echo "(c) 2002 Stephan Schmidt and Matthias L. Jugel. All Rights Reserved."
echo "See License Agreement for terms and conditions of use."
echo ""

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

if [ ! -f $base/$jar/neotis.jar ]; then
  echo "$jar/neotis.jar missing, please compile application first"
  exit
fi

if [ ! -x $base/db/data ]; then
  echo "No database found, creating one ..."
  $JAVA_HOME/bin/java -cp lib/neotis.jar com.neotis.config.CreateDB
fi

# put classpath together
CLASSPATH=lib/jakarta.jar:lib/javax.servlet.jar:lib/mckoidb.jar:lib/org.apache.jasper.jar:lib/org.mortbay.jetty.jar:$JAVA_HOME/lib/tools.jar

# execute application server
$JAVA_HOME/bin/java -cp $CLASSPATH:lib/neotis.jar com.neotis.net.AppServer
