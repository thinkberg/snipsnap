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
package com.neotis.config;

import com.neotis.app.Application;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.user.User;
import com.neotis.user.UserManager;

/**
 * Post an example comment.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class PostComment {
  public static void main(String[] args) {
    SnipSpace space = SnipSpace.getInstance();

    Application app = new Application();
    User user = UserManager.getInstance().load("funzel");
    app.setUser(user);

    Snip snip = space.load("about");
    snip.getComments().postComment("Hahaha, sowas __bloedes__ ist [das]", app);
  }
}
