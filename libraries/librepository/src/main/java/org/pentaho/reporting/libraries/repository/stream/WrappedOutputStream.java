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

package org.pentaho.reporting.libraries.repository.stream;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Creation-Date: 13.11.2006, 17:30:06
 *
 * @author Thomas Morgner
 */
public class WrappedOutputStream extends OutputStream {
  private OutputStream stream;
  private boolean closed;

  public WrappedOutputStream( final OutputStream stream ) {
    if ( stream == null ) {
      throw new NullPointerException();
    }
    this.stream = stream;
  }

  public void write( final int b )
    throws IOException {
    stream.write( b );
  }

  public void write( final byte[] b )
    throws IOException {
    stream.write( b );
  }

  public void write( final byte[] b, final int off, final int len )
    throws IOException {
    stream.write( b, off, len );
  }

  public void flush()
    throws IOException {
    stream.flush();
  }

  public void close()
    throws IOException {
    closed = true;
    stream.flush();
  }

  public boolean isClosed() {
    return closed;
  }
}
