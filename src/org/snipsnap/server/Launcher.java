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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.Policy;
import java.util.ArrayList;
import java.util.List;
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
    // init error log
    String errorLog = System.getProperty(ERRORLOG);
    if (errorLog != null && errorLog.length() > 0) {
      initSystemErr(errorLog);
    }

    ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
    if (null == parentClassLoader) {
      parentClassLoader = Launcher.class.getClassLoader();
    }
    if (null == parentClassLoader) {
      parentClassLoader = ClassLoader.getSystemClassLoader();
    }
    ClassLoader classLoader = new URLClassLoader(initClassPath(System.getProperty(CLASSPATH)),
                                                 parentClassLoader);
    Thread.currentThread().setContextClassLoader(classLoader);

    try {
      Policy.getPolicy().refresh();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // load and start main class
    Class mainClass = classLoader.loadClass(mainClassName);
    Method main = mainClass.getDeclaredMethod("main", new Class[]{String[].class});
    main.invoke(null, new Object[]{args});
  }

  protected static void initSystemErr(String fileName) {
    try {
      File serverLog = new File(fileName);
      if (serverLog.exists()) {
        File serverLogOld = new File(fileName + ".old");
        serverLog.renameTo(serverLogOld);
      }
      System.setErr(new PrintStream(new FileOutputStream(serverLog)));
    } catch (FileNotFoundException e) {
      if (debug) {
        System.err.println("Launcher: System.err not redirected to " + fileName);
        e.printStackTrace();
      }
    }
  }

  protected static URL[] initClassPath(String extraClassPath) {
    List urlArray = new ArrayList();
    try {
      URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();
      Manifest launcherManifest = new JarInputStream(location.openStream()).getManifest();
      Attributes launcherAttribs = launcherManifest.getMainAttributes();
      String mainJar = launcherAttribs.getValue("Launcher-Main-Jar");
      if(System.getProperty("launcher.main.jar") != null) {
        mainJar = System.getProperty("launcher.main.jar");
      }
      URL mainJarUrl = new URL(location, mainJar);
      Manifest mainManifest = new JarInputStream(mainJarUrl.openStream()).getManifest();
      Attributes mainAttributes = mainManifest.getMainAttributes();
      String manifestClassPath = mainAttributes.getValue("Class-Path");

      urlArray.add(mainJarUrl);
      // append extra class path to manifest class path (after replacing separatorchar)
      if (extraClassPath != null && extraClassPath.length() > 0) {
        manifestClassPath += " " + extraClassPath.replace(':', ' ');
      }

      File directoryBase = new File(location.getFile()).getParentFile();
      StringBuffer classPath = new StringBuffer(location.getFile());
      StringTokenizer tokenizer = new StringTokenizer(manifestClassPath, " \t" + File.pathSeparatorChar, false);
      while (tokenizer.hasMoreTokens()) {
        classPath.append(File.pathSeparatorChar);
        File file = new File(tokenizer.nextToken());
        if (!file.isAbsolute()) {
          file = new File(directoryBase, file.getPath());
        }
        urlArray.add(file.toURL());
        classPath.append(file.getCanonicalPath());
      }
      System.setProperty("java.class.path", classPath.toString());
    } catch (IOException e) {
      System.err.println("Error: Set the system property launcher.main.jar to specify the jar file to start.");
    }
    return (URL[]) urlArray.toArray(new URL[0]);
  }
}