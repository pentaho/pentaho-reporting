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


package org.pentaho.reporting.libraries.base.util;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * @author Thomas Morgner
 * @author Rob Edgeler
 */
public class CSVTokenizerTest extends TestCase {
  public CSVTokenizerTest( final String name ) {
    super( name );
  }

  /**
   * @return a <code>TestSuite</code>
   */
  public static Test suite() {
    final TestSuite suite = new TestSuite();
    suite.setName( "Test for CSVTokenizer." );
    suite.addTest( new CSVTokenizerTest( "testHasMoreTokens" ) );
    return suite;
  }

  public void testHasMoreTokens() {
    CSVTokenizer tokeniser = new CSVTokenizer( "", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertTrue( "Should have no more tokens.", ( !tokeniser.hasMoreTokens() ) );

    tokeniser = new CSVTokenizer( "a,b,c", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "a", tokeniser.nextToken() );
    assertEquals( "b", tokeniser.nextToken() );
    assertEquals( "c", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( ",b,c", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "", tokeniser.nextToken() );
    assertEquals( "b", tokeniser.nextToken() );
    assertEquals( "c", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( "a,,c", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "a", tokeniser.nextToken() );
    assertEquals( "", tokeniser.nextToken() );
    assertEquals( "c", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( "a,b,", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "a", tokeniser.nextToken() );
    assertEquals( "b", tokeniser.nextToken() );
    assertEquals( "", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( ",,", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "", tokeniser.nextToken() );
    assertEquals( "", tokeniser.nextToken() );
    assertEquals( "", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( "\"\",\"\",\"\"", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "", tokeniser.nextToken() );
    assertEquals( "", tokeniser.nextToken() );
    assertEquals( "", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( "", "",
      CSVTokenizer.DOUBLE_QUATE );
    assertTrue( "Should have no more tokens.", ( !tokeniser.hasMoreTokens() ) );

    tokeniser = new CSVTokenizer( "A;B", "",
      CSVTokenizer.DOUBLE_QUATE );
    assertTrue( "Should have no more tokens.", ( !tokeniser.hasMoreTokens() ) );

    tokeniser = new CSVTokenizer( "a,b,c", CSVTokenizer.SEPARATOR_COMMA,
            "" );
    assertEquals( "Should count tokens correctly", 3, tokeniser.countTokens() );
    assertEquals( "a", tokeniser.nextToken() );
    assertEquals( "b", tokeniser.nextToken() );
    assertEquals( "c", tokeniser.nextToken() );
  }

  public void testNextToken() {
    CSVTokenizer tokeniser = new CSVTokenizer( "\"Test\"\"Test\"", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 1, tokeniser.countTokens() );
    assertEquals( "Test\"Test", tokeniser.nextToken() );

    tokeniser = new CSVTokenizer( "\"Test Test\"", CSVTokenizer.SEPARATOR_COMMA,
      CSVTokenizer.DOUBLE_QUATE );
    assertEquals( "Should count tokens correctly", 1, tokeniser.countTokens() );
    assertEquals( "Test Test", tokeniser.nextToken() );
  }

}
