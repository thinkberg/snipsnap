package com.neotis.test.snip;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;

public class SnipSpaceTest extends TestCase {
  public SnipSpaceTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(SnipSpaceTest.class);
    return s;
  }

  public void testSingleton() {
    assertNotNull("Singleton instance", SnipSpace.getInstance());
  }

  public void testLoadSnip() {
    Snip snip1 = SnipSpace.getInstance().create("A","A Content");
    Snip snip2 = SnipSpace.getInstance().load("A");
    assertEquals(snip2.getName(), "A");
    SnipSpace.getInstance().remove(snip1);
  }

  public void testParent() {
    Snip snip1 = SnipSpace.getInstance().create("A","A Content");
    Snip snip2 = SnipSpace.getInstance().create("B","B Content");
    snip2.setParent(snip1);
    assertEquals(snip1, snip2.getParent());
    SnipSpace.getInstance().remove(snip1);
    SnipSpace.getInstance().remove(snip2);
  }

  public void testExists() {
    Snip snip1 = SnipSpace.getInstance().create("A","A Content");
    assertTrue("Snip exists", SnipSpace.getInstance().exists("A"));
    SnipSpace.getInstance().remove(snip1);
  }

  public void testCreateAndDeleteSnip() {
    Snip snip1 = SnipSpace.getInstance().create("A","A Content");
    assertEquals(snip1.getName(), "A");
    assertEquals(snip1.getContent(), "A Content");
    SnipSpace.getInstance().remove(snip1);
  }

}