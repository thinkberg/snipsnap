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

import java.util.Vector;

/**
 * Pings weblogs.com
 *
 * @author stephan
 * @version $Id$
 */

public class WeblogsPing extends Thread {
  AppConfiguration config;
  Snip weblog;

  public WeblogsPing(AppConfiguration configuration, Snip weblog) {
    this.config = configuration;
    this.weblog = weblog;
  }

  public void run() {
    try {
      if (config.allow(AppConfiguration.PERM_WEBLOGS_PING)) {
        // Ping weblogs.com
        XmlRpcClient weblogs_com = new XmlRpcClient("http://rpc.weblogs.com/RPC2");
        Vector params = new Vector();
        // Name of the weblog
        params.addElement(config.getName());
        // Url/CheckUrl of the weblog
        params.addElement(config.getSnipUrl(weblog.getName()));
        Object result = weblogs_com.execute("weblogUpdates.ping", params);
        //System.err.println("weblogs.ping received: " + result);

        // Ping blog.gs
        XmlRpcClient blo_gs = new XmlRpcClient("http://http://ping.blo.gs/");
        params.clear();
        // Name of the weblog
        params.addElement(config.getName());
        // Url of the weblog
        params.addElement(config.getUrl());
        // Url to check for news
        params.addElement(config.getSnipUrl(weblog.getName()));
        // RSS feed
        params.addElement(config.getUrl() + "/exec/rss");
        result = weblogs_com.execute("weblogUpdates.extendedPing", params);
        //System.err.println("weblogs.ping received: " + result);

        XmlRpcClient home = new XmlRpcClient("http://www.snipsnap.org/RPC2");
        params.clear();
        // Name of the weblog
        params.addElement(config.getName());
        // Url/CheckUrl of the weblog
        params.addElement(config.getSnipUrl(weblog.getName()));
        result = home.execute("weblogUpdates.ping", params);
        //System.err.println("xmlrpc result="+result);
      }
    } catch (Exception e) {
      System.err.println("Unable to ping weblogs.com " + e);
    }
  }

  public static void ping(Snip weblog) {
    new WeblogsPing(Application.get().getConfiguration(), weblog).start();
  }
}
