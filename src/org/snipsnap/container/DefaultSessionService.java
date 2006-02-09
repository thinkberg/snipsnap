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
import org.snipsnap.snip.HomePage;
import org.snipsnap.user.AuthenticationService;
import org.snipsnap.user.Digest;
import org.snipsnap.user.UserManager;
import org.snipsnap.util.Base64;
import org.snipsnap.util.X509NameTokenizer;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.container.Components;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.storage.UserStorage;
import snipsnap.api.user.User;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class DefaultSessionService implements SessionService {
  private final static String COOKIE_NAME = "SnipSnapUser";
  private final static String ATT_USER = "user";
  private final static int SECONDS_PER_YEAR = 60 * 60 * 24 * 365;
  private final static int HTTP_UNAUTHORIZED = 401;


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
        String line;
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
    String appOid = (String) Application.get().getObject(Application.OID);
    if (null != user && !appOid.equals(user.getApplication())) {
      user = null;
    }

    // TODO: refactor to several session service modules
    // TODO: Idea: Basic, Digest or Certificate access and possible guest access (Cookie)
    if (null == user) {
      if ("Cookie".equals(Application.get().getConfiguration().getAuth())) {
        Cookie cookie = getCookie(request, COOKIE_NAME);
        if (cookie != null) {
          String auth = cookie.getValue();
          if (!authHash.containsKey(auth)) {
            updateAuthHash();
          }

          user = (User) authHash.get(auth);
          if (user != null && appOid.equals(user.getApplication())) {
            user = authService.authenticate(user.getLogin(), user.getPasswd(), AuthenticationService.ENCRYPTED);
            if (null != user) {
              setCookie(request, response, user);
            }
          } else {
            Logger.warn("SessionService: invalid hash: " + auth);
            user = null;
          }
        }
      } else if ("Basic".equals(Application.get().getConfiguration().getAuth())) {
        // make sure the user is authorized
        String auth = request.getHeader("Authorization");
        String login = "", password = "";

        if (auth != null) {
          auth = new String(Base64.decode(auth.substring(auth.indexOf(' ') + 1)));
          login = auth.substring(0, auth.indexOf(':'));
          password = auth.substring(auth.indexOf(':') + 1);
        }

        user = authService.authenticate(login, password);
        if (user == null) {
          response.setHeader("WWW-Authenticate", "Basic realm=\"" + Application.get().getConfiguration().getName() + "\"");
          response.setStatus(HTTP_UNAUTHORIZED);
          return null;
        }
      } else if ("Certificate".equals(Application.get().getConfiguration().getAuth())) {
        // Part for authenticating users with X509Certificates. If the user have a trusted client certificate
        // he can get access to the server. Since the certificate is trusted already, by java/jsse, we don't
        // have to verify it here.
        // If the CA puts the users uid in the DN we can use that as login.

        // Check if we have a user in the certificate authentication
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");
        if (certs != null) {
          X509Certificate clientCert = certs[0];
          if (clientCert != null) {
            // Get the Distinguised Name for the user.
            java.security.Principal userDN = clientCert.getSubjectDN();
            String dn = userDN.toString();
            // Get uid, which is the username we will use
            String uid = getPartFromDN(dn, "UID");
            String email = getPartFromDN(dn, "emailAddress");
            // Create users home page if it does not exist
            UserManager um = (UserManager) Components.getComponent(UserManager.class);
            user = authService.authenticate(uid);
            // create a user and home page for new logins
            if (null == user) {
              // set password to "*", if we switch back to Cookie auth service
              // this is no problem as the users password is expected to be encrypted
              // switching to Basic auth poses a security risk as it compares unencrypted
              // passwords.
              user = um.create(uid, "*", email);
              Application.get().setUser(user, session);
              HomePage.create(uid);
              user = authService.authenticate(uid);
            }
          }
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
   *
   * @param request
   * @param name
   * @return the cookie
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

  /**
   * Gets a specified part of a DN. Specifically the first occurrence it the DN contains several
   * instances of a part (i.e. cn=x, cn=y returns x).
   *
   * @param dn     String containing DN, The DN string has the format "C=SE, O=xx, OU=yy, CN=zz".
   * @param dnpart String specifying which part of the DN to get, should be "CN" or "OU" etc.
   * @return String containing dnpart or null if dnpart is not present
   */
  private String getPartFromDN(String dn, String dnpart) {
    String part = null;
    if ((dn != null) && (dnpart != null)) {
      String o;
      dnpart += "="; // we search for 'CN=' etc.
      X509NameTokenizer xt = new X509NameTokenizer(dn);
      while (xt.hasMoreTokens()) {
        o = xt.nextToken();
        if ((o.length() > dnpart.length()) &&
                o.substring(0, dnpart.length()).equalsIgnoreCase(dnpart)) {
          part = o.substring(dnpart.length());
          break;
        }
      }
    }
    return part;
  } //getPartFromDN


}
