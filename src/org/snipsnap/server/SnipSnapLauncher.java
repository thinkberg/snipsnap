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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 * SnipSnap launcher that takes care of adding the compiler to the classpath before
 * invoking AppServer.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipSnapLauncher extends Launcher {

  /**
   * Start SnipSnap after adding the sdk tools.jar or similar to the classpath.
   *
   * @param args command line arguments
   */
  public static void main(String[] args) {
    // try to add the java compiler path
    File toolsJar = new File(new File(System.getProperty("java.home")), "lib/tools.jar");
    if (!toolsJar.exists()) {
      toolsJar = new File(new File(System.getProperty("java.home")), "../lib/tools.jar");
    }
    if (!toolsJar.exists()) {
      String system = System.getProperty("os.name");
      if (system.startsWith("Mac OS X")) {
        toolsJar = new File("/System/Library/Frameworks/JavaVM.framework/Classes/classes.jar");
      } else {
        System.out.println("Java SDK not found: " + toolsJar);
        System.out.println("Please set JAVA_HOME to the SDK home directory.");
        System.out.println("SnipSnap will run, but you cannot use source JSP files.");
      }
    }
    try {
      System.setProperty(Launcher.CLASSPATH, toolsJar.getCanonicalPath());
    } catch (IOException e) {
      System.out.println("SnipSnapLauncher: unable to add java compiler library: " + e.getMessage());
    }

    try {
      File errorLog = null;
      if (System.getProperty("launcher.errlog") != null) {
        errorLog = new File(System.getProperty("launcher.errlog"));
      } else {
        errorLog = File.createTempFile("snipsnap_", ".log");
      }
      System.err.println("Launcher: System.err redirected to " + errorLog.getPath());
      System.setErr(new PrintStream(new FileOutputStream(errorLog)));
    } catch (IOException e) {
      System.err.println("Launcher: unable to redirect error log: " + e.getMessage());
    }

    try {
      invokeMain("org.snipsnap.server.AppServer", args);
    } catch (Exception e) {
      System.out.println("SnipSnapLauncher: unable to start server: " + e.getMessage());
      e.printStackTrace();
    }
  }
}