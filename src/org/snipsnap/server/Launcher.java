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

import java.net.URL;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.List;
import java.util.LinkedList;
import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Launcher for Java Applications. Creates the classpath and then starts the application.
 *
 * @version $Id$
 * @author Matthias L. Jugel (thanks to nexos)
 */
public class Launcher {

  protected static List urlList = new LinkedList();

  /**
   * Make an URL array from a list.
   * @param list the
   */
  protected static URL[] listToURLArray(List list) {
    return (URL[]) list.toArray(new URL[list.size()]);
  }

  /**
   * Add resource only if the class is not already present in the current class path.
   * @param resource the file resource to add
   * @param className a class name to check
   */
  public static void addResource(File resource, String className) throws IOException {
    try {
      Class.forName(className);
    } catch (ClassNotFoundException e) {
      addResource(resource);
    }
  }

  /**
   * Add a file resource.
   * @param resource the file to add
   */
  public static void addResource(File resource) throws IOException {
    URL url;

    if (resource.exists()) {
      url = resource.getCanonicalFile().toURL();
      urlList.add(url);
    } else {
      throw new FileNotFoundException(resource.getCanonicalFile().toString());
    }
  }

  /**
   * Add a resource by naming it in a string. Do not add this resource if the class name
   * already exists.
   * @param resource the resource to add
   * @param className the class name to check
   */
  public static void addResource(String resource, String className) throws IOException {
    addResource(new File(resource), className);
  }

  /**
   * Add a resource by naming it in a string.
   * @param resource the resource to add
   */
  public static void addResource(String resource) throws IOException {
    addResource(new File(resource));
  }

  /**
   * Invoke main class' main method after createing a new classloader that contains the
   * previously added resources.
   * @param main the main class to start
   * @param args main method arguments
   */
  public static void invokeMain(String main, String[] args) throws MalformedURLException, IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

    // prepare class path
    StringBuffer classPath = new StringBuffer();
    if(urlList.size() > 0) {
      classPath.append(((URL)urlList.get(0)).getFile());
    }
    for(int i = 1; i < urlList.size(); i++) {
      classPath.append(File.pathSeparatorChar);
      classPath.append(((URL)urlList.get(i)).getFile());
    }
    System.setProperty("java.class.path", classPath.toString());

    // prepare class loader
    ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
    if(null == classLoader) {
      classLoader = Launcher.class.getClassLoader();
    }
    if(null == classLoader) {
      ClassLoader.getSystemClassLoader();
    }
    URLClassLoader urlClassLoader = URLClassLoader.newInstance(listToURLArray(urlList), classLoader);

    // start main class
    Thread.currentThread().setContextClassLoader(urlClassLoader);
    Class mainClass = urlClassLoader.loadClass(main);
    Method method = mainClass.getDeclaredMethod("main", new Class[]{String[].class});
    method.invoke(null, new Object[]{args});
  }
}