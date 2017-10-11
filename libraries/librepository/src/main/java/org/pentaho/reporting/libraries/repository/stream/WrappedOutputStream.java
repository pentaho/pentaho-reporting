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
