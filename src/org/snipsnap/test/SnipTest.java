package com.neotis.test;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;

public class SnipTest extends TestCase {
  public SnipTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SnipTest.class);
    return s;
  }

  public void testName() {
    Snip snip1 = new Snip("testName", "testContent");
    assertEquals(snip1.getName(), "testName");
  }

  public void testContent() {
    Snip snip1 = new Snip("testName", "testContent");
    assertEquals(snip1.getContent(), "testContent");
  }
}
