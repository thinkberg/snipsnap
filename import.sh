#! /bin/sh
base=`dirname $0`
jar=lib
if [ "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  echo "A Java SDK of at least version 1.3 is required!"
  exit
fi

if [ ! -f $base/$jar/snipsnap.jar ]; then
  echo "$jar/snipsnap.jar missing, please compile application first"
  exit
fi

# put classpath together
CLASSPATH=lib/xmlrpc-1.1.jar:lib/jakarta.jar:lib/javax.servlet.jar:lib/mckoidb.jar:lib/org.apache.jasper.jar:lib/org.mortbay.jetty.jar:lib/jdbcpool.jar:lib/lucene-1.2.jar:$JAVA_HOME/lib/tools.jar

$JAVA_HOME/bin/java -cp $CLASSPATH:lib/snipsnap.jar org.snipsnap.util.DBImport "$1" "$2" "$3"
