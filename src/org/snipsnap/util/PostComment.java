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
package org.snipsnap.util;

import snipsnap.api.app.Application;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.snip.SnipSpaceFactory;
import snipsnap.api.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

/**
 * Post an example comment.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class PostComment {
  public static void main(String[] args) {
    SnipSpace space = SnipSpaceFactory.getInstance();

    Application app = Application.get();
    snipsnap.api.user.User user = UserManagerFactory.getInstance().load("funzel");
    app.setUser(user);

    snipsnap.api.snip.Snip snip = space.load("about");
    snip.getComments().postComment("Hahaha, sowas __bloedes__ ist [das]");
  }
}
