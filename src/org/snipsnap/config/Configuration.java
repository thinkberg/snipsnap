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
package com.neotis.config;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Configuration Object that contains local installation information.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class Configuration {
  private Properties properties = new Properties();

  public Configuration(String configFile) {
    try {
      load(configFile);
    } catch (IOException e) {
      System.err.println("Configuration: unable to load from file: "+e);
    }
  }

  public void load(String configFile) throws IOException {
    properties.load(new FileInputStream(configFile));
  }

  public void save(String configFile) throws IOException {
    properties.store(new FileOutputStream(configFile), "SnipSnap configuration $Revision$");
  }

  public void setConfigured(boolean configured) {
    properties.setProperty("configured", configured ? "true" : "false");
  }

  public boolean isConfigured() {
    return !properties.isEmpty() && "true".equals(properties.getProperty("configured"));
  }

  public void setUserName(String userName) {
    properties.setProperty("user", userName);
  }

  public String getUserName() {
    return properties.getProperty("user");
  }

  public void setEmail(String email) {
    properties.setProperty("email", email);
  }

  public void setPassword(String password) {
    properties.setProperty("password", password);
  }

  public String getPassword() {
    return properties.getProperty("password");
  }

  public String getEmail() {
    return properties.getProperty("email");
  }

  public void setHost(String host) {
    properties.setProperty("host", host);
  }

  public String getHost() {
    String host = properties.getProperty("host");
    return host != null && host.length() == 0 ? null : host;
  }

  public void setPort(int port) {
    properties.setProperty("port", ""+port);
  }

  public int getPort() {
    try {
      return Integer.parseInt(properties.getProperty("port"));
    } catch (NumberFormatException e) {
      return 8668;
    }
  }

  public void setContextPath(String contextPath) {
    properties.setProperty("contextPath", contextPath);
  }

  public String getContextPath() {
    return properties.getProperty("contextPath");
  }
}
