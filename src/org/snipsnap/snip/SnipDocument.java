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

import java.util.Iterator;

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
    Document doc = new Document();
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
    doc.add(Field.Text("owner", snip.getOwner()));

    Labels labels = snip.getLabels();
    Iterator iterator = labels.getAll().iterator();
    while (iterator.hasNext()) {
      Label label = (Label) iterator.next();
      label.index(doc);
    }
    return doc;
  }
}
