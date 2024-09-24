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

package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

import java.util.Arrays;

public class BulkDataUtilityTest extends TestCase {

  public BulkDataUtilityTest() {
  }

  public BulkDataUtilityTest( final String s ) {
    super( s );
  }

  public void testPushUp() {
    final String[] vals1 = new String[] { "1", "2", "3", "4", "5", "6" };
    final String[] vals2 = new String[] { "1", "2", "3", "4", "5", "6" };
    final String[] result1 = new String[] { "1", "3", "4", "2", "6", "5" };
    final String[] result2 = new String[] { "2", "3", "1", "4", "5", "6" };
    final boolean[] sels1 = new boolean[] { true, false, true, true, false, true };
    final boolean[] sels2 = new boolean[] { false, true, true, false, false, false };
    final boolean[] ressels1 = new boolean[] { true, true, true, false, true, false };
    final boolean[] ressels2 = new boolean[] { true, true, false, false, false, false };

    BulkDataUtility.pushUp( vals1, sels1 );
    BulkDataUtility.pushUp( vals2, sels2 );

    if ( Arrays.equals( vals1, result1 ) == false ) {
      throw new NullPointerException();
    }
    if ( Arrays.equals( vals2, result2 ) == false ) {
      throw new NullPointerException();
    }
    if ( Arrays.equals( sels1, ressels1 ) == false ) {
      throw new NullPointerException();
    }
    if ( Arrays.equals( sels2, ressels2 ) == false ) {
      throw new NullPointerException();
    }
  }
}
