/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.base.util;

import java.io.IOException;
import java.io.Writer;

public class StringBufferWriter extends Writer {
  private StringBuffer buffer;

  public StringBufferWriter( final StringBuffer buffer ) {
    if ( buffer == null ) {
      throw new NullPointerException();
    }
    this.buffer = buffer;
  }

  public StringBuffer getBuffer() {
    return buffer;
  }

  /**
   * Write a single character.  The character to be written is contained in the 16 low-order bits of the given integer
   * value; the 16 high-order bits are ignored.
   * <p/>
   * <p> Subclasses that intend to support efficient single-character output should override this method.
   *
   * @param c int specifying a character to be written.
   * @throws IOException If an I/O error occurs
   */
  public void write( final int c ) throws IOException {
    buffer.append( (char) c );
  }

  /**
   * Write a string.
   *
   * @param str String to be written
   * @throws IOException If an I/O error occurs
   */
  public void write( final String str ) throws IOException {
    buffer.append( str );
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param cbuf Array of characters
   * @param off  Offset from which to start writing characters
   * @param len  Number of characters to write
   * @throws IOException If an I/O error occurs
   */
  public void write( final char[] cbuf, final int off, final int len ) throws IOException {
    buffer.append( cbuf, off, len );
  }

  /**
   * Flush the stream.  If the stream has saved any characters from the various write() methods in a buffer, write them
   * immediately to their intended destination.  Then, if that destination is another character or byte stream, flush
   * it.  Thus one flush() invocation will flush all the buffers in a chain of Writers and OutputStreams.
   * <p/>
   * If the intended destination of this stream is an abstraction provided by the underlying operating system, for
   * example a file, then flushing the stream guarantees only that bytes previously written to the stream are passed to
   * the operating system for writing; it does not guarantee that they are actually written to a physical device such as
   * a disk drive.
   *
   * @throws IOException If an I/O error occurs
   */
  public void flush() throws IOException {

  }

  /**
   * Close the stream, flushing it first.  Once a stream has been closed, further write() or flush() invocations will
   * cause an IOException to be thrown.  Closing a previously-closed stream, however, has no effect.
   *
   * @throws IOException If an I/O error occurs
   */
  public void close() throws IOException {

  }
}
