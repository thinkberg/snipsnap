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
 * Class that applies a RegexFilter, can be subclassed
 * for special Filters
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

package com.neotis.snip.filter.regex;

import org.apache.oro.text.regex.*;

import java.util.List;
import java.util.ArrayList;

import com.neotis.snip.filter.Filter;
import com.neotis.snip.Snip;

public abstract class RegexFilter extends Filter {
  int limit = Util.SUBSTITUTE_ALL;
  int interps = Perl5Substitution.INTERPOLATE_ALL;
  PatternMatcher matcher = new Perl5Matcher();
  PatternCompiler compiler = new Perl5Compiler();
  List pattern = new ArrayList();
  List substitute = new ArrayList();
  public final static boolean MULTILINE = true;
  public final static boolean SINGLELINE = false;

  public void addRegex(String regex, String substitute) {
    addRegex(regex, substitute, MULTILINE);
  }

  public void addRegex(String regex, String substitute, boolean multiline) {
    try {
      this.pattern.add(compiler.compile(regex, multiline ? Perl5Compiler.MULTILINE_MASK : Perl5Compiler.SINGLELINE_MASK));
      this.substitute.add(substitute);
    } catch(MalformedPatternException e) {
      System.err.println("bad pattern: " + e);
    }
  }

  public abstract String filter(String input, Snip snip);
}
