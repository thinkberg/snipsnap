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

package org.snipsnap.jdbc;

import org.snipsnap.cache.Cache;

/**
 * Convenience factory to generate Finders
 *
 * @author stephan
 * @version $Id$
 */
public class FinderFactory {
  private Cache cache;
  private Class type;
  private String statementRoot;
  private String keyName;
  private JDBCCreator creator;

  public FinderFactory(String statement, Cache cache, Class type, String keyName, JDBCCreator creator) {
    this.cache = cache;
    this.type = type;
    this.statementRoot = statement + " ";
    this.keyName = keyName;
    this.creator = creator;
  }

  public Finder getFinder() {
    return new Finder(statementRoot, cache, keyName, type, creator);
  }

  public Finder getFinder(String statement) {
    return new Finder(statementRoot + statement, cache, keyName, type, creator);
  }
}
