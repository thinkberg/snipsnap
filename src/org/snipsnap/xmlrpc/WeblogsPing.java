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

package org.snipsnap.xmlrpc;

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;
import org.snipsnap.xmlrpc.ping.ExtendedPingHandler;
import org.snipsnap.xmlrpc.ping.PingHandler;
import org.snipsnap.xmlrpc.ping.RssPingHandler;
import org.snipsnap.xmlrpc.ping.SimplePingHandler;
import org.radeox.util.logging.Logger;
import org.radeox.util.Encoder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileInputStream;

/**
 * Pings weblogs.com
 *
 * @author stephan
 * @version $Id$
 */

public class WeblogsPing extends Thread {
  private AppConfiguration config;
  private Snip weblog;
  private static List handlers;

  public WeblogsPing(AppConfiguration configuration, Snip weblog) {
    this.config = configuration;
    this.weblog = weblog;

    if (null == handlers) {
      handlers = new ArrayList();
      boolean fileNotFound = false;
      try {
        BufferedReader br = new BufferedReader(
            new InputStreamReader(
                new FileInputStream(getFileName())));
        addHandler(br);
      } catch (IOException e) {
        Logger.warn("Unable to read " + getFileName(), e);
        fileNotFound = true;
      }

      if (fileNotFound) {
        BufferedReader br = null;
        try {
          br = new BufferedReader(
              new InputStreamReader(
                  WeblogsPing.class.getResourceAsStream(getFileName())));
          addHandler(br);
        } catch (Exception e) {
          Logger.warn("Unable to read " + getFileName() + " from jar", e);
        }
      }
    }
  }

  private String getFileName() {
    return "conf/weblogsping.txt";
  }

  public void addHandler(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      if (!line.startsWith("#")) {
        int index = line.indexOf(" ");
        String type = line.substring(0, index);
        try {
          PingHandler handler = (PingHandler) Class.forName(type).newInstance();
          handler.setPingUrl(line.substring(index + 1));
          handlers.add(handler);
        } catch (Exception e) {
          Logger.warn("WeblogsPing: Unable to add handler for: " + line, e);
        }
      }
    }
  }

  public void run() {
    if (config.allow(AppConfiguration.PERM_WEBLOGS_PING) && handlers.size() > 0) {
      Iterator iterator = handlers.iterator();
      while (iterator.hasNext()) {
        PingHandler handler = (PingHandler) iterator.next();
        handler.ping(weblog);
      }
    }
  }

  public static void ping(Snip weblog) {
    new WeblogsPing(Application.get().getConfiguration(), weblog).start();
  }
}
