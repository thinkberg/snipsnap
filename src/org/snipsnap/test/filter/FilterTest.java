package com.neotis.test.filter;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;
import com.neotis.snip.filter.BoldFilter;
import com.neotis.snip.filter.Filter;
import com.neotis.snip.filter.ItalicFilter;

public class FilterTest extends TestCase {
  public FilterTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(FilterTest.class);
    return s;
  }

  public Snip getMock() {
    return new Snip("mock","mock");
  }

  public void testBold() {
    Filter filter = new BoldFilter();
    assertEquals("<span class=\"bold\">Text</span>", filter.filter("__Text__" , getMock()));
  }

  public void testItalic() {
    Filter filter = new ItalicFilter();
    assertEquals("<span class=\"italic\">Text</span>", filter.filter("~~Text~~", getMock()));
  }

}
