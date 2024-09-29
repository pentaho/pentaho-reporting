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
import java.io.InputStream;

/**
 * Creation-Date: 13.11.2006, 17:28:24
 *
 * @author Thomas Morgner
 */
public class WrappedInputStream extends InputStream {
  private boolean closed;
  private InputStream parent;

  public WrappedInputStream( final InputStream parent ) {
    if ( parent == null ) {
      throw new NullPointerException();
    }
    this.parent = parent;
  }

  public int read()
    throws IOException {
    return parent.read();
  }

  public int read( final byte[] b )
    throws IOException {
    return parent.read( b );
  }

  public int read( final byte[] b, final int off, final int len )
    throws IOException {
    return parent.read( b, off, len );
  }

  public long skip( final long n )
    throws IOException {
    return parent.skip( n );
  }

  public int available()
    throws IOException {
    return parent.available();
  }

  public void close()
    throws IOException {
    closed = true;
  }

  public boolean isClosed() {
    return closed;
  }

  public void mark( final int readlimit ) {
    parent.mark( readlimit );
  }

  public void reset()
    throws IOException {
    parent.reset();
  }

  public boolean markSupported() {
    return parent.markSupported();
  }
}
