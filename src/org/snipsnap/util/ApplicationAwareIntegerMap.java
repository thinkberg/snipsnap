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
 * along with this program; if not, write to the Free Software                                                       Queue
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */

package org.snipsnap.util;

import org.snipsnap.jdbc.IntHolder;

/**
 * Wrapper for an Integer which knows about different applications
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class ApplicationAwareIntegerMap extends ApplicationAwareMap {
  public ApplicationAwareIntegerMap(Class outerMap) {
    super(outerMap);
  }

  protected Object newInstance() {
    return new IntHolder(-1);
  }

  public IntHolder getIntHolder() {
    return (IntHolder) getObject();
  }
}
