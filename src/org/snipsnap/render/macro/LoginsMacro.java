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

import org.radeox.util.i18n.ResourceManager;
import snipsnap.api.app.Application;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.List;

/*
 * Macro that displays a list of currently logged on users.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class LoginsMacro extends ListOutputMacro {
  public String getName() {
    return "logins";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.logins.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.logins.description").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
    throws IllegalArgumentException, IOException {

    String type = "Vertical";
    boolean showSize = true;
    if (params.getLength() > 0) {
      type = params.get("0");
    }
    if (params.getLength() <= 1) {
      List users = Application.getCurrentUsers();
      users.addAll(Application.getCurrentNonUsers());

      output(writer, params.getSnipRenderContext().getSnip(),
             ResourceManager.getString("i18n.messages", "macro.logins.users"), users, "", type, showSize);
      int guests = Application.getGuestCount();
      if (guests > 0) {
        MessageFormat formatter = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.logins.guests"),
                                                    ResourceManager.getLocale("i18n.messages"));
        writer.write(formatter.format(new Object[]{new Integer(guests)}));
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
