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

import snipsnap.api.config.*;
import snipsnap.api.config.Configuration;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Arrays;

public class ConfigurationProxy implements InvocationHandler {
  private Map methodCache = new HashMap();
  private Map propertyMethodCache = new HashMap();

  private ConfigurationMap config = null;
  private Method getMethod, setMethod;
  private Method staticGetMethod, staticSetMethod;
  private Map propertyMethodMap;

  public ConfigurationProxy(ConfigurationMap config) {
    this.config = config;
    try {
      // static get/set methods for globals
      staticGetMethod = config.getClass().getMethod("getGlobal", new Class[]{String.class});
      staticSetMethod = config.getClass().getMethod("setGlobal", new Class[]{String.class, String.class});
      methodCache.put(staticGetMethod.toString(), staticGetMethod);
      methodCache.put(staticGetMethod.toString(), staticSetMethod);

      // simple get/set methods
      getMethod = config.getClass().getMethod("get", new Class[]{String.class});
      setMethod = config.getClass().getMethod("set", new Class[]{String.class, String.class});
      methodCache.put(getMethod.toString(), getMethod);
      methodCache.put(setMethod.toString(), setMethod);
    } catch (Exception e) {
      System.err.println("FATAL ERROR: unable to get get/set methods of configuration map");
      e.printStackTrace();
      getMethod = null;
    }

    propertyMethodMap = new HashMap();
    Field[] fields = Configuration.class.getFields();
    for (int fieldCount = 0; fieldCount < fields.length; fieldCount++) {
      try {
        String value = (String) fields[fieldCount].get(Configuration.class);
        propertyMethodMap.put("get" + getCamlCase(value, "app."), value);
        propertyMethodMap.put("set" + getCamlCase(value, "app."), value);
        propertyMethodCache.put("get" + getCamlCase(value, "app."), getMethod);
        propertyMethodCache.put("set" + getCamlCase(value, "app."), setMethod);
      } catch (Exception e) {
        System.err.println("ERROR unable to load property names: " + e);
        e.printStackTrace();
      }
    }

    fields = Globals.class.getFields();
    for(int fieldCount = 0; fieldCount < fields.length; fieldCount++) {
      try {
        String value = (String) fields[fieldCount].get(Globals.class);
        propertyMethodMap.put("get" + getCamlCase(value, "app."), value);
        propertyMethodMap.put("set" + getCamlCase(value, "app."), value);
        propertyMethodCache.put("get" + getCamlCase(value, "app."), staticGetMethod);
        propertyMethodCache.put("set" + getCamlCase(value, "app."), staticSetMethod);
      } catch (Exception e) {
        System.err.println("ERROR unable to load global property names: " + e);
        e.printStackTrace();
      }
    }
  }

  /**
   * Transforms a property name into a CamlCase method name for reflection.
   *
   * @param name the name of the property
   * @param prefix the prefix that should be removed before transforming
   * @return a java method name usable for reflection
   */
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
    Object[] invokeArgs = args;

//    System.out.println(method.getName() + "(" + (args != null ? "" + Arrays.asList(args) : "") + ") ");

    Method targetMethod = getTargetMethod(method);

    // if we cannot find a method this must be a get/set method
    if (targetMethod == null) {
      String methodName = method.getName();
      targetMethod = (Method)propertyMethodCache.get(methodName);
      // extend arguments by one and insert property name in front (works for
      // set(property, value) and get(property), TODO may be optimized
      if(args != null) {
        invokeArgs = new Object[args.length + 1];
        System.arraycopy(args, 0, invokeArgs, 1, args.length);
      } else {
        invokeArgs = new Object[1];
      }
      invokeArgs[0] = propertyMethodMap.get(methodName);
    }

    try {
//      System.out.print(targetMethod.getName() + "(" +
//                       (invokeArgs != null ? "" + Arrays.asList(invokeArgs) : "") +
//                       ") => ");
      result = targetMethod.invoke(config, invokeArgs);
    } catch (IllegalAccessException e) {
      System.err.println("ConfigurationProxy: illegal access to method: "+targetMethod);
    } catch (IllegalArgumentException e) {
      System.err.println("ConfigurationProxy: illegal arguments: "+Arrays.asList(invokeArgs));
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.getTargetException().printStackTrace();

    }
//    System.out.println(result);
    return result;
  }

  private Method getTargetMethod(Method method) {
    String methodKey = method.toString();
    if (methodCache.containsKey(methodKey)) {
      return (Method) methodCache.get(methodKey);
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

  public static snipsnap.api.config.Configuration getInstance() {
    if (proxy == null) {
      newInstance();
    }
    return proxy;
  }

  public static Configuration newInstance() {
    return newProxyInstance(new ConfigurationMap());
  }

  public static Configuration newInstance(Configuration config) {
    return newProxyInstance(new ConfigurationMap(config));
  }

  private static Configuration newProxyInstance(ConfigurationMap config) {
    proxy = (Configuration) Proxy.newProxyInstance(config.getClass().getClassLoader(),
                                                   new Class[]{Configuration.class, Globals.class},
                                                   new ConfigurationProxy(config));
    return proxy;
  }

}
