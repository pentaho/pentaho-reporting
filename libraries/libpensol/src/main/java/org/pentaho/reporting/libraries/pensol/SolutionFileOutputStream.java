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

package org.pentaho.reporting.libraries.pensol;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SolutionFileOutputStream extends OutputStream {
  private ByteArrayOutputStream outputStream;
  private boolean closed;
  private WebSolutionFileObject item;

  public SolutionFileOutputStream( final WebSolutionFileObject item, final byte[] existingData ) throws IOException {
    this.item = item;
    this.outputStream = new ByteArrayOutputStream( Math.max( 8192, existingData.length ) );
    this.outputStream.write( existingData );
  }

  public void write( final int b )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.write( b );
  }

  public void write( final byte[] b, final int off, final int len )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.write( b, off, len );
  }

  public void close()
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }

    closed = true;

    outputStream.close();
    final byte[] data = outputStream.toByteArray();
    item.writeData( data );


  }

  public void write( final byte[] b )
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.write( b );
  }

  public void flush()
    throws IOException {
    if ( closed ) {
      throw new IOException( "Already closed" );
    }
    outputStream.flush();
  }
}
