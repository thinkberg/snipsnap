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

import java.util.Arrays;
import java.util.Collection;
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
  private Properties props;

  public ApplicationManager(ApplicationStorage applicationStorage) {
    this.storage = applicationStorage;
    props = (Properties)applicationStorage.getApplications().get("/");
  }

  public Properties createApplication(String name, String prefix) {
    return (props = storage.createApplication(name, "/"));
  }

  public void removeApplication(String oid) {
    storage.removeApplication(oid);
  }

  public String getPrefix(String oid) {
    return props != null ? "/" : null;
  }

  public Collection getPrefixes() {
    return storage.getApplications().keySet();
  }

  public Collection getApplications() {
    return storage.getApplications().values();
  }

  public String getApplication(String prefix) {
    return props != null ? props.getProperty(ApplicationStorage.OID) : null;
  }
}
