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

package snipsnap.api.snip;

import org.radeox.util.logging.Logger;
import org.radeox.util.Encoder;
import org.radeox.util.i18n.ResourceManager;
import snipsnap.api.app.Application;
import snipsnap.api.config.Configuration;
import org.snipsnap.util.URLEncoderDecoder;
import org.snipsnap.snip.name.NameFormatter;
import org.snipsnap.snip.name.PathRemoveFormatter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import java.text.MessageFormat;

import snipsnap.api.snip.*;
import snipsnap.api.snip.Snip;

/**
 *  Generates links for snips
 *
 * @author stephan
 * @version $Id: SnipLink.java 1606 2004-05-17 10:56:18Z leo $
 */

public class SnipLink {

  public static NameFormatter formatter = new PathRemoveFormatter();

  /**
   * Append a URL String that contains a base a name-to-be-encoded and an optional anchor target.
   * @param writer the writer to append to
   * @param base the url base
   * @param name the name (to be encoded)
   * @param target the anchor target
   */
  public static Writer appendUrlWithBase(Writer writer, String base, String name, String target)
    throws IOException {
    writer.write(base);
    writer.write("/");
    writer.write(encode(name));
    if(target != null) {
      writer.write("#");
      writer.write(target);
    }
    return writer;
  }

  public static Writer appendUrl(Writer writer, String name, String target) throws IOException {
    return appendUrlWithBase(writer, getSpaceRoot(), name, target);
  }

  public static Writer appendUrl(Writer writer, String name) throws IOException {
    return appendUrlWithBase(writer, getSpaceRoot(), name, null);
  }

  public static Writer appendCommentsUrl(Writer writer, String name, String target) throws IOException {
    return appendUrlWithBase(writer, getCommentsRoot(), name, target);
  }

  /**
   * Append a create link for the specified name.
   */
  public static Writer appendCreateLink(Writer writer, String name) throws IOException {
    writer.write(makeCreateLink(name));
    return writer;
  }

  public static StringBuffer appendCreateLink(StringBuffer buffer, String name) {
    return buffer.append(makeCreateLink(name));
  }

  private static String makeCreateLink(String name) {
    MessageFormat formatter = new MessageFormat(ResourceManager.getString("i18n.messages", "sniplink.create"),
                                                ResourceManager.getLocale("i18n.messages"));

    StringBuffer link = new StringBuffer();
    link.append("<a href=\"");
    link.append(getExecRoot());
    link.append("/new?name=");
    link.append(encode(name));
    link.append("\">").append(Encoder.escape(name)).append("</a>");

    return formatter.format(new Object[]{link.toString()});
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
    return appendLinkWithRoot(buffer, root, encode(name), view).toString();
  }

  public static Writer appendLink(Writer writer, Snip snip) throws IOException {
    return appendLink(writer, snip.getName());
  }

  public static StringBuffer appendLink(StringBuffer buffer, snipsnap.api.snip.Snip snip) {
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
    return appendLinkWithRoot(writer, getSpaceRoot(), encode(name), view);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name, String view, String target) {
    return appendLinkWithRoot(buffer, getSpaceRoot(), encode(name) + "#" + target, view);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name, String view) {
    return appendLinkWithRoot(buffer, getSpaceRoot(), encode(name), view);
  }

  public static Writer appendLinkWithRoot(Writer writer, String root, String name, String view) throws IOException {
    writer.write("<a href=\"");
    writer.write(root);
    writer.write("/");
    writer.write(name);
    writer.write("\">");
    view = formatter.format(view);
    writer.write(Encoder.escape(view));
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
    view = formatter.format(view);
    buffer.append(Encoder.escape(view));
    buffer.append("</a>");
    return buffer;
  }

//  private static String absoluteLink(String path) {
//    Configuration config = Application.get().getConfiguration();
//    String contextPath = config.getPath();
//    String prefix = config.getPrefix();
//    return (contextPath != null ? contextPath : "")  +
//      (prefix != null && !"/".equals(prefix) ? prefix + "/" : "") + path;
//  }

  public static String getImageRoot() {
    return "theme/images";
  }

  public static String getSpaceRoot() {
    return "space";
  }

  public static String getExecRoot() {
    return "exec";
  }

  public static String getCommentsRoot() {
    return "comments";
  }

  private static List extensions = Arrays.asList(new String[]{"png", "jpg", "jpeg", "gif"});

  public static Writer appendImage(Writer writer, String name, String alt) throws IOException {
    return appendImageWithRoot(writer, getImageRoot(), name, alt, "png", null);
  }

  public static Writer appendImage(Writer writer, String name, String alt, String ext) throws IOException {
    return appendImageWithRoot(writer, getImageRoot(), name, alt, ext, null);
  }

  public static Writer appendImage(Writer writer, Snip snip, String name, String alt, String ext, String position) throws IOException {
    return appendImageWithRoot(writer, getSpaceRoot()+"/"+snip.getNameEncoded(), name, alt, ext, position);
  }

  /**
   * Append and image tag to a string buffer. Additionally takes an alternative text to display
   * if the browser cannot display the image.
   * @param writer the writer to append to
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
    } else {
      writer.write(" alt=\"");
      writer.write(name);
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

  // TODO 1.4 buffer.append(URLEncoder.encode(key, "iso-8859-1"));
  public static String encode(String s) {
    try {
      snipsnap.api.config.Configuration config = Application.get().getConfiguration();
      String encodedSpace = config.getEncodedSpace();
      if(null != encodedSpace && encodedSpace.length() > 0) {
        return URLEncoderDecoder.encode(s, config.getEncoding()).replace(' ', encodedSpace.charAt(0));
      } else {
        return URLEncoderDecoder.encode(s, config.getEncoding());
      }
    } catch (UnsupportedEncodingException e) {
      Logger.log(Logger.FATAL, "unsupported encoding: " + e);
      return s;
    }
  }

  public static String decode(String s) {
    try {
      snipsnap.api.config.Configuration config = Application.get().getConfiguration();
      String encodedSpace = config.getEncodedSpace();
      if (null != encodedSpace && encodedSpace.length() > 0) {
        s = s.replace(encodedSpace.charAt(0), ' ');
      }
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
