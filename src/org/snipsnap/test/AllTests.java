package com.neotis.test.snip;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.neotis.test.filter.FilterTest;

public class AllTests extends TestCase {
  public AllTests(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SnipSpaceTest.class);
    s.addTestSuite(SnipTest.class);
    s.addTestSuite(FilterTest.class);
    s.addTestSuite(ChildrenTest.class);
    return s;
  }
}