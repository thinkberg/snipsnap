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

package org.snipsnap.render.filter.links;

/**
 * Formats urls to a short url
 *
 * Example:
 * http://www.google.de/search?q=pivot%20weblog&ie=UTF-8&oe=UTF-8&hl=de&meta=
 *
 * @author stephan
 * @version $Id$
 */

public class CutLengthFormatter implements UrlFormatter {
  private UrlFormatter parent;

  public void setParent(UrlFormatter parent) {
    this.parent = parent;
  };

  public String format(String urlString) {
    String result = urlString;
    if (urlString.startsWith("http://")) {
      result = cutLength(urlString ,90);
    }
    return result;
  };

  public static String cutLength(String url, int len) {
    if (url != null && len > 3 && url.length() > len) {
      return url.substring(0, len - 3) + "...";
    }
    return url;
  }

}
