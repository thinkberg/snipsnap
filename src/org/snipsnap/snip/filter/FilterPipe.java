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
 * FilterPipe is a collection of Filters which are
 * applied to an input to generate output
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package org.snipsnap.snip.filter;

import org.snipsnap.snip.Snip;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FilterPipe {

  Collection filterList = null;

  public FilterPipe() {
    filterList = new ArrayList();
  }

  public void addFilter(Filter filter) {
    filterList.add(filter);
  }

  public String filter(String input, Snip snip) {

    String output = input;
    Iterator filterIterator = filterList.iterator();

    // Apply every filter in _filterList to input string
    while(filterIterator.hasNext()) {
      Filter f = (Filter) filterIterator.next();
      String tmp = f.filter(output, snip);
      if(null == tmp) {
        System.err.println("error while filtering: "+f);
      } else {
        output = tmp;
      }
    }

    return output;
  }
}