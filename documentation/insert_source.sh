DIR=`dirname $0`
FILE=`basename $1 .xml`

cp $FILE.xml $FILE.xml.bak
ruby $DIR/cut.rb $FILE.xml  > tmp.xml && mv tmp.xml $FILE.xml
