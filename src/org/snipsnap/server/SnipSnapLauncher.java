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

/**
 * SnipSnap launcher that takes care of adding the compiler to the classpath before
 * invoking AppServer.
 *
 * @version $Id$
 * @author Matthias L. Jugel
 */
public class SnipSnapLauncher extends Launcher {

  /**
   * Start SnipSnap after adding the sdk tools.jar or similar to the classpath.
   * @param args command line arguments
   */
  public static void main(String[] args) {
    File toolsJar = new File(new File(System.getProperty("java.home")), "lib/tools.jar");
    if (!toolsJar.exists()) {
      String system = System.getProperty("os.name");
      if (system.startsWith("Mac OS X")) {
        toolsJar = new File("/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar");
      } else {
        System.out.println("Java SDK not found: " + toolsJar);
        System.out.println("Please set JAVA_HOME to the SDK home directory.");
        System.exit(-1);
      }
    }
    try {
      System.setProperty(Launcher.CLASSPATH, toolsJar.getCanonicalPath());
    } catch (IOException e) {
      System.out.println("SnipSnapLauncher: unable to add java compiler library: "+e.getMessage());
    }
    System.setProperty(Launcher.ERRORLOG, "server.log");

    try {
      invokeMain("org.snipsnap.server.AppServer", args);
    } catch (Exception e) {
      System.out.println("SnipSnapLauncher: unable to start server: "+e.getMessage());
      e.printStackTrace();
    }
  }

}
