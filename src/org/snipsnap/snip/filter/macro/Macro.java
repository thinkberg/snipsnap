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
 * Class that executes implements macros for filters
 *
 * @author stephan
 * @version $Id$
 */

package org.snipsnap.snip.filter.macro;

import org.snipsnap.snip.filter.macro.parameter.MacroParameter;

import java.io.IOException;
import java.io.Writer;

public abstract class Macro implements Comparable {
  protected String description = " ";
  protected String[] paramDescription = {"unexplained, lazy programmer, probably [funzel]"};

  public abstract String getName();

  public String getDescription() {
    return description;
  }

  public String[] getParamDescription() {
    return paramDescription;
  }

  public abstract void execute(Writer writer, MacroParameter params)
      throws IllegalArgumentException, IOException;

  public String toString() {
    return getName();
  }

  public int compareTo(Object object) {
    Macro macro = (Macro) object;
    return getName().compareTo(macro.getName());
  }
}
