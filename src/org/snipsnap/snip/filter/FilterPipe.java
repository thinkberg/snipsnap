/*
 * FilterPipe is a collection of Filters which are
 * applied to an input to generate output
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */
package com.neotis.snip.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class FilterPipe {

  Collection _filterList = null;

  public FilterPipe() {
    _filterList = new ArrayList();
  }

  public void addFilter(Filter filter) {
    _filterList.add(filter);
  }

  public String filter(String input) {

    String output = input;
    Iterator filterIterator = _filterList.iterator();

    // Apply every filter in _filterList to input string
    while(filterIterator.hasNext()) {
      Filter f = (Filter) filterIterator.next();
      String tmp = f.filter(output);
      if(null == tmp) {
        System.err.println("error while filtering: "+f);
      } else {
        output = tmp;
      }
    }

    return output;
  }
}