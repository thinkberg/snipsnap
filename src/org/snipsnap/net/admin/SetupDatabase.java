/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */
package org.snipsnap.net.admin;

import snipsnap.api.config.Configuration;
import org.snipsnap.config.Globals;
import org.snipsnap.app.JDBCApplicationStorage;
import org.snipsnap.snip.storage.JDBCSnipStorage;
import org.snipsnap.snip.storage.JDBCUserStorage;
import org.snipsnap.versioning.JDBCVersionStorage;
import org.snipsnap.jdbc.ConnectionManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Properties;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class SetupDatabase implements SetupHandler {
  public String getName() {
    return "database";
  }

  /**
   * Set up the database which is the central data store
   */
  public Map setup(HttpServletRequest request, HttpServletResponse response, snipsnap.api.config.Configuration config, Map errors) {
    String database = request.getParameter(Configuration.APP_DATABASE);
    config.setDatabase(database);

    if ("file".equals(database)) {
      config.setFileStore(request.getParameter(Globals.APP_FILE_STORE));
      File fileStore = new File(config.getGlobal(Globals.APP_FILE_STORE));
      if (checkPath(config.getGlobal(Globals.APP_FILE_STORE))) {
        fileStore.mkdirs();
      } else {
        errors.put(Globals.APP_FILE_STORE, Globals.APP_FILE_STORE);
      }
    } else if (database.startsWith("jdbc")) {
      boolean internalDatabase = "jdbc.mckoi".equals(database);
      if (internalDatabase) {
        config.setJdbcDriver(config.getGlobalDefault(Globals.APP_JDBC_DRIVER));
        config.setJdbcUrl(config.getGlobalDefault(Globals.APP_JDBC_URL));
        config.setJdbcUser("snipsnap");
        config.setJdbcPassword("snipsnap");
      } else {
        String jdbcDriver = request.getParameter(Globals.APP_JDBC_DRIVER);
        config.setJdbcDriver(jdbcDriver != null ? jdbcDriver : "");
        config.setJdbcUrl(request.getParameter(Globals.APP_JDBC_URL));
        config.setJdbcUser(request.getParameter(Globals.APP_JDBC_USER));
        String passwd = request.getParameter(Globals.APP_JDBC_PASSWORD);
        if (null != passwd) {
          config.setJdbcPassword(passwd);
        }
      }

      try {
        Class.forName(config.getJdbcDriver());
      } catch (ClassNotFoundException e) {
        errors.put(Globals.APP_JDBC_DRIVER, Globals.APP_JDBC_DRIVER);
        return errors;
      }

      try {
        if (internalDatabase) {
          createInternalDatabase(config);
        }

        // initialize storages
        // TODO: make generic or check for type of storage
        JDBCApplicationStorage.createStorage();
        JDBCSnipStorage.createStorage();
        JDBCVersionStorage.createStorage();
        JDBCUserStorage.createStorage();

      } catch (Exception e) {
        ConnectionManager.removeInstance();
        if (e instanceof RuntimeException) {
          errors.put(Globals.APP_JDBC_URL, Globals.APP_JDBC_URL);
          errors.put(Globals.APP_JDBC_USER, Globals.APP_JDBC_USER);
          errors.put(Globals.APP_JDBC_PASSWORD, Globals.APP_JDBC_PASSWORD);
        } else {
          errors.put("fatal", "fatal");
        }
        e.printStackTrace();
        return errors;
      }
    }

    config.setInstalled("true");
    File configFile = new File(config.getWebInfDir(), "application.conf");
    try {
      config.storeGlobals(new FileOutputStream(configFile));
    } catch (IOException e) {
      errors.put("fatal", "fatal");
      e.printStackTrace();
    }

    return errors;
  }

  private static void createInternalDatabase(Globals config) throws IOException, SQLException {
    System.err.println("creating internal database");
// create directories
    File dbDir = new File(config.getWebInfDir(), "mckoidb");
    dbDir.mkdir();
// store default configurationn file
    File dbConfFile = new File(config.getWebInfDir(), "mckoidb.conf");
    Properties dbConf = new Properties();
    System.err.println("Creating internal database config file: " + dbConfFile.toString());
    dbConf.load(ConfigureServlet.class.getResourceAsStream("/defaults/mckoidb.conf"));
    dbConf.store(new FileOutputStream(dbConfFile), "SnipSnap Database configuration");
  }

  /**
   * Check a path if it is writable. Returns true if one of the parents is writable and
   * false if one of the parents is not writable.
   * @param path the path to check
   * @return whether the path if writable or creatable
   */
  private boolean checkPath(String path) {
    File pathFile = new File(path);
    while (pathFile.getParentFile() != null && !pathFile.exists()) {
      pathFile = pathFile.getParentFile();
    }
    return pathFile.exists() && pathFile.canWrite();
  }

}
