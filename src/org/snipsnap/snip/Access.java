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

import org.snipsnap.app.Application;
import org.snipsnap.user.User;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Stores Access information for a snip like viewCount, backLinks, ...
 *
 * @author stephan
 * @version $Id$
 */

public class Access {
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
    User user = Application.get().getUser();
    if (!user.isNonUser()) {
      incViewCount();
      String referrer = request.getHeader("REFERER");
      if (null != referrer) {
        // Decode URL to remove jsessionid for example
        // referrer =
        String domain = Application.get().getConfiguration().getUrl();
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
            if (!Application.get().getConfiguration().getStartSnip().equals(name)
              && !snipName.equals(name)) {
              snipLinks.addLink(name);
            }
          }
        } else {
          // Referrer was external link
          String url = SnipLink.decode(referrer);
          // do not count localhosts, single hosts. Will
          // not find local network IPs and MacOS X
          // hosts like megid.local
          if (! isLocalhost(referrer)) {
            backLinks.addLink(url);
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

  public static boolean isLocalhost(String url) {
    boolean isLocalhost = true;
    try {
      URL refURL = new URL(url);
      if (refURL.getHost().indexOf(".") > -1) {
        isLocalhost = false;
      }
    } catch (MalformedURLException e) {
      // not an URL, so allways drop
    }
    return isLocalhost;
  }

}
