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

import org.radeox.macro.Macro;
import org.radeox.macro.parameter.MacroParameter;
import org.snipsnap.app.Application;

import java.io.IOException;
import java.io.Writer;

/*
 * Macro that displays the running SnipSnap version
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class VersionMacro extends Macro {
  private String version;

  private String[] paramDescription =
     {"none"};

  public String[] getParamDescription() {
    return paramDescription;
  }

  public VersionMacro() {
    version = Application.get().getConfiguration().getVersion();
  }

  public String getDescription() {
    return "Displays the SnipSnap version.";
  }

  public String getName() {
    return "version";
  }

  public void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException {
    if (params.getLength() == 0) {
       writer.write(version);
    } else {
      throw new IllegalArgumentException("Number of arguments does not match");
    }
  }
}
