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

import org.radeox.engine.ImageRenderEngine;
import org.radeox.engine.RenderEngine;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays images
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class ImageMacro extends SnipMacro {
  AppConfiguration config;

  public String getName() {
    return "image";
  }

  public String getDescription() {
    return "Displays an image file.";
  }

  public ImageMacro() {
    config = Application.get().getConfiguration();
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {

    RenderEngine engine = params.getContext().getRenderEngine();

    if (engine instanceof ImageRenderEngine) {
      ImageRenderEngine imageEngine = (ImageRenderEngine) engine;

      StringBuffer buffer = new StringBuffer();
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
            appendExternalImage(buffer, img, align);
          }
        } else {
          // Does the name contain an extension?
          int dotIndex = img.lastIndexOf('.');
          if (-1 != dotIndex) {
            ext = img.substring(dotIndex + 1);
            img = img.substring(0, dotIndex);
          }

          Snip snip = params.getSnip();
          String imageName = "image-" + snip.getName() + "-" + img;
          if ("svg".equals(ext)) {
            // SVG cannot be used with <image>
            buffer.append("<object data=\"");
            buffer.append("../images/");
            buffer.append(imageName);
            buffer.append(".");
            buffer.append(ext);
            buffer.append("\" type=\"image/svg+xml\" width=\"400\" height=\"400\"></object>");
          } else {
            SnipLink.appendImage(buffer, imageName, alt, ext, align);
          }
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

  public static StringBuffer appendExternalImage(StringBuffer buffer, String url, String position) {
    buffer.append("<img src=\"");
    buffer.append(url);
    buffer.append("\" ");
    if (position != null) {
      buffer.append("class=\"");
      buffer.append(position);
      buffer.append("\" ");
    }
    buffer.append("border=\"0\"/>");
    return buffer;
  }

}
