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

import org.snipsnap.render.filter.links.BackLinks;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays a list of the backlinks for the snip
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class BackLinkMacro extends ListOutputMacro {
  public String getName() {
    return "backlinks";
  }

  public String getDescription() {
    return "Renders a list of backlinks for the snip.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {
    int count = 10;
    if (params.getLength() == 1) {
      count = Integer.parseInt(params.get("0"));
    }
    // Dangerous!!
    BackLinks.appendTo(writer, params.getSnipRenderContext().getSnip().getAccess().getBackLinks(), count);
  }
}
