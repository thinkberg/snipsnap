package com.neotis.test.snip;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllSnips extends TestCase {
  public AllSnips(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SnipSpaceTest.class);
    s.addTestSuite(SnipTest.class);
    s.addTestSuite(ChildrenTest.class);
    return s;
  }
}