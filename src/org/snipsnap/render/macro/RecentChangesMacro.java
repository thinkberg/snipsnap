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

import org.radeox.util.logging.Logger;
import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.SnipSpaceFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/*
 * Macro that displays recently changed snips
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class RecentChangesMacro extends ListOutputMacro {
  public String getName() {
    return "recent-changes";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.recentchanges.params");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.recentchanges.params").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    String type = "Vertical";
    boolean showSize = false;
    int length = 10;
    if (params.getLength() > 0) {
      try {
        length = Integer.parseInt(params.get("0"));
      } catch (NumberFormatException e) {
        Logger.warn("RecentChangesMacro: illegal parameter count='" + params.get("1") + "'");
      }
    }
    if (params.getLength() > 1) {
      type = params.get("1");
    }

    if (params.getLength() <= 3) {
      List changed = SnipSpaceFactory.getInstance().getChanged(length);
      output(writer, params.getSnipRenderContext().getSnip(),
             ResourceManager.getString("i18n.messages", "macro.recentchanges.title"),
             changed,
             ResourceManager.getString("i18n.messages", "macro.recentchanges.nochanges"),
             type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
