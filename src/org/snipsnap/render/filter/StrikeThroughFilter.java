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

package org.snipsnap.render.filter;

import org.snipsnap.render.filter.regex.RegexReplaceFilter;

/*
 * StrikeThrough finds --text-- in its input and transforms this
 * striked through text.
 *
 * @author Stephan J. Schmidt
 * @team sonicteam
 * @version $Id$
 */

public class StrikeThroughFilter extends RegexReplaceFilter {

  public StrikeThroughFilter() {
    super("([^-]|^)--([^-]+)--([^-]|$)", "$1<strike class=\"strike\">$2</strike>$3");
  };
}