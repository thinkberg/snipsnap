package com.neotis.test.snip;

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
    Snip snip1 = new Snip("A", "A Content");
    assertEquals(snip1.getName(), "A");
  }

  public void testContent() {
    Snip snip1 = new Snip("A", "A Content");
    assertEquals(snip1.getContent(), "A Content");
  }

}
