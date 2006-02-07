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

package org.snipsnap.app;

import snipsnap.api.storage.ApplicationStorage;

import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.Properties;

/**
 * ApplicationManager creates, removes and lists applications in one
 * SnipSnap instance.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class ApplicationManager {
  private ApplicationStorage storage;
  private Map prefixMap;

  public ApplicationManager(ApplicationStorage applicationStorage) {
    this.storage = applicationStorage;
    prefixMap = storage.getApplications();
  }

  public Properties createApplication(String name, String prefix) {
    Properties prefixProps = storage.createApplication(name, prefix);
    prefixMap.put(prefix, prefixProps);
    return prefixProps;
  }

  public void removeApplication(String oid) {
    storage.removeApplication(oid);
  }

  public String getPrefix(String oid) {
    String result = null;
    Iterator iterator = prefixMap.keySet().iterator();
    while (iterator.hasNext()) {
      String prefix  = (String) iterator.next();
      String aOid = getApplication(prefix);
      if (oid.equals(aOid)) {
        result = prefix;
      }
    }
    return result;
  }

  public Collection getPrefixes() {
    return storage.getApplications().keySet();
  }

  public Collection getApplications() {
    return storage.getApplications().values();
  }

  public String getApplication(String prefix) {
    Properties prefixProps = (Properties) prefixMap.get(prefix);
    if(null != prefixProps) {
      return prefixProps.getProperty(ApplicationStorage.OID);
    }
    return null;
  }
}
