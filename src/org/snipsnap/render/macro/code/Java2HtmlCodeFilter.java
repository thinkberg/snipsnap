/*
 * This file is part of "SnipSnap Radeox Rendering Engine".
 *
 * Copyright (c) 2002 Stephan J. Schmidt, Matthias L. Jugel
 * All Rights Reserved.
 *
 * Please visit http://radeox.org/ for updates and contact.
 *
 * --LICENSE NOTICE--
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * --LICENSE NOTICE--
 */

package org.snipsnap.render.macro.code;

import de.java2html.converter.JavaSource;
import de.java2html.converter.JavaSource2HTMLConverter;
import de.java2html.converter.JavaSourceType;
import de.java2html.util.HTMLTools;
import org.radeox.filter.context.FilterContext;
import org.radeox.macro.code.SourceCodeFormatter;
import org.radeox.util.logging.Logger;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

/*
 * Java2HtmlCodeFilter colourizes Java source code. Uses the Java2HTML library
 * from http://www.java2html.de
 *
 * Original by MelamedZ
 *
 * @author stephan
 * @team sonicteam
 * @version $Id$
 */

public class Java2HtmlCodeFilter extends JavaSource2HTMLConverter implements SourceCodeFormatter {
  public int getPriority() {
    return 1;
  }

  public String getName() {
    return "java";
  }

  public String filter(String content, FilterContext context) {
    StringWriter writer = new StringWriter();
    try {
      JavaSource source = new JavaSource(content);
      source.doParse();
      convert(writer, source);
    } catch (IOException e) {
      Logger.warn("JavaCodeFilter: unable to convert to html");
    }
    return writer.getBuffer().toString();
  }

  public void convert(Writer writer, JavaSource source) throws IOException {
    setSource(source);
    toHTML(writer);
  }

  public String getDocumentHeader() {
    return "";
  }

  public String getDocumentFooter() {
    return "";
  }

  protected void toHTML(Writer writer) throws IOException {
    sourceCode = source.getCode();
    sourceTypes = source.getClassification();

    int start = 0;
    int end = 0;

    while (start < sourceTypes.length) {

      while (end < sourceTypes.length - 1
          && (sourceTypes[end + 1] == sourceTypes[start] || sourceTypes[end + 1] == JavaSourceType.EMPTY)) {
        ++end;
      }
      toHTML(start, end, writer);
      start = end + 1;
      end = start;
    }
    //writer.write("{link:java2html|http://www.java2html.de/|img=\"none\"}");
  }


  protected void toHTML(int start, int end, Writer writer) throws IOException {
    writer.write("<font color=\"" + sourceTypes[start].getHtmlColor() + "\">");

    String t = HTMLTools.encode(sourceCode, start, end + 1);
    writer.write(t);
//    //Replace white space by non-breaking space and line breaks by <br>
//    for (int i = 0; i < t.length(); ++i) {
//      char ch = t.charAt(i);
//      if (ch == ' ')
//        writer.write("&nbsp;");
//      else if (ch == '\n')
//        writer.write("<br>\n");
//      else
//        writer.write(ch);
//    }

    writer.write("</font>");
  }
}