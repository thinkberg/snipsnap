/*
 * This file is part of "SnipSnap Wiki/Weblog".
 *
 * Copyright (c) 2002-2004 Stephan J. Schmidt, Matthias L. Jugel
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
package org.snipsnap.jsp;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.radeox.util.logging.Logger;
import org.radeox.util.i18n.ResourceManager;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;


/*
 * Tag creates .
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class PathSelectorTag extends TagSupport {
  String parent = null;

  public int doStartTag() throws JspException {
    if (null != parent ) {
      try {
        JspWriter out = pageContext.getOut();
        String[] elements = parent.split("/");
        out.write("<select name=\"parent\" size=\"1\"");
        if ("".equals(parent)) {
          out.write(" disabled=\"disabled\"");
        }
        out.write(">");
        out.write("<option value=\"\">");
        out.write(ResourceManager.getString("i18n.messages", "snip.path.noparent"));
        out.write("</option>");
        String path = "";
        String value = "";
        for (int i = 0; i < elements.length; i++) {
          String element = elements[i];
          if (i !=0 ) {
            path = path + " > ";
            value = value + "/";
          }
          path = path + element;
          value = value + element;
          out.write("<option value=\""+value+"\">");
          out.write(path);
          out.write("</option>");
        }
        out.write("</select>");
      } catch (IOException e) {
        Logger.warn("doStartTag in PathTag", e);
      }
    }
    return super.doStartTag();
  }

  public void setParentName(String parent) {
    try {
      this.parent = (String) ExpressionEvaluatorManager.evaluate("parent", parent, String.class, this, pageContext);
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }
  }
}
