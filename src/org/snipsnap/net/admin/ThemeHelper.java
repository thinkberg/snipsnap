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
package org.snipsnap.net.admin;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.radeox.util.logging.Logger;
import org.snipsnap.config.Configuration;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ThemeHelper {
  public final static String THEME_PREFIX = "SnipSnap/themes/";
  public final static int FILES = 0;
  public final static int DOCUMENTS = 1;
  public final static int CONTENT = 2;

  public static Map getInstalledThemes() {
    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
    Snip[] themeSnips = space.match(THEME_PREFIX);
    Map themes = new HashMap();
    for (int t = 0; t < themeSnips.length; t++) {
      String name = themeSnips[t].getName();
      if(name.indexOf('/', THEME_PREFIX.length()) == -1) {
        themes.put(name.substring(THEME_PREFIX.length()), themeSnips[t]);
      }
    }
    return themes;
  }

  public static Map getThemeDocuments(Configuration config, int valueType) {
    // find theme files in filesystem
    File themeDir = new File(config.getWebInfDir(), "themes");
    File[] files = themeDir.listFiles(new FilenameFilter() {
      public boolean accept(File file, String s) {
        return s.endsWith(".snip");
      }
    });

    Map themeDocs = new HashMap();
    SAXReader saxReader = new SAXReader();
    for (int f = 0; f < files.length; f++) {
      try {
        Document themeDoc = saxReader.read(new FileReader(files[f]));
        Iterator it = themeDoc.getRootElement().elementIterator("snip");
        while (it.hasNext()) {
          Element snipEl = (Element) it.next();
          String tagName = snipEl.element("name").getText();
          if (tagName.startsWith(THEME_PREFIX) &&
            tagName.indexOf('/', THEME_PREFIX.length()) == -1) {
            String themeName = tagName.substring(tagName.lastIndexOf('/') + 1);
            switch(valueType) {
              case FILES:
                themeDocs.put(themeName, files[f]);
                break;
              case DOCUMENTS:
                themeDocs.put(themeName, themeDoc);
                break;
              case CONTENT:
                themeDocs.put(themeName, snipEl.elementText("content"));
                break;
            }
          }
        }
      } catch (Exception e) {
        Logger.warn("Error reading potential theme file", e);
      }
    }
    return themeDocs;
  }
}
