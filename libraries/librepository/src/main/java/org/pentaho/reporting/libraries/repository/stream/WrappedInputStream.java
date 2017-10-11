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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
