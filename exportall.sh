#! /bin/sh
base=`dirname $0`
for appdir in $base/applications/*
do
  app=`basename $appdir`
  echo "Exporting $app to $appdir/WEB-INF/$app.snip ..."
  $base/dbexport.sh -config $appdir/WEB-INF/application.conf \
	> $appdir/WEB-INF/$app.snip 2>$appdir/WEB-INF/export.err
done
