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
package org.snipsnap.net.filter;

import org.radeox.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.UnsupportedEncodingException;

public class EncRequestWrapper extends HttpServletRequestWrapper {
  String encoding = "UTF-8";

  public EncRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public EncRequestWrapper(HttpServletRequest request, String enc) throws UnsupportedEncodingException {
    super(request);
    "".getBytes(enc);
    encoding = enc;
  }

  private String getEncodedString(String src) {
    if (src != null) {
      try {
        return new String(src.getBytes("iso-8859-1"), encoding);
      } catch (UnsupportedEncodingException e) {
        Logger.warn("Error: illegal encoding: " + e);
      }
    }
    return src;
  }

  public String getHeader(String name) {
    return getEncodedString(super.getHeader(name));
  }

  public String getQueryString() {
    return getEncodedString(super.getQueryString());
  }

  public String getRequestURI() {
    return getEncodedString(super.getRequestURI());
  }

  public String getPathInfo() {
    return getEncodedString(super.getPathInfo());
  }
}
