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
