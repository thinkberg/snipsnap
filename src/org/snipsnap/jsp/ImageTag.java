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
import snipsnap.api.snip.SnipLink;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

public class ImageTag extends TagSupport {
  private String ext = null;
  private String name = null;
  private String alt = null;
  private String root = null;

  public int doStartTag() throws JspException {
    try {
      name = (String) ExpressionEvaluatorManager.evaluate("name", name, String.class, this, pageContext);
      if (alt != null) {
        alt = (String) ExpressionEvaluatorManager.evaluate("alt", alt, String.class, this, pageContext);
      }
      if (ext != null) {
        ext = (String) ExpressionEvaluatorManager.evaluate("ext", ext, String.class, this, pageContext);
      }
    } catch (JspException e) {
      Logger.warn("unable to evaluate expression", e);
    }

    JspWriter out = pageContext.getOut();
    try {
      if(null == root) {

      SnipLink.appendImage(out, name, alt, ext);
      } else {
        SnipLink.appendImageWithRoot(out, SnipLink.getSpaceRoot()+"/" + root, name, alt, ext, null);
      }
    } catch (IOException e) {
      Logger.warn("ImageTag: error writing image tag for " + name);
    }
    return SKIP_BODY;
  }

  public void setExt(String ext) {
    this.ext = ext;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setAlt(String alt) {
    this.alt = alt;
  }

  public void setRoot(String root) {
    this.root = root;
  }
}
