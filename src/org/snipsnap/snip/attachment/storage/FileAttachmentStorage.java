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

package org.snipsnap.snip.attachment.storage;

import org.snipsnap.snip.attachment.Attachment;
import org.snipsnap.config.Configuration;
import org.snipsnap.app.Application;
import org.radeox.util.logging.Logger;

import java.io.*;

/**
 * AttachmentStorage which stores attachment in the file system
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class FileAttachmentStorage implements AttachmentStorage {

  private File getFile(Attachment attachment) {
    Configuration config = Application.get().getConfiguration();
    File filePath = config.getFilePath();

    return  new File(filePath, attachment.getLocation());
  }

  public boolean exists(Attachment attachment) {
    return getFile(attachment).exists();
  }

  public OutputStream getOutputStream(Attachment attachment) throws IOException {
    File file = getFile(attachment);

    // check and create the directory, where to store the snip attachments
    if (!file.getParentFile().isDirectory()) {
      file.getParentFile().mkdirs();
    }

    return new FileOutputStream(file);
  }

  public InputStream getInputStream(Attachment attachment) throws IOException {
    return new FileInputStream(getFile(attachment));
  }

  public void delete(Attachment attachment) {
    getFile(attachment).delete();
  }

  public boolean verify(Attachment attachment) throws IOException {
    if(exists(attachment)) {
      boolean modified = false;
      File file = getFile(attachment);
      if(file.length() != attachment.getSize()) {
        attachment.setSize(file.length());
        modified = true;
      }
      return !modified;
    }

    // throw file not found exception if it does not exist
    throw new FileNotFoundException(getFile(attachment).getCanonicalPath());
  }

  /**
   * Copy one attachment to another
   *
   * @param from the source attachment
   * @param to   the destination attachment
   * @throws java.io.IOException
   */
  public void copy(Attachment from, Attachment to) throws IOException {
    to.setDate(from.getDate());

    if (exists(from)) {
        BufferedOutputStream out = new BufferedOutputStream(getOutputStream(to));
        BufferedInputStream in = new BufferedInputStream(getInputStream(from));
        byte buf[] = new byte[4096];
        int length = -1;
        while ((length = in.read(buf)) != -1) {
          out.write(buf, 0, length);
        }
        out.flush();
        out.close();
        in.close();
        to.setLocation(getFile(to).getPath());
    } else {
      throw new IOException("Cannot find source file for copy");
    }
  }
}
