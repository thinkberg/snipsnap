/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */

package org.snipsnap.feeder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.radeox.macro.PluginLoader;
import org.radeox.macro.Repository;

/**
 * Plugin loader for feeder
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class FeederLoader extends PluginLoader {
  private static Log log = LogFactory.getLog(FeederLoader.class);

  public Class getLoadClass() {
    return Feeder.class;
  }

  /**
   * Add a plugin to the known plugin map
   *
   * @param repository the repository to add the feeder to
   * @param plugin a feeder to add to the repository
   */
  public void add(Repository repository, Object plugin) {
    if (plugin instanceof Feeder) {
      repository.put(((Feeder) plugin).getName(), plugin);
    } else {
      log.debug("Feeder Loader: " + plugin.getClass() + " not of Type " + getLoadClass());
    }
  }

}
