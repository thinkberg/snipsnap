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

import org.radeox.filter.FilterPipe;
import org.radeox.filter.ListFilter;
import org.radeox.filter.context.FilterContext;
import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import org.snipsnap.render.filter.context.SnipFilterContext;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.snip.SnipSpaceFactory;
import org.snipsnap.config.ServerConfiguration;
import snipsnap.api.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;

import java.io.IOException;
import java.io.FileInputStream;
import java.util.StringTokenizer;

/**
 * Export a Presentation from the SnipSpace
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Presentation {
  public static void main(String[] args) {
    String name = args[1];

    Logger.debug("Loading server config.");
    ServerConfiguration serverConfig = null;
    try {
      serverConfig = new ServerConfiguration("./conf/server.conf");
    } catch (IOException e) {
      Logger.warn("Unable to load server config: " + e);
      System.exit(-1);
    }

    Application app = snipsnap.api.app.Application.get();

    Logger.debug("Loading application config.");
    Configuration config = null;
    try {
      config = ConfigurationProxy.newInstance();
      config.load(new FileInputStream(serverConfig.getProperty(ServerConfiguration.WEBAPP_ROOT) + args[0] + "/application.conf"));
    } catch (IOException e) {
      Logger.warn("Unable to load application config: " + e);
      System.exit(-1);
    }
    app.setConfiguration(config);

    snipsnap.api.snip.SnipSpace space = snipsnap.api.snip.SnipSpaceFactory.getInstance();
    FilterPipe fp = new FilterPipe();
    //fp.addFilter(new EscapeFilter());
    //fp.addFilter(new ParamFilter());
    //fp.addFilter(new MacroFilter());
    //fp.addFilter(new MacroFilter());
    //fp.addFilter(new HeadingFilter());
    fp.addFilter(new ListFilter());
    //fp.addFilter(new NewlineFilter());
    //fp.addFilter(new ParagraphFilter());
    //fp.addFilter(new LineFilter());
    //fp.addFilter(new BoldFilter());
    //fp.addFilter(new ItalicFilter());
    //fp.addFilter(new UrlFilter());
    //fp.addFilter(new LinkTestFilter(SnipSpace.newInstance()));
    //fp.addFilter(new MarkFilter());
    //fp.addFilter(new KeyFilter());

    snipsnap.api.snip.Snip snip = space.load(name);
    FilterContext context = new SnipFilterContext(snip);

    StringTokenizer st = new StringTokenizer(fp.filter(snip.getContent(), context), "\n");
    boolean first = true;
    while (st.hasMoreTokens()) {
      String line = st.nextToken();
      if (line.startsWith("1 ")) {
        String title = line.substring(2);
        if (!first) {
          System.out.println("</slide>\n");
        } else {
          first = false;
        }
        System.out.println("<slide title=\"" + title + "\">");
      } else {
        System.out.println(line);
      }
    }
    System.out.println("</slide>");

    System.exit(0);
  }
}
