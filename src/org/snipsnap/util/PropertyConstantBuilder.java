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
package org.snipsnap.util;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class PropertyConstantBuilder extends Task {
  public static void main(String args[]) {
    if (args.length < 2) {
      System.err.println("usage: " + PropertyConstantBuilder.class.getName() +
                         " propertiesfile interface [prefix]");
      System.exit(-1);
    }
    String prefix = "";
    if(args.length > 2) {
      prefix = args[2];
    }

    try {
      new PropertyConstantBuilder().buildClass(args[0], new File(args[1]), prefix);
    } catch (BuildException e) {
      System.err.println("class build failed: " + e.getMessage());
      System.exit(-1);
    }
    System.exit(0);
  }

  private String propertiesFile = null;
  private String stubFile = null;
  private String prefix = "";

  public void execute() throws BuildException {
    buildClass(propertiesFile, new File(stubFile), prefix);
  }

  public void setFile(String file) {
    this.stubFile = file;
  }

  public void setProperties(String file) {
    this.propertiesFile = file;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  private void buildClass(String propertiesFile, File file, String prefix) throws BuildException {
    Properties defaults = new Properties();
    try {
      defaults.load(new FileInputStream(propertiesFile));
    } catch (IOException e) {
      throw new BuildException("properties file '" + propertiesFile + "' not found.");
    }

    try {
      PrintWriter stubWriter = new PrintWriter(new FileWriter(stubFile));
      stubWriter.println();
      stubWriter.println("  // automatically created interface/constants stub from");
      stubWriter.println("  // "+propertiesFile);
      stubWriter.println("  // generated on " + new SimpleDateFormat().format(new Date()));

      createConstants(stubWriter, defaults, prefix);

      stubWriter.close();
    } catch (IOException e) {
      throw new BuildException("error writing class sources: " + e.getMessage());
    }
  }

  private void createConstants(PrintWriter stubWriter, Properties properties, String prefix) {
    TreeSet sorted = new TreeSet(properties.keySet());
    Iterator it = sorted.iterator();
    while (it.hasNext()) {
      String property = (String) it.next();
      // write constants
      stubWriter.println("  // constant/getter for '"+property+"'");
      stubWriter.print("  public final static String " + property.toUpperCase().replace('.', '_'));
      stubWriter.println(" = \"" + property + "\";");
      stubWriter.println("  public String get" + getCamlCase(property, prefix) + "();");
    }
  }

  private String getCamlCase(String name, String prefix) {
    if(name.startsWith(prefix)) {
      name = name.substring(prefix.length());
    }
    StringTokenizer tokenizer = new StringTokenizer(name, ".", false);
    StringBuffer result = new StringBuffer();
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      result.append(token.substring(0, 1).toUpperCase());
      if (token.length() > 1) {
        result.append(token.substring(1));
      }
    }
    return result.toString();
  }
}
