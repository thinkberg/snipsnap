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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
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
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Launcher {
  public final static String CLASSPATH = "launcher.classpath";

  private final static URL location = Launcher.class.getProtectionDomain().getCodeSource().getLocation();

  public static void invokeMain(String mainClassName, final String args[])
          throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();
    if (null == parentClassLoader) {
      parentClassLoader = Launcher.class.getClassLoader();
    }
    if (null == parentClassLoader) {
      parentClassLoader = ClassLoader.getSystemClassLoader();
    }
    URLClassLoader classLoader = new URLClassLoader(initClassPath(System.getProperty(CLASSPATH)),
                                                    parentClassLoader);
    Thread.currentThread().setContextClassLoader(classLoader);
    if (System.getSecurityManager() != null) {
      System.err.println("Launcher: uninstalling security manager ...");
      System.setSecurityManager(null);
    }

    try {
      Policy.getPolicy().refresh();
    } catch (Exception e) {
      e.printStackTrace();
    }

    // load and start main class
    Class mainClass = classLoader.loadClass(mainClassName);
    final Method main = mainClass.getDeclaredMethod("main", new Class[]{String[].class});
    main.invoke(null, new Object[]{args});
  }

  protected static URL[] initClassPath(String extraClassPath) {
    List urlArray = new ArrayList();
    InputStream manifestIn = null;
    InputStream jarIn = null;
    try {
      manifestIn = location.openStream();
      Manifest launcherManifest = new JarInputStream(manifestIn).getManifest();
      Attributes launcherAttribs = launcherManifest.getMainAttributes();
      String mainJarAttr = launcherAttribs.getValue("Launcher-Main-Jar");
      if (System.getProperty("launcher.main.jar") != null) {
        mainJarAttr = System.getProperty("launcher.main.jar");
      }
      URL mainJarUrl = getResourceUrl(mainJarAttr);
      jarIn = mainJarUrl.openStream();
      Manifest mainManifest = new JarInputStream(jarIn).getManifest();
      Attributes mainAttributes = mainManifest.getMainAttributes();
      String manifestClassPath = mainAttributes.getValue("Class-Path");

      urlArray.add(mainJarUrl);
      // append extra class path to manifest class path (after replacing separatorchar)
      if (extraClassPath != null && extraClassPath.length() > 0) {
        manifestClassPath += " " + extraClassPath.replace(File.pathSeparatorChar, ' ');
      }

      StringBuffer classPath = new StringBuffer(location.getFile());
      StringTokenizer tokenizer = new StringTokenizer(manifestClassPath, " \t" + File.pathSeparatorChar, false);
      while (tokenizer.hasMoreTokens()) {
        classPath.append(File.pathSeparatorChar);
        URL classPathEntry = getResourceUrl(tokenizer.nextToken());
        urlArray.add(classPathEntry);
        classPath.append(classPathEntry.getFile());
      }
      System.setProperty("java.class.path", classPath.toString());
    } catch (IOException e) {
      System.err.println("Error: Set the system property launcher.main.jar to specify the jar file to start.");
    } finally {
      try { manifestIn.close(); } catch (Throwable ignore) { };
      try { jarIn.close(); } catch (Throwable ignore) { };
    }
    return (URL[]) urlArray.toArray(new URL[0]);
  }

  private static URL getResourceUrl(String resource) throws IOException {
    File directoryBase = new File(location.getFile()).getParentFile();
    File file = new File(resource);
    // see if this  is an absolute URL
    if (file.isAbsolute() && file.exists()) {
      return file.toURL();
    }
    // handle non-absolute URLs
    file = new File(directoryBase, resource);
    if (file.exists()) {
      return file.toURL();
    }

    URL resourceURL = Launcher.class.getResource("/" + resource);
    if (null != resourceURL) {
      return extract(resourceURL);
    }

    throw new MalformedURLException("missing resource: " + resource);
  }

  /**
   * Extract file from launcher jar to be able to access is via classpath.
   *
   * @param resource the jar resource to be extracted
   * @return a url pointing to the new file
   * @throws IOException if the extraction was not possible
   */
  private static URL extract(URL resource) throws IOException {
    System.err.println("Launcher: extracting '" + resource.getFile() + "' ...");
    File f = File.createTempFile("launcher_", ".jar");
    f.deleteOnExit();
    if (f.getParentFile() != null) {
      f.getParentFile().mkdirs();
    }
    InputStream is = new BufferedInputStream(resource.openStream());
    FileOutputStream os = new FileOutputStream(f);
    byte[] arr = new byte[8192];
    for (int i = 0; i >= 0; i = is.read(arr)) {
      os.write(arr, 0, i);
    }
    is.close();
    os.close();
    return f.toURL();
  }
}