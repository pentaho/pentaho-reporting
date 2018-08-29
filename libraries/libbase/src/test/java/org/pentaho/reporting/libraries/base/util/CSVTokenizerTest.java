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
* Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
*/

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
