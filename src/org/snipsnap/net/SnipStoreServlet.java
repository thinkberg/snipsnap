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
import org.snipsnap.snip.filter.SnipFormatter;
import org.snipsnap.user.User;
import org.snipsnap.user.UserManager;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Servlet to store snips into the database after they have been edited.
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipStoreServlet extends SnipSnapServlet {
  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {

    String name = request.getParameter("name");
    SnipSpace space = SnipSpace.getInstance();
    Snip snip = space.load(name);

    String content = request.getParameter("content");
    if (request.getParameter("preview") != null) {
      request.setAttribute("preview", SnipFormatter.toXML(snip, content));
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("upload") != null) {
      // @TODO refactor out of this servlet
      MultipartWrapper wrapper = (MultipartWrapper) request;
      BodyPart part = wrapper.getBodyPart("image");
      try {
        if (part != null && wrapper.getFileContentType("image") != null && part.getFileName() != null) {
          AppConfiguration config = Application.get().getConfiguration();
          File imageDir = new File(config.getFile().getParentFile().getParentFile(), "images");
          File file = new File(imageDir, "image-" + name + "-" + part.getFileName());
          System.err.println("Uploading '" + part.getFileName() + "' to '" + file.getAbsolutePath() + "'");
          FileOutputStream out = new FileOutputStream(file);
          InputStream in = part.getInputStream();
          byte[] buf = new byte[4096];
          int length = 0;
          while ((length = in.read(buf)) != -1) {
            out.write(buf, 0, length);
          }
          out.close();
          in.close();
        } else {
          request.setAttribute("error", "Please provide an image file for upload.");
        }
      } catch (IOException e) {
        request.setAttribute("error", "I/O Error while uploading image.");
        e.printStackTrace();
      } catch (MessagingException e) {
        request.setAttribute("error", "Uploaded image may be corrupted.");
      }
      RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit");
      dispatcher.forward(request, response);
      return;
    } else if (request.getParameter("cancel") == null) {
      HttpSession session = request.getSession();
      Application app = null;
      if (session != null) {
        app = Application.getInstance(session);
        User user = app.getUser();
        if (UserManager.getInstance().isAuthenticated(user)) {
          if (snip != null) {
            snip.setContent(content);
            space.store(snip);
          } else {
            snip = space.create(name, content);
          }
        } else {
          response.sendError(HttpServletResponse.SC_FORBIDDEN);
        }
      }
    } else if (snip == null) {
      // return to referrer if the snip cannot be found
      response.sendRedirect(request.getParameter("referer"));
      return;
    }

    response.sendRedirect(SnipLink.absoluteLink(request, "/space/" + SnipLink.encode(name)));
  }
}
