/*
 * Copyright (c) 2004 Thomas Mohaupt
 * All Rights Reserved.
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
package org.snipsnap.test.label;

import java.util.Collection;

import junit.framework.Test;
import junit.framework.TestSuite;

import snipsnap.api.snip.Snip;
import org.snipsnap.snip.SnipImpl;
import org.snipsnap.snip.label.CategoryLabel;
import org.snipsnap.snip.label.DefaultLabel;
import snipsnap.api.label.Label;
import snipsnap.api.label.Labels;
import org.snipsnap.snip.label.TypeLabel;
import org.snipsnap.test.snip.SnipTestSupport;


/**
 * LabelsTest
 *
 */
public class LabelsTest extends SnipTestSupport {
  private snipsnap.api.label.Labels m_emptyLabels;
  private snipsnap.api.label.Labels m_filledLabels;
  private int m_numberOfLabels;
  private Snip m_aSnip;

  public LabelsTest(String name) {
    super(name);
  }

  public static Test suite() {
    return new TestSuite(LabelsTest.class);
  }

  protected void setUp() throws Exception {
    m_aSnip = new SnipImpl("Labeled Snip", "Test Content");
    m_emptyLabels = new snipsnap.api.label.Labels();
    m_filledLabels = new snipsnap.api.label.Labels(m_aSnip, "TypeLabel:Type:Template"
        + "|CategoryLabel:Category:zz"
        + "|CategoryLabel:Category:yy"
        + "|CategoryLabel:Category:xx"
    );
    m_numberOfLabels = 4;
    super.setUp();
  }

  public void testInitLabels() {
    Collection c = m_emptyLabels.getAll();
    assertTrue(c.isEmpty());

    c = m_filledLabels.getAll();
    assertTrue(!c.isEmpty());
  }

  public void testGetAll() {
    assertEquals(0, m_emptyLabels.getAll().size());
    assertEquals(m_numberOfLabels, m_filledLabels.getAll().size());
  }

  public void testAddLabel() {
    Label label = new DefaultLabel();
    m_emptyLabels.addLabel(label);
    m_filledLabels.addLabel(label);
    assertEquals(1, m_emptyLabels.getAll().size());
    assertEquals(m_numberOfLabels + 1, m_filledLabels.getAll().size());
  }

// Searching by Value is not supported
//
//  public void testGetLabel() {
//    String value = "testcategory";
//    Label label = new CategoryLabel(value);
//    m_emptyLabels.addLabel(label);
//    assertEquals(label, m_emptyLabels.getLabel(value));
//
//    m_filledLabels.addLabel(label);
//    m_filledLabels.addLabel(label);
//    assertEquals(label, m_filledLabels.getLabel(value));
//  }

  protected void getLabels(Labels aLabels, String name) {
    int before = aLabels.getAll().size();

    snipsnap.api.label.Label c1 = new CategoryLabel("1");
    snipsnap.api.label.Label c2 = new CategoryLabel("2");
    snipsnap.api.label.Label c3 = new CategoryLabel("3");
    Label t1 = new TypeLabel("1");
    snipsnap.api.label.Label t2 = new TypeLabel("2");

    aLabels.addLabel(c1);
    aLabels.addLabel(c2);
    aLabels.addLabel(c3);
    aLabels.addLabel(t1);
    aLabels.addLabel(t2);

    Collection coll = aLabels.getAll();
    assertEquals(before + 5, coll.size());

    assertTrue(coll.contains(c1));
    assertTrue(coll.contains(c2));
    assertTrue(coll.contains(c3));
    assertTrue(coll.contains(t1));
    assertTrue(coll.contains(t2));
  }

  public void testGetLabels() {
    getLabels(m_emptyLabels, "m_emptyLabels");
    getLabels(m_filledLabels, "m_filledLabels");
  }

  protected void removeLabels(snipsnap.api.label.Labels aLabels, String name) {
    int before = aLabels.getAll().size();

    snipsnap.api.label.Label c1 = new CategoryLabel("1");
    Label c2 = new CategoryLabel("2");
    snipsnap.api.label.Label t1 = new TypeLabel("1");

    aLabels.addLabel(c1);
    aLabels.addLabel(c2);
    aLabels.addLabel(t1);

    aLabels.removeLabel(c2.getName(), c2.getValue());
    aLabels.removeLabel(t1.getName(), t1.getValue());
    aLabels.removeLabel(c1.getName(), c1.getValue());

    Collection coll = aLabels.getAll();
    assertEquals(before, coll.size());

    assertTrue(name + ": remove labels", !coll.contains(c1));
    assertTrue(name + ": remove labels", !coll.contains(c2));
    assertTrue(name + ": remove labels", !coll.contains(t1));

    //assertTrue(false);
  }

  protected void removeOneLabel(Labels aLabels, String name) {
    try {
      aLabels.removeLabel("any", "any");
    } catch (Exception ex) {
      assertTrue(name + ": remove not existing label", false);
      ex.printStackTrace();
    }

    int before = aLabels.getAll().size();

    snipsnap.api.label.Label c1 = new CategoryLabel("1");
    aLabels.addLabel(c1);

    aLabels.removeLabel(c1.getName(), c1.getValue());

    Collection coll = aLabels.getAll();
    assertEquals(name + ": remove a label", before, coll.size());

    assertTrue(name + ": remove right label", !coll.contains(c1));
  }

  public void testRemoveOneLabel() {
    removeOneLabel(m_emptyLabels, "m_emptyLabels");
    removeOneLabel(m_filledLabels, "m_filledLabels");
  }

  public void testRemoveLabels() {
    removeLabels(m_emptyLabels, "m_emptyLabels");
    removeLabels(m_filledLabels, "m_filledLabels");
  }

}
