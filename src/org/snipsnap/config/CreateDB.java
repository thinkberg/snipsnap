package com.neotis.config;

import com.neotis.snip.SnipSpace;
import com.neotis.user.User;
import com.neotis.user.UserManager;
import com.neotis.app.Application;

import java.sql.*;

public class CreateDB {

  public static void main(String[] args) {
    createDB();
  }

  private static void createDB() {
    System.out.println();

    // Register the Mckoi JDBC Driver
    try {
      Class.forName("com.mckoi.JDBCDriver").newInstance();
    }
    catch (Exception e) {
      System.out.println(
     "Unable to register the JDBC Driver.\n" +
     "Make sure the classpath is correct.\n" +
     "For example on Win32;  java -cp ../../mckoidb.jar;. SimpleApplicationDemo\n" +
     "On Unix;  java -cp ../../mckoidb.jar:. SimpleApplicationDemo");
      return;
    }

    // This URL specifies we are creating a local database.  The
    // configuration file for the database is found at './ExampleDB.conf'
    // The 'create=true' argument means we want to create the database.  If
    // the database already exists, it can not be created.
    String url = "jdbc:mckoi:local://conf/db.conf?create=true";

    // The username/password for the database.  This will be the username/
    // password for the user that has full control over the database.
    // ( Don't use this demo username/password in your application! )
    String username = "funzel";
    String password = "funzel";

    // Make a connection with the database.  This will create the database
    // and log into the newly created database.
    Connection connection;
    try {
      connection = DriverManager.getConnection(url, username, password);
    }
    catch (SQLException e) {
      System.out.println(
     "Unable to create the database.\n" +
     "The reason: " + e.getMessage());
      return;
    }

    // --- Set up the database ---

    try {
      // Create a Statement object to execute the queries on,
      Statement statement = connection.createStatement();
      ResultSet result;

      System.out.println("-- Creating Tables --");

      // Create a Person table,
      statement.executeQuery(
      "    CREATE TABLE Snip ( " +
      "       name      VARCHAR(100) NOT NULL, " +
      "       content   TEXT, " +
      "       cTime     TIMESTAMP, " +
      "       mTime     TIMESTAMP, " +
      "       cUser     CHAR(55), " +
      "       mUser     CHAR(55), " +
      "       parentSnip VARCHAR(100) ) " );

      statement.executeQuery(
      "    CREATE TABLE User ( " +
      "       login    VARCHAR(100) NOT NULL, " +
      "       passwd   CHAR(20) )");

      System.out.println("-- Inserting Data --");

     statement.executeQuery(
      "    INSERT INTO User ( login, passwd ) VALUES " +
      "      ( '"+username+"', '"+password+"')");

      // Close the statement and the connection.
      statement.close();
      connection.close();

		  Application app  = new Application();
			User user = UserManager.getInstance().load("funzel");
			app.setUser(user);
			
		  SnipSpace.getInstance().create("about","[SnipSnap] is a [Weblog] and [Wiki] tool", app);

      System.out.println("--- Complete ---");

    }
    catch (SQLException e) {
      System.out.println(
    "An error occured\n" +
    "The SQLException message is: " + e.getMessage());

    }

    // Close the the connection.
    try {
      connection.close();
    }
    catch (SQLException e2) {
      e2.printStackTrace(System.err);
    }
  }
}
