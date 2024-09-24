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
* Copyright (c) 2008 - 2009 Larry Ogrodnek, Hitachi Vantara and Contributors.  All rights reserved.
*/
package org.pentaho.reporting.libraries.libsparklines.util;

import junit.framework.TestCase;

/**
 * GraphUtils tests.
 */
public class GraphUtilsTest extends TestCase {

  public void testGetAxe_returns_minus_one_for_negative_or_zero_maximum() {
    float actual = GraphUtils.getAxe( new Integer[] { -1, -10 } );
    assertEquals( -1F, actual );
    float actualForZero = GraphUtils.getAxe( new Integer[] { 0, -10 } );
    assertEquals( -1F, actualForZero );
  }

  public void testGetAxe_returns_zero_for_positive_or_zero_minimum() {
    float actual = GraphUtils.getAxe( new Integer[] { 23, 2 } );
    assertEquals( 0F, actual );
    float actualForZero = GraphUtils.getAxe( new Integer[] { 23, 0 } );
    assertEquals( 0F, actual );
  }

  public void testGetAxe_throws_exception_on_null_data_array() {
    try {
      GraphUtils.getAxe( null );
      fail( "GraphUtils.getAxe should throws exception on null data array" );
    } catch ( NullPointerException e ) {
    }
  }

  public void testGetAxe_returns_value_equals_to_maximum_positive_number() {
    float actual = GraphUtils.getAxe( new Integer[] { 10, 3, -7 } );
    assertEquals( 10F, actual );
    float actual2 = GraphUtils.getAxe( new Integer[] { 10, 3, -20 } );
    assertEquals( 10F, actual2 );
    float actual3 = GraphUtils.getAxe( new Integer[] { 40, 3, -20 } );
    assertEquals( 40F, actual3 );
  }

}
