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
import org.snipsnap.render.macro.parameter.MacroParameter;

import java.io.Writer;
import java.io.IOException;

/**
 * Base macro class for macros that deal in a SnipSnap enviroment.
 * They receive an execute call with SnipMacroParameter instead of
 * MacroParameter
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public abstract class SnipMacro extends Macro {
  public abstract void execute(Writer writer, SnipMacroParameter params) throws IllegalArgumentException, IOException;

  public void execute(Writer writer, MacroParameter params) throws IllegalArgumentException, IOException {
    if (params instanceof SnipMacroParameter) {
      execute(writer, (SnipMacroParameter) params);
      return;
    } else {
      throw new IllegalArgumentException("Macro must be called in a SnipSnap enviroment.");
    }
  }
}

