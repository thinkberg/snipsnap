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

package org.snipsnap.render.filter;

import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.filter.MacroFilter;
import org.radeox.macro.Repository;
import org.snipsnap.render.macro.WeblogMacro;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * Macro filter that is called later than MacroFilter.
 * Only used to call {weblog} macro because we do not
 * want to call filters on already filtered content
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class LateMacroFilter extends MacroFilter {
  protected Repository macroRepository = new Repository() {
    private Map macros = new HashMap();

    public boolean containsKey(String key) {
      return macros.containsKey(key);
    }

    public Object get(String key) {
      return macros.get(key);
    }

    public List getPlugins() {
      return new ArrayList(macros.values());
    }

    public void put(String key, Object value) {
      macros.put(key, value);
    }
  };

  public void setInitialContext(InitialRenderContext context) {
    WeblogMacro weblogMacro = new WeblogMacro();
    macroRepository.put(weblogMacro.getName(), weblogMacro);
  }

  protected Repository getMacroRepository() {
    return macroRepository;
  }
}
