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
 * Macro that replaces external links
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.filter.macro.api.ApiDoc;
import org.snipsnap.snip.filter.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;


public class ApiMacro extends Macro {
  private String[] paramDescription =
     {"1: class name, e.g. java.lang.Object or java.lang.Object@Java131",
      "?2: mode, e.g. Java12, Ruby, defaults to Java"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "api";
  }

  public String getDescription() {
    return "Generates links to Java or Ruby API documentation.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    String mode;
    String klass;

    if (params.getLength() == 1) {
      klass = params.get("0");
      int index = klass.indexOf("@");
      if (index > 0) {
        mode = klass.substring(index + 1);
        klass = klass.substring(0, index);
      } else {
        mode = "java";
      }
    } else if (params.getLength() == 2) {
      mode = params.get("1").toLowerCase();
      klass = params.get("0");
    } else {
      throw new IllegalArgumentException("api macro needs one or two paramaters");
    }

    ApiDoc.getInstance().expand(writer, klass, mode);
    return;
  }
}
