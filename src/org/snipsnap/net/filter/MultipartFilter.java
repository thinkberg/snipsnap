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

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * A ServletFilter that parses multipart/form-data requests and wraps the data into
 * a HttpRequestWrapper. It uses JavaMail API for parsing MIME bodies.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class MultipartFilter implements Filter {

  public void init(FilterConfig config) throws ServletException {
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    String type = req.getHeader("Content-Type");

    // If this is not a multipart/form-data request continue
    if (type == null || !type.startsWith("multipart/form-data")) {
      chain.doFilter(req, response);
    } else {
      try {
        chain.doFilter(new MultipartWrapper(req, "UTF-8"), response);
      } catch (IllegalArgumentException e) {
        Logger.warn("MultipartFilter: "+e.getMessage());
      }
    }
  }
}