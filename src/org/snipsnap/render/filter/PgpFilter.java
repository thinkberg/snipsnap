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

import org.radeox.filter.regex.RegexTokenFilter;
import org.radeox.regex.MatchResult;
import org.radeox.filter.context.FilterContext;
import org.radeox.util.StringBufferWriter;
import org.snipsnap.render.filter.context.SnipFilterContext;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipLink;

import java.io.IOException;
import java.io.Writer;

/*
 * Class that finds snippets that are surrounded
 * by PGP wrapper. If the snip is signed with PGP
 * this si shown with a link to the raw context
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class PgpFilter extends RegexTokenFilter {

  public PgpFilter() {
    super("--PGP SIGNED--", SINGLELINE);
  }

  public void handleMatch(StringBuffer buffer, MatchResult result, FilterContext context) {
    Snip snip = ((SnipFilterContext) context).getSnip();
    Writer writer = new StringBufferWriter(buffer);
    try {
      SnipLink.appendImage(writer, "Icon-Key", "");
      writer.write(result.group(0));
    } catch (IOException e) {
      // ignore
    }
  }
}
