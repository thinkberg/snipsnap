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

package org.snipsnap.xmlrpc.ping;

import org.apache.xmlrpc.XmlRpcClient;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;

import java.util.Vector;

/**
 * Iplementation of the extended weblogs ping, e.g. at blo.gs
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class ExtendedPingHandler implements PingHandler {
  private String url;

  public ExtendedPingHandler(String url) {
    this.url = url;
  }

  public void ping(Snip weblog) {
    AppConfiguration config = Application.get().getConfiguration();
    Vector params = new Vector();
    try {
      // Ping blog.gs
      XmlRpcClient blo_gs = new XmlRpcClient(url);
      params.clear();
      // Name of the weblog
      params.addElement(config.getName());
      // Url of the weblog
      params.addElement(config.getUrl());
      // Url to check for news
      params.addElement(config.getSnipUrl(weblog.getName()));
      // RSS feed
      params.addElement(config.getUrl() + "/exec/rss");
      Object result = blo_gs.execute("weblogUpdates.extendedPing", params);
      //System.err.println("weblogs.ping received: " + result);
    } catch (Exception e) {
      System.err.println("Unable to ping " + url + " " + e);
    }
  }
}
