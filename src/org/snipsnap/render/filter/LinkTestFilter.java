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
 * LinkTestFilter finds [text] in its input and transforms this
 * to <link name="text">
 * Additonally it checks, if the link really exists.
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package org.snipsnap.render.filter;

import org.apache.oro.text.regex.*;
import org.radeox.filter.Filter;
import org.radeox.filter.LinkTester;
import org.radeox.filter.context.FilterContext;
import org.radeox.util.StringBufferWriter;
import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.render.filter.interwiki.InterWiki;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.user.UserManager;

import java.io.IOException;
import java.io.Writer;

public class LinkTestFilter extends Filter {

  private LinkTester linkTester;
  private int limit = Util.SUBSTITUTE_ALL;
  private int interps = Perl5Substitution.INTERPOLATE_ALL;
  private PatternMatcher matcher = new Perl5Matcher();
  private Pattern pattern = null;
  private PatternCompiler compiler = new Perl5Compiler();
  private String _substitute;

  public LinkTestFilter() {
    linkTester = SnipSpace.getInstance();

    try {
      pattern = compiler.compile("\\[(.*?)\\]");
    } catch (MalformedPatternException e) {
      Logger.warn("error compiling pattern", e);
    }
    // super("\\[(.*?)\\]", "<link href=\"$1\"/>");
  }

  public String filter(String input, FilterContext context) {
    StringBuffer buffer = new StringBuffer("");

    PatternMatcherInput patternMatcherInput = new PatternMatcherInput(input);

    int lastmatch = 0;

    // Loop until there are no more matches left.
    MatchResult result;
    Writer writer = new StringBufferWriter(buffer);
    try {
      while (matcher.contains(patternMatcherInput, pattern)) {
        // Since we're still in the loop, fetch match that was found.
        result = matcher.getMatch();
        buffer.append(input.substring(lastmatch, result.beginOffset(0)));
        String targetSnip = result.group(1).trim();

        if (targetSnip != null) {
          int colonIndex = targetSnip.indexOf(':');
          int atIndex = targetSnip.indexOf('@');
          int hashIndex = targetSnip.indexOf('#');
          // typed link ?
          if (-1 != colonIndex) {
            // for now throw away the type information
            targetSnip = targetSnip.substring(colonIndex + 1);
          }
          // external link ?
          if (-1 != atIndex) {
            // We do not support # in external spaces
            String extSpace = targetSnip.substring(atIndex + 1);
            // known extarnal space ?
            InterWiki interWiki = InterWiki.getInstance();
            if (interWiki.contains(extSpace)) {
              targetSnip = targetSnip.substring(0, atIndex);
              interWiki.expand(writer, extSpace, targetSnip);
            } else {
              buffer.append(result.group(1)).append("*link error*");
            }
            // internal link
          } else {
            String hash = "";
            if (-1 != hashIndex) {
              hash = targetSnip.substring(hashIndex + 1);
              targetSnip = targetSnip.substring(0, hashIndex);
            }
            Application app = Application.get();

            if (linkTester.exists(targetSnip)) {
              if (-1 != hashIndex) {
                SnipLink.appendLink(buffer, targetSnip, result.group(1), hash);
              } else {
                SnipLink.appendLink(buffer, targetSnip, result.group(1));
              }
            } else if (UserManager.getInstance().isAuthenticated(app.getUser())) {
              SnipLink.createCreateLink(buffer, targetSnip);
            } else {
              // cannot edit/create snip, so just display the text
              buffer.append(targetSnip);
            }
          }
        } else {
          buffer.append(result.group(1));
        }

        lastmatch = result.endOffset(0);
      }
    } catch (IOException e) {
      Logger.warn("Unable to write LinkTestFilter", e);
    }
    buffer.append(input.substring(lastmatch));
    return buffer.toString();
  }
}
