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
import snipsnap.api.app.Application;
import org.snipsnap.config.Globals;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * A ServletFilter that parses ensures correctly encoded requests
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class EncRequestFilter implements Filter {

  public void init(FilterConfig config) throws ServletException {
  }

  public void destroy() {
  }

  public void doFilter(ServletRequest request, ServletResponse response,
                       FilterChain chain) throws IOException, ServletException {
    System.out.println("EncRequestFilter");
    HttpServletRequest req = (HttpServletRequest) request;

    // make sure the request has a correct character encoding
    // the enc-wrapper ensures some methods return correct strings too
    try {
      req = new EncRequestWrapper(req, req.getCharacterEncoding());
    } catch (UnsupportedEncodingException e) {
      Logger.log(Logger.FATAL, "InitFilter: unsupported encoding '" + req.getCharacterEncoding() + "'", e);
    }

    chain.doFilter(req, response);
  }
}