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

package examples;

import org.snipsnap.snip.attachment.Attachment;
import org.snipsnap.snip.attachment.storage.AttachmentStorage;

import java.io.*;
import java.util.Map;
import java.util.HashMap;

/**
 * Attachment storage which stores attachments in a Map in memory.
 * Attachments are stored with the location as key and a byte array
 * as the value.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

// cut:start-1
public class MapAttachmentStorage
    implements AttachmentStorage {

  private Map storage;

  public MapAttachmentStorage() {
    storage = new HashMap();
  }

  public boolean exists(Attachment attachment)
      throws IOException {
    return storage.containsKey(attachment.getLocation());
  }

  public OutputStream getOutputStream(Attachment attachment)
      throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    storage.put(attachment.getLocation(), out);
    return out;
  }

  public InputStream getInputStream(Attachment attachment)
      throws IOException {
    ByteArrayOutputStream out = (ByteArrayOutputStream)
        storage.get(attachment.getLocation());
    byte[] data = out.toByteArray();
    return new ByteArrayInputStream(data);
  }

  public void delete(Attachment attachment)
      throws IOException {
    storage.remove(attachment.getLocation());

  }
// cut:end-1
}
