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
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.label.MIMETypeLabel;
import org.snipsnap.user.Permissions;
import org.snipsnap.user.Roles;
import org.snipsnap.user.Security;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

/**
 * Load a snip to edit. Loads the snip into the request context. In case
 * the snip is newly created put the name into "snip_name".
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */
public class SnipEditServlet extends HttpServlet {

  private final static Roles authRoles = new Roles(Roles.AUTHENTICATED);

  public void doPost(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {
    doGet(request, response);
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
    throws IOException, ServletException {

    String name = request.getParameter("name");
    if (null == name) {
      Configuration config = Application.get().getConfiguration();
      response.sendRedirect(config.getUrl("/space/" + config.getStartSnip()));
      return;
    }

    // try to load the snip and set the snip_name property in case it is a new
    // snip, so the target store servlet knows how to name the new snip
    Snip snip = SnipSpaceFactory.getInstance().load(name);
    request.setAttribute("snip", snip);
    request.setAttribute("snip_name", name);

    String content = request.getParameter("content");
    if (null != content) {
      request.setAttribute("content", content);
    } else {
      request.setAttribute("content", snip != null ? snip.getContent() : "");
    }

    String mimeType = request.getParameter("mime");
    String editHandler = request.getParameter("handler");

    if (null != snip) {
      // get all mime types associated with the snip
      Collection mimeTypes = snip.getLabels().getLabels("mime-type");
      if (!mimeTypes.isEmpty()) {
        Iterator handlerIt = mimeTypes.iterator();
        while (handlerIt.hasNext()) {
          MIMETypeLabel mimeTypeLabel = (MIMETypeLabel) handlerIt.next();
          editHandler = mimeTypeLabel.getEditHandler();
          // check that an edit handler is set
          if (null != editHandler && !"".equals(editHandler)) {
            if (Security.checkPermission(Permissions.EDIT_SNIP, Application.get().getUser(), snip) &&
              Security.hasRoles(Application.get().getUser(), snip, authRoles)) {
              Logger.log("SnipEditServlet: calling " + editHandler);
              mimeType = mimeTypeLabel.getMIMEType();
            } else {
              editHandler = null;
            }
            break;
          }
        }
      }
    }

    if(null != editHandler && !"".equals(editHandler)) {
      if(Security.hasRoles(Application.get().getUser(), snip, authRoles)) {
        request.setAttribute("edit_page", "/exec/" + editHandler);
        request.setAttribute("mime_type", mimeType);
      }
    }

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit.jsp");
    dispatcher.forward(request, response);
  }

}
