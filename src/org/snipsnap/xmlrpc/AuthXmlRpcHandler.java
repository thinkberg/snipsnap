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

import org.apache.xmlrpc.AuthenticatedXmlRpcHandler;
import org.apache.xmlrpc.XmlRpcException;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.Vector;

/**
 * Authenticated XML-RPC Handler that overcomes the limits of the original
 * implementation by handling authentication and calling methods in a selected
 * target or a sub class.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public abstract class AuthXmlRpcHandler implements AuthenticatedXmlRpcHandler {

  protected Object target = null;

  public AuthXmlRpcHandler() {
    target = this;
  }

  public AuthXmlRpcHandler(Object target) {
    this.target = target;
  }

  protected abstract boolean authenticate(String user, String password);

  public Object execute(String method, Vector vector, String user, String password) throws Exception {
    //System.out.println("execute("+method+","+vector+")");
    if (authenticate(user, password)) {
      return execute(method, vector);
    } else {
      throw new XmlRpcException(0, "Username or password does not match");
    }
  }

  public Object execute(String methodName, Vector args) throws Exception {
    Class argClasses[] = null;
    if(args.size() > 0) {
      argClasses = new Class[args.size()];
      for (int argNum = 0; argNum < args.size(); argNum++) {
        argClasses[argNum] = args.get(argNum).getClass();
      }
    }
    if(methodName.indexOf('.') != -1) {
      methodName = methodName.substring(methodName.indexOf(".")+1);
    }

    Method method = target.getClass().getMethod(methodName, argClasses);
    try {
      return method.invoke(target, (args.size() > 0 ? args.toArray() : null));
    } catch (InvocationTargetException e) {
      throw (Exception)e.getTargetException();
    }
  }
}
