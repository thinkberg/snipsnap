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
package org.snipsnap.net;

import org.radeox.util.logging.Logger;
import org.snipsnap.app.Application;
import org.snipsnap.config.Configuration;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.attachment.Attachment;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Servlet for downloading attachments.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class FileDownloadServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    doPost(request, response);
  }

  public final static String FILENAME = "filename";
  public final static String SNIP = "snip";

  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

    Snip snip = (Snip) request.getAttribute(SNIP);
    String fileName = (String) request.getAttribute(FILENAME);

    if (snip != null) {
      Attachment attachment = snip.getAttachments().getAttachment(fileName);

      // make sure the attachment exists
      if (attachment != null) {
        Configuration config = Application.get().getConfiguration();
        File fileStore = new File(config.getFilePath());
        File file = new File(fileStore, attachment.getLocation());

        if (file.exists()) {
          response.setContentType(attachment.getContentType());
          response.setContentLength((int)attachment.getSize());
          BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
          BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
          byte buf[] = new byte[4096];
          int length = -1;
          while ((length = in.read(buf)) != -1) {
            out.write(buf, 0, length);
          }
          out.flush();
          in.close();
          out.close();
          return;
        }
      } else {
        // legacy: found a default image download
        Logger.log(Logger.DEBUG, "old style image: " + fileName);
        String oldStyleFile = "/images/image-" + snip.getName() + "-" + fileName;
        if (getServletContext().getResource(oldStyleFile) != null) {
          RequestDispatcher dispatcher = request.getRequestDispatcher(oldStyleFile);
          if (dispatcher != null) {
            dispatcher.forward(request, response);
            return;
          }
        }
      }
    }

    // file does not exist, tell caller
    throw new ServletException("file does not exist: " + fileName);
  }
}
