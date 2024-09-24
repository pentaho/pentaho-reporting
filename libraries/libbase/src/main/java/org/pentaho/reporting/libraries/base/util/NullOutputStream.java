/*
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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.libraries.base.util;

import java.io.IOException;
import java.io.OutputStream;

/**
 * A null output stream. All data written to this stream is ignored.
 *
 * @author Thomas Morgner
 */
public class NullOutputStream extends OutputStream {
  /**
   * Default constructor.
   */
  public NullOutputStream() {
  }

  /**
   * Writes to the stream (in this case, does nothing).
   *
   * @param i the value.
   * @throws IOException if there is an I/O problem.
   */
  public void write( final int i )
    throws IOException {
    // no i wont do anything here ...
  }

  /**
   * Writes to the stream (in this case, does nothing).
   *
   * @param bytes the bytes.
   * @throws IOException if there is an I/O problem.
   */
  public void write( final byte[] bytes )
    throws IOException {
    // no i wont do anything here ...
  }

  /**
   * Writes to the stream (in this case, does nothing).
   *
   * @param bytes the bytes.
   * @param off   the start offset in the data.
   * @param len   the number of bytes to write.
   * @throws IOException if there is an I/O problem.
   */
  public void write( final byte[] bytes, final int off, final int len )
    throws IOException {
    // no i wont do anything here ...
  }

}
