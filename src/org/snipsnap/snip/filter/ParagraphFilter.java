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
/*
 * The paragraph filter finds any text between two empty lines and sourrounds
 * that text with a <p> ... </p> tag.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package org.snipsnap.snip.filter;

import org.snipsnap.snip.filter.regex.RegexReplaceFilter;

public class ParagraphFilter extends RegexReplaceFilter {

  public ParagraphFilter() {
    // match anything between two empty lines (normalize sequential empty lines to one)
    super("(?:[ \t\r]*[\n]){2}", "<p class=\"paragraph\"/>", RegexReplaceFilter.SINGLELINE);
  };
}
