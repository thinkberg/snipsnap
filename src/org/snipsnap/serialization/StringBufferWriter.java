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

package org.snipsnap.serialization;

import java.io.IOException;
import java.io.Writer;

/**
 * The same as StringWriter, but takes an existing StringBuffer in its
 * constructor.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 */

public class StringBufferWriter extends Writer {

  private StringBuffer buf;

  /**
   * Flag indicating whether the stream has been closed.
   */
  private boolean isClosed = false;

  /** Check to make sure that the stream has not been closed */
  private void ensureOpen() {
    /* This method does nothing for now.  Once we add throws clauses
 * to the I/O methods in this class, it will throw an IOException
 * if the stream has been closed.
 */
  }

  public StringBufferWriter(StringBuffer buffer) {
    buf = buffer;
    lock = buf;
  }

  /**
   * Create a new string writer, using the default initial string-buffer
   * size.
   */
  public StringBufferWriter() {
    buf = new StringBuffer();
    lock = buf;
  }

  /**
   * Create a new string writer, using the specified initial string-buffer
   * size.
   *
   * @param initialSize  an int specifying the initial size of the buffer.
   */
  public StringBufferWriter(int initialSize) {
    if (initialSize < 0) {
      throw new IllegalArgumentException("Negative buffer size");
    }
    buf = new StringBuffer(initialSize);
    lock = buf;
  }

  /**
   * Write a single character.
   */
  public void write(int c) {
    ensureOpen();
    buf.append((char) c);
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param  cbuf  Array of characters
   * @param  off   Offset from which to start writing characters
   * @param  len   Number of characters to write
   */
  public void write(char cbuf[], int off, int len) {
    ensureOpen();
    if ((off < 0) || (off > cbuf.length) || (len < 0) ||
        ((off + len) > cbuf.length) || ((off + len) < 0)) {
      throw new IndexOutOfBoundsException();
    } else if (len == 0) {
      return;
    }
    buf.append(cbuf, off, len);
  }

  /**
   * Write a string.
   */
  public void write(String str) {
    ensureOpen();
    buf.append(str);
  }

  /**
   * Write a portion of a string.
   *
   * @param  str  String to be written
   * @param  off  Offset from which to start writing characters
   * @param  len  Number of characters to write
   */
  public void write(String str, int off, int len) {
    ensureOpen();
    buf.append(str.substring(off, off + len));
  }

  /**
   * Return the buffer's current value as a string.
   */
  public String toString() {
    return buf.toString();
  }

  /**
   * Return the string buffer itself.
   *
   * @return StringBuffer holding the current buffer value.
   */
  public StringBuffer getBuffer() {
    return buf;
  }

  /**
   * Flush the stream.
   */
  public void flush() {
    ensureOpen();
  }

  /**
   * Close the stream.  This method does not release the buffer, since its
   * contents might still be required.
   */
  public void close() throws IOException {
    isClosed = true;
  }

}
