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
import org.snipsnap.snip.SnipLink;
import org.radeox.util.i18n.ResourceManager;

import java.io.IOException;
import java.io.Writer;
import java.util.ResourceBundle;

/*
 * Macro that creates (HTML) input fields with forms.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class FieldMacro extends SnipMacro {
  public String getName() {
    return "field";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.field.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.field.params").split(";");
  }

  /**
   * {field:id|value|target|button}
   */
  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    if (params.getLength() > 0) {
      writer.write("<form class=\"form\" action=\"");
      if (params.getLength() >= 3) {
        SnipLink.appendUrl(writer, params.get("2"));
      } else {
        SnipLink.appendUrl(writer, params.getSnipRenderContext().getSnip().getName());
      }
      writer.write("\" method=\"get\">");
      writer.write("<input size=\"18\" name=\"");
      writer.write(params.get("0"));
      writer.write("\"");
      if (params.getLength() >= 2) {
        writer.write(" value=\"");
        writer.write(params.get("1"));
        writer.write("\"");
      }
      writer.write("/>");
      if (params.getLength() >= 4) {
        writer.write(" <input type=\"submit\" name=\"submit\" value=\"");
        writer.write(params.get("3"));
        writer.write("\"/>");
      }
      writer.write("</form>");
      return;
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}