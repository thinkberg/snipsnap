/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
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
package org.snipsnap.server;

import org.snipsnap.config.ServerConfiguration;
import org.snipsnap.util.XMLSnipRepair;
import org.snipsnap.util.LocaleComparator;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.Arrays;
import java.util.Iterator;
import java.util.TreeSet;
import java.text.NumberFormat;

public class AdminClient {

  public static void main(String args[]) {
    printCopyright();
    Properties config = new Properties();
    try {
      config.load(AdminClient.class.getResourceAsStream("/conf/snipsnap.conf"));
    } catch (Exception e) {
      System.err.println("AdminClient: unable to load config defaults: " + e);
    }
    try {
      config.load(new FileInputStream("conf/server.conf"));
    } catch (IOException e) {
//      System.err.println("AdminClient: unable to load conf/server.conf: " + e);
    }

    List commands = parseOptions(args, config);
    if (commands.size() > 0) {
      execute(commands, config);
      System.exit(0);
    } else {
      System.err.println("usage: AdminClient command arguments");
      System.err.println("       command may be either an XML-RPC method or 'repair'");
    }
    System.exit(1);
  }

  private static void printCopyright() {
    System.err.println("SnipSnap AdminClient ($Revision$)");

    // output version and copyright information
    try {
      BufferedReader copyrightReader = new BufferedReader(new InputStreamReader(AdminClient.class.getResourceAsStream("/conf/copyright.txt")));
      String line = null;
      while ((line = copyrightReader.readLine()) != null) {
        System.err.println(line);
      }
    } catch (Exception e) {
      // ignore io exception here ...
    }
  }

