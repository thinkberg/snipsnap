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
package org.snipsnap.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * A utility to find files matching a certain regular expression and
 * potentially replacing the matched part with the file specified.
 *
 * Example: "(/\*.*?part of.*?\* /)" (remove last space!)
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class ExprEditor {

  public static void main(String[] args) {
    String dir = args[0];
    String regex = args[1];
    String replacement = null;

    if(regex.startsWith("file:")) {
      try {
        regex = getFileContent(new File(regex.substring("file:".length())));
        System.err.println("searching for files matching: "+regex);
      } catch (IOException e) {
        System.err.println("Error: can't find file '"+regex+"'");
        System.exit(1);
      }
    }


    if(args.length > 2) {
      replacement = args[2];
      if (replacement.startsWith("file:")) {
        try {
          replacement = getFileContent(new File(replacement.substring("file:".length())));
        } catch (IOException e) {
          System.err.println("Error: can't find file '" + replacement + "'");
          System.exit(1);
        }
      }
    }

    Pattern pattern = Pattern.compile(regex, Pattern.DOTALL|Pattern.MULTILINE);

    traverse(new File(dir), pattern, replacement);
  }

  private static String getFileContent(File file) throws IOException {
    BufferedReader reader = new BufferedReader(new FileReader(file));
    StringBuffer content = new StringBuffer();
    char[] buffer = new char[1024];
    int len = 0;
    while((len = reader.read(buffer)) != -1) {
      content.append(buffer, 0, len);
    }
    reader.close();
    return content.toString();
  }

  private static void traverse(File root, Pattern pattern, String replacement) {
    File[] files = root.listFiles(new FilenameFilter() {
      public boolean accept(File file, String s) {
        return new File(file, s).isDirectory() || s.endsWith(".java");
      }
    });
    for (int entry = 0; files != null && entry < files.length; entry++) {
      if (files[entry].isDirectory()) {
        traverse(files[entry], pattern, replacement);
      } else {
        replace(files[entry], pattern, replacement);
      }
    }
  }

  private static void replace(File file, Pattern pattern, String replacement) {
    try {
      String content = getFileContent(file);
//      System.err.println("checking '" + file + "' ("+content.length()+")");
      Matcher matcher = pattern.matcher(content);
      if(matcher.find()) {
        if(null != replacement) {
          content = matcher.replaceFirst(replacement);
          storeFileContent(file, content);
          System.err.println("modified '"+file.getPath()+"' ("+matcher.groupCount()+")");
        } else {
          System.err.println("found '"+file.getPath()+"' ("+matcher.groupCount()+")");
        }
      }
    } catch (IOException e) {
      System.err.println("unable to load '"+file+"'");
    }
  }

  private static void storeFileContent(File file, String content) throws IOException {
    File modifiedFile = new File(file.getParentFile(), file.getName() + ".new");
    BufferedWriter writer = new BufferedWriter(new FileWriter(modifiedFile));
    writer.write(content);
    writer.close();
    modifiedFile.renameTo(file);
  }

}
