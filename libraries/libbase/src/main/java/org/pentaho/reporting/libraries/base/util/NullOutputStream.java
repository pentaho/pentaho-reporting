/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
