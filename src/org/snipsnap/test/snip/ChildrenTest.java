package com.neotis.test.snip;

import com.neotis.snip.Snip;
import com.neotis.snip.SnipSpace;
import com.neotis.app.Application;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ChildrenTest extends TestCase {
  public ChildrenTest(String name) {
    super(name);
  }

  public static Test suite() {
    TestSuite s = new TestSuite();
    s.addTestSuite(ChildrenTest.class);
    return s;
  }

  public void testChildren() {
    Application app = new Application();
    Snip snip1 = SnipSpace.getInstance().create("A", "A Content", app);
    Snip snip2 = SnipSpace.getInstance().create("B", "B Content", app);
    snip1.addSnip(snip2);
    assertEquals("Correct Parent", snip1, snip2.getParent());
    assertTrue("Children not null", snip1.getChildren() != null);
    assertTrue("One Child", snip1.getChildren().size() == 1);
    assertTrue("Children contain added Snip", snip1.getChildren().contains(snip2));

    snip1.removeSnip(snip2);
    assertTrue("Empty after removal", snip2.getChildren().isEmpty());

    SnipSpace.getInstance().remove(snip1);
    SnipSpace.getInstance().remove(snip2);
  }

}