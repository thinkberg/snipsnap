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
package org.snipsnap.net;

import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.snip.Blog;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.semanticweb.rss.*;
import org.snipsnap.feeder.Feeder;
import org.snipsnap.feeder.FeederRepository;
import org.snipsnap.container.Components;
import org.snipsnap.render.PlainTextRenderEngine;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Load a snip for output as RSS
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class RssServlet extends HttpServlet {
  private Configuration config;

  public void init(ServletConfig servletConfig) throws ServletException {
    config = ConfigurationProxy.getInstance();
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws IOException, ServletException {

    SnipSpace space = SnipSpaceFactory.getInstance();

    String eTag = request.getHeader("If-None-Match");
    if (null != eTag && eTag.equals(space.getETag())) {
      response.setHeader("ETag", space.getETag());
      response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
      return;
    } else {
      String version = request.getParameter("version");
      String type = request.getParameter("type");
      String sourceSnipName = request.getParameter("snip");

      if (null == sourceSnipName) {
        sourceSnipName = config.getStartSnip();
      }

      Snip sourceSnip = space.load(sourceSnipName);

      Object object =  Components.getComponent(PlainTextRenderEngine.class);
      System.err.println("object = "+object.getClass());

      FeederRepository repository = (FeederRepository) Components.getComponent(FeederRepository.class);

      Feeder feeder = (Feeder) repository.get(type);

      System.out.println("Feeder repository: "+repository.getPlugins());
      if (null == feeder || "blog".equals(feeder.getName())) {
        if (sourceSnip.isWeblog()) {
          feeder = new BlogFeeder(sourceSnipName);
        } else {
          feeder = new BlogFeeder();
        }
      }

      Snip snip = feeder.getContextSnip();

      request.setAttribute("snip", snip);
      request.setAttribute("rsssnips", feeder.getFeed());
      request.setAttribute("space", space);
      request.setAttribute("config", config);

      request.setAttribute("url", config.getUrl("/space"));

      RequestDispatcher dispatcher;
      if ("1.0".equals(version)) {
        dispatcher = request.getRequestDispatcher("/rdf.jsp");
      } else if ("0.92".equals(version)) {
        dispatcher = request.getRequestDispatcher("/rss.jsp");
      } else if ("plain".equals(version)) {
        dispatcher = request.getRequestDispatcher("/plain.jsp");
      } else {
        dispatcher = request.getRequestDispatcher("/rss2.jsp");
      }

      dispatcher.forward(request, response);
    }
  }
}