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

package com.neotis.snip;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 *  Generates links for snips
 *
 * @author stephan
 * @version $Id$
 */
public class SnipLink {

  public static String createLink(String name, String view) {
    StringBuffer buffer = new StringBuffer();
    return appendLink(buffer, name, view).toString();
  }

  public static String createLink(String root, String name, String view) {
    StringBuffer buffer = new StringBuffer();
    return appendLink(buffer, root, name, view).toString();
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name) {
    return appendLink(buffer, name, name);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String name, String view) {
    return appendLink(buffer, "../space", name, view);
  }

  public static StringBuffer appendLink(StringBuffer buffer, String root, String name, String view) {
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

  private final static String IMAGES_ROOT = "../images";
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
    return appendImage(buffer, SnipLink.IMAGES_ROOT, name, alt, ext).toString();
  }

  /**
   * Append and image tag to a string buffer.
   * @param buffer the string buffer to append to
   * @param name the image name
   * @return the string buffer
   */
  public static StringBuffer appendImage(StringBuffer buffer, String name) {
    return appendImage(buffer, name, name);
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
    return appendImage(buffer, SnipLink.IMAGES_ROOT, name, alt);
  }

  public static StringBuffer appendImage(StringBuffer buffer, String root, String name, String alt) {
    return appendImage(buffer, root, name, alt, "png");
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
  public static StringBuffer appendImage(StringBuffer buffer, String root, String name, String alt, String ext) {
    // extract extension or leave as is, default is to append .png
    int dotIndex = name.lastIndexOf('.');
    if (dotIndex != -1) {
      String imageExt = name.substring(dotIndex + 1);
      if (extensions.contains(imageExt)) {
        ext = imageExt;
        name = name.substring(0, dotIndex);
      }
    }

    buffer.append("<img src=\"");
    buffer.append(root);
    buffer.append("/");
    buffer.append(name).append(".").append(ext);
    buffer.append("\"");
    if (alt != null) {
      buffer.append(" alt=\"");
      buffer.append(alt);
      buffer.append("\"");
    }
    buffer.append(" border=\"0\">");
    return buffer;
  }
}