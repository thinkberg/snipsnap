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
package org.snipsnap.snip;


/**
 * Static class to create a home-page snip.
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class HomePage {
  /**
   * Create a snip as a homepage for the user. Generates a new
   * one or appends the snip-by-user to existing one.
   *
   * @param login User for the homepage
   */
  public static void create(String login) {
    Snip snip = null;
    String hp = "\n\n ~~Describe here who you are!~~\n\n__Configure this box!__\n1. Login\n" +
        "1. Click edit to change this snip\n\n" +
        "{snips-by-user:" + login + "}";
    SnipSpace space = SnipSpaceFactory.getInstance();
    if (space.exists(login)) {
      snip = space.load(login);
      snip.setContent(snip.getContent() + hp);
      space.store(snip);
    } else {
      snip = space.create(login, hp);
    }
//    snip.addPermission(Permissions.EDIT_SNIP, Security.OWNER);
//    space.store(snip);
    return;
  }
}