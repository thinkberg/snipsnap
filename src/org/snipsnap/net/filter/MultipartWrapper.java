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

import org.snipsnap.util.mail.InputStreamDataSource;
import org.snipsnap.util.log.Logger;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.ContentDisposition;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.PrintStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * A MultipartWrapper that takes a request object and parses the incoming data.
 * Access is provided for queryString parameters and all multipart/form-data entries.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class MultipartWrapper extends HttpServletRequestWrapper {

  MimeMultipart multipart = null;
  Hashtable params = null;
  Map files = new HashMap();

  public MultipartWrapper(HttpServletRequest request) throws IOException {
    super(request);

    InputStreamDataSource ds = new InputStreamDataSource(request.getInputStream(), request.getContentType());
    try {
      multipart = new MimeMultipart(ds);
      params = new Hashtable(request.getParameterMap());

      int count = multipart.getCount();
      for (int i = 0; i < count; i++) {
        MimeBodyPart body = (MimeBodyPart) multipart.getBodyPart(i);
        ContentDisposition disp = new ContentDisposition(body.getHeader("content-disposition", null));
        String name = disp.getParameter("name");
        if (body.getContentType().startsWith("text")) {
          String value = new String(((String)body.getContent()).getBytes("iso-8859-1"), request.getCharacterEncoding());
          String[] values = (String[]) params.get(name);
          if (null == values) {
            params.put(name, new String[]{ value });
          } else {
            String[] tmp = new String[values.length + 1];
            System.arraycopy(values, 0, tmp, 0, values.length);
            tmp[values.length + 1] = value;
          }
        } else {
          files.put(name, body);
        }
      }
    } catch (MessagingException e) {
      throw new IllegalArgumentException("Error parsing request (not multipart/form-data?)");
    }
  }

  public Enumeration getParameterNames() {
    return params.keys();
  }

  public String getParameter(String name) {
    String[] values = (String[]) params.get(name);
    if (values != null && values.length > 0) {
      return values[0];
    }
    return null;
  }

  public String[] getParameterValues(String name) {
    return (String[]) params.get(name);
  }

  /**
   * Returns a map of all parameters except special body parts
   */
  public Map getParameterMap() {
    return (Map) params;
  }

  /**
   * If a special input field is not text it can be retrieved as BodyPart
   * for further processing.
   * @param name the name of the input field
   * @return a body part object from JavaMail
   */
  public BodyPart getBodyPart(String name) {
    return (BodyPart) files.get(name);
  }

  /**
   * Get the content type of a file parameter from the request.
   * @param name the name of the input field
   * @return the content type
   */
  public String getFileContentType(String name) {
    BodyPart part = (BodyPart) files.get(name);
    if (part != null) {
      try {
        return part.getContentType();
      } catch (MessagingException e) {
        // ignore and simply return null
      }
    }
    return null;
  }
}

