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

import org.apache.xmlrpc.XmlRpcClient;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;
import org.snipsnap.xmlrpc.ping.PingHandler;
import org.snipsnap.xmlrpc.ping.SimplePingHandler;
import org.snipsnap.xmlrpc.ping.RssPingHandler;
import org.snipsnap.xmlrpc.ping.ExtendedPingHandler;

import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Pings weblogs.com
 *
 * @author stephan
 * @version $Id$
 */

public class WeblogsPing extends Thread {
  private AppConfiguration config;
  private Snip weblog;
  private List handlers;

  public WeblogsPing(AppConfiguration configuration, Snip weblog) {
    this.config = configuration;
    this.weblog = weblog;
    handlers = new ArrayList();
    handlers.add(new SimplePingHandler("http://rpc.weblogs.com/RPC2"));
    handlers.add(new SimplePingHandler("http://www.snipsnap.org/RPC2"));
    handlers.add(new RssPingHandler("http://rssrpc.weblogs.com/RPC2"));
    handlers.add(new ExtendedPingHandler("http://ping.blo.gs/"));
  }

  public void run() {
    if (config.allow(AppConfiguration.PERM_WEBLOGS_PING) && handlers.size()>0) {
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
