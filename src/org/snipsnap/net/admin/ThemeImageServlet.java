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
package org.snipsnap.net.admin;

import org.apache.xmlrpc.Base64;
import org.dom4j.Document;
import org.dom4j.Element;
import org.radeox.util.logging.Logger;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import snipsnap.api.snip.Snip;
import org.snipsnap.snip.attachment.Attachment;
import org.snipsnap.snip.attachment.storage.AttachmentStorage;
import org.snipsnap.container.Components;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;

public class ThemeImageServlet extends HttpServlet {
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String name = request.getParameter("name");

    Configuration config = Application.get().getConfiguration();
    Map installedThemes = ThemeHelper.getInstalledThemes();
    AttachmentStorage storage = (AttachmentStorage) Components.getComponent(AttachmentStorage.class);
    if(installedThemes.containsKey(name)) {
      Snip themeSnip = (snipsnap.api.snip.Snip)installedThemes.get(name);
      Attachment att = themeSnip.getAttachments().getAttachment("screenshot.png");
      if(att != null) {
        sendImage(response, storage.getInputStream(att),
                  (int) att.getSize(), att.getContentType());
        return;
      }
    } else {
      Map themeDocs = ThemeHelper.getThemeDocuments(config, ThemeHelper.DOCUMENTS);
      Document themeDoc = (Document)themeDocs.get(name);
      if(null != themeDoc) {
        Element attEl = getThemeElement(themeDoc, name);
        if(attEl.element("data") != null) {
          try {
            sendImage(response, getImageStream(attEl.elementText("data")),
                      Integer.parseInt(attEl.elementText("size")), attEl.elementText("contentType"));
            return;
          } catch (Exception e) {
            Logger.warn("unable to read image stream", e);
          }
        }
      }
    }
    response.sendError(HttpServletResponse.SC_NOT_FOUND);
  }

  private InputStream getImageStream(String base64str) throws Exception {
    byte buffer[] = Base64.decode(base64str.getBytes("UTF-8"));
    return new ByteArrayInputStream(buffer);
  }

  private Element getThemeElement(Document doc, String name) {
    Iterator it = doc.getRootElement().elementIterator("snip");
    while (it.hasNext()) {
      Element element = (Element) it.next();
      String snipName = element.elementText("name");
      if(null != snipName && snipName.endsWith(name)) {
        Iterator attIt = element.element("attachments").elementIterator("attachment");
        while (attIt.hasNext()) {
          Element attEl = (Element) attIt.next();
          if(attEl.elementText("name").equals("screenshot.png")) {
            return attEl;
          }
        }
      }
    }
    return null;
  }

  private void sendImage(HttpServletResponse response, InputStream imageStream,
                         int contentLength, String contentType) throws IOException {
    response.setContentType(contentType);
    response.setContentLength(contentLength);

    OutputStream out = response.getOutputStream();
    byte[] buffer = new byte[4096];
    int n = 0;
    while((n = imageStream.read(buffer)) != -1) {
      out.write(buffer, 0, n);
    }
    out.close();
  }

}
