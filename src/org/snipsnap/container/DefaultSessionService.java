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

package org.snipsnap.container;

import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpace;
import org.snipsnap.snip.storage.UserStorage;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.Digest;
import snipsnap.api.user.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultSessionService implements SessionService {
  private final static String COOKIE_NAME = "SnipSnapUser";
  private final static String ATT_USER = "user";
  private final static int SECONDS_PER_YEAR = 60 * 60 * 24 * 365;

  private Map authHash = new HashMap();
  private Map robots = new HashMap();
  private Map robotIds = new HashMap();

  private UserStorage storage;
  private AuthenticationService authService;

  public DefaultSessionService(SnipSpace space, UserStorage storage, AuthenticationService authService) {
    this.storage = storage;
    this.authService = authService;

    try {
      snipsnap.api.snip.Snip robots = space.load(snipsnap.api.config.Configuration.SNIPSNAP_CONFIG_ROBOTS);
      if (robots != null) {
        BufferedReader crawler = new BufferedReader(new StringReader(robots.getContent()));
        String line = null;
        int ln = 0;
        while ((line = crawler.readLine()) != null) {
          ln++;
          if (line.length() > 0 && !line.startsWith("#")) {
            try {
              String id = line.substring(0, line.indexOf(' '));
              String url = line.substring(line.indexOf(' ') + 1);
              if (url.indexOf("IGNORE") != -1) {
                robotIds.put(id, "IGNORE");
              } else {
                robotIds.put(id, url);
              }
            } catch (Exception e) {
              Logger.warn("SessionService: " + Configuration.SNIPSNAP_CONFIG_ROBOTS + " line " + ln + ": syntax error", e);
            }
          }
        }
      }
    } catch (Exception e) {
      Logger.warn("SessionService: unable to read " + Configuration.SNIPSNAP_CONFIG_ROBOTS, e);
    }
  }

  // update the auth hash by removing all entries and updating from the database
  private void updateAuthHash() {
    authHash.clear();
    Iterator users = storage.storageAll().iterator();
    while (users.hasNext()) {
      User user = (User) users.next();
      authHash.put(getCookieDigest(user), user);
    }
  }

  /**
   * Get a hexadecimal cookie digest from a user.
   */
  public static String getCookieDigest(User user) {
    String tmp = user.getLogin() + user.getPasswd() + user.getLastLogin().toString();
    return Digest.getDigest(tmp);
  }

  public void setUser(HttpServletRequest request, HttpServletResponse response, User user) {
    HttpSession session = request.getSession();
    session.setAttribute(ATT_USER, user);
    setCookie(request, response, user);
  }

  /**
   * Get user from session or cookie.
   */
  public User getUser(HttpServletRequest request, HttpServletResponse response) {
    HttpSession session = request.getSession();
    User user = (User) session.getAttribute(ATT_USER);
    String appOid = (String)Application.get().getObject(Application.OID);
    if (null != user && !appOid.equals(user.getApplication())) {
      user = null; 
    }

    if (null == user) {
      Cookie cookie = getCookie(request, COOKIE_NAME);
      if (cookie != null) {
        String auth = cookie.getValue();
        if (!authHash.containsKey(auth)) {
          updateAuthHash();
        }

        user = (User) authHash.get(auth);
        if (user != null && appOid.equals(user.getApplication())) {
          user = authService.authenticate(user.getLogin(), user.getPasswd(), AuthenticationService.ENCRYPTED);
          if(null != user) {
            setCookie(request, response, user);
          }
        } else {
          Logger.warn("SessionService: invalid hash: " + auth);
          user = null;
        }
      }

      if (null == user) {
        String agent = request.getHeader("User-Agent");
        Iterator it = robotIds.keySet().iterator();
        while (agent != null && user == null && it.hasNext()) {
          String key = (String) it.next();
          if (agent.toLowerCase().indexOf(key.toLowerCase()) != -1) {
            user = (User) robots.get(key);
            if (null == user) {
              user = new User(key, key, (String) robotIds.get(key));
              user.setNonUser(true);
              robots.put(key, user);
            }
            break;
          }
        }

        if (user != null) {
          Logger.debug("Found robot: " + user);
        } else {
          Logger.debug("User agent of unknown user: '" + agent + "'");
          user = new User("Guest", "Guest", "");
          user.setApplication(appOid);
          user.setGuest(true);
        }
        removeCookie(request, response);
      }
      session.setAttribute(ATT_USER, user);
    }
    return user;
  }

  /**
   * Set cookie with has of encoded user/pass and last login time.
   */
  public void setCookie(HttpServletRequest request, HttpServletResponse response, User user) {
    String auth = getCookieDigest(user);
    // @TODO find better solution by removing by value
    updateAuthHash();

    authHash.put(auth, user);
    Cookie cookie = new Cookie(COOKIE_NAME, auth);
    cookie.setMaxAge(SECONDS_PER_YEAR);
    cookie.setPath(getCookiePath());
    cookie.setComment("SnipSnap User");
    response.addCookie(cookie);
  }


  public void removeCookie(HttpServletRequest request, HttpServletResponse response) {
    Cookie cookie = getCookie(request, COOKIE_NAME);
    if (cookie != null) {
      cookie.setPath(getCookiePath());
      cookie.setMaxAge(0);
      response.addCookie(cookie);
    }
  }

  private String getCookiePath() {
    String path;
    snipsnap.api.config.Configuration config = snipsnap.api.app.Application.get().getConfiguration();
    try {
      path = new URL(config.getUrl()).getPath();
      if (path == null || path.length() == 0) {
        path = "/";
      }
    } catch (MalformedURLException e) {
      Logger.warn("Malformed URL: " + Application.get().getConfiguration().getUrl(), e);
      path = "/";
    }
//    System.out.println("Cookie path: "+path);
    return path;
  }

  /**
   * Helper method for getUser to extract user from request/cookie/session
   * @param request
   * @param name
   * @return
   */
  public Cookie getCookie(HttpServletRequest request, String name) {
    Cookie cookies[] = request.getCookies();
    for (int i = 0; cookies != null && i < cookies.length; i++) {
      if (cookies[i].getName().equals(name)) {
        return cookies[i];
      }
    }
    return null;
  }
}
