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

package com.neotis.snip;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * Prepares a snip for indexing by lucene
 *
 * @author stephan
 * @version $Id$
 */

public class SnipDocument {
  public static Document Document(Snip snip) {
    Document doc = new Document();
    doc.add(Field.Text("id", snip.getName().hashCode() + ""));
    doc.add(Field.Text("content", snip.getContent()));
    doc.add(Field.Text("title", snip.getName()));
    return doc;
  }
}
