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

package org.snipsnap.render.macro.parameter;

import org.radeox.api.engine.context.RenderContext;
import org.radeox.macro.parameter.BaseMacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.render.context.SnipRenderContext;

import java.util.HashMap;

/**
 * Encapsulates parameters for an execute Macro call.
 * This extends MacroParameter for usage in a SnipSnap
 * enviroment. For example SnipMacroParameter contains
 * a getSnip() method to get the current Snip
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class SnipMacroParameter extends BaseMacroParameter  {

  public SnipMacroParameter(RenderContext context) {
    params = new HashMap();
    this.context = context;
  }

  public SnipMacroParameter(String stringParams) {
    setParams(stringParams);
  }

  public SnipRenderContext getSnipRenderContext() {
    return (SnipRenderContext) context;
  }
}
