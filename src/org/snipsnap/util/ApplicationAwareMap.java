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

package org.snipsnap.util;

import snipsnap.api.app.Application;
import org.radeox.util.logging.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Wrapper for a Map which knows about different applications
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class ApplicationAwareMap {
  protected Map applicationMaps;
  private Class mapType;

  public ApplicationAwareMap() {
  }

  public ApplicationAwareMap(Class outerMapType) {
    try {
      applicationMaps = (Map) outerMapType.newInstance();
    } catch (Exception e) {
      applicationMaps = new HashMap();
    }
  }

  // @TODO Replace with generics
  public ApplicationAwareMap(Class outerMapType, Class mapType) {
    this(outerMapType);
    this.mapType = mapType;
  }

  protected Object newInstance() {
    Object map = null;
    try {
      map = mapType.newInstance();
    } catch (Exception e) {
      // This should not happen
      System.err.println("Cannot instantiate " + mapType.getName() + " " + e);
    }
    return map;
  }

  public Object getObject(String applicationOid) {
    Object map = applicationMaps.get(applicationOid);
    if(null == map) {
      map = newInstance();
      applicationMaps.put(applicationOid, map);
    }
    return map;
  }

  public Queue getQueue() {
    return (Queue) getObject();
  }

  public Map getMap() {
    return (Map) getObject();
  }

  public Map getMap(String applicationOid) {
    return (Map) getObject(applicationOid);
  }

  public Object getObject() {
    String application = (String) snipsnap.api.app.Application.get().getObject(Application.OID);
    return getObject(application);
  }

  /**
   * Returns the first map containing the object.
   * @param object
   * @return
   */
  public Map findMap(Object object) {
    //Logger.debug("searching: ["+object+"] "+applicationMaps);
    Iterator mapIt = applicationMaps.values().iterator();
    while (mapIt.hasNext()) {
      Map map = (Map) mapIt.next();
      if (map.containsKey(object)) {
        return map;
      }
    }
    return null;
  }
}
