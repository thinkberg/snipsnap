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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

public class LocaleComparator {
  public static void main(String args[]) {
    if(args.length >= 2) {
      File messagesFile = new File(args[0]);
      File referenceFile = new File(args[1]);
      Properties messages = new Properties();
      Properties reference = new Properties();

      try {
        messages.load(new FileInputStream(messagesFile));
        reference.load(new FileInputStream(referenceFile));
      } catch (IOException e) {
        System.err.println("Error: "+e.getMessage());
      }

      Properties changes[] = compareBundles(messages, reference);
      if(!changes[0].isEmpty()) {
        System.out.println("There are "+changes[0].size()+" missing messages in '"+messagesFile+"':");
        Iterator it = new TreeSet(changes[0].keySet()).iterator();
        while(it.hasNext()) {
          String key = (String)it.next();
          System.out.println(key+"\t=\t"+changes[0].getProperty(key));
        }
      }
      if(!changes[1].isEmpty()) {
        System.out.println("The following " + changes[1].size() + " messages are not translated in '" + messagesFile + "':");
        System.out.println("Ignore them if they are the same in other languages.");
        Iterator it = new TreeSet(changes[1].keySet()).iterator();
        while (it.hasNext()) {
          String key = (String) it.next();
          System.out.println(key + "\t=\t" + changes[1].getProperty(key));
        }
      }
    } else {
      System.err.println("usage: LocaleComparator <messages file> <reference file>");
    }
    System.exit(0);
  }

  private static Properties[] compareBundles(Properties bundle, Properties refBundle) {
    Properties missingKeys = new Properties();
    Properties nonTranslatedKeys = new Properties();

    Iterator refIt = refBundle.keySet().iterator();
    while(refIt.hasNext()) {
      String key = (String)refIt.next();
      String value = refBundle.getProperty(key).trim();
      if(!bundle.containsKey(key)) {
        missingKeys.setProperty(key, value);
      } else if(value.equals(bundle.getProperty(key).trim())) {
        nonTranslatedKeys.setProperty(key, value);
      }
    }
    return new Properties[] { missingKeys, nonTranslatedKeys };
  }

}
