java -cp lib/mckoidb.jar com.mckoi.tools.JDBCQueryTool \
       -url "jdbc:mckoi:local://./conf/db.conf" \
       -u "$1" -p "$2"
