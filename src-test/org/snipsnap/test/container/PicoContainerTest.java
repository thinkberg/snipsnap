package org.snipsnap.test.container;

import junit.framework.TestCase;
import org.snipsnap.container.PicoContainer;
import org.snipsnap.container.Container;

public class PicoContainerTest extends TestCase {
    public void testFindComponents() {
       Container container = new PicoContainer();
        container.addComponent(TestService.class);
        assertTrue("findComponents finds one component", container.findComponents(TestService.class).size() == 1);
    }
}
