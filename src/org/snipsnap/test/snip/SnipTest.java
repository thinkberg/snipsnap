package com.neotis.test.snip;

import junit.framework.TestCase;
import junit.framework.Test;
import junit.framework.TestSuite;
import com.neotis.snip.Snip;

import java.sql.Date;

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

  public void testDateName() {
    assertEquals( Snip.toName(new Date(new java.util.Date("01 Jan 2002").getTime())) , "2002-01-01");
  }

}
