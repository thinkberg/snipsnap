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

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.radeox.util.Service;
import org.snipsnap.app.Application;
import org.snipsnap.graph.*;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;

/**
 * Get some data from a snip and render the content
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */
public class NameSpaceServlet extends HttpServlet {

	public void init() throws ServletException {
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		String name = request.getParameter("name");

    response.setContentType("image/png");

    ServletOutputStream out = response.getOutputStream();

    TreeBuilder builder = new NameSpaceTreeBuilder(name);
    Renderer renderer = new ExplorerRenderer();
    //DrawTree drawTree = new DrawTree();
    //drawTree.draw(builder.build(), renderer, out);
	}
}