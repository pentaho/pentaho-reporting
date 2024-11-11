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


package org.pentaho.reporting.libraries.libsparklines;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.StringTokenizer;

/**
 * StringUtils Tester.
 *
 * @author Cedric Pronzato
 */
public class StringUtilsTest extends TestCase {
  public StringUtilsTest( final String name ) {
    super( name );
  }

  public static void testBigDecimal() {
    final BigDecimal bigDecimal = new BigDecimal( "10e+6" );
    assertNotNull( bigDecimal );
    final StringTokenizer stringTokenizer = new StringTokenizer( "10, 10e+6 ", "," );
    assertEquals( "Unexpected Tokenizer size", 2, stringTokenizer.countTokens() );
    while ( stringTokenizer.hasMoreTokens() ) {
      final String s = stringTokenizer.nextToken().trim();
      final BigDecimal bigDecimal2 = new BigDecimal( s );
      assertNotNull( bigDecimal2 );
    }
  }
}
