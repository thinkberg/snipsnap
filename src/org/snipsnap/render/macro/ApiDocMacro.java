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


import org.snipsnap.render.macro.api.ApiDoc;
import org.snipsnap.render.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;

/*
 * Lists all known API documentation repositorys and
 * mappings
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class ApiDocMacro extends Macro {
  private String[] paramDescription = { };

  public String[] getParamDescription() {
    return paramDescription;
  }

  public String getName() {
    return "api-docs";
  }

  public String getDescription() {
    return "Displays a list of known online API documentations and mappings.";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    ApiDoc apiDoc = ApiDoc.getInstance();
    apiDoc.appendTo(writer);
    return;
  }
}
