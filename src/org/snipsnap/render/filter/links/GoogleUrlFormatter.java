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

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Formats urls with backlinks from Google to show search terms etc.
 *
 * Example:
 * http://www.google.de/search?q=pivot%20weblog&ie=UTF-8&oe=UTF-8&hl=de&meta=
 *
 * @author stephan
 * @version $Id$
 */

public class GoogleUrlFormatter implements UrlFormatter {
  private UrlFormatter parent;

  public void setParent(UrlFormatter parent) {
    this.parent = parent;
  };

  public String format(String urlString) {
    String result = urlString;

    if (urlString.startsWith("http://www.google.de/")) {
      URL url = null;
      try {
        url = new URL(urlString);
        result = "Google: ";
        String query = url.getQuery();
      } catch (MalformedURLException e) {
        // silently ignore.
      }
    }
    return result;
  };
}
