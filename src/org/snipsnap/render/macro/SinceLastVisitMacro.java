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

package org.snipsnap.render.macro;

import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;
import org.snipsnap.user.UserManagerFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;


/*
 * Macro that displays all changed Snips
 * since the users last login
 *
 * @author stephan
 * @version $Id$
 */


public class SinceLastVisitMacro extends ListOutputMacro {
  private String[] paramDescription =
     {"1: login name", "?2: Lister to render snips"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "since-last-visit";
  }

  public String getDescription() {
    return "Show all snips that have been changed since the last visit of the user.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {
    String type = null;
    boolean showSize = true;
    String userName = null;
    if (params.getLength() > 0) {
      userName = params.get("0");
    }
    if (params.getLength() > 1) {
      type = params.get("1");
    }

    if (params.getLength() > 0) {
      User user = UserManagerFactory.getInstance().load(userName);
      // Logger.debug("Hashcode lastVisit=" + ((Object) user).hashCode());
      // Logger.debug("SinceLastVisit: " + user.getLastLogout());
      Collection c = SnipSpaceFactory.getInstance().getSince(user.getLastLogout());
      output(writer, "changed snips since last visit", c, "no recent changes.", type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
