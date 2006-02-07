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

package snipsnap.api.storage;

import java.util.Map;
import java.util.Properties;

/**
 * ApplicationStorage is a DAO for applications.
 *
 * @author Stephan J. Schmidt
 * @version $Id: ApplicationStorage.java 1257 2003-12-11 13:36:55Z leo $
 */

public interface ApplicationStorage {
  public final static String OID = "OID";
  public final static String PREFIX = "prefix";
  public final static String NAME = "name";


  public Map getApplications();
  public void removeApplication(String oid);
  public Properties createApplication(String name, String prefix);
}
