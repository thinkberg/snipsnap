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
    super("(^|\\s+)neotis(\\s?|[.!,:]?|$)", " <elink href=\"http://neotis.de/\" name=\"neotis&#174;\"/> ");
  };
}