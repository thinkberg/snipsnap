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

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.snipsnap.app.Application;

import java.io.File;
import java.io.IOException;

/**
 * Indexes snips for fulltext searches
 *
 * @author stephan
 * @version $Id$
 */

public class SnipIndexer {
  //private static String indexFile = "./index";
  private static final String[] searchFields = new String[]{"content", "title"};
  private File indexFile;

  private File indexFile() {
    if(indexFile == null) {
      indexFile = new File(Application.get().getConfiguration().getFile().getParentFile(), "index");
    }
    return indexFile;
  }

  public void removeIndex(Snip snip) {
    IndexReader reader = null;
    try {
      reader = IndexReader.open(indexFile());
      int count = reader.delete(new Term("id", Integer.toHexString(snip.getName().hashCode())));
      // System.err.println("Deleted: "+ count );
    } catch (IOException e) {
      System.err.println("Unable to delete snip " + snip.getName() + " from index.");
      e.printStackTrace();
    } finally {
      close(reader);
    }
    return;
  }

  public void reIndex(Snip snip) {
    index(snip, true);
    return;
  }

  public void index(Snip snip) {
    index(snip, false);
    return;
  }

  private void index(Snip snip, boolean exists) {
    IndexWriter writer = null;

    if (exists) {
      removeIndex(snip);
    }

    try {
      File f;
      boolean create = true;
      // create index if the directory does not exist
      if ((f = indexFile()).exists() && f.isDirectory()) {
        create = false;
      } else {
        create = true;
      }
      writer = new IndexWriter(f, new SnipAnalyzer(), create);

      writer.mergeFactor = 20;
      writer.addDocument(SnipDocument.Document(snip));
      writer.optimize();
    } catch (IOException e) {
      System.err.println("Unable to index snip.");
      e.printStackTrace();
    } finally {
      close(writer);
    }
  }

  public Hits search(String queryString) {
    Searcher searcher = null;
    try {
      searcher = new IndexSearcher(indexFile().getAbsolutePath());
    } catch (IOException e) {
      System.out.println("Unable to open index file: " + indexFile());
      e.printStackTrace();
    }

    // When there is only one term, then add '*' to
    // end of the query to get more matches
    if (-1 == queryString.indexOf(' ')) {
      queryString = queryString + "*";
    }

    // parse the query String.
    Query query = null;
    try {
      query = MultiFieldQueryParser.parse(queryString, searchFields, new SnipAnalyzer());
    } catch (org.apache.lucene.queryParser.ParseException e1) {
      close(searcher);
      System.out.println("Unable to parse: " + queryString);
    }

    // get the hits from the searcher for
    // the given query
    Hits hits = null;
    try {
      hits = searcher.search(query);
    } catch (IOException e) {
      close(searcher);
      System.out.println("IO Error.");
      e.printStackTrace();
    }
    return hits;
  }

  private static void close(IndexWriter writer) {
    if (null != writer) {
      try {
        writer.close();
      } catch (Exception e) {
      }
    }
  }

  private static void close(Searcher searcher) {
    if (null != searcher) {
      try {
        searcher.close();
      } catch (Exception e) {
      }
    }
  }

  private static void close(IndexReader reader) {
    if (null != reader) {
      try {
        reader.close();
      } catch (Exception e) {
      }
    }
  }

}


