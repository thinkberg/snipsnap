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
/*
 * Macro that show last logout time from user
 *
 * @author stephan
 * @version $Id$
 */

package org.snipsnap.render.macro;

import org.snipsnap.snip.Modified;
import org.snipsnap.render.macro.parameter.MacroParameter;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import java.io.IOException;
import java.io.Writer;

public class LastVisitMacro extends Macro {
  private String[] paramDescription =
     {"1: login name"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "last-visit";
  }

  public String getDescription() {
    return "Show the last login of the user, which usually is the last logout time.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() == 1) {
      User user = UserManager.getInstance().load(params.get("0"));
      writer.write("<b>Last visit was:</b> ");
      writer.write(Modified.getNiceTime(user.getLastLogout()));
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
