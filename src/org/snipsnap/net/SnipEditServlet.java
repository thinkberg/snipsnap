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
import org.radeox.util.Encoder;
import org.snipsnap.app.Application;
import org.snipsnap.container.Components;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpace;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.Labels;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
    String content = request.getParameter("content");
    String mimeType = request.getParameter("mime");
    String editHandler = request.getParameter("handler");

    SnipSpace space = (SnipSpace) Components.getComponent(SnipSpace.class);
    Snip snip = null;
    if (name != null && space.exists(name)) {
      snip = space.load(name);
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
              Logger.log("SnipEditServlet: using edit handler '" + editHandler+"'");
              mimeType = mimeTypeLabel.getMIMEType();
            } else {
              editHandler = null;
            }
            break;
          }
        }
      }
    } else {

      // handle new snips (they can get a parent and a template)
      String parent = request.getParameter("parent");
      String parentBefore = request.getParameter("parentBefore");
      if (null == parentBefore) {
        parentBefore = parent;
      }
      request.setAttribute("parent", parent);
      request.setAttribute("parentBefore", parentBefore);
      request.setAttribute("templates", getTemplates());
    }

    // copy a template into the content if it was requested
    String template = request.getParameter("template");
    boolean copyTemplate = request.getParameter("copy.template") != null;
    if (copyTemplate && template != null) {
      Snip templateSnip = space.load(template);
      content = (content != null ? content : "") + templateSnip.getContent();
    }

    // set the attributes to transport snip and snip name (used when nonexisting snip)
    request.setAttribute("snip", snip);
    request.setAttribute("snip_name", name);

    if (null != content) {
      request.setAttribute("content", content);
    } else {
      request.setAttribute("content", snip != null ? snip.getContent() : "");
    }

    if(null != editHandler && !"".equals(editHandler)) {
      if(Security.hasRoles(Application.get().getUser(), snip, authRoles)) {
        request.setAttribute("edit_handler", editHandler);
        request.setAttribute("mime_type", mimeType);
      }
    }

    String referer = request.getParameter("referer");
    if(null == referer && request.getHeader("REFERER") != null) {
      referer = Encoder.escape(request.getHeader("REFERER"));
    }
    request.setAttribute("referer", referer == null ? "" : referer);

    RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/edit.jsp");
    dispatcher.forward(request, response);
  }

  private List getTemplates() {
    List templates = new ArrayList();

    SnipSpace snipspace = (SnipSpace) Components.getComponent(SnipSpace.class);
    List snipList = snipspace.getAll();

    Iterator iterator = snipList.iterator();
    while (iterator.hasNext()) {
      Snip snip = (Snip) iterator.next();
      Labels labels = snip.getLabels();
      boolean noLabelsAll = labels.getAll().isEmpty();

      if (!noLabelsAll) {
        Collection labelsCat = labels.getLabels("TypeLabel");
        if (!labelsCat.isEmpty()) {
          Iterator iter = labelsCat.iterator();
          while (iter.hasNext()) {
            Label label = (Label) iter.next();
            if (label.getValue().equals("Template")) {
              templates.add(snip.getName());
            }
          }
        }
      }
    }
    return templates;
  }
}
