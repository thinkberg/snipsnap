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
 * Macro that displays all Snips by user
 *
 * @author stephan
 * @version $Id$
 */

package org.snipsnap.render.macro;

import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.SnipSpaceFactory;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

public class UserSnipMacro extends ListOutputMacro {
  private String[] paramDescription =
     {"1: login name"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "snips-by-user";
  }

  public String getDescription() {
    return "Show all snips created by a specified user.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {
    String type = null;
    boolean showSize = true;
    if (params.getLength() > 1) {
        type = params.get("1");
    }

    if (params.getLength() > 0) {
      Collection c = SnipSpaceFactory.getInstance().getByUser(params.get("0"));
      output(writer, "this user's snips:", c, "none written yet.", type, showSize);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
