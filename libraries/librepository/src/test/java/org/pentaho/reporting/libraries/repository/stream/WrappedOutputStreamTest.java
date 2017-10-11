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
import java.io.OutputStream;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WrappedOutputStreamTest {

  private OutputStream parent;

  private class TestStream extends OutputStream {


    @Override public void write( final int b ) throws IOException {

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
    final WrappedOutputStream wrappedOutputStream = new WrappedOutputStream( parent );
    assertFalse( wrappedOutputStream.isClosed() );
    wrappedOutputStream.close();
    assertTrue( wrappedOutputStream.isClosed() );
  }

}
