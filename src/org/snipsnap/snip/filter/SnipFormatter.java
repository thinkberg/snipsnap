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
package org.snipsnap.snip.filter;

import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.Snip;

/**
 * SnipFormatter supplies some methods for handling Snip Content.
 *
 * @author stephan
 * @version $Id$
 **/
public class SnipFormatter {

  static public FilterPipe fp;
  private static Object monitor = new Object();

  public static String toXML(Snip snip, String content) {
    synchronized (monitor) {
      if (null == fp) {
        fp = new FilterPipe();
        fp.addFilter("org.snipsnap.snip.filter.EscapeFilter");
        fp.addFilter("org.snipsnap.snip.filter.ParamFilter");
        fp.addFilter("org.snipsnap.snip.filter.MacroFilter");
        fp.addFilter("org.snipsnap.snip.filter.MacroFilter");
        fp.addFilter("org.snipsnap.snip.filter.CalendarFilter");
        fp.addFilter("org.snipsnap.snip.filter.HeadingFilter");
        fp.addFilter("org.snipsnap.snip.filter.StrikeThroughFilter");
        fp.addFilter("org.snipsnap.snip.filter.ListFilter");
        fp.addFilter("org.snipsnap.snip.filter.NewlineFilter");
        fp.addFilter("org.snipsnap.snip.filter.ParagraphFilter");
        fp.addFilter("org.snipsnap.snip.filter.LineFilter");
        fp.addFilter("org.snipsnap.snip.filter.BoldFilter");
        fp.addFilter("org.snipsnap.snip.filter.ItalicFilter");
        fp.addFilter("org.snipsnap.snip.filter.UrlFilter");
        fp.addFilter("org.snipsnap.snip.filter.LinkTestFilter");
        fp.addFilter("org.snipsnap.snip.filter.MarkFilter");
        fp.addFilter("org.snipsnap.snip.filter.KeyFilter");
        fp.addFilter("org.snipsnap.snip.filter.LateMacroFilter");
        fp.addFilter("org.snipsnap.snip.filter.TypographyFilter");
      }
    }
    return fp.filter(content, snip);
  }
}