/*
 * Created by IntelliJ IDEA.
 * User: leo
 * Date: Jul 11, 2002
 * Time: 1:51:52 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.neotis.admin;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class Configuration {
  private Properties properties = new Properties();

  public Configuration() {
    /* nothing to do yet */
  }

  public Configuration(String configFile) {
    try {
      properties.load(new FileInputStream(configFile));
    } catch (IOException e) {
      System.err.println("Configuration: cannot load configuration file: "+configFile);
    }
  }

  public void save(String configFile) {
    try {
      properties.store(new FileOutputStream(configFile), "");
    } catch(IOException e) {
      System.err.println("Configuration: cannot save configuration file: "+configFile);
    }
  }

  public boolean isConfigured() {
    return !properties.isEmpty() && "true".equals(properties.getProperty("configured"));
  }

  public void setUserName(String userName) {
    properties.setProperty("userName", userName);
  }

  public String getUserName() {
    return properties.getProperty("userName");
  }

  public void setEmail(String email) {
    properties.setProperty("email", email);
  }

  public String getEmail() {
    return properties.getProperty("email");
  }

  public void setHost(String host) {
    properties.setProperty("host", host);
  }

  public String getHost() {
    return properties.getProperty("host");
  }

  public void setPort(int port) {
    properties.setProperty("port", ""+port);
  }

  public int getPort() {
    try {
      return Integer.parseInt(properties.getProperty("port"));
    } catch (NumberFormatException e) {
      System.err.println("Configuration: illegal port");
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
