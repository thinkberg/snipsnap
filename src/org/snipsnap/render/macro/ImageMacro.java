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

import org.radeox.api.engine.ImageRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.util.Encoder;
import org.radeox.util.i18n.ResourceManager;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpaceFactory;

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
  Configuration config;

  public String getName() {
    return "image";
  }

  public String getDescription() {
    return ResourceManager.getString("i18n.messages", "macro.image.description");
  }

  public String[] getParamDescription() {
    return ResourceManager.getString("i18n.messages", "macro.image.params").split(";");
  }

  public ImageMacro() {
    config = Application.get().getConfiguration();
  }

  public void execute(Writer writer, SnipMacroParameter params)
          throws IllegalArgumentException, IOException {

    RenderEngine engine = params.getContext().getRenderEngine();

    if (engine instanceof ImageRenderEngine) {
      ImageRenderEngine imageEngine = (ImageRenderEngine) engine;

      if (params.getLength() > 0) {
        String img = params.get("img");
        String alt = null, ext = null, align = null, target = null;
        boolean qualifiedParams = img != null;
        if (qualifiedParams) {
          alt = params.get("alt");
          ext = params.get("ext");
          align = params.get("align");
          target = params.get("target");
        } else {
          img = params.get(0);
          alt = params.get(1);
          ext = params.get(2);
          align = params.get(3);
          target = params.get(4);
        }

        String link = params.get("link");
        if (link != null) {
          writer.write("<a href=\"" + Encoder.escape(link) + "\"");
          if (target != null) {
            writer.write("target=\"" + Encoder.escape(target) + "\"");
          }
          writer.write(">");
        }

        String imageName = img;

        if (imageName.startsWith("http://") || imageName.startsWith("https://")) {
          if (config.allow(Configuration.APP_PERM_EXTERNALIMAGES)) {
            appendExternalImage(writer, imageName, align);
          }
        } else {
          // Does the name contain an extension?
          int dotIndex = imageName.lastIndexOf('.');
          if (-1 != dotIndex) {
            ext = imageName.substring(dotIndex + 1);
            imageName = imageName.substring(0, dotIndex);
          }

          Snip snip = params.getSnipRenderContext().getSnip();
          int slashIndex = imageName.lastIndexOf('/');
          if (-1 != slashIndex) {
            String snipName = imageName.substring(0, slashIndex);
            snip = SnipSpaceFactory.getInstance().load(snipName);
            imageName = imageName.substring(slashIndex + 1);
          }

          if ("svg".equals(ext)) {
            // SVG cannot be used with <image>
            writer.write("<object data=\"");
            writer.write(snip.getNameEncoded() + "/" + imageName);
            writer.write(".");
            writer.write(ext);
            writer.write("\" type=\"image/svg+xml\" width=\"400\" height=\"400\"></object>");
          } else {
            SnipLink.appendImage(writer, snip, imageName, alt, ext, align);
          }
        }

        if (link != null) {
          writer.write("</a>");
        }
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
    return;
  }


  public static Writer appendExternalImage(Writer writer, String url, String position) throws IOException {
    writer.write("<img src=\"");
    writer.write(url);
    writer.write("\" ");
    if (position != null) {
      writer.write("class=\"");
      writer.write(position);
      writer.write("\" ");
    }
    writer.write("border=\"0\"/>");
    return writer;
  }
}
