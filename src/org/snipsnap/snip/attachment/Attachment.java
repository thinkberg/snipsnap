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

package org.snipsnap.snip.attachment;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Date;

/**
 * Class for grouping and managing attachments for a snip
 *
 * @author Matthias L. Jugel
 * @version $Id$
 */

public class Attachment {

  private String name;
  private String contentType;
  private long size;
  private Date date;
  private File file;

  /**
   * Create an attachment and check whether it's a local file or URL.
   */
  public Attachment(String name, String contentType, Date date, long size, String location) {
    this(name, contentType, size, date, new File(location));
  }


  public Attachment(String name, String contentType, long size,  Date date, File file) {
    setName(name);
    setContentType(contentType);
    setSize(size);
    setDate(date);
    setFile(file);
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public String getContentType() {
    return contentType;
  }

  public void setSize(long size) {
    this.size = size;
  }

  public long getSize() {
    return size;
  }

  public void setDate(Date date) {
    this.date = date;
  }

  public Date getDate() {
    return date;
  }

  public void setFile(File file) {
    this.file = file;
  }

  public File getFile() {
    return file;
  }

  public InputStream getInputStream() throws IOException {
    return new FileInputStream(file);
  }

  public void destroy() {
    file.delete();
  }
}
