package com.neotis.test;

import com.neotis.test.filter.FilterTest;
import com.neotis.test.snip.AllSnips;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AllTests extends TestCase {
  public AllTests(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(FilterTest.class);
    s.addTestSuite(AllSnips.class);
    return s;
  }
}