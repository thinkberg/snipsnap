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

import org.snipsnap.snip.filter.regex.RegexTokenFilter;
import org.snipsnap.snip.Snip;
import org.snipsnap.util.log.Logger;
import org.snipsnap.util.log.SystemOutLogger;
import org.apache.oro.text.regex.MatchResult;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;
import java.util.Map;
import java.util.HashMap;

/*
 * NewlineFilter finds # in its input and transforms this
 * to <newline/>
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
public class ListFilter extends RegexTokenFilter {

  private final static Map openList = new HashMap();
  private final static Map closeList = new HashMap();

  public ListFilter() {
    super("^[[:space:]]*([-*][[:space:]]+|[iIaA1ghHkK]\\.[[:space:]]+)(\r?\n[[:space:]]*(?:[-*][[:space:]]+|[iIaA1ghHkK]\\.[[:space:]]+)|.)*$", MULTILINE);
    openList.put("-", "<ul class=\"minus\">");
    openList.put("*", "<ul class=\"star\">");
    openList.put("i", "<ol class=\"roman\">");
    openList.put("I", "<ol class=\"ROMAN\">");
    openList.put("a", "<ol class=\"alpha\">");
    openList.put("A", "<ol class=\"ALPHA\">");
    openList.put("g", "<ol class=\"greek\">");
    openList.put("h", "<ol class=\"hiragana\">");
    openList.put("H", "<ol class=\"HIRAGANA\">");
    openList.put("k", "<ol class=\"katakana\">");
    openList.put("K", "<ol class=\"KATAKANA\">");
    openList.put("1", "<ol>");
    closeList.put("-", "</ul>");
    closeList.put("*", "</ul>");
    closeList.put("i", "</ol>");
    closeList.put("I", "</ol>");
    closeList.put("a", "</ol>");
    closeList.put("A", "</ol>");
    closeList.put("1", "</ol>");
    closeList.put("g", "</ol>");
    closeList.put("G", "</ol>");
    closeList.put("h", "</ol>");
    closeList.put("H", "</ol>");
    closeList.put("k", "</ol>");
    closeList.put("K", "</ol>");
  };

  public void handleMatch(StringBuffer buffer, MatchResult result, Snip snip) {
    try {
      Logger.log("ListFilter: "+result.groups());
      BufferedReader reader = new BufferedReader(new StringReader(result.group(0)));
      addList(buffer, reader);
    } catch (Exception e) {
      Logger.log("ListFilter: cannot read list: "+e);
      e.printStackTrace();
    }
  }

  private void addList(StringBuffer buffer, BufferedReader reader) throws IOException {
    String lastBullet = null;
    String line = null;
    while((line = reader.readLine()) != null) {
      Logger.log("'"+line+"'");
      // no nested list handling, trim lines:
      line = line.trim();
      if(line.length() == 0) {
        continue;
      }
      String bullet = line.substring(0,1);

      Logger.log("found bullet: ('"+lastBullet+"') '"+bullet+"'");
      // check whether we find a new list
      if(!bullet.equals(lastBullet)) {
        Logger.log("new list detected ...");
        if(lastBullet != null) {
          buffer.append(closeList.get(lastBullet)).append("\n");
        }
        buffer.append(openList.get(bullet)).append("\n");
      }
      buffer.append("<li>");
      buffer.append(line.substring(line.indexOf(' ')+1));
      buffer.append("</li>\n");
      lastBullet = bullet;
    }
    buffer.append(closeList.get(lastBullet));
  }
/*
  public static void main(String args[]) {
    Logger.setHandler(new SystemOutLogger());
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(args[0]));
    } catch (FileNotFoundException e) {
      System.err.println("can't read file: "+args[0]);
      System.exit(-1);
    }
    String buf = "", line = null;
    try {
      while(null != (line = reader.readLine())) {
        buf += line + "\n";
      }
    } catch (IOException e) {
      System.err.println("can't read input");
      System.exit(-1);
    }

    ListFilter filter = new ListFilter();
    System.out.println(filter.filter(buf, null));

  }
*/
}
