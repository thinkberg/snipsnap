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
import snipsnap.api.snip.SnipSpaceFactory;
import org.radeox.util.i18n.ResourceManager;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.text.MessageFormat;

/*
 * Macro that displays all Snips by user
 *
 * @author stephan
 * @version $Id$
 */

public class UserSnipMacro extends ListOutputMacro {
  public String getName() {
    return "snips-by-user";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.snipsbyuser.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.snipsbyuser.params").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {
    String type = null;
    boolean showSize = true;
    if (params.getLength() > 1) {
        type = params.get("1");
    }

    if (params.getLength() > 0) {
      Collection c = snipsnap.api.snip.SnipSpaceFactory.getInstance().getByUser(params.get("0"));
      MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.snipsbyuser.title"),
                                           ResourceManager.getLocale("i18n.messages"));
      output(writer, params.getSnipRenderContext().getSnip(),
             mf.format(new Object[] { params.get("0") }),
             c, ResourceManager.getString("i18n.messages", "macro.snipsbyuser.nosnips"),
             type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
