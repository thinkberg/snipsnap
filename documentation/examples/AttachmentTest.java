/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */

package examples;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.radeox.macro.Macro;
import org.radeox.api.engine.context.RenderContext;
import org.snipsnap.render.context.SnipRenderContext;
import org.snipsnap.render.macro.parameter.SnipMacroParameter;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.test.mock.MockSnipSpace;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Test for the attachment examples
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class AttachmentTest extends TestCase {
  private StringWriter writer;

  public AttachmentTest(String name) {
    super(name);
  }

  protected void setUp() throws Exception {
    writer = new StringWriter();
    super.setUp();
  }

  public static Test suite() {
    return new TestSuite(AttachmentTest.class);
  }

  public void testShowAttachments() {
    SnipSpace space = new MockSnipSpace();
    Snip snip = new SnipImpl("HelloSnip","HelloSnip");

    RenderContext context = new SnipRenderContext(snip, space);
    SnipMacroParameter parameter = new SnipMacroParameter(context);

    Macro macro = new ShowAttachmentsMacro();
    try {
      macro.execute(writer, parameter);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();

    } catch (IOException e) {
      e.printStackTrace();
    }
    assertEquals("Attachments are written.", "hello", writer.toString());
  }
}
