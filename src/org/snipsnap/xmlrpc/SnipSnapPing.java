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

package org.snipsnap.xmlrpc;

import org.snipsnap.util.Queue;
import org.snipsnap.util.WeblogQueue;
import org.snipsnap.util.Weblog;

import java.util.List;

/**
 * Stores the last changed SnipSnap weblogs, received
 * bye XML-RPC weblog ping
 *
 * @author Stephan Schmidt
 * @version $Id$
 */

public class SnipSnapPing {
  private static SnipSnapPing instance;
  private WeblogQueue changed;

  public static synchronized SnipSnapPing getInstance() {
    if (null == instance) {
      instance = new SnipSnapPing();
    }
    return instance;
  }

  public SnipSnapPing() {
    changed = new WeblogQueue(10);
  }

  public synchronized void addChangedWeblog(String name, String url) {
    changed.add(new Weblog(name, url));
  }

  public List getChanged(int count) {
    return changed.get(count);
  }
}
