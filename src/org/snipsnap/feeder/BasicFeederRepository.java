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
import org.radeox.api.engine.context.InitialRenderContext;
import org.radeox.macro.PluginRepository;

import java.util.*;

/**
 * Repository for feeders
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class BasicFeederRepository extends PluginRepository implements FeederRepository {
  private static Log log = LogFactory.getLog(FeederRepository.class);

  private InitialRenderContext context;

  protected List loaders;

  public BasicFeederRepository() {
    super();
    init();
    loaders = new ArrayList();
    loaders.add(new GroovyFeederLoader());
    loaders.add(new FeederLoader());
    load();
  }

  private void init() {
    Map newPlugins = new HashMap();

    Iterator iterator = list.iterator();
    while (iterator.hasNext()) {
      Feeder feeder = (Feeder) iterator.next();
      newPlugins.put(feeder.getName(), feeder);
    }
    plugins = newPlugins;
  }

  /**
   * Loads macros from all loaders into plugins.
   */
  private void load() {
    Iterator iterator = loaders.iterator();
    while (iterator.hasNext()) {
      FeederLoader loader = (FeederLoader) iterator.next();
      loader.setRepository(this);
      log.debug("Loading from: " + loader.getClass());
      loader.loadPlugins(this);
    }
  }

  public void addLoader(FeederLoader loader) {
    loader.setRepository(this);
    loaders.add(loader);
    plugins = new HashMap();
    list = new ArrayList();
    load();
  }
}
