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
import org.snipsnap.snip.SnipSpaceFactory;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Servlet to store snips into the database after they have been edited.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class FileUploadServlet extends HttpServlet {

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    doPost(request, response);
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String name = request.getParameter("name");
    SnipSpace space = SnipSpaceFactory.getInstance();
    Snip snip = space.load(name);

    if (request.getParameter("upload") != null) {
      MultipartWrapper wrapper = (MultipartWrapper) request;
      BodyPart part = wrapper.getBodyPart("file");
      String contentType = wrapper.getFileContentType("file");
      try {
        if (part != null && contentType != null && part.getFileName() != null) {
          AppConfiguration config = Application.get().getConfiguration();
          File imageDir = new File(config.getFile().getParentFile().getParentFile(), "images");
          File file = new File(imageDir, "att-" + name + "-" + part.getFileName());
          System.err.println("Uploading '" + part.getFileName() + "' to '" + file.getAbsolutePath() + "'");
          FileOutputStream out = new FileOutputStream(file);
          InputStream in = part.getInputStream();
          byte[] buf = new byte[4096];
          int length = 0, size = 0;
          while ((length = in.read(buf)) != -1) {
            out.write(buf, 0, length);
            size += length;
          }
          out.close();
          in.close();
          snip.getAttachments().addAttachment(part.getFileName(), contentType, size, file);
          SnipSpaceFactory.getInstance().store(snip);
        } else {
          request.setAttribute("error", "Please provide a file for upload.");
        }
      } catch (IOException e) {
        request.setAttribute("error", "I/O Error while uploading.");
        e.printStackTrace();
      } catch (MessagingException e) {
        request.setAttribute("error", "Uploaded filew may be corrupted.");
      }
      response.sendRedirect(SnipLink.absoluteLink(request, "/space/" + snip.getNameEncoded()));
    } else {
      request.setAttribute("snip", snip);
      request.setAttribute("snip_name", name);
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/upload.jsp");
      dispatcher.forward(request, response);
    }
  }
}
