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

package org.pentaho.reporting.engine.classic.core.util;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * A Wrapper stream that does never calls close on its parent. This implementation is needed when creating
 * ZipOutputStream, as the final ZipDirectory is written when close is called on the ZipOutputSteam.
 *
 * @author Thomas Morgner
 */
public class NoCloseOutputStream extends FilterOutputStream {
  /**
   * Create a new NoCloseOutputStream with the given output stream a parent.
   *
   * @param out
   *          the parent stream
   */
  public NoCloseOutputStream( final OutputStream out ) {
    super( out );
    if ( out == null ) {
      throw new NullPointerException( "Given Output Stream is null!" );
    }
  }

  /**
   * Closes this output stream and releases any system resources associated with the stream, but does not close the
   * underlying output stream.
   * <p/>
   * The <code>close</code> method of <code>FilterOutputStream</code> calls its <code>flush</code> method.
   *
   * @throws IOException
   *           if an I/O error occurs.
   * @see FilterOutputStream#flush()
   * @see FilterOutputStream#out
   */
  public void close() throws IOException {
    flush();
    // do not close the parent stream ... !
  }
}
