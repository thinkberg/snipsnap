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
 * Simple implementation of the default weblogs.com ping
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SimplePingHandler implements PingHandler {
  private String url;

  public SimplePingHandler(String url) {
    this.url = url;
  }

  public void ping(Snip weblog) {
    AppConfiguration config = Application.get().getConfiguration();
    Vector params = new Vector();
    try {
      // Ping weblogs.com
      XmlRpcClient weblogs_com = new XmlRpcClient(url);
      params.clear();
      // Name of the weblog
      params.addElement(config.getName());
      // Url/CheckUrl of the weblog
      params.addElement(config.getSnipUrl(weblog.getName()));
      Object result = weblogs_com.execute("weblogUpdates.ping", params);
      //System.err.println("weblogs.ping received: " + result);
    } catch (Exception e) {
      System.err.println("Unable to ping " + url + " " + e);
    }

  }
}
