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

import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipLink;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.snip.label.Label;
import org.snipsnap.snip.label.LabelManager;
import org.snipsnap.app.Application;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Adding a label to a snip
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class AddLabelServlet extends HttpServlet {
    protected void doPost(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
        throws ServletException, IOException {
            doGet(httpServletRequest, httpServletResponse);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String snipName = request.getParameter("snipname");
        if (null == snipName) {
            response.sendRedirect(SnipLink.absoluteLink("/space/"+Application.get().getConfiguration().getStartSnip()));
            return;
        }
        // cancel pressed
        if (null != request.getParameter("cancel")) {
            response.sendRedirect(SnipLink.absoluteLink("/exec/labels?snipname=" + SnipLink.encode(snipName)));
            return;
        }

        Snip snip = SnipSpaceFactory.getInstance().load(snipName);
        request.setAttribute("snip", snip);

        String labelType = request.getParameter("labeltype");
        LabelManager manager = LabelManager.getInstance();
        Label label = manager.getLabel(labelType);
        request.setAttribute("label", label);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/exec/addlabel.jsp");
        dispatcher.forward(request, response);
    }

}
