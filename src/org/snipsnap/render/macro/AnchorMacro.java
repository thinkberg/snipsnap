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
import org.snipsnap.render.context.SnipRenderContext;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.radeox.util.i18n.ResourceManager;

import java.io.IOException;
import java.io.Writer;
import java.text.MessageFormat;
import java.util.ResourceBundle;

/*
 * Places a HTML anchor tag into the snip
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class AnchorMacro extends SnipMacro {

  public AnchorMacro() {
  }

  public String getName() {
    return "anchor";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.anchor.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.anchor.params").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() == 1) {
      String anchor = params.get("0").replace(' ', '_');
      writer.write("<a name=\"");
      writer.write(anchor);
      writer.write("\"/>");
      writer.write("<a href=\"");
      Snip snip = params.getSnipRenderContext().getSnip();
      if (null != snip) {
        SnipLink.appendUrl(writer, snip.getName(), anchor);
      } else {
        SnipLink.appendUrl(writer, "", anchor);
      }
      writer.write("\" title=\"");
      MessageFormat mf = new MessageFormat(ResourceManager.getString("i18n.messages", "macro.anchor.permalink"));
      writer.write(mf.format(new Object[] { anchor }));
      writer.write("\">");
      SnipLink.appendImage(writer, "Icon-Permalink", "");
      writer.write("</a>");
    }
    return;
  }
}