  private static void execute(List commands, Properties config) {
    if ("repair".equals(commands.get(0))) {
      File in = null, out = null, webapp = null;
      switch (commands.size()) {
        case 1:
          System.err.println("repair needs arguments: <input> <output> <webapp directory>");
          System.exit(0);
          break;
        case 4:
          webapp = new File((String) commands.get(3));
        case 3:
          out = new File((String) commands.get(2));
        case 2:
          in = new File((String) commands.get(1));
      }
      XMLSnipRepair.repair(in, out, webapp);
    } else if ("checklocale".equals(commands.get(0))) {
      boolean show = false;
      switch (commands.size()) {
        case 1:
          System.err.println("checklocale needs at least one argument: [-show] <locale to check>");
          System.exit(0);
          break;
        case 3:
          show = "-show".equals(commands.get(1));
          commands.remove(1);
        case 2:
          File[] localefiles = new File("src/apps/default/WEB-INF/classes/i18n").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
              return name.endsWith("_en.properties");
            }
          });
          String locale = (String) commands.get(1);
          checkLocaleFiles(localefiles, locale, show);
          localefiles = new File("src/apps/installer/WEB-INF/classes/i18n").listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
              return name.endsWith("_en.properties");
            }
          });
          checkLocaleFiles(localefiles, locale, show);
          break;
      }
    } else {
      try {
        System.err.println("Contacting Remote Server ...");
        AdminXmlRpcClient client = new AdminXmlRpcClient(config.getProperty(ServerConfiguration.ADMIN_URL),
                                                         config.getProperty(ServerConfiguration.ADMIN_USER),
                                                         config.getProperty(ServerConfiguration.ADMIN_PASS));
        String method = (String) commands.get(0);
        Vector args = new Vector();
        for (int i = 1; i < commands.size(); i++) {
          args.addElement(commands.get(i));
        }
        Object result = client.execute(method, args);
        System.err.println("Operation '" + method + "' okay:");
        if (result instanceof Object[]) {
          System.out.println("" + Arrays.asList((Object[]) result));
        } else if (result instanceof byte[]) {
          System.out.println(new String((byte[]) result, "UTF-8"));
        } else {
          System.out.println(result);
        }
      } catch (Exception e) {
        System.err.println("AdminClient: error executing command: " + e.getMessage());
      }
    }
  }

  private static void checkLocaleFiles(File[] localefiles, String locale, boolean show) {
    for (int i = 0; i < localefiles.length; i++) {
      Properties defaultBundle = new Properties();
      Properties compareBundle = new Properties();
      try {
        defaultBundle.load(new FileInputStream(localefiles[i]));
        String bundleName = localefiles[i].getName();
        bundleName = bundleName.substring(0, bundleName.length() - "_en.properties".length());
        File bundleFile = new File(localefiles[i].getParentFile(), bundleName + "_" + locale + ".properties");
        compareBundle.load(new FileInputStream(bundleFile));
        System.out.println("== Checking bundle " + bundleFile.getName());
        Properties problems[] = LocaleComparator.compareBundles(compareBundle, defaultBundle);
        if (!problems[0].isEmpty()) {
          System.out.println("== "+bundleFile.getName() + ": " + problems[0].size() + " missing properties.");
          if (show) {
            Iterator it = new TreeSet(problems[0].keySet()).iterator();
            while (it.hasNext()) {
              String key = (String) it.next();
              System.out.println(key + "\t=\t" + problems[0].getProperty(key));
            }
          }
        }
        if (!problems[1].isEmpty()) {
          System.out.println("== "+bundleFile.getName() + ": " + problems[1].size() + " not translated.");
          if (show) {
            Iterator it = new TreeSet(problems[1].keySet()).iterator();
            while (it.hasNext()) {
              String key = (String) it.next();
              System.out.println(key + "\t=\t" + problems[1].getProperty(key));
            }
          }
        }
      } catch (IOException e) {
        System.err.println("can't find bundle: " + e.getMessage());
      }
    }
  }


  private static List parseOptions(String args[], Properties config) {
    int argNo;
    List commands = new ArrayList();
    for (argNo = 0; argNo < args.length; argNo++) {
      if ("-url".equals(args[argNo]) && args.length > argNo + 1) {
        config.setProperty(ServerConfiguration.ADMIN_URL, args[argNo + 1]);
        argNo++;
      } else if ("-config".equals(args[argNo]) && args.length > argNo + 1) {
        try {
          config.load(new FileInputStream(args[argNo + 1]));
        } catch (IOException e) {
          System.err.println("AdminClient: unable to load configuration: " + e);
        }
        argNo++;
      } else if ("-user".equals(args[argNo]) && args.length > argNo + 1) {
        config.setProperty(ServerConfiguration.ADMIN_USER, args[argNo + 1]);
        argNo++;
      } else if ("-password".equals(args[argNo]) && args.length > argNo + 1) {
        config.setProperty(ServerConfiguration.ADMIN_PASS, args[argNo + 1]);
        argNo++;
      } else {
        if (args[argNo] != null && args[argNo].startsWith("file:")) {
          String fileName = args[argNo].substring("file:".length());
          System.err.print("Reading file '" + fileName + "'");
          try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            File file = new File(fileName);
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            long fileLength = file.length();
            System.err.println(" " + (fileLength / 1024) + " kB");
            System.err.print("0%");
            byte[] buffer = new byte[4096];
            int n = 0;
            int current = 0;
            while ((n = in.read(buffer)) != -1) {
              current += n;
              loadProgress(fileLength, current, 4096);
              bos.write(buffer, 0, n);
            }
            commands.add(bos.toByteArray());
          } catch (IOException e) {
            System.err.println("AdminClient: unable to load file: " + args[argNo] + ": " + e);
          }
          System.err.println();
        } else if (args[argNo] != null && args[argNo].startsWith("properties:")) {
          String fileName = args[argNo].substring("properties:".length());
          System.err.println("Reading properties from file '" + fileName + "'");
          try {
            Properties props = new Properties();
            props.load(new FileInputStream(fileName));
            commands.add(props);
          } catch (IOException e) {
            System.err.println("AdminClient: unable to load properties: " + args[argNo] + ": " + e);
          }
        } else {
          commands.add(args[argNo]);
        }
      }
    }
    return commands;
  }

  private static void loadProgress(long length, long current, int blockSize) {
    long percentage = current * 100 / length;
    if (percentage % 5 != 0 && ((current - blockSize) * 100 / length) % 5 == 0) {
      System.err.print(".");
    } else if (percentage % 20 == 0 && ((current - blockSize) * 100 / length) % 20 != 0) {
      System.err.print(NumberFormat.getIntegerInstance().format(percentage) + "%");
    }
  }


}
