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


package org.pentaho.reporting.engine.classic.core.util;

import junit.framework.Assert;
import junit.framework.TestCase;

public class RingBufferTest extends TestCase {
  public RingBufferTest() {
  }

  public void testInvalidSize() {
    try {
      new RingBuffer( 0 );
      Assert.fail();
    } catch ( IllegalArgumentException e ) {
      // ignore
    }
  }

  public void testAdding() {
    final RingBuffer<Integer> tc = new RingBuffer<Integer>( 3 );
    tc.add( 10 );
    tc.add( 20 );
    tc.add( 30 );
    tc.add( 40 );
    assertEquals( 40, (int) tc.getLastValue() );
    assertEquals( 20, (int) tc.getFirstValue() );
  }

  public void testAdding2() {
    final RingBuffer<Integer> tc = new RingBuffer<Integer>( 3 );
    tc.add( 10 );
    tc.add( 20 );
    tc.add( 30 );
    assertEquals( 30, (int) tc.getLastValue() );
    assertEquals( 10, (int) tc.getFirstValue() );
  }

  public void testReplace() {
    final RingBuffer<Integer> tc = new RingBuffer<Integer>( 3 );
    tc.add( 10 );
    tc.add( 20 );
    tc.add( 30 );
    assertEquals( 30, (int) tc.getLastValue() );
    assertEquals( 10, (int) tc.getFirstValue() );
    tc.replaceLastAdded( 40 );
    assertEquals( 40, (int) tc.getLastValue() );
    assertEquals( 10, (int) tc.getFirstValue() );
    tc.add( 50 );
    assertEquals( 50, (int) tc.getLastValue() );
    assertEquals( 20, (int) tc.getFirstValue() );
  }

  public void testReplaceFirst() {
    final RingBuffer<Integer> tc = new RingBuffer<Integer>( 3 );
    tc.replaceLastAdded( 10 );
    assertEquals( 10, (int) tc.getFirstValue() );
    assertEquals( 10, (int) tc.getLastValue() );
    tc.add( 10 );
    tc.add( 20 );
    tc.add( 30 );
    tc.add( 40 );
    assertEquals( 40, (int) tc.getLastValue() );
    assertEquals( 20, (int) tc.getFirstValue() );
  }

}
