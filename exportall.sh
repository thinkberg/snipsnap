#! /bin/sh
base=`dirname $0`
for appdir in $base/applications/*
do
  app=`basename $appdir`
  echo "Exporting $app to $appdir/webapp/WEB-INF/$app.snip ..."
  $base/dbexport.sh -config $appdir/webapp/WEB-INF/application.conf \
	> $appdir/webapp/WEB-INF/$app.snip 2>$appdir/webapp/WEB-INF/export.err
done
