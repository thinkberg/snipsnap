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
package org.snipsnap.snip.filter;

import org.apache.oro.text.regex.*;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.util.Transliterate;
import org.snipsnap.app.Application;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import java.util.HashMap;
import java.util.Map;

public class LinkTestFilter extends Filter {

  LinkTester linkTester;
  int limit = Util.SUBSTITUTE_ALL;
  int interps = Perl5Substitution.INTERPOLATE_ALL;
  PatternMatcher matcher = new Perl5Matcher();
  Pattern pattern = null;
  PatternCompiler compiler = new Perl5Compiler();
  String _substitute;
  Transliterate trans;
  Map wikiSpaces = new HashMap();

  public LinkTestFilter(LinkTester linkTester) {
    this.linkTester = linkTester;
    trans = new Transliterate("romaji.properties");

    try {
      pattern = compiler.compile("\\[(.*?)\\]");
    } catch (MalformedPatternException e) {
      System.err.println("error compiling pattern: " + e);
    }

    // @TODO read from config
    wikiSpaces.put("LCOM", "http://www.langreiter.com/space/");
    wikiSpaces.put("ESA", "http://earl.strain.at/space/");
    wikiSpaces.put("C2", "http://www.c2.com/cgi/wiki?");
    wikiSpaces.put("WeblogKitchen", "http://www.weblogkitchen.com/wiki.cgi?");
    wikiSpaces.put("meatball", "http://www.usemod.com/cgi-bin/mb.pl?");

    // super("\\[(.*?)\\]", "<link href=\"$1\"/>");
  }

  public String filter(String input, Snip snip) {
    StringBuffer buffer = new StringBuffer("");

    PatternMatcherInput patternMatcherInput = new PatternMatcherInput(input);

    int lastmatch = 0;

    // Loop until there are no more matches left.
    MatchResult result;
    while (matcher.contains(patternMatcherInput, pattern)) {
      // Since we're still in the loop, fetch match that was found.
      result = matcher.getMatch();
      buffer.append(input.substring(lastmatch, result.beginOffset(0)));
      String targetSnip = result.group(1);
      if (targetSnip.startsWith("&#")) {
        System.out.println("native2ascii: "+targetSnip);
        targetSnip = trans.nativeToAscii(targetSnip);
      }

      if (targetSnip != null) {
        int colonIndex = targetSnip.indexOf(':');
        int atIndex = targetSnip.indexOf('@');
        // typed link ?
        if (-1 != colonIndex) {
          // for now throw away the type information
          targetSnip = targetSnip.substring(colonIndex + 1);
        }
        // external link ?
        if (-1 != atIndex) {
          String extSpace = targetSnip.substring(atIndex + 1);
          // known extarnal space ?
          if (wikiSpaces.containsKey(extSpace)) {
            targetSnip = targetSnip.substring(0, atIndex);
            buffer.append("<a href=\"");
            buffer.append(wikiSpaces.get(extSpace));
            buffer.append(SnipLink.encode(targetSnip));
            buffer.append("\">");
            buffer.append(targetSnip);
            buffer.append("@");
            buffer.append(extSpace);
            buffer.append("</a>");
          } else {
            buffer.append(result.group(1)).append("*link error*");
          }
          // internal link
        } else {
          Application app = Application.get();
          if (linkTester.exists(targetSnip)) {
            SnipLink.appendLink(buffer, targetSnip, result.group(1));
          } else if(UserManager.getInstance().isAuthenticated(app.getUser())) {
            buffer.append(EscapeFilter.escape('['));
            buffer.append("create <a href=\"../exec/edit?name=");
            buffer.append(SnipLink.encode(targetSnip));
            buffer.append("\">").append(result.group(1)).append("</a>");
            buffer.append(EscapeFilter.escape(']'));
          } else {
            // cannot edit/create snip, so just display the text
            buffer.append(result.group(1));
          }
        }
      } else {
        buffer.append(result.group(1)).append("*link error*");
      }

      lastmatch = result.endOffset(0);
    }
    buffer.append(input.substring(lastmatch));
    return buffer.toString();
  }
}
