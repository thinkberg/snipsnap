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

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

/**
 * Launcher for Java Applications. Creates the classpath and then starts the application.
 *
 * @version $Id$
 * @author Matthias L. Jugel
 */
public class Launcher {
  public final static String CLASSPATH = "launcher.classpath";
  public final static String ERRORLOG = "launcher.errlog";

  private static boolean debug = false;

  public static void invokeMain(String mainClassName, String args[])
    throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    // init class path
    initClassPath(System.getProperty(CLASSPATH));

    // init error log
    String errorLog = System.getProperty(ERRORLOG);
    if(errorLog != null && errorLog.length() > 0) {
      initSystemErr(errorLog);
    }

    Class mainClass = Launcher.class.getClassLoader().loadClass(mainClassName);
    Method main = mainClass.getDeclaredMethod("main", new Class[] { String[].class });
    main.invoke(null, new Object[] { args });
  }

  protected static void initSystemErr(String fileName) {
    try {
      File serverLog = new File(fileName);
      if(serverLog.exists()) {
        File serverLogOld = new File(fileName+".old");
        serverLog.renameTo(serverLogOld);
      }
      System.setErr(new PrintStream(new FileOutputStream(serverLog)));
    } catch (FileNotFoundException e) {
      if(debug) {
        System.err.println("Launcher: System.err not redirected to "+fileName);
        e.printStackTrace();
      }
    }
  }

  protected static void initClassPath(String extraClassPath) {
    try {
      URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
      JarInputStream jarInputStream = new JarInputStream(location.openStream());
      Manifest manifest = jarInputStream.getManifest();
      Attributes mainAttributes = manifest.getMainAttributes();
      String manifestClassPath = mainAttributes.getValue("Class-Path");

      // append extra class path to manifest class path (after replacing separatorchar)
      if(extraClassPath != null && extraClassPath.length() > 0) {
        manifestClassPath += " " + extraClassPath;
      }

      File directoryBase = new File(location.getFile()).getParentFile();
      StringBuffer classPath = new StringBuffer(location.getFile());
      StringTokenizer tokenizer = new StringTokenizer(manifestClassPath, " \t"+File.separatorChar, false);
      while(tokenizer.hasMoreTokens()) {
        classPath.append(File.pathSeparatorChar);
        String file = tokenizer.nextToken();
        classPath.append(new File(directoryBase, file).getCanonicalPath());
      }
      System.setProperty("java.class.path", classPath.toString());
    } catch (IOException e) {
      System.err.println("Warning: not running from a jar: make sure your CLASSPATH is set correctly.");
    }
  }
}