package com.neotis.snip.filter;

import com.neotis.snip.filter.macro.MacroFilter;
import com.neotis.snip.SnipSpace;

/**
 * SnipFormatter supplies some methods for handling Snip Content.
 *
 * @author stephan
 * @version $Id$
 **/
public class SnipFormatter {

  public static String toXML(String content) {
    FilterPipe fp = new FilterPipe();
    fp.addFilter(new EscapeFilter());
    fp.addFilter(new MacroFilter());
    fp.addFilter(new HeadingFilter());
    fp.addFilter(new ListFilter());
    fp.addFilter(new NewlineFilter());
    fp.addFilter(new ParagraphFilter());
    fp.addFilter(new LineFilter());
    fp.addFilter(new BoldFilter());
    fp.addFilter(new ItalicFilter());
    fp.addFilter(new LinkTestFilter(SnipSpace.getInstance());
    fp.addFilter(new MarkFilter());
    fp.addFilter(new UrlFilter());
    fp.addFilter(new KeyFilter());

    return fp.filter(content);
  }
}