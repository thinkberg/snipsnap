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

package org.snipsnap.snip.filter.macro;

/*
 * Macro that displays all cahnged Snips
 * since the users last login
 *
 * @author stephan
 * @version $Id$
 */


import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.Modified;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.User;
import org.snipsnap.serialization.StringBufferWriter;

import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.io.IOException;
import java.io.Writer;

public class SinceLastVisitMacro extends ListoutputMacro {
  public String getName() {
    return "since-last-visit";
  }

  public void execute(Writer writer, String[] params, String content, Snip snip)
      throws IllegalArgumentException, IOException {
    String type = null;
    boolean showSize = true;
    String userName = null;
    if(params != null) {
      if(params.length > 0) {
        userName = params[0];
      }
      if(params.length > 1) {
        type = params[1];
      }
    }

    if (params.length > 0) {
      User user = UserManager.getInstance().load(params[0]);
      System.err.println("Hashcode lastVisit="+((Object) user).hashCode());
      System.err.println("SinceLastVisit: "+user.getLastLogout());
      Collection c = space.getSince(user.getLastLogout());
      output(writer, "changed snips since last visit", c, "no recent changes.", type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
