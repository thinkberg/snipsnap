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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;

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
    if(params.getLength() > 0) {
      if (params.get("-").startsWith("http://")) {
        if (config.allowExternalImages()) {
          SnipLink.appendExternalImage(buffer, params.get("0"), params.getLength() > 1 ? params.get("1") : null);
        }
      } else if(params.getLength() == 3) {
        SnipLink.appendImage(buffer, snip.getName()+"-image-"+params.get("0"), params.get("1"), null, params.get("2"));
      } else if (params.getLength() == 2) {
        SnipLink.appendImage(buffer, snip.getName()+"-image-"+params.get("0"), params.get("1"));
      } else {
        SnipLink.appendImage(buffer, snip.getName()+"-image-"+params.get("0"), "default");
      }
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
    writer.write(buffer.toString());
    return;
  }
}
