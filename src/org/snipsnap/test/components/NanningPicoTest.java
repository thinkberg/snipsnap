/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://snipsnap.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 * --LICENSE NOTICE--
 */

package org.snipsnap.test.components;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.config.AspectSystem;
import org.snipsnap.interceptor.custom.MissingSnipAspect;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.test.mock.MockSnipSpace;
import org.nanocontainer.nanning.NanningNanoContainer;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultComponentFactory;
import org.picocontainer.hierarchical.HierarchicalPicoContainer;

public class NanningPicoTest extends TestCase {
  public NanningPicoTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
  }

  public static Test suite() {
    return new TestSuite(NanningPicoTest.class);
  }

  public void testSnipSpaceIsAdviced() {
    AspectSystem as = new AspectSystem();
    as.addAspect(new MissingSnipAspect());

    NanningNanoContainer nc = null;
    try {
      RegistrationPicoContainer c = new HierarchicalPicoContainer.Default();

      nc = new NanningNanoContainer(new DefaultComponentFactory(), c, as);
      nc.registerComponent(SnipSpace.class, MockSnipSpace.class);

      nc.instantiateComponents();
    } catch (Exception e) {
      e.printStackTrace();  //To change body of catch statement use Options | File Templates.
    }

    SnipSpace space = (SnipSpace) nc.getComponent(SnipSpace.class);

    assertNotNull("SnipSpace is not null from Components", space);
    assertTrue("SnipSpace is aspected", Aspects.isAspectObject(space));
  }
}
