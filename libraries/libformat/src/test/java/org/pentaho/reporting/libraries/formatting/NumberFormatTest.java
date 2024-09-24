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

package org.pentaho.reporting.libraries.formatting;

import junit.framework.TestCase;

import java.math.BigDecimal;
import java.util.Locale;

public class NumberFormatTest extends TestCase {
  public NumberFormatTest( final String name ) {
    super( name );
  }

  public NumberFormatTest() {
  }

  public void testFormatting() {
    final FastDecimalFormat decimalFormat = new FastDecimalFormat( "#,###", Locale.US );
    assertEquals( "20", decimalFormat.format( new BigDecimal( 19.937 ) ) );
    assertEquals( "21", decimalFormat.format( new BigDecimal( 20.999999999999999999999999999 ) ) );
    assertEquals( "19", decimalFormat.format( new BigDecimal( 19.0371 ) ) );
    assertEquals( "19", decimalFormat.format( new BigDecimal( 19.0375 ) ) );
    assertEquals( "19", decimalFormat.format( new BigDecimal( 19.0377 ) ) );

    assertEquals( "-20", decimalFormat.format( new BigDecimal( -19.937 ) ) );
    assertEquals( "-21", decimalFormat.format( new BigDecimal( -20.999999999999999999999999999 ) ) );
    assertEquals( "-19", decimalFormat.format( new BigDecimal( -19.0371 ) ) );
    assertEquals( "-19", decimalFormat.format( new BigDecimal( -19.0375 ) ) );
    assertEquals( "-19", decimalFormat.format( new BigDecimal( -19.0377 ) ) );

    final FastDecimalFormat percentFormat = new FastDecimalFormat( "####.00%", Locale.US );
    assertEquals( "1993.70%", percentFormat.format( new BigDecimal( 19.937 ) ) );
    assertEquals( "2100.00%", percentFormat.format( new BigDecimal( 20.999999999999999999999999999 ) ) );
    assertEquals( "2099.99%", percentFormat.format( new BigDecimal( 20.9999 ) ) );
    assertEquals( "1903.71%", percentFormat.format( new BigDecimal( 19.0371 ) ) );
    assertEquals( "1903.75%", percentFormat.format( new BigDecimal( 19.0375 ) ) );
    assertEquals( "1903.77%", percentFormat.format( new BigDecimal( 19.0377 ) ) );

    final FastDecimalFormat format = new FastDecimalFormat( "####.00%", Locale.US );
    assertEquals( "2099.99%", format.format( new BigDecimal( 20.9999 ) ) );
    assertEquals( "2100.00%", format.format( new BigDecimal( 20.999999999 ) ) );

    final FastDecimalFormat fmt2 = new FastDecimalFormat( "####.00", Locale.US );
    assertEquals( "1234.56", fmt2.format( new BigDecimal( 1234.564 ) ) );
    assertEquals( "1234.57", fmt2.format( new BigDecimal( 1234.565 ) ) );
    assertEquals( "1234.57", fmt2.format( new BigDecimal( 1234.566 ) ) );
  }

  public void testCritical() {
    final FastDecimalFormat fmt2 = new FastDecimalFormat( "####.00", Locale.US );
    assertEquals( "1234.57", fmt2.format( new BigDecimal( 1234.565 ) ) );

  }

}
