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

package org.snipsnap.snip;

import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import org.snipsnap.container.Components;
import snipsnap.api.user.User;
import org.snipsnap.util.ApplicationAwareMap;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import snipsnap.api.snip.*;
import snipsnap.api.snip.SnipSpace;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;

/**
 * Stores Access information for a snip like viewCount, backLinks, ...
 *
 * @author stephan
 * @version $Id$
 */

public class Access {

  private final static String BLACKLIST = "SnipSnap/blacklist/referrer";

  // cache of the blacklist
  private static ApplicationAwareMap blackListCache = new ApplicationAwareMap(HashMap.class, ArrayList.class);
  private static Map lastModified = new HashMap();

  /**
   * Get a list of blacklisted referrers as a list of patterns.
   *
   * @return the blacklist patterns
   */
  public static List getReferrerBlackList() {
    List cachedBlackList = (List) blackListCache.getObject();

    SnipSpace space = (SnipSpace) Components.getComponent(snipsnap.api.snip.SnipSpace.class);
    if (space.exists(BLACKLIST)) {
      Snip blackListSnip = space.load(BLACKLIST);
      Timestamp mTime = blackListSnip.getMTime();
      String appOid = (String) snipsnap.api.app.Application.get().getObject(snipsnap.api.app.Application.OID);
      Timestamp cachedMTime = (Timestamp) lastModified.get(appOid);

      // update blacklist from snip if it does not exist or is new
      if (null == cachedMTime || cachedMTime.getTime() < mTime.getTime()) {
        cachedBlackList.clear();
        lastModified.put(appOid, mTime);

        String content = blackListSnip.getContent();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content.getBytes())));
        String line;
        try {
          while ((line = reader.readLine()) != null) {
            if (!line.startsWith("#")) {
              line = line.trim();
              if (!"".equals(line)) {
                cachedBlackList.add(line.trim());
              }
            }
          }
        } catch (IOException e) {
          Logger.warn("Referrer Blacklist Error: " + e.getLocalizedMessage());
          e.printStackTrace();
        }
      }
    }
    return cachedBlackList;
  }

  private Links backLinks, snipLinks;
  private int viewCount = 0;
  private boolean isModified;

  public Access() {
  }

  public Access(Links backLinks, Links snipLinks, int viewCount) {
    this.backLinks = backLinks;
    this.snipLinks = snipLinks;
    this.viewCount = viewCount;
  }

  // DO NOT store snip in Acess this creates problems with Aspects
  public void handle(String snipName, HttpServletRequest request) {
    User user = snipsnap.api.app.Application.get().getUser();
    if (!user.isNonUser()) {
      incViewCount();
//      preparation for better link statistics
//      HttpSession session = request.getSession();
//      if(session != null) {
//        String lastSnip = (String)session.getAttribute("fromSnip");
//        session.setAttribute("fromSnip", snipName);
//      }

      String referrer = request.getHeader("REFERER");
      if (null != referrer) {
        // Decode URL to remove jsessionid for example
        // referrer =
        String domain = snipsnap.api.app.Application.get().getConfiguration().getUrl();
        if (referrer.startsWith(domain)) {
          int index = referrer.indexOf("/space/");
          // Does the referrer point to a snip ?
          // Forget otherwise (e.g. "/exec/login.jsp")
          if (index != -1) {
            // @TODO replace all with regex.
            String url = referrer.substring(index + "/space/".length());
            index = url.indexOf("?");
            if (index != -1) {
              url = url.substring(0, index);
            }
            index = url.indexOf("#");
            if (index != -1) {
              url = url.substring(0, index);
            }
            // Hack to remove possible jsessionid
            index = url.indexOf(";jsessionid");
            if (index != -1) {
              url = url.substring(0, index);
            }

            String name = SnipLink.decode(url);

            if (!snipsnap.api.app.Application.get().getConfiguration().getStartSnip().equals(name)
                && !snipName.equals(name)) {
              snipLinks.addLink(name);
            }
          }
        } else {
          // do not count localhosts, single hosts and ignored urls. Will
          // not find local network IPs and MacOS X
          // hosts like megid.local
          if (isValidReferrer(referrer)) {
            backLinks.addLink(referrer);
          }
        }
      }
    }
  }

  public boolean isModified() {
    return isModified;
  }

  public void addLink(String url) {
    isModified = true;
    snipLinks.addLink(url);
  }

  public Links getBackLinks() {
    return backLinks;
  }

  public void setBackLinks(Links backLinks) {
    isModified = true;
    this.backLinks = backLinks;
  }

  public Links getSnipLinks() {
    return snipLinks;
  }

  public void setSnipLinks(Links snipLinks) {
    isModified = true;
    this.snipLinks = snipLinks;
  }

  public int getViewCount() {
    return viewCount;
  }

  public void setViewCount(int viewCount) {
    isModified = true;
    this.viewCount = viewCount;
  }

  public int incViewCount() {
    isModified = true;
    return ++this.viewCount;
  }

  public static boolean isValidReferrer(String url) {
    try {
      URL refURL = new URL(url);
      if (refURL.getHost().indexOf(".") == -1) {
        return false;
      }
      List blackList = Access.getReferrerBlackList();
      if (null != blackList && !blackList.isEmpty()) {
        Iterator blackListIt = blackList.iterator();
        while (blackListIt.hasNext()) {
          String entry = ((String) blackListIt.next()).toLowerCase();
          if (entry.startsWith("pattern:")) {
            String pattern = entry.substring("pattern:".length()).trim();
            if (url.matches(pattern)) {
              Logger.warn("invalid referrer url '" + url + "' by pattern '" + pattern + "'");
              return false;
            }
          } else {
            String host = new URL(url).getHost().toLowerCase();
            if (host.endsWith(entry.trim())) {
              Logger.warn("invalid referrer url '" + url + "' by domain '" + entry + "'");
              return false;
            }
          }
        }
      }
    } catch (MalformedURLException e) {
      Logger.warn("invalid referrer url '" + url + "': " + e.getMessage());
      return false;
    }
    return true;
  }

}
