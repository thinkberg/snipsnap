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

package org.snipsnap.snip;

import org.snipsnap.app.Application;
import org.snipsnap.config.AppConfiguration;
import org.radeox.util.StringBufferWriter;
import org.radeox.filter.EscapeFilter;
import org.snipsnap.util.URLEncoderDecoder;
import org.radeox.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 *  Generates links for snips
 *
 * @author stephan
 * @version $Id$
 */

public class SnipLink {

  public static void appendUrl(Writer writer, String name) throws IOException {
    writer.write("../space/");
    writer.write(name);
    return;
  }

  public static void appendUrl(StringBuffer buffer, String name) {
    buffer.append("../space/");
    buffer.append(name);
    return;
  }

  public static void createCreateLink(Writer writer, String name) throws IOException {
    writer.write(EscapeFilter.escape('['));
    writer.write("create <a href=\"../exec/edit?name=");
    writer.write(SnipLink.encode(name));
    writer.write("\">");
    writer.write(name);
    writer.write("</a>");
    writer.write(EscapeFilter.escape(']'));
    return;
  }

  public static void createCreateLink(StringBuffer buffer, String name) {
    buffer.append(EscapeFilter.escape('['));
    buffer.append("create <a href=\"../exec/edit?name=");
    buffer.append(SnipLink.encode(name));
    buffer.append("\">").append(name).append("</a>");
    buffer.append(EscapeFilter.escape(']'));
    return;
  }

  public static String createLink(String name) {
    StringBuffer buffer = new StringBuffer();
    return appendLink(buffer, name, name).toString();
  }

  public static String createLink(String name, String view) {
    StringBuffer buffer = new StringBuffer();
    return appendLink(buffer, name, view).toString();
  }

  public static String createLink(String root, String name, String view) {
    StringBuffer buffer = new StringBuffer();
    return appendLinkWithRoot(buffer, root, name, view).toString();
  }

  public static Writer appendLink(Writer writer, Snip snip) throws IOException {
    return appendLink(writer, snip.getName());
  }

  public static StringBuffer appendLink(StringBuffer buffer, Snip snip) {
    return appendLink(buffer, snip.getName());
  }

  public static Writer appendLink(Writer writer, String name) throws IOException {
    StringBuffer buffer = new StringBuffer();
    appendLink(buffer, name, name);
    writer.write(buffer.toString());
    return writer;
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name) {
    return appendLink(buffer, name, name);
  }

  public static Writer appendLink(Writer writer, String name, String view) throws IOException {
    return appendLinkWithRoot(writer, "../space", encode(name), view);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name, String view, String target) {
    return appendLinkWithRoot(buffer, "../space", encode(name) + "#" + target, view);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name, String view) {
    return appendLinkWithRoot(buffer, "../space", encode(name), view);
  }

  public static Writer appendLinkWithRoot(Writer writer, String root, String name, String view) throws IOException {
    writer.write("<a href=\"");
    writer.write(root);
    writer.write("/");
    writer.write(name);
    writer.write("\">");
    writer.write(view);
    writer.write("</a>");
    return writer;
  }

  /**
   * Create a link with a root and a special view. The name will not be url encoded!
   */
  public static StringBuffer appendLinkWithRoot(StringBuffer buffer, String root, String name, String view) {
    buffer.append("<a href=\"");
    buffer.append(root);
    buffer.append("/");
    buffer.append(name);
    buffer.append("\">");
    buffer.append(view);
    buffer.append("</a>");
    return buffer;
  }

  public static String absoluteLink(HttpServletRequest request, String path) {
    return request.getContextPath() + path;
  }

  //private final static String IMAGES_ROOT = "../images";
  private static String getImagesRoot() {
    // return "../images";
    return Application.get().getConfiguration().getUrl("/images");
  }

  private static List extensions = Arrays.asList(new String[]{"png", "jpg", "jpeg", "gif"});

  /**
   * Create a new img tag with the name as specified. The alt attribute will be the same as the name.
   * @param name the actual image name
   * @return a string containing the img tag
   */
  public static String createImage(String name) {
    StringBuffer buffer = new StringBuffer();
    return appendImage(buffer, name, name).toString();
  }

