#! /bin/sh
base=`dirname $0`
jar=lib
if [ "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  exit
fi

if [ ! -f $JAVA_HOME/lib/tools.jar ]; then
  echo "Warning: missing $JAVA_HOME/lib/tools.jar, cannot compile jsp files"
  exit 
fi

if [ ! -f $base/$jar/neotis.jar ]; then
  echo "$jar/neotis.jar missing, please compile application first."
  exit
fi

echo "(c) 2002 Stephan Schmidt and Matthias L. Jugel"
echo "All Rights Reserved"
echo "See License Agreement for terms and conditions of use."

if [ ! -d $base/db/data ]; then
  echo "No database found, creating one ..."
  $JAVA_HOME/bin/java -cp lib/neotis.jar com.neotis.config.CreateDB
fi

# put classpath together
CLASSPATH=lib/jakarta.jar:lib/javax.servlet.jar:lib/mckoidb.jar:lib/org.apache.jasper.jar:lib/org.mortbay.jetty.jar:$JAVA_HOME/lib/tools.jar

# execute application server
$JAVA_HOME/bin/java -cp $CLASSPATH:lib/neotis.jar com.neotis.net.AppServer
