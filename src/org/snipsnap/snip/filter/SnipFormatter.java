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
package com.neotis.snip.filter;

import com.neotis.snip.SnipSpace;
import com.neotis.snip.Snip;

/**
 * SnipFormatter supplies some methods for handling Snip Content.
 *
 * @author stephan
 * @version $Id$
 **/
public class SnipFormatter {

  public static String toXML(Snip snip, String content) {
    FilterPipe fp = new FilterPipe();
    fp.addFilter(new EscapeFilter());
    fp.addFilter(new MacroFilter());
    fp.addFilter(new MacroFilter());
    fp.addFilter(new HeadingFilter());
    fp.addFilter(new ListFilter());
    fp.addFilter(new NewlineFilter());
    fp.addFilter(new ParagraphFilter());
    fp.addFilter(new LineFilter());
    fp.addFilter(new BoldFilter());
    fp.addFilter(new ItalicFilter());
    fp.addFilter(new LinkTestFilter(SnipSpace.getInstance()));
    fp.addFilter(new MarkFilter());
    fp.addFilter(new UrlFilter());
    fp.addFilter(new KeyFilter());

    return fp.filter(content, snip);
  }
}