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

import org.apache.xmlrpc.XmlRpcException;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.container.Components;

/**
 * Utility base class for XML-RPC handlers.
 *
 * Constants were taken from Blojsom. This
 * should make understanding tyhe code easier.
 * (both ways).
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public abstract class XmlRpcSupport implements XmlRpcHandler {
   protected AuthenticationService authenticationService;

   public static final int    AUTHORIZATION_EXCEPTION = 0001;
   public static final String AUTHORIZATION_EXCEPTION_MSG = "Invalid Username and/or Password";

   public static final int    UNKNOWN_EXCEPTION = 1000;
   public static final String UNKNOWN_EXCEPTION_MSG = "An error occured processing your request";

   public static final int    UNSUPPORTED_EXCEPTION = 1001;
   public static final String UNSUPPORTED_EXCEPTION_MSG = "Unsupported method - Snipsnap does not support this XML-RPC concept";


  /**
   *  Utility method for XML-RPC handlers. Method authenticates
   *  an user, sets this user as the current user of
   *  the Application thread  and throws an XmlRpcException if
   *  user couldn't be authenticated
   *
   *  @param username Login name of the user received from XML-RPC call
   *  @param password Password credential received from XML-RPC call
   */

  protected User authenticate(String username, String password) throws XmlRpcException {
    User user = authenticationService.authenticate(username, password);
    if (user != null) {
      Application.get().setUser(user);
      return user;
    }
    Logger.warn("XML-RPC authenticate: invalid login for "+username);
    throw new XmlRpcException(AUTHORIZATION_EXCEPTION, AUTHORIZATION_EXCEPTION_MSG);
  }

}
