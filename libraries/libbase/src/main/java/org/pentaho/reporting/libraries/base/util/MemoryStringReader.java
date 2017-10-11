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
import java.io.Reader;

public class MemoryStringReader extends Reader {
  private char[] backend;
  private int offset;
  private int length;

  public MemoryStringReader( final char[] backend, final int offset, final int length ) {
    this.backend = backend.clone();
    this.offset = offset;
    this.length = length;
  }

  public int read( final char[] chars, final int offset, final int length ) throws IOException {
    if ( length == 0 ) {
      return 0;
    }
    if ( this.length == 0 ) {
      // signal eof
      return -1;
    }
    final int readLength = Math.min( length, this.length );
    System.arraycopy( backend, this.offset, chars, offset, readLength );
    this.offset += readLength;
    this.length -= readLength;
    return readLength;
  }

  public void close() throws IOException {
    // do nothing ..
  }
}
