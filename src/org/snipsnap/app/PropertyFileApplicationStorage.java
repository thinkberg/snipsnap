/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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

import snipsnap.api.config.Configuration;
import org.snipsnap.config.Globals;
import org.snipsnap.jdbc.UIDGenerator;
import org.radeox.util.logging.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.FileFilter;
import java.util.Map;
import java.util.Properties;
import java.util.HashMap;
import java.util.Iterator;

import snipsnap.api.app.*;
import snipsnap.api.app.Application;

public class PropertyFileApplicationStorage implements ApplicationStorage {
  private final static String PREFIX_FILE = "prefix.properties";

  Map applications;
  File appFile = null;

  public PropertyFileApplicationStorage() {
    applications = new HashMap();
    snipsnap.api.config.Configuration config = snipsnap.api.app.Application.get().getConfiguration();
    File fileStore = new File(config.getGlobal(Globals.APP_FILE_STORE));
    File[] instances = fileStore.listFiles(new FileFilter() {
      public boolean accept(File file) {
        return file.isDirectory();
      }
    });

    for(int f = 0; f < instances.length; f++) {
      File prefixInfo = new File(instances[f], PREFIX_FILE);
      if(prefixInfo.exists()) {
        Properties prefixProps = new Properties();
        try {
          prefixProps.load(new FileInputStream(prefixInfo));
          applications.put(prefixProps.getProperty(PREFIX), prefixProps);
        } catch (IOException e) {
          Logger.warn("ignoring prefix: "+instances[f].getPath());
        }
      }
    }
  }


  public Map getApplications() {
    return applications;
  }

  public void removeApplication(String oid) {
    Iterator it = applications.keySet().iterator();
    while(it.hasNext()) {
      String prefix = (String)it.next();
      Properties prefixProps = (Properties)applications.get(prefix);
      if(oid.equals(prefixProps.getProperty(OID))) {
        File prefixFile = getPrefixFile(prefixProps);
        prefixFile.renameTo(new File(prefixFile.getPath()+".removed"));
        it.remove();
      }
    }
  }

  public Properties createApplication(String name, String prefix) {
    String oid = UIDGenerator.generate(ApplicationStorage.class);
    Properties prefixProps = new Properties();
    prefixProps.setProperty(OID, oid);
    prefixProps.setProperty(PREFIX, prefix);
    prefixProps.setProperty(NAME, name);
    applications.put(prefix, prefixProps);
    save(prefixProps);
    return prefixProps;
  }

  private void save(Properties prefixProps) {
    try {
      File prefixFile = getPrefixFile(prefixProps);
      prefixFile.getParentFile().mkdirs();
      prefixProps.store(new FileOutputStream(prefixFile), "$Revision$");
    } catch (IOException e) {
      System.err.println("unable to store prefix properties: " + e);
      e.printStackTrace();
    }
  }

  private File getPrefixFile(Properties prefixProps) {
    Globals config = Application.get().getConfiguration();
    File fileStore = new File(config.getGlobal(Globals.APP_FILE_STORE));
    File prefixDir = new File(fileStore, prefixProps.getProperty(OID));
    return new File(prefixDir, PREFIX_FILE);
  }
}
