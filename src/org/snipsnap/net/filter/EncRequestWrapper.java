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
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Collections;

public class EncRequestWrapper extends HttpServletRequestWrapper {
  String encoding = "UTF-8";
  Map paramMap = null;

  public EncRequestWrapper(HttpServletRequest request) throws UnsupportedEncodingException {
    this(request, request.getCharacterEncoding());
  }

  public EncRequestWrapper(HttpServletRequest request, String enc) throws UnsupportedEncodingException {
    super(request);
    "".getBytes(enc);
    encoding = enc;

    Map params = request.getParameterMap();
    Iterator iterator = params.keySet().iterator();
    Map encodedParams = new HashMap();
    while (iterator.hasNext()) {
      String key = (String) iterator.next();
      String[] values = (String[]) params.get(key);
      for(int n = 0; n < values.length; n++) {
        values[n] = getEncodedString(values[n]);
      }
      encodedParams.put(key, values);
    }
    paramMap = Collections.unmodifiableMap(encodedParams);
  }

  private String getEncodedString(String src) {
    if (src != null) {
      try {
        return new String(src.getBytes("iso-8859-1"), encoding);
      } catch (UnsupportedEncodingException e) {
        System.err.println(e);
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

  public String getParameter(String name) {
    String[] values = (String[])paramMap.get(name);
    if(null != values && values.length > 0) {
      return ((String[])paramMap.get(name))[0];
    }
    return null;
  }

  public Map getParameterMap() {
    return new HashMap(paramMap);
  }

  public Enumeration getParameterNames() {
    final Iterator nameIt = paramMap.keySet().iterator();
    return new Enumeration() {
      public boolean hasMoreElements() {
        return nameIt.hasNext();
      }

      public Object nextElement() {
        return nameIt.next();
      }
    };
  }

  public String[] getParameterValues(String name) {
    return (String[])paramMap.get(name);
  }
}
