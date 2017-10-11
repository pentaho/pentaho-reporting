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
