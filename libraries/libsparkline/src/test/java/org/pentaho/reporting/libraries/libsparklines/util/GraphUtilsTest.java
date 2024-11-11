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
