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

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.radeox.util.logging.Logger;
import snipsnap.api.snip.Snip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

/**
 * SnipStorage backend that uses files for persisting data. This storage
 * has limitations in the snip name length and possibly characters as well
 * since not all filesystems can store UTF-8 file names.
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class XMLFileSnipStorage extends OneFileSnipStorage {
  public final static String SNIP_XML = "snip.xml";

  private SnipSerializer serializer = SnipSerializer.getInstance();

  public static void createStorage() {
  }

  protected String getFileName() {
    return SNIP_XML;
  }

  public XMLFileSnipStorage() {
  }

  protected Map loadSnip(InputStream in) throws IOException {
    SAXReader saxReader = new SAXReader();

    try {
      Document snipDocument = saxReader.read(in);
      return serializer.getElementMap(snipDocument.getRootElement());
    } catch (DocumentException e) {
      Logger.log("XMLFileSnipStorage: unable to parse snip", e);
    }

    return null;
  }

  protected void storeSnip(snipsnap.api.snip.Snip snip, OutputStream out) {
    Document snipDocument = DocumentHelper.createDocument();
    snipDocument.add(serializer.serialize(snip));

    try {
      OutputFormat outputFormat = new OutputFormat();
      outputFormat.setEncoding("UTF-8");
      XMLWriter xmlWriter = new XMLWriter(out, outputFormat);
      xmlWriter.write(snipDocument);
      xmlWriter.flush();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
