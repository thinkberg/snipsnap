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

package org.snipsnap.versioning.cookbook;

import org.snipsnap.versioning.ChangeInfo;

import java.io.*;
import java.util.List;
import java.util.Iterator;

/**
 * Returns differences between two files
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class DiffFile {
  public static void main(String argstrings[]) {
    if (argstrings.length != 2) {
      System.err.println("Usage: diff oldfile newfile");
      System.exit(1);
    }
    CookbookDiff d = new CookbookDiff();
    String oldFile = argstrings[0];
    String newFile = argstrings[1];

    System.out.println(">>>> Difference of file \"" + oldFile +
                       "\" and file \"" + newFile + "\".\n");

    List result = d.diff(readFromFile(oldFile), readFromFile(newFile));
    Iterator iterator = result.iterator();
    while (iterator.hasNext()) {
      ChangeInfo info = (ChangeInfo) iterator.next();
      if (ChangeInfo.DELETE.equals(info.getType())) {
        System.out.println("DELETE AT " + info.getFrom());
      } else if (ChangeInfo.INSERT.equals(info.getType())) {
        System.out.println("INSERT BEFORE " + info.getFrom());
      } else if (ChangeInfo.CHANGE.equals(info.getType())) {
        System.out.println("CHANGE AT " + info.getFrom());
      } else if (ChangeInfo.MOVE.equals(info.getType())) {
        System.out.println("MOVE FROM " + info.getFrom() + " THROUGH "+(info.getFrom()+info.getSize()-1)+" TO "+ info.getTo());
      }
      String[] lines = info.getLines();
      for (int i = 0; i < lines.length; i++) {
        String line = lines[i];
        System.out.println("  "+line);
      }
    }

    if (result.size() != 0) {
      System.out.println(">>>> End of differences.");
    } else {
      System.out.println(">>>> Files are identical.");
    }
    return;
  }

  public static String readFromFile(String name) {
    StringBuffer buffer = new StringBuffer();
    BufferedReader file = null;
    try {
      file = new BufferedReader(
        new InputStreamReader(
          new FileInputStream(name)));

      String lineBuffer;
      while ((lineBuffer = file.readLine()) != null) {
        buffer.append(lineBuffer);
        buffer.append("\n");
      }
    } catch (IOException e) {
      System.err.println("Diff can't read file " +
                         name);
      System.err.println("Error Exception was:" + e);
      System.exit(1);
    }
    return buffer.toString();
  }
}
