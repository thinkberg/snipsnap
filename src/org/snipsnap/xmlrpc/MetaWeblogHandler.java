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

/**
 * Handles XML-RPC calls for the MetaWeblog API
 * http://www.xmlrpc.com/metaWeblogApi
 *
 * Some design ideas taken from Blojsom
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class MetaWeblogHandler extends XmlRpcSupport {
  public static final String API_PREFIX = "metaWeblog";

  public String getName() {
    return API_PREFIX;
  }

  /**
   metaWeblog.newPost (blogid, username, password, struct, publish) returns string
   metaWeblog.editPost (postid, username, password, struct, publish) returns true
   metaWeblog.getPost (postid, username, password) returns struct
   */
  public String newPost(String blogid, String username, String password, String struct, String publish) {
    return "";
  }
}
