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

import org.snipsnap.config.Configuration;
import org.snipsnap.config.ConfigurationProxy;
import org.snipsnap.snip.Blog;
import org.snipsnap.snip.Snip;
import org.snipsnap.snip.SnipSpaceFactory;
import org.snipsnap.serialization.SerializerFactory;
import org.snipsnap.serialization.Serializer;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.Properties;

/**
 * Output a snip as RDF
 * @author Marco Mosconi
 * @version $Id$
 */
public class RdfServlet extends HttpServlet {
    private Configuration config;

    public void init(ServletConfig servletConfig) throws ServletException {
        config = ConfigurationProxy.getInstance();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        // handle the snip name
        String name = request.getPathInfo();
        if (null == name || "/".equals(name)) {
            name = config.getStartSnip();
        } else {
            name = name.substring(1);
        }
        name = name.replace('+', ' ');

        // load snip
        Snip snip = SnipSpaceFactory.getInstance().load(name);

        // snip doesn't exist
        if (snip == null) {
            snip = SnipSpaceFactory.getInstance().load("snipsnap-notfound");
        }

        // set output
        response.setContentType("text/xml");
        Writer writer = response.getWriter();

        // serialize snip
        try {
            Serializer ser = SerializerFactory.createSerializer(SerializerFactory.RDF_10);
            Properties props = new Properties();
            props.setProperty("uri.prefix", config.getUrl("/rdf"));
            props.setProperty("rdf.format", "RDF/XML-ABBREV");
            ser.configure(props);
            ser.serialize(snip, writer);
        } catch (Exception e) {
			// some exception handling here ...
        }

        writer.close();
    }
}
