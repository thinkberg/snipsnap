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

import org.radeox.util.Encoder;
import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.net.RenderServlet;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that renders graphs.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class GraphMacro extends SnipMacro {
  public GraphMacro() {
  }

  public String getName() {
    return "graph";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.graph.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.graph.params").split(";");
  }

  public void execute(Writer writer, SnipMacroParameter params)
          throws IllegalArgumentException, IOException {
    Snip snip = params.getSnipRenderContext().getSnip();
    String name = snip.getName();
    String handler = params.get("handler", 0);
    writer.write("<img src=\"exec/render?name=");
    writer.write(SnipLink.encode(name));
    writer.write("&amp;handler=");
    writer.write(handler);

    String content = Encoder.unescape(params.getContent());
    String id = "" + content.hashCode();
    RenderServlet.addContent(id, content);
    writer.write("&amp;id=" + id);
    writer.write("\"/>");
  }
}
