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

package org.snipsnap.test.snip;

import junit.framework.TestCase;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.app.Application;

import java.util.Properties;

public class SnipTestSupport extends TestCase {
  public SnipTestSupport(String s) {
    super(s);
  }

  protected void setUp() throws Exception {
    AppConfiguration config = new AppConfiguration();
    Properties properties = new Properties();
    properties.setProperty(AppConfiguration.APP_NAME, "SnipSnap");
    // set some basic properties
    config.setHost("snipsnap.org");
    config.setContextPath("");
    Application.get().setConfiguration(config);
    super.setUp();
  }
}
