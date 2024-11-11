/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
