#! /bin/sh
base=`dirname $0`
jar=lib
if [ ! -f /System/Library/Frameworks/JavaVM.framework/Classes/classes.jar -a "$JAVA_HOME" = "" ]; then
  echo "Please set JAVA_HOME environment variable!"
  echo "A Java SDK of at least version 1.3 is required!"
  exit
fi

# check whether the java compiler is available
if [ ! -f $JAVA_HOME/lib/tools.jar -a ! -f /System/Library/Frameworks/JavaVM.framework/Classes/classes.jar ]; then
  echo "$JAVA_HOME/lib/tools.jar or MacOS X pendant not found, cannot compile jsp files"
  echo "Make sure tools.jar or similar from the Java SDK is in the classpath!"
  exit
else
  if [ -f /System/Library/Frameworks/JavaVM.framework/Classes/classes.jar ]; then
    TOOLS=/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar
    JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Home/
  else
    TOOLS=$JAVA_HOME/lib/tools.jar
  fi
fi

if [ ! -f $base/$jar/snipsnap.jar ]; then
  echo "$jar/snipsnap.jar missing, please compile application first"
  exit
fi

# put classpath together (this is a script-local variable)
CLASSPATH=lib/org.mortbay.jetty.jar:lib/javax.servlet.jar:lib/org.apache.crimson.jar:lib/org.apache.jasper.jar:lib/jdbcpool.jar:lib/mckoidb.jar:$TOOLS

if [ "$1" = "admin" ]; then
  $JAVA_HOME/bin/java -cp $CLASSPATH:lib/snipsnap.jar org.snipsnap.server.AppServer -admin "$2" "$3"
  exit
fi

if [ "$1" = "-debug" ]; then
  DBG="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5000"
fi

# execute application server
[ -f server.log ] && mv server.log server.log.old
$JAVA_HOME/bin/java -server -verbose:gc $DBG -cp $CLASSPATH:lib/snipsnap.jar org.snipsnap.server.AppServer $cmdline 2> server.log

