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

package org.snipsnap.snip.storage;

import org.radeox.util.logging.Logger;
import org.snipsnap.snip.Snip;
import org.snipsnap.app.Application;

import java.io.*;
import java.util.Map;
import java.util.Properties;

/**
 * SnipStorage backend that writes the metadata to a property file
 * and the content to a plain text file
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class PropertyFileSnipStorage extends TwoFileSnipStorage {
  private final static String SNIP_FILE_PROPERTIES = "metadata.properties";
  private final static String SNIP_FILE_CONTENT = "content.txt";

  public static void createStorage() {
    // there is nothing to create here, all handled dynamically
  }

  public PropertyFileSnipStorage() {
  }

  /**
   * Return a file name for the metadata property file
   * @return
   */
  protected String getMetadataFileName() {
    return SNIP_FILE_PROPERTIES;
  }

  /**
   * Return a filename for the content text file
   * @return
   */
  protected String getContentFileName() {
    return SNIP_FILE_CONTENT;
  }

  /**
   * Store the content of a snip to
   * the output stream. Content is
   * stored as plain text
   *
   * @param snip
   * @param out
   */
  protected void storeContent(Snip snip, OutputStream out) {
    PrintWriter snipWriter = null;
    try {
      String enc = Application.get().getConfiguration().getEncoding();
      snipWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(out, enc != null ? enc : "UTF-8")));
      snipWriter.print(snip.getContent());
    } catch (Exception e) {
      Logger.log("FileSnipStorage: unable to store snip content" + snip.getName(), e);
    } finally {
      snipWriter.flush();
      snipWriter.close();
    }
  }

  /**
   * Store the metadata of a snip to
   * the output stream. Metadata is
   * stored as a property file
   *
   * @param snip
   * @param out
   */
  protected void storeMetadata(Snip snip, OutputStream out) {
    // serialize snip, remove content and add application oid
    Properties snipProps = new Properties();
    SnipSerializer serializer = SnipSerializer.getInstance();
    snipProps.putAll(serializer.createSnipMap(snip)); // new String[]{ SNIP_CONTENT });
    snipProps.remove(SnipSerializer.SNIP_CONTENT);

    try {
      snipProps.store(out, "Properties for " + snip.getName());
    } catch (IOException e) {
      Logger.log("FileSnipStorage: unable to store properties for '" + snip.getName() + "'");
    }
  }

  /**
   * Load the metadata of a snip from an InputStream and
   * store the metadata in a Map. Metadata is in a propery
   * file.
   *
   * @param in Stream to read from
   * @return
   * @throws IOException
   */
  public Map loadMetadata(InputStream in) throws IOException {
    Properties snipProps = new Properties();
    snipProps.load(in);
    return snipProps;
  }

  /**
   * Load the content of a snip from an InputStream.
   * Content is in a text file.
   *
   * @param in Stream to read from
   * @return
   * @throws IOException
   */
  public String loadContent(InputStream in) throws IOException {
    StringBuffer content = new StringBuffer();
    String enc = Application.get().getConfiguration().getEncoding();
    BufferedReader snipReader = new BufferedReader(new InputStreamReader(in, enc != null ? enc : "UTF-8"));
    char[] buffer = new char[8192];
    int length = 0;
    while ((length = snipReader.read(buffer)) != -1) {
      content.append(buffer, 0, length);
    }
    return content.toString();
  }
}
