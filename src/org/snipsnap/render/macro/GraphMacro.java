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

import org.python.util.PythonInterpreter;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

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
    return "Render a graph like an organigram or mindmap.";
  }

  public void execute(Writer writer, SnipMacroParameter params)
      throws IllegalArgumentException, IOException {
      writer.write("<img src=\"");
      writer.write("?start=");
      // Remove {graph} from start and end offset
      int start = params.getStart() + getName().length() + 2;
      int end = params.getEnd() + getName().length() - 2;
      writer.write(start);
      writer.write("&end=");
      writer.write(end);
      writer.write("\"/>");
  }
}
