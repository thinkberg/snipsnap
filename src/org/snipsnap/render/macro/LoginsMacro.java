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

import org.snipsnap.app.Application;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.render.context.SnipRenderContext;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.ResourceBundle;

/*
 * Macro that displays a list of currently logged on users.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class LoginsMacro extends ListOutputMacro {
  private String[] paramDescription =
     {"?1: Lister to render users"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "logins";
  }

  public String getDescription() {
    return "Displays all currently logged in users and guests.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    ResourceBundle bundle = (ResourceBundle) params.getContext().get(SnipRenderContext.LANGUAGE_BUNDLE);

    String type = "Vertical";
    boolean showSize = true;
    if (params.getLength() > 0) {
        type = params.get("0");
    }
    if (params.getLength() <= 1) {
      List users = Application.getCurrentUsers();
      users.addAll(Application.getCurrentNonUsers());

      output(writer, bundle.getString("Macro.Logins.Users"), users, "", type, showSize);
      int guests = Application.getGuestCount();
      if (guests > 0) {
        writer.write("... and ");
        writer.write("" + guests);
        writer.write(" Guest");
        if (guests > 1) {
          writer.write("s");
        }
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
