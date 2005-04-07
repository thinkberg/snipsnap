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

import org.radeox.util.logging.Logger;
import snipsnap.api.xmlrpc.*;
import snipsnap.api.xmlrpc.SnipSnapPing;


/**
 * Handles XML-RPC calls for the Weblogs Ping API
 * http://www.weblogs.com
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class WeblogsPingHandler extends XmlRpcSupport {
  public static final String API_PREFIX = "weblogUpdates";

  public String getName() {
    return API_PREFIX;
  }

  /**
   * Like the spec version, but added
   * the name and version of the weblog tool
   */
  public String versionPing(String weblogName, String weblogUrl, String toolName, String toolVersion) {
    Logger.debug("XML-RPC call to ping() from " + weblogName+" "+toolName+" "+toolVersion);
    SnipSnapPing.getInstance().addChangedWeblog(weblogName, weblogUrl);
    return "";
  }

  /**
   * From the spec:
   * weblogUpdates.ping()
   **/
  public String ping(String weblogName, String weblogUrl) {
    Logger.debug("XML-RPC call to ping() from " + weblogName);
    snipsnap.api.xmlrpc.SnipSnapPing.getInstance().addChangedWeblog(weblogName, weblogUrl);
    return "";
  }
}
