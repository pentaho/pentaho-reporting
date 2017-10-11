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
