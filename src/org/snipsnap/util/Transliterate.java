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
package com.neotis.util;

import java.util.Properties;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Transliterate 16-bit unicode characters to ASCII for conversion of
 * non-ascii characters in a URL.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Transliterate {
  Properties asciinative = new Properties();
  Properties nativeascii = new Properties();

  public Transliterate(String configuration) {
    try {
      Properties tmp = new Properties();
      tmp.load(new FileInputStream("./conf/"+configuration));

      Enumeration en = tmp.keys();
      while(en.hasMoreElements()) {
	String key = (String)en.nextElement();
	String keys[] = key.split("/");
	String value = tmp.getProperty(key);
	String values[] = value.split("/");

	for(int i = 0; i < keys.length; i++) {
	  for(int j = 0; j < values.length; j++) {
	    asciinative.put(keys[i], values[j]);
	    nativeascii.put(values[j].toLowerCase(), keys[0]);
	  }
	}
      }
    } catch(IOException e) {
      System.err.println("Transliterate: unable to load database: "+e);
    }
  }

  public String asciiToNative(String in) {
    String result = "";
    int i = 0, s = 0;
    while(i < in.length()) {
      if(i >= s+1) {
	String n = asciinative.getProperty(in.substring(s, i));
	// override single-char syllable by multi-char
	if(i - s == 1 && i + 1 < in.length()) {
	  String tmp = asciinative.getProperty(in.substring(s, i + 1));
	  if(tmp != null) {
	    n = tmp;
	    i++;
	  }
	}
	if(n != null) {
	  result += n;
	  s = i;
	}
      }
      i++;
    }
    if(s != i) {
      String n = asciinative.getProperty(in.substring(s, i));
      if(n != null) {
	return result + n;
      }
    }
    return null;
  }

  public String nativeToAscii(String in) {
    String parts[] = in.split("[&#;]");
    String result = "", key = "";
    for(int i = 0; i < parts.length; i++) {
      try {
        key += "&#x"+Integer.toHexString(Integer.valueOf(parts[i]).intValue())+";";
        String ascii = nativeascii.getProperty(key);
	if(ascii != null) {
	  result += ascii;
	  key = "";
        } else {
	  return null;
	}
      } catch(Exception e) {
	// ignore non-numbers
      }
    }
    return result;
  }

  public static void main(String args[]) {
    Transliterate t = new Transliterate(args[0]);
    String ascii = t.nativeToAscii(args[1]);
    System.out.println("n2a ==> "+ascii);
    System.out.println("a2n ==> "+t.asciiToNative(ascii));
  }
}
