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

package org.pentaho.reporting.libraries.css.values;

import junit.framework.TestCase;

/**
 * JUnit Test class to test CSSNumericValue class.
 *
 * @author Ravi Hasija
 */
public class CSSNumericValueTest extends TestCase {

  /**
   * @param arg0
   */
  public CSSNumericValueTest( String arg0 ) {
    super( arg0 );
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception {
    super.setUp();
  }

  /* (non-Javadoc)
   * @see junit.framework.TestCase#tearDown()
   */
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testHappySet() {
    CSSNumericValue numValue1 = new CSSNumericValue( CSSNumericType.NUMBER, 255.00 );
    CSSNumericValue numValue2 = new CSSNumericValue( CSSNumericType.NUMBER, 255.0 );
    assertEquals( numValue1, numValue2 );

    numValue1 = new CSSNumericValue( CSSNumericType.NUMBER, 0 );
    numValue2 = new CSSNumericValue( CSSNumericType.NUMBER, 0 );
    assertEquals( numValue1, numValue2 );

    numValue1 = new CSSNumericValue( CSSNumericType.DEG, 12 );
    numValue2 = new CSSNumericValue( CSSNumericType.DEG, 12 );
    assertEquals( numValue1, numValue2 );

    numValue1 = new CSSNumericValue( CSSNumericType.CM, 12 );
    numValue2 = new CSSNumericValue( CSSNumericType.CM, 12 );
    assertEquals( numValue1, numValue2 );

    numValue1 = new CSSNumericValue( CSSNumericType.EM, 12 );
    numValue2 = new CSSNumericValue( CSSNumericType.EM, 12 );
    assertEquals( numValue1, numValue2 );

    numValue1 = new CSSNumericValue( CSSNumericType.PX, 12 );
    numValue2 = new CSSNumericValue( CSSNumericType.PX, 12 );
    assertEquals( numValue1, numValue2 );

    numValue1 = new CSSNumericValue( CSSNumericType.INCH, 12 );
    numValue2 = new CSSNumericValue( CSSNumericType.INCH, 12 );
    assertEquals( numValue1, numValue2 );

    numValue1 = CSSNumericValue.createValue( CSSNumericType.NUMBER, 12 );
    assertEquals( 12.0, numValue1.getValue(), 0 );

    numValue1 = CSSNumericValue.createPtValue( 12 );
    assertEquals( 12.0, numValue1.getValue(), 0 );
    assertEquals( CSSNumericType.PT, numValue1.getType() );
    assertEquals( "12pt", numValue1.getCSSText() ); //$NON-NLS-1$
    assertEquals( "12pt", numValue1.toString() ); //$NON-NLS-1$

    numValue1 = CSSNumericValue.createValue( CSSNumericType.CM, 95 );
    assertEquals( 95.0, numValue1.getValue(), 0 );
    assertEquals( CSSNumericType.CM, numValue1.getType() );
    assertEquals( "95cm", numValue1.getCSSText() ); //$NON-NLS-1$
    assertEquals( "95cm", numValue1.toString() ); //$NON-NLS-1$
  }

  public void testFailSet() {
    CSSNumericValue numValue1 = new CSSNumericValue( CSSNumericType.NUMBER, 255.00 );
    CSSNumericValue numValue2 = new CSSNumericValue( CSSNumericType.NUMBER, 22.0 );
    assertEquals( numValue1.getType(), numValue2.getType() );
    assertNotSame( new Double( numValue1.getValue() ), new Double( numValue2.getValue() ) );

    numValue1 = CSSNumericValue.createValue( CSSNumericType.NUMBER, 12 );
    assertNotSame( new Double( 12.0 ), new Double( numValue1.getValue() ) );
    assertEquals( 12.0, numValue1.getValue(), 0 );

    numValue1 = CSSNumericValue.createValue( CSSNumericType.NUMBER, 12 );
    numValue2 = CSSNumericValue.createValue( CSSNumericType.CM, 12 );
    assertEquals( numValue1.getValue(), numValue2.getValue(), 0 );
    assertNotSame( numValue1.getType(), numValue2.getType() );
  }
}
