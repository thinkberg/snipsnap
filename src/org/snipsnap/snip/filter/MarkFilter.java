/*
 * Filter for special words ...
 * @author leo
 * @team other
 * @version $Id$
 */
package com.neotis.snip.filter;

import com.neotis.snip.filter.regex.RegexReplaceFilter;

public class MarkFilter extends RegexReplaceFilter {

  public MarkFilter() {
    super("(^|\\s+)neotis(\\s?|[.!,:]?|$)", "<a href=\"http://neotis.de/\">neotis&#174;</a>");
  };
}