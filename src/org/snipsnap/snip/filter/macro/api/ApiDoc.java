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

package org.snipsnap.snip.filter.macro.api;

import org.snipsnap.util.log.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Stores information and links to api documentation, e.g. for Java, Ruby, JBoss
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class ApiDoc {
  private static ApiDoc instance;
  private Map apiDocs;

  public static synchronized ApiDoc getInstance() {
    if (null == instance) {
      instance = new ApiDoc();
    }
    return instance;
  }

  public ApiDoc() {
    apiDocs = new HashMap();

    try {
      BufferedReader br = new BufferedReader(
          new InputStreamReader(
              new FileInputStream("conf/apidocs.txt")));
      addApiDoc(br);
    } catch (IOException e) {
      System.err.println("Unable to read conf/apidocs.txt ");
    }
  }

  public void addApiDoc(BufferedReader reader) throws IOException {
    String line;
    while ((line = reader.readLine()) != null) {
      StringTokenizer tokenizer = new StringTokenizer(line," ");
      String mode = tokenizer.nextToken();
      String baseUrl = tokenizer.nextToken();
      String converterName = tokenizer.nextToken();
      ApiConverter converter = null;
      try {
        converter = (ApiConverter) Class.forName("org.snipsnap.snip.filter.macro.api."+converterName + "ApiConverter").newInstance();
      } catch (Exception e) {
        Logger.log("Unable to load converter: "+converterName+"ApiConverter", e);
      }
      converter.setBaseUrl(baseUrl);
      apiDocs.put(mode.toLowerCase(), converter);
    }
  }

  public boolean contains(String external) {
    return apiDocs.containsKey(external);
  }

  public Writer expand(Writer writer, String className, String mode) throws IOException {
    mode = mode.toLowerCase();
    if (apiDocs.containsKey(mode)) {
      writer.write("<a href=\"");
      writer.write(((ApiConverter) apiDocs.get(mode)).convert(className));
      writer.write("\">");
      writer.write(className);
      writer.write("</a>");
    } else {
      System.err.println(mode+" not found");
      System.err.println(apiDocs);
    }
    return writer;
  }
}
