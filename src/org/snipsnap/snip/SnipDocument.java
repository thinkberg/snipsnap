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

package org.snipsnap.snip;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.Labels;
import org.radeox.filter.LinkTestFilter;
import org.radeox.filter.context.BaseFilterContext;
import org.radeox.filter.context.FilterContext;
import org.radeox.engine.context.BaseRenderContext;
import org.radeox.engine.context.BaseInitialRenderContext;
import org.radeox.api.engine.WikiRenderEngine;
import org.radeox.api.engine.RenderEngine;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.test.filter.mock.MockWikiRenderEngine;

import java.util.Iterator;
import java.io.Writer;
import java.io.IOException;
import java.io.Reader;

/**
 * Prepares a snip for indexing by lucene. Lucene needs
 * data stored in "documents" to index them. SnipDocument
 * gets information from a Snip and sets the corresponding
 * fields in a document, e.g. "author", "content", "title" etc.
 *
 * @author stephan
 * @version $Id$
 */

public class SnipDocument {
  /**
   * Get the information of a snip wrapped in a Lucene
   * document. Lucene can only index documents.
   *
   * @param snip Snip to wrap
   * @return document Document that Lucene can index
   */
  public static Document Document(Snip snip) {
    final Document doc = new Document();
    // We need this ID to correctly remove documents from
    // the index. This is done with hashCode because
    // lucene has problems with Unicode in id's
    // (at least the version used when this was written)
    doc.add(Field.Text("id", Integer.toHexString(snip.getName().hashCode())));
    doc.add(Field.Text("content", snip.getContent()));
    doc.add(Field.Text("title", snip.getName()));
    // author instead of CUser is clearer to the user
    doc.add(Field.Text("author", snip.getCUser()));
    doc.add(Field.Text("muser", snip.getMUser()));
    doc.add(Field.Text("owner", snip.getOwner().getName()));

    Labels labels = snip.getLabels();
    Iterator iterator = labels.getAll().iterator();
    while (iterator.hasNext()) {
      Label label = (Label) iterator.next();
      label.index(doc);
    }

    LinkTestFilter linkDetector = new LinkTestFilter();
    linkDetector.setInitialContext(new BaseInitialRenderContext());

    FilterContext context = new BaseFilterContext();
    context.setRenderContext(new BaseRenderContext());
    context.getRenderContext().setRenderEngine(new ReferenceRenderEngine(doc));
//    System.err.println("reference from snip '" + snip.getName() + "'");
    linkDetector.filter(snip.getContent(), context);

    return doc;
  }

  private static class ReferenceRenderEngine implements RenderEngine, WikiRenderEngine {
    Document doc;

    public ReferenceRenderEngine(Document doc) {
      this.doc = doc;
    }

    public String getName() {
      return "ReferenceRenderEngine";
    }

    public String render(String content, RenderContext context) {
      return "";
    }

    public void render(Writer out, String content, RenderContext context) throws IOException {
    }

    public String render(Reader in, RenderContext context) throws IOException {
      return "";
    }

    public boolean exists(String name) {
      return true;
    }

    public boolean showCreate() {
      return true;
    }

    public void appendLink(StringBuffer buffer, String name, String view, String anchor) {
//      System.err.println("Adding reference to '"+name+"'");
      doc.add(Field.Text("reference", name));
    }

    public void appendLink(StringBuffer buffer, String name, String view) {
//      System.err.println("Adding reference to '" + name + "'");
      doc.add(Field.Text("reference", name));
    }

    public void appendCreateLink(StringBuffer buffer, String name, String view) {
//      System.err.println("Adding reference to '" + name + "'");
      doc.add(Field.Text("reference", name));
    }
  }
}
