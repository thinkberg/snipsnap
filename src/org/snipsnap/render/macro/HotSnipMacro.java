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

import org.radeox.api.engine.context.RenderContext;
import org.radeox.macro.Macro;
import org.radeox.macro.BaseMacro;
import org.radeox.macro.parameter.MacroParameter;
import org.radeox.util.logging.Logger;
import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.render.context.SnipRenderContext;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

/*
 * Macro that displays the hottest snips, currently the most viewed.
 *
 * @author stephan
 * @version $Id$
 */

public class HotSnipMacro extends BaseMacro {
  public HotSnipMacro() {
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.hotsnip.params").split(";");
  }

  public String getName() {
    return "snips-by-hotness";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.hotsnip.description");
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {

    RenderContext context = params.getContext();
    if (context instanceof SnipRenderContext) {
      SnipSpace space = ((SnipRenderContext) context).getSpace();

      int length = 10;
      boolean showSize = false;
      if (params.getLength() > 0) {
        try {
          length = Integer.parseInt(params.get("0"));
        } catch (NumberFormatException e) {
          Logger.warn("HotnessMacro: illegal parameter count='" + params.get("0") + "'");
        }
      }

      if (params.getLength() <= 1) {
        Collection c = space.getHot(length);
        Iterator iterator = c.iterator();
        writer.write("<div class=\"list\"><div class=\"list-title\">");
        ResourceManager.getString("i18n.messages", "macro.hotsnip.viewed");
        if (showSize) {
          writer.write(" (");
          writer.write("" + length);
          writer.write(")");
        }
        writer.write("</div><ul>");
        while (iterator.hasNext()) {
          Snip hotSnip = (Snip) iterator.next();
          writer.write("<li><span class=\"count\">");
          writer.write("" + hotSnip.getViewCount());
          writer.write("</span>");
          writer.write("<span class=\"content\">");
          SnipLink.appendLink(writer, hotSnip);
          writer.write("</span></li>");
        }
        writer.write("</ul></div>");
      } else {
        throw new IllegalArgumentException("Number of arguments does not match");
      }
    }
  }
}