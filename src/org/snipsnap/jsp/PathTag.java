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
package org.snipsnap.jsp;

import org.apache.taglibs.standard.lang.support.ExpressionEvaluatorManager;
import org.radeox.util.logging.Logger;
import snipsnap.api.snip.Snip;
import snipsnap.api.snip.SnipSpaceFactory;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;


/*
 * Tag that displays the path of a snip.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class PathTag extends TagSupport {
  Snip snip = null;

  public int doStartTag() throws JspException {
    if (null != snip) {
       try {
        JspWriter out = pageContext.getOut();
        snip.getPath().append(out, snipsnap.api.snip.SnipSpaceFactory.getInstance());
      } catch (IOException e) {
        Logger.warn("doStartTag in PathTag", e);
      }
    }
    return super.doStartTag();
  }

  public void setSnip(String snip) {
    try {
      this.snip = (Snip) ExpressionEvaluatorManager.evaluate("snip", snip, Snip.class, this, pageContext);
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }
  }
}
