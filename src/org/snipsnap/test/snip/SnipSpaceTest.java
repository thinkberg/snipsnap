package com.neotis.test;

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
    Snip snip1 = SnipSpace.getInstance().load("about");
    assertEquals(snip1.getName(), "about");
  }

  public void testExists() {
    Snip snip1 = SnipSpace.getInstance().create("funzel","stephan");
    assertTrue("Snip exists", SnipSpace.getInstance().exists("funzel"));
    SnipSpace.getInstance().remove(snip1);
  }

  public void testCreateAndDeleteSnip() {
    Snip snip1 = SnipSpace.getInstance().create("funzel","stephan");
    assertEquals(snip1.getName(), "funzel");
    assertEquals(snip1.getContent(), "stephan");
    SnipSpace.getInstance().remove(snip1);
  }

}