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
package org.snipsnap.snip.storage;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.radeox.util.logging.Logger;
import org.snipsnap.user.Roles;
import snipsnap.api.user.User;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A user serializer that can store and load users in XML format.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class UserSerializer extends SerializerSupport {
  public final static String USER = "user";

  public final static String USER_NAME = "login";
  public final static String USER_PASSWORD = "passwd";
  public final static String USER_EMAIL = "email";
  public final static String USER_ROLES = "roles";
  public final static String USER_STATUS = "status";
  public final static String USER_CTIME = "cTime";
  public final static String USER_MTIME = "mTime";
  public final static String USER_LAST_ACCESS = "lastAccess";
  public final static String USER_LAST_LOGIN = "lastLogin";
  public final static String USER_LAST_LOGOUT = "lastLogout";
  public final static String USER_APPLICATION = "application";

  private static UserSerializer serializer = null;

  /**
   * Get an instance of the user serializer.
   * @return the serializer
   */
  public synchronized static UserSerializer getInstance() {
    if (null == serializer) {
      serializer = new UserSerializer();
    }
    return serializer;
  }

  protected UserSerializer() {
  }

  /**
   * Store a snip in an XML node.
   * @param user the user to store
   * @return the serialized user as XML
   */
  public Element serialize(snipsnap.api.user.User user) {
    Element userElement = DocumentHelper.createElement(USER);
    userElement.addElement(USER_NAME).addText(user.getLogin());
    userElement.addElement(USER_PASSWORD).addText(notNull(user.getPasswd()));
    userElement.addElement(USER_EMAIL).addText(notNull(user.getEmail()));
    userElement.addElement(USER_ROLES).addText(user.getRoles().toString());
    userElement.addElement(USER_STATUS).addText(notNull(user.getStatus()));
    userElement.addElement(USER_CTIME).addText(getStringTimestamp(user.getCTime()));
    userElement.addElement(USER_MTIME).addText(getStringTimestamp(user.getMTime()));
    userElement.addElement(USER_LAST_ACCESS).addText(getStringTimestamp(user.getLastAccess()));
    userElement.addElement(USER_LAST_LOGIN).addText(getStringTimestamp(user.getLastLogin()));
    userElement.addElement(USER_LAST_LOGOUT).addText(getStringTimestamp(user.getLastLogout()));
    userElement.addElement(USER_APPLICATION).addText(notNull(user.getApplication()));

    return userElement;
  }

  /**
   * Load user from XML serialized file.
   * @param userEl the XML node containing the user
   * @return the modified user
   */
  public snipsnap.api.user.User deserialize(Element userEl, snipsnap.api.user.User user) {
    Map snipMap = getElementMap(userEl);
    return deserialize(snipMap, user);
  }

  public User deserialize(Map userMap, snipsnap.api.user.User user) {
    Iterator elementIt = userMap.keySet().iterator();
    while (elementIt.hasNext()) {
      String element = (String) elementIt.next();
      String value = "";
      Object elementValue = userMap.get(element);
      if (elementValue instanceof String) {
        value = notNull((String) userMap.get(element));
      }

      if(USER_NAME.equals(element)) {
        user.setLogin(value);
      } else if(USER_PASSWORD.equals(element)) {
        user.setPasswd(notNull(value));
      } else if(USER_EMAIL.equals(element)) {
        user.setEmail(notNull(value));
      } else if(USER_ROLES.equals(element)) {
        user.setRoles(new Roles(value));
      } else if(USER_STATUS.equals(element)) {
        user.setStatus(notNull(value));
      } else if(USER_CTIME.equals(element)) {
        user.setCTime(getTimestamp(value));
      } else if(USER_MTIME.equals(element)) {
        user.setMTime(getTimestamp(value));
      } else if(USER_LAST_ACCESS.equals(element)) {
        user.setLastAccess(getTimestamp(value));
      } else if(USER_LAST_LOGIN.equals(element)) {
        user.setLastLogin(getTimestamp(value));
      } else if(USER_LAST_LOGOUT.equals(element)) {
        user.setLastLogout(getTimestamp(value));
      } else if(USER_APPLICATION.equals(element)) {
        user.setApplication(value);
      } else {
        Logger.warn("unknown entry in serialized user: " + element + "='" + value + "'");
      }
    }

    return user;
  }

  public Map createUserMap(snipsnap.api.user.User user) {
    Map userMap = new HashMap();
    userMap.put(USER_NAME, user.getLogin());
    userMap.put(USER_PASSWORD, notNull(user.getPasswd()));
    userMap.put(USER_EMAIL, notNull(user.getEmail()));
    userMap.put(USER_STATUS, notNull(user.getStatus()));
    userMap.put(USER_ROLES, user.getRoles().toString());
    userMap.put(USER_CTIME, getStringTimestamp(user.getCTime()));
    userMap.put(USER_MTIME, getStringTimestamp(user.getMTime()));
    userMap.put(USER_LAST_LOGIN, getStringTimestamp(user.getLastLogin()));
    userMap.put(USER_LAST_ACCESS, getStringTimestamp(user.getLastAccess()));
    userMap.put(USER_LAST_LOGOUT, getStringTimestamp(user.getLastLogout()));
    userMap.put(USER_APPLICATION, notNull(user.getApplication()));
    return userMap;
  }
}
