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
package org.snipsnap.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Field;
import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class ConfigurationProxy implements InvocationHandler {

  private ConfigurationMap config = null;
  private Method getMethod, setMethod;
  private Map propertyMethodMap;

  public ConfigurationProxy(ConfigurationMap config) {
    this.config = config;
    try {
      getMethod = config.getClass().getMethod("get", new Class[]{String.class});
      setMethod = config.getClass().getMethod("set", new Class[]{String.class, String.class});
      methodCache.put(getMethod.toString(), getMethod);
      methodCache.put(setMethod.toString(), setMethod);
    } catch (Exception e) {
      System.err.println("FATAL ERROR: unable to get get/set methods of configuration map");
      e.printStackTrace();
      getMethod = null;
    }

    Field fields[] = Configuration.class.getFields();
    propertyMethodMap = new HashMap();
    for(int fieldCount = 0; fieldCount < fields.length; fieldCount++) {
      try {
        String value = (String)fields[fieldCount].get(Configuration.class);
        propertyMethodMap.put("get"+getCamlCase(value, "app."), value);
      } catch (Exception e) {
        System.err.println("ERROR unable to load property names: "+e);
        e.printStackTrace();
      }
    }
  }

  private String getCamlCase(String name, String prefix) {
    if (name.startsWith(prefix)) {
      name = name.substring(prefix.length());
    }
    StringTokenizer tokenizer = new StringTokenizer(name, ".", false);
    StringBuffer result = new StringBuffer();
    while (tokenizer.hasMoreTokens()) {
      String token = tokenizer.nextToken();
      result.append(token.substring(0, 1).toUpperCase());
      if (token.length() > 1) {
        result.append(token.substring(1));
      }
    }
    return result.toString();
  }

  // DYNAMIC PROXY METHODS
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object result = null;
    Method targetMethod = getTargetMethod(method);
    if(targetMethod == null) {
      String methodName = method.getName();
      //System.out.print(methodName+ "(" + (args != null ? "" + Arrays.asList(args) : "") + ") => ");
      String property = (String)propertyMethodMap.get(methodName);
      if(methodName.startsWith("get")) {
        result = config.get(property);
      } else if(methodName.startsWith("set")) {
        Object setArgs[] = new Object[args.length + 1];
        setArgs[0] = property;
        System.arraycopy(args, 1, setArgs, 0, args.length);
        result = setMethod.invoke(config, setArgs);
      } else {
        System.err.println("FATAL: unknown method "+methodName+" called.");
      }
    } else {
      //System.out.print(targetMethod.getName() + "(" + (args != null ? "" + Arrays.asList(args) : "") + ") => ");
      result = targetMethod.invoke(config, args);
    }
    System.out.println(result);
    return result;
  }

  private Map methodCache = new HashMap();

  private Method getTargetMethod(Method method) {
    String methodKey = method.toString();
    if(methodCache.containsKey(methodKey)) {
      return (Method)methodCache.get(methodKey);
    } else {
      try {
        Method targetMethod = config.getClass().getMethod(method.getName(), method.getParameterTypes());
        methodCache.put(targetMethod.toString(), targetMethod);
        return targetMethod;
      } catch (NoSuchMethodException e) {
        // ignore non-existing methods
      } catch (SecurityException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  // PROXY FACTORY HANDLING
  public static Configuration proxy = null;

  public static Configuration getInstance() {
    if(proxy == null) newInstance();
    return proxy;
  }

  public static Configuration newInstance() {
    return newProxyInstance(new ConfigurationMap());
  }

  public static Configuration newInstance(String configPath) throws IOException {
    return newProxyInstance(new ConfigurationMap(configPath));
  }

  public static Configuration newInstance(File configFile) throws IOException {
    return newProxyInstance(new ConfigurationMap(configFile));
  }

  private static Configuration newProxyInstance(ConfigurationMap config) {
    proxy = (Configuration) Proxy.newProxyInstance(config.getClass().getClassLoader(),
                                                     new Class[]{ Configuration.class },
                                                     new ConfigurationProxy(config));
    return proxy;
  }

}
