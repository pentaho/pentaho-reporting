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
