package org.snipsnap.util;

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.snip.XMLSnipExport;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * Dump current database contents. The database is read directly and meta-data
 * is used to detect xml tag names. First all users are dumped and then all
 * snips.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class DBDump {

  public static void main(String[] args) {

    Configuration config = null;

    if (args.length > 0 && "-config".equals(args[0])) {
      if (args.length > 1) {
        try {
          config = ConfigurationProxy.newInstance();
          File appConf = new File(args[1]);
          config.load(new FileInputStream(appConf));
          config.setWebInfDir(appConf.getParentFile());
        } catch (IOException e) {
          System.err.println("DBDump: unable to read configuration file: " + e);
          System.exit(-1);
        }
      }
    }


    if (args.length > 0 && "-db".equals(args[0])) {
      config = ConfigurationProxy.newInstance();
      config.set(Configuration.APP_JDBC_DRIVER, "org.snipsnap.util.MckoiEmbeddedJDBCDriver");
      config.set(Configuration.APP_JDBC_URL, "jdbc:mckoi:local://" + args[1]);
      config.set(Configuration.APP_ADMIN_LOGIN, args[2]);
      config.set(Configuration.APP_ADMIN_PASSWORD, args[3]);
    }

    if (config == null) {
      System.err.println("usage: DBDump [-config file] [-db dbconf user pass]");
      System.exit(-1);
    }

    Application app = Application.get();
    app.setConfiguration(config);

    try {
      XMLSnipExport.store(System.out);
    } catch (IOException e) {
      Logger.warn("DBDump: unable to dump database", e);
    }
    System.exit(0);
  }
}
