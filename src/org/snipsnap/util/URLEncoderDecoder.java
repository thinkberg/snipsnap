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

import java.util.BitSet;
import java.util.StringTokenizer;

/**
 * Replacement for URLEncoder/URLDecoder of the standard SDK. This was necessary,
 * as the default encoder/decoder used always the platform encoding, not ISO-8859-1
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class URLEncoderDecoder {

  static BitSet dontNeedEncoding;

  static {
    dontNeedEncoding = new BitSet(256);
    int i;
    for (i = 'a'; i <= 'z'; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = 'A'; i <= 'Z'; i++) {
      dontNeedEncoding.set(i);
    }
    for (i = '0'; i <= '9'; i++) {
      dontNeedEncoding.set(i);
    }
    dontNeedEncoding.set(' '); /* encoding a space to a + is done in the encode() method */
    dontNeedEncoding.set('-');
    dontNeedEncoding.set('_');
    dontNeedEncoding.set('.');
    dontNeedEncoding.set('*');
  }


  public static String encode(String s) {
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < s.length(); i++) {
      int c = (int) s.charAt(i);
      if (dontNeedEncoding.get(c)) {
        // @TODO sometimes it is bad to encode space to +
        if (c == ' ') {
          result.append('+');
        } else {
          result.append((char) c);
        }
      } else {
        result.append('%').append(Integer.toHexString(c & 0xFF).toUpperCase());
      }
    }
    return result.toString();
  }

  public static String decode(String s) {
    StringBuffer result = new StringBuffer();
    for(int i = 0; i < s.length(); i++) {
      char c = s.charAt(i);
      // @TODO sometimes it is bad to deencode space to +
      if(c == '+') {
        result.append(' ');
      } else if(c == '%') {
        result.append((char)Integer.parseInt(s.substring(i+1, i+3), 16));
        i+= 2;
      } else {
        result.append(c);
      }
    }
    return result.toString();
  }
}