  /**
   * Create a new img tag with the name as specified. The alt attribute will be set as well.
   * @param name the actual image name
   * @param alt an alternative text for image-less browsers
   * @return a string containing the img tag
   */
  public static String createImage(String name, String alt) {
    StringBuffer buffer = new StringBuffer();
    return appendImage(buffer, name, alt).toString();
  }

  public static String createImage(String name, String alt, String ext) {
    StringBuffer buffer = new StringBuffer();
    return appendImageWithRoot(buffer, getImagesRoot(), name, alt, ext, null).toString();
  }

  public static StringBuffer appendExternalImage(StringBuffer buffer, String url, String position) {
    buffer.append("<img src=\"");
    buffer.append(url);
    buffer.append("\" ");
    if (position != null) {
      buffer.append("class=\"");
      buffer.append(position);
      buffer.append("\" ");
    }
    buffer.append("border=\"0\"/>");
    return buffer;
  }

  /**
   * Append and image tag to a string buffer. Additionally takes an alternative text to display
   * if the browser cannot display the image.
   * @param buffer the string buffer to append to
   * @param name the image name
   * @param alt the alternative text
   * @return the string buffer
   */
  public static StringBuffer appendImage(StringBuffer buffer, String name, String alt) {
    return appendImageWithRoot(buffer, getImagesRoot(), name, alt, "png", null);
  }

  public static Writer appendImage(Writer writer, String name, String alt, String ext) throws IOException {
    return appendImageWithRoot(writer, getImagesRoot(), name, alt, ext, null);
  }

  public static Writer appendImage(Writer writer, String name, String alt) throws IOException {
    return appendImageWithRoot(writer, getImagesRoot(), name, alt, "png", null);
  }

  public static StringBuffer appendImage(StringBuffer buffer, String name, String alt, String ext, String position) {
    return appendImageWithRoot(buffer, getImagesRoot(), name, alt, ext, position);
  }

  /**
   * Append and image tag to a string buffer. Additionally takes an alternative text to display
   * if the browser cannot display the image.
   * @param buffer the string buffer to append to
   * @param root the root path for images
   * @param name the image name
   * @param alt an alternative text
   * @return the string buffer
   */
  public static Writer appendImageWithRoot(Writer writer, String root, String name,
                                           String alt, String ext, String position)
      throws IOException {
    // extract extension or leave as is, default is to append .png
    int dotIndex = name.lastIndexOf('.');
    if (dotIndex != -1) {
      String imageExt = name.substring(dotIndex + 1);
      if (extensions.contains(imageExt.toLowerCase())) {
        ext = imageExt;
        name = name.substring(0, dotIndex);
      }
    }
    if (null == ext) {
      ext = "png";
    }

    writer.write("<img src=\"");
    writer.write(root);
    writer.write("/");
    writer.write(name);
    writer.write(".");
    writer.write(ext);
    writer.write("\"");
    if (alt != null) {
      writer.write(" alt=\"");
      writer.write(alt);
      writer.write("\"");
    }
    if (position != null) {
      writer.write(" class=\"");
      writer.write(position);
      writer.write("\"");
    }
    writer.write(" border=\"0\"/>");
    return writer;
  }


  public static StringBuffer appendImageWithRoot(StringBuffer buffer, String root,
                                                 String name, String alt, String ext, String position) {
    Writer writer = new StringBufferWriter(buffer);
    try {
      appendImageWithRoot(writer, root, name, alt, ext, position);
    } catch (IOException e) {
    }
    return buffer;
  }


  // TODO 1.4 buffer.append(URLEncoder.encode(key, "iso-8859-1"));
  public static String encode(String s) {
    try {
      AppConfiguration config = Application.get().getConfiguration();
      return URLEncoderDecoder.encode(s, config.getEncoding());
    } catch (UnsupportedEncodingException e) {
      Logger.log(Logger.FATAL, "unsupported encoding: " + e);
      return s;
    }
  }

  public static String decode(String s) {
    try {
      AppConfiguration config = Application.get().getConfiguration();
      return URLEncoderDecoder.decode(s, config.getEncoding());
    } catch (UnsupportedEncodingException e) {
      Logger.log(Logger.FATAL, "unsupported encoding: " + e);
      return s;
    }
  }

  public static String cutLength(String url, int len) {
    if (url != null && len > 3 && url.length() > len) {
      return url.substring(0, len - 3) + "...";
    }
    return url;
  }

}
