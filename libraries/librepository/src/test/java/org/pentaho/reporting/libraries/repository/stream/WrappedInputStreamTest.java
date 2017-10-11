/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.libraries.repository.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WrappedInputStreamTest {

  private InputStream parent;

  private class TestStream extends InputStream {

    @Override public int read() throws IOException {
      return 0;
    }

    @Override public void close() throws IOException {
      throw new AssertionError( "Should not be closed" );
    }
  }



  @Before
  public void setup() {
    parent = new TestStream();
  }

  @After
  public void destroy() {
   parent = null;
  }

  @Test
  public void notCloseEntireStream() throws Exception {
    final WrappedInputStream wrappedInputStream = new WrappedInputStream( parent );
    assertFalse( wrappedInputStream.isClosed() );
    wrappedInputStream.close();
    assertTrue( wrappedInputStream.isClosed() );
  }

}
