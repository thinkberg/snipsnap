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
package org.snipsnap.snip.filter.macro;

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class ImageMacro extends Macro {
  AppConfiguration config;

  public String getName() {
    return "image";
  }

  public ImageMacro() {
    config = Application.get().getConfiguration();
  }

  public void execute(Writer writer, MacroParameter params)
    throws IllegalArgumentException, IOException {

    StringBuffer buffer = new StringBuffer();
    Snip snip = params.getSnip();
    if (params.getLength() > 0) {
      String img = params.get("img");
      String alt = null, ext = null, align = null;
      boolean qualifiedParams = img != null;
      if (qualifiedParams) {
        alt = params.get("alt");
        ext = params.get("ext");
        align = params.get("align");
      } else {
        img = params.get(0);
        alt = params.get(1);
        ext = params.get(2);
        align = params.get(3);
      }

      if (img.startsWith("http://")) {
        if (config.allowExternalImages()) {
          SnipLink.appendExternalImage(buffer, img, align);
        }
      } else {
        SnipLink.appendImage(buffer, "image-" + snip.getName() + "-" + img, alt, ext, align);
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
    String link = params.get("link");
    if (link != null) {
      buffer.insert(0, "<a href=\"" + link + "\">");
      buffer.append("</a>");
    }
    writer.write(buffer.toString());
    return;
  }
}
