#! /bin/sh
base=`dirname $0`
jar=lib
if [ "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  exit
fi

if [ ! -f $base/$jar/neotis.jar ]; then
  echo "$jar/neotis.jar missing, please compile application first."
  exit
fi

# execute application server
$JAVA_HOME/bin/java -jar lib/neotis.jar
