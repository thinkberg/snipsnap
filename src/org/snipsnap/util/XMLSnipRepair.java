/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * CopyAtright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
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
package org.snipsnap.util;

import org.apache.xmlrpc.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.xml.sax.InputSource;

import java.io.*;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.text.NumberFormat;

/**
 * Repair XML File
 */
public class XMLSnipRepair {
  public static void main(String args[]) {
    if (args.length < 2) {
      System.err.println("usage: XMLSnipRepair <input file> <output file> [<webapp directory>]");
      System.exit(0);
    }


    System.err.println("STEP 1: parsing input file ...");
    Document document = null;
    try {
      document = load(new File(args[0]));
    } catch (Exception e) {
      System.err.println("Unable to read input document: " + e);
      System.err.println("This is usually the case for illegal XML characters, please manually edit the file and remove them.");
      System.exit(0);
    }

    System.err.println("STEP 2: checking SnipSpace consistency ...");
    Document repaired = repair(document, args.length > 2 ? new File(args[2]) : null);

    System.err.println("STEP 3: writing output file ...");
    OutputFormat outputFormat = new OutputFormat();
    outputFormat.setEncoding("UTF-8");
    outputFormat.setNewlines(true);
    try {
      XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(args[1]));
      xmlWriter.write(repaired);
      xmlWriter.flush();
    } catch (Exception e) {
      System.err.println("Error: unable to write data: " + e);
    }
    System.err.println("Finished.");
  }

  static int errCount = 0;
  static int curr = 0;
  /**
   * Load snips and users into the SnipSpace from an xml document out of a stream.
   * @param file  the file to load from
   */
  private static Document load(File file) throws Exception {
    final long fileLength = file.length();
    System.err.print("0%");
    SAXReader saxReader = new SAXReader();
    InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8") {
      public int read(char[] chars) throws IOException {
        int n = super.read(chars);
        for (int i = 0; i < n; i++) {
          chars[i] = replaceIfIllegal(chars[i]);
        }
        return n;
      }

      public int read(char[] chars, int start, int length) throws IOException {
        int n = super.read(chars, start, length);
        for (int i = 0; i < n; i++) {
          chars[i] = replaceIfIllegal(chars[i]);
        }
        readProgress(fileLength, curr += n, length);
        return n;
      }

      private char replaceIfIllegal(char c) {
        if (c < 0x20 && !(c == 0x09 || c == 0x0a || c == 0x0d)) {
//          System.err.println("Replacing illegal character '" + c + "' by space.");
          errCount++;
          return (char) 0x20;
        }
        return c;
      }

      private void readProgress(long length, long current, int blockSize) {
        long percentage = current * 100 / length;
        if (percentage % 5 != 0 && ((current - blockSize) * 100 / length) % 5 == 0) {
          System.err.print(".");
        } else if (percentage % 20 == 0 && ((current - blockSize) * 100 / length) % 20 != 0) {
          System.err.print(NumberFormat.getIntegerInstance().format(percentage) + "%");
        }
      }
    };

    Document document = saxReader.read(reader);
    System.err.println();

    if(errCount > 0) {
      System.err.println("Replaced "+errCount+" illegal characters in input document by a space.");
      System.err.println("Characters not considered valid in an XML document are considered illegal.");
      System.err.println("This includes all characters with a code below 32 unless its TAB, CR or LF.");
    }

    return document;
  }

  private static Document repair(Document document, File webAppRoot) {
    Map userData = new TreeMap();
    Map snipData = new TreeMap();
    Map unknown = new TreeMap();

    Element rootEl = document.getRootElement();
    Iterator elementIt = rootEl.elementIterator();

    System.err.println("STEP 2.1: checking for duplicates ...");
    long identDup = 0;
    long oldDup = 0;
    long newDup = 0;
    while (elementIt.hasNext()) {
      Element element = (Element) elementIt.next();
      Element idElement = null;
      Map data = null;
      if ("user".equals(element.getName())) {
        idElement = element.element("login");
        data = userData;
      } else if ("snip".equals(element.getName())) {
        idElement = element.element("name");
        data = snipData;
      }

      if (null != data && null != idElement) {
        String id = element.getName() + "[" + idElement.getText() + "]";
        long mtime = Long.parseLong(element.element("mTime").getTextTrim());

        Element existingElement = (Element) data.get(id);
        if (existingElement != null) {
          long lastmtime = Long.parseLong(existingElement.element("mTime").getTextTrim());
          if (mtime > lastmtime) {
            newDup++;
            System.err.println("Replacing duplicate by newer element: " + id + " (" + (mtime - lastmtime) + "ms)");
            data.put(id, element);
          } else if (mtime == lastmtime) {
            identDup++;
            System.err.println("Identical duplicate found: " + id);
          } else {
            oldDup++;
            System.err.println("Older duplicate found: " + id);
          }
          if (snipData == data) {
            String name = idElement.getText();
            if (name.startsWith("comment-")) {
              String commentSnip = name.substring("comment-".length(), name.lastIndexOf("-"));
              Element commentEl = element.element("commentSnip");
              if (commentEl == null) {
                commentEl = element.addElement("commentSnip");
              }
              if (!commentSnip.equals(commentEl.getText())) {
                commentEl.addText(commentSnip);
                System.err.println("Fixing commented snip for '" + name + "' (" + commentSnip + ")");
              }
            } else if (name.matches("\\d\\d\\d\\d-\\d\\d-\\d\\d")) {
              Element parentEl = element.element("parentSnip");
              if (null == parentEl) {
                parentEl = element.addElement("parentSnip");
              }
              if (!"start".equals(parentEl.getText())) {
                parentEl.addText("start");
                System.err.println("Fixing parent snip for '" + name + "'");
              }
            }

          }
        } else {
          data.put(id, element);
        }
      } else {
        System.err.println("Unknown element '" + element.getName() + "', ignoring ...");
        unknown.put(element, element);
      }
    }

    System.err.println("Found " + identDup + " identical duplicates, replaced " + newDup + ", ignored " + oldDup + ".");
    if (unknown.size() > 0) {
      System.err.println("Found " + unknown.size() + " unknown xml elements.");
    }

    Document outputDocument = DocumentHelper.createDocument();
    outputDocument.addElement(rootEl.getName());
    rootEl = outputDocument.getRootElement();

    System.err.println("STEP 2.2: finishing user data (" + userData.size() + ")...");
    Iterator userIt = userData.values().iterator();
    while (userIt.hasNext()) {
      Element userEl = (Element) userIt.next();
      rootEl.add(userEl.detach());
    }

    int attCount = 0;
    System.err.print("STEP 2.3: fixing snip data (" + snipData.size() + ")");
    if(webAppRoot != null) {
      System.out.println(" and attachments ...");
    } else {
      System.out.println();
    }
    Iterator snipIt = snipData.values().iterator();
    while (snipIt.hasNext()) {
      Element snipEl = (Element) snipIt.next();
      if(webAppRoot != null) {
        attCount += storeAttachments(snipEl, new File(webAppRoot, "/WEB-INF/files"));
        attCount += storeOldImages(snipEl, new File(webAppRoot, "/images"));
      }
      rootEl.add(snipEl.detach());
    }
    System.err.println("Added " + attCount + " attachments.");
    return outputDocument;
  }

  private static int storeOldImages(Element snipEl, File imageRoot) {
    int attCount = 0;
    final String snipName = snipEl.element("name").getText();
    File[] files = imageRoot.listFiles(new FilenameFilter() {
      public boolean accept(File file, String s) {
        return s.startsWith("image-" + snipName);
      }
    });

    Element attachmentsEl = snipEl.element("attachments");
    if (null == attachmentsEl) {
      attachmentsEl = DocumentHelper.createElement("attachments");
      snipEl.add(attachmentsEl);
    }

    Set attList = new HashSet();
    Iterator attIt = attachmentsEl.elementIterator("attachment");
    while (attIt.hasNext()) {
      Element attEl = (Element) attIt.next();
      attList.add(attEl.element("name").getText());
    }

    for (int n = 0; n < files.length; n++) {
      File file = files[n];
      String fileName = file.getName().substring(("image-" + snipName + "-").length());
      if (!attList.contains(fileName)) {
        Element attEl = attachmentsEl.addElement("attachment");
        attEl.addElement("name").addText(fileName);
        attEl.addElement("content-type").addText("image/" + fileName.substring(fileName.lastIndexOf(".") + 1));
        attEl.addElement("size").addText("" + file.length());
        attEl.addElement("date").addText("" + file.lastModified());
        attEl.addElement("location").addText(snipName + "/" + fileName);
        try {
          addAttachmentFile(attEl, file);
          attCount++;
        } catch (IOException e) {
          System.err.println("Error adding attachment data: " + e.getMessage());
          attEl.detach();
        }
        System.err.println("Added old image attachment '" + fileName + "' to '" + snipName + "'");
      }
    }
    return attCount;
  }

  private static int storeAttachments(Element snipEl, File attRoot) {
    Element attachmentsEl = snipEl.element("attachments");
    attachmentsEl.detach();
    String textContent = attachmentsEl.getText();
    if (textContent != null && textContent.length() > 0 && attachmentsEl.elements("attachment").size() == 0) {
      SAXReader saxReader = new SAXReader();
      try {
        attachmentsEl = saxReader.read(new StringReader("<attachments>" + textContent + "</attachments>")).getRootElement();
      } catch (DocumentException e) {
        System.err.println("Error parsing the attachments ...: " + e.getMessage());
      }
    }

    int attCount = 0;
    Iterator attIt = attachmentsEl.elements("attachment").iterator();
    while (attIt.hasNext()) {
      Element att = (Element) attIt.next();
      File file = new File(attRoot, att.elementText("location"));
      String snipName = snipEl.element("name").getText();
      if (att.element("data") == null) {
        if (file.exists()) {
          try {
            addAttachmentFile(att, file);
            attCount++;
            //          System.err.println("Added '" + file.getPath() + "' to " + snipName);
          } catch (Exception e) {
            System.err.println("Error adding '" + file.getPath() + "' to '" + snipName + "'");
            e.printStackTrace();
            att.detach();
          }
        } else {
          System.err.println("Missing file '" + file.getPath() + "' attached to '" + snipName + "'");
          att.detach();
        }
      }
    }
    snipEl.add(attachmentsEl);
    return attCount;
  }

  public static void addAttachmentFile(Element att, File attFile) throws IOException {
    ByteArrayOutputStream data = new ByteArrayOutputStream();
    BufferedInputStream fileIs = new BufferedInputStream(new FileInputStream(attFile));
    int count = 0;
    byte[] buffer = new byte[8192];
    while ((count = fileIs.read(buffer)) != -1) {
      data.write(buffer, 0, count);
    }
    data.close();
    att.addElement("data").addText(new String(Base64.encode(data.toByteArray()), "UTF-8"));
  }

}
