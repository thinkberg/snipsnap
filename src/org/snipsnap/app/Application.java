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
package com.neotis.app;

import com.neotis.user.User;

import javax.servlet.http.HttpSession;

/**
 * The application object contains information about current users and other
 * session specific information.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class Application {
  private User user;

  private static ThreadLocal instance = new ThreadLocal() {
    protected synchronized Object initValue() {
      System.out.println("Reading init value.");
      return new Application();
    }
  };

   public static Application get() {
    return (Application) instance.get();
  }

  public static void set(Application application) {
    instance.set(application);
  }

  public static Application getInstance(HttpSession session) {
    if (session != null) {
      Application application = (Application) session.getAttribute("app");
      if (null == application) {
        application = (Application) instance.get();
        // Workaround, because initValue doesn't work
        if (null == application) {
          application = new Application();
        }
      }
      instance.set(application);
      return application;
    }
    return null;
  }

  public User getUser() {
     return user;
   }

  public void setUser(User user) {
    this.user = user;
    return;
  }
}
