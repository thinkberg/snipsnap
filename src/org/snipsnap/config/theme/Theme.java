/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002,2003 Fraunhofer Gesellschaft
 * Fraunhofer Institut for Computer Architecture and Software Technology
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
package org.snipsnap.config.theme;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.radeox.util.logging.Logger;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipFactory;
import org.snipsnap.snip.storage.SnipSerializer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class Theme {
  private final static String THEME_NAME = "SnipSnap-Theme";
  private final static String THEME_AUTHOR = "SnipSnap-Theme-Author";
  private final static String THEME_DATE = "SnipSnap-Theme-Date";

  private final static String THEME_DESCR = "description.txt";
  private final static String THEME_SNIP = "theme.snip";
  private final static String THEME_THUMB = "thumbnail.png";
  private final static String THEME_DEFAULT_CSS = "default.css";
  private final static String THEME_CSS = "css";
  private final static String THEME_IMAGES = "images";

  private JarFile themeFile;
  private String name = "default";
  private String author = "unknown";
  private Date date = new Date();
  private String description = "";
  private byte[] thumbnail = null;
  private Collection snips = null;
  private Map stylesheets = null;
  private Map images = null;

  private DocumentBuilder documentBuilder = null;

  public Theme() {
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilder = documentBuilderFactory.newDocumentBuilder();
    } catch (FactoryConfigurationError error) {
      Logger.warn("Unable to create document builder factory: " + error);
    } catch (ParserConfigurationException e) {
      Logger.warn("Unable to create document builder", e);
    }
  }

  public Theme(File pkgFile) throws IOException {
    this();
    loadTheme(pkgFile);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getAuthor() {
    return author;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getDate() {
    return date;
  }

  public void setThumbnail(byte[] image) {
    thumbnail = image;
  }

  public byte[] getThumbnail() {
    return thumbnail;
  }

  public void setStylesheets(Map stylesheets) {
    this.stylesheets = stylesheets;
  }

  public Map getStylesheets() {
    return stylesheets;
  }

  public void setImages(Map images) {
    this.images = images;
  }

  public Map getImages() {
    return images;
  }


  public void setSnips(Collection snips) {
    this.snips = snips;
  }

  public Collection getSnips() {
    return snips;
  }

  /**
   * Load theme from a jar file. The structure of the jar should be as follows:
   * META-INF/MANIFEST.MF (contains theme name, author and date)
   * description.txt (a description of the theme, snip markup)
   * thumbnail.png (a thumbnail picture of the theme)
   * theme.snip (special snips required for this theme, e.g. SnipSnap/portlet/1)
   * default.css (the default.css file which should @include other css files)
   * css/xxxx.css (any number of css files)
   * images/image.png (any number of images attached to the theme)
   *
   * @param file the jar file containing the theme
   * @throws IOException if the jar is not readable
   */
  public void loadTheme(File file) throws IOException {
    themeFile = new JarFile(file);
    Manifest manifest = themeFile.getManifest();
    Attributes manifestEntries = manifest.getMainAttributes();
    // get meta info from manifest and description from text file
    name = manifestEntries.getValue(THEME_NAME);
    if (null == name) {
      name = file.getName();
    }
    author = manifestEntries.getValue(THEME_AUTHOR);
    try {
      date = (new SimpleDateFormat()).parse(manifestEntries.getValue(THEME_DATE));
    } catch (Exception e) {
      date = new Date();
    }
    description = readTextEntry(themeFile, THEME_DESCR);

    // create new images and stylesheets and read all files from the jar
    images = new HashMap();
    stylesheets = new HashMap();
    String defaultCSS = readTextEntry(themeFile, THEME_DEFAULT_CSS);
    stylesheets.put(THEME_DEFAULT_CSS, defaultCSS != null ? defaultCSS : "");
    thumbnail = readBinaryEntry(themeFile, THEME_THUMB);
    for (Enumeration entries = themeFile.entries(); entries.hasMoreElements();) {
      JarEntry entry = (JarEntry) entries.nextElement();
      String entryName = entry.getName();
      if (entryName.startsWith(THEME_CSS) && !entry.isDirectory()) {
        stylesheets.put(entryName, readTextEntry(themeFile, entryName));
      } else if (entryName.startsWith(THEME_IMAGES) && !entry.isDirectory()) {
        images.put(entryName.substring((THEME_IMAGES + "/").length()), readBinaryEntry(themeFile, entryName));
      }
    }

    JarEntry themeSnips = themeFile.getJarEntry(THEME_SNIP);
    if (null != themeSnips) {
      InputStream snipInputStream = themeFile.getInputStream(themeSnips);

      Document document = null;
      SAXReader saxReader = new SAXReader();
      try {
        document = saxReader.read(themeFile.getInputStream(themeSnips));
      } catch (DocumentException e) {
        Logger.warn("XMLSnipImport: unable to parse document", e);
        throw new IOException("Error parsing document: " + e);
      }

      SnipSerializer serializer = SnipSerializer.getInstance();
      Iterator snipElementIt = document.getRootElement().elementIterator(SnipSerializer.SNIP);
      while (snipElementIt.hasNext()) {
        Element snipEl = (Element) snipElementIt.next();
        Map snipMap = serializer.getElementMap(snipEl);
        String name = (String) snipMap.get(SnipSerializer.SNIP_NAME);
        String content = (String) snipMap.get(SnipSerializer.SNIP_CONTENT);
        if (null != name && null != content) {
          snips.add(SnipFactory.createSnip(name, content));
        }
      }
    }
  }

  private byte[] readBinaryEntry(JarFile jarFile, String file) {
    JarEntry entry = jarFile.getJarEntry(file);

    if(entry != null) {
      try {
        int size = (int)entry.getSize();
        InputStream in = jarFile.getInputStream(entry);
        byte[] binEntry = new byte[size];
        byte[] buffer = new byte[4096];
        int count = 0;
        int position = 0;
        while(((count = in.read(buffer))) != -1) {
          System.arraycopy(buffer, 0, binEntry, position, count);
          position += count;
        }
        return binEntry;
      } catch (IOException e) {
        System.err.println("Theme: " + jarFile.getName() + " can't read  '" + file + "'" + e.getMessage());
      }
    }
    return null;
  }

  private String readTextEntry(JarFile jarFile, String file) {
    JarEntry entry = jarFile.getJarEntry(file);

    if (entry != null) {
      try {
        return getResourceAsString(jarFile.getInputStream(entry));
      } catch (IOException e) {
        System.err.println("Theme: " + jarFile.getName() + " can't read  '" + file + "'" + e.getMessage());
      }
    }
    return null;
  }

  private static String getResourceAsString(InputStream is) throws IOException {
    BufferedReader in = new BufferedReader(new InputStreamReader(is));
    StringBuffer contents = new StringBuffer();
    String line = null;
    while ((line = in.readLine()) != null) {
      contents.append(line).append("\n");
    }
    return contents.toString();
  }


  public void storeTheme(File file) throws IOException {
    StringBuffer mText = new StringBuffer();
    mText.append("Manifest-Version: 1.0\n");
    mText.append(THEME_NAME + ": " + (name != null ? name : "default")).append("\n");
    mText.append(THEME_AUTHOR + ": " + (author != null ? author : "unknown")).append("\n");
    mText.append(THEME_DATE + ": " + (new SimpleDateFormat()).format(date)).append("\n");

    InputStream mIs = new ByteArrayInputStream(mText.toString().getBytes("UTF-8"));
    Manifest manifest = new Manifest(mIs);
    JarOutputStream jarOutput = new JarOutputStream(new FileOutputStream(file), manifest);

    // output directories
    jarOutput.putNextEntry(new JarEntry(THEME_CSS + "/"));
    jarOutput.closeEntry();
    jarOutput.putNextEntry(new JarEntry(THEME_IMAGES + "/"));
    jarOutput.closeEntry();

    if (null != description) {
      jarOutput.putNextEntry(new JarEntry(THEME_DESCR));
      jarOutput.write(description.getBytes("UTF-8"));
      jarOutput.closeEntry();
    }

    if(null != thumbnail) {
      jarOutput.putNextEntry(new JarEntry(THEME_THUMB));
      InputStream is = new ByteArrayInputStream(thumbnail);
      byte[] buffer = new byte[4096];
      int amount;
      while ((amount = is.read(buffer)) != -1) {
        jarOutput.write(buffer, 0, amount);
      }
      jarOutput.closeEntry();

    }

    if (null != snips) {
      jarOutput.putNextEntry(new JarEntry(THEME_SNIP));
      SnipSerializer serializer = SnipSerializer.getInstance();

      Document themeSnips = DocumentHelper.createDocument();
      themeSnips.addElement("snipspace");
      Iterator snipIt = snips.iterator();
      while (snipIt.hasNext()) {
        Snip snip = (Snip) snipIt.next();
        themeSnips.add(serializer.serialize(snip));
      }

      XMLWriter xmlWriter = new XMLWriter(jarOutput, OutputFormat.createCompactFormat());
      xmlWriter.write(themeSnips);
      xmlWriter.flush();
      jarOutput.closeEntry();
    }

    if (null != stylesheets) {
      Iterator styleIt = stylesheets.keySet().iterator();
      while (styleIt.hasNext()) {
        String stylesheet = (String) styleIt.next();
        jarOutput.putNextEntry(new JarEntry(stylesheet));
        jarOutput.write(((String) stylesheets.get(stylesheet)).getBytes("UTF-8"));
        jarOutput.closeEntry();
      }
    }

    if (null != images) {
      Iterator imageIt = images.keySet().iterator();
      while (imageIt.hasNext()) {
        String image = THEME_IMAGES + "/" + (String) imageIt.next();
        jarOutput.putNextEntry(new JarEntry(image));
        InputStream is = themeFile.getInputStream(themeFile.getEntry(image));
        byte[] buffer = new byte[4096];
        int amount;
        while ((amount = is.read(buffer)) != -1) {
          jarOutput.write(buffer, 0, amount);
        }
        jarOutput.closeEntry();
      }
    }
    jarOutput.close();
  }

  public static void main(String[] args) {
    if (args != null && args.length > 0) {
      try {
        Theme theme = new Theme(new File(args[0]));

        System.out.println("Name: " + theme.getName());
        System.out.println("Author: " + theme.getAuthor());
        System.out.println("Date: " + new SimpleDateFormat().format(theme.getDate()));
        byte[] thumb = theme.getThumbnail();
        System.out.println("Thumbnail: " +  (thumb == null ? "no thumbnail" : "["+thumb.length+" bytes]"));
        System.out.println("Description:");
        if (null != theme.getDescription()) {
          System.out.println(theme.getDescription());
        } else {
          System.out.println("  no description");
        }
        System.out.println("Stylesheets:");
        Map stylesheets = theme.getStylesheets();
        if (null != stylesheets) {
          Iterator styleIt = stylesheets.keySet().iterator();
          while (styleIt.hasNext()) {
            String stylesheet = (String) styleIt.next();
            System.out.println("  " + stylesheet + " [" + ((String) stylesheets.get(stylesheet)).length() + " bytes]");
          }
        } else {
          System.out.println("  no stylesheets");
        }
        System.out.println("Images:");
        Map images = theme.getImages();
        if (null != images) {
          Iterator imageIt = images.keySet().iterator();
          while (imageIt.hasNext()) {
            String image = (String) imageIt.next();
            System.out.println("  " + image + " [" + ((byte[]) images.get(image)).length + " bytes]");
          }
        }
        System.out.println("Snips:");
        Collection snips = theme.getSnips();
        if (null != snips) {
          Iterator snipIt = snips.iterator();
          while (snipIt.hasNext()) {
            Snip snip = (Snip) snipIt.next();
            System.out.println("  " + snip.getName());
          }
        } else {
          System.out.println("  no snips");
        }
        if (args.length > 1) {
          File outFile = new File(args[1]);
          theme.storeTheme(outFile);
        }
      } catch (IOException e) {
        System.out.println("error: file '" + args[0] + "' not found/unreadable");
        e.printStackTrace();
      }
    } else {
      System.out.println("usage: Theme theme.jar");
    }
    System.exit(0);
  }
}
