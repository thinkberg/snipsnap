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

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.snipsnap.net.filter.MultipartWrapper;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.attachment.Attachment;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.StringTokenizer;

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

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    Snip snip = (Snip)request.getAttribute("snip");
    Attachment attachment = (Attachment)request.getAttribute("attachment");

    AppConfiguration config = Application.get().getConfiguration();
    File fileStore = new File(config.getFileStorePath());

    File file = new File(fileStore, attachment.getLocation());

    response.setContentType(attachment.getContentType());
    response.setContentLength(attachment.getSize());
    BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
    BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
    byte buf[] = new byte[4096];
    int length = -1;
    while((length = in.read(buf)) != -1) {
      out.write(buf, 0, length);
    }
    out.flush();
    in.close();
    out.close();
  }

}
