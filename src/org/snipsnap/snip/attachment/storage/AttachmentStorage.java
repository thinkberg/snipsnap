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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * Interface that describes backends for attachment storage
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public interface AttachmentStorage {
  /**
   * Check if the attachment actually exists.
   * @param attachment the attachment to check
   * @return true for existing data or false if missing
   */
  public boolean exists(Attachment attachment);
  /**
   * Get the output stream for this attachment to store its data in.
   * @param attachment the attachment meta data
   * @return an output stream where the data can be written to
   * @throws IOException if the attachment cannot be stored
   */
  public OutputStream getOutputStream(Attachment attachment) throws IOException;
  /**
   * Get an input stream to read the attachment data.
   * @param attachment the attachment meta data
   * @return the input stream to read from
   * @throws IOException if the attachment cannot be read
   */
  public InputStream getInputStream(Attachment attachment) throws IOException;
  /**
   * Delete the attachment
   * @param attachment the attachment meta data
   * @throws IOException if the data cannot be deleted
   */
  public void delete(Attachment attachment) throws IOException;

  /**
   * Copy one attachment to another
   *
   * @param from the source attachment
   * @param to the destination attachment
   * @throws IOException
   */
  public void copy(Attachment from, Attachment to) throws IOException;
  /**
   * Verify the meta data of the attachment. An implementation should check
   * all available information about the attachment, like size, modification
   * time and if possible the file type. If there are differences the metda data
   * will be modified and the method returns false.
   *
   * @param attachment the attachment meta data
   * @return true if the meta data is correct, false if the attachment meta data was modified
   * @throws IOException if the attachment is missing or not verifyable
   *
   */
  public boolean verify(Attachment attachment) throws IOException;
}
