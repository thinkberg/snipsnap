package com.neotis.snip.filter;

import com.neotis.snip.filter.BoldFilter;
import com.neotis.snip.filter.EscapeFilter;
import com.neotis.snip.filter.FilterPipe;
import com.neotis.snip.filter.HeadingFilter;
import com.neotis.snip.filter.ItalicFilter;
import com.neotis.snip.filter.KeyFilter;
import com.neotis.snip.filter.LineFilter;
import com.neotis.snip.filter.LinkTestFilter;
import com.neotis.snip.filter.ListFilter;
import com.neotis.snip.filter.MarkFilter;
import com.neotis.snip.filter.NewlineFilter;
import com.neotis.snip.filter.ParagraphFilter;
import com.neotis.snip.filter.UrlFilter;
import com.neotis.snip.filter.macro.MacroFilter;
import com.neotis.xml.JDOMUtil;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.StringReader;

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
    fp.addFilter(new LinkTestFilter(new LinkTester() {
      public boolean exists(String link) {
        return false;
      }
    }));
    fp.addFilter(new MarkFilter());
    fp.addFilter(new UrlFilter());
    fp.addFilter(new KeyFilter());

    return fp.filter(content);
  }

  private static XMLOutputter xmlOutputter = new XMLOutputter();
  private static Element dummy = new Element("dummy");
  private static SAXBuilder saxBuilder = new SAXBuilder();

  /**
   * Create a partial document from content by first parsing it and then
   * transforming it into an XML document.
   * @param content the textual content
   * @param root the root element name the content is sourrounded with
   * @return a root element named after root containing the text content
   */
  public static Element toXML(String content, String root) {
    dummy.setText(content);
    String text = xmlOutputter.outputString(dummy);
    text = "<" + root + ">" + toXML(text.substring(7, text.length() - 8)) + "</" + root + ">";

    StringReader sr = new StringReader(text);

    try {
      // build document from string and detach root element
      Element xml = saxBuilder.build(sr).getRootElement();
      xml.detach();
      return xml;
    } catch (JDOMException e) {
      System.err.println("error while reading converted document: " + e);
      return JDOMUtil.getElementInstance(root, e.toString());
    }

  }
}