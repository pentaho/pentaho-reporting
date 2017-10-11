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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.formula.util;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.math.BigDecimal;

public class NumberUtil {
  public static final BigDecimal DELTA = new BigDecimal( "0.000000000000000000000000000000000000005" );
  public static final BigDecimal MINUTE_ROUNDING_DELTA = new BigDecimal( "0.00000000000000000000000000000000000009" );
  public static final BigDecimal INT_TEST_DELTA = new BigDecimal( "0.00000000000000000000000000000000005" );
  private static final int ROUND_SCALE = LibFormulaBoot.GLOBAL_SCALE - 6;

  private NumberUtil() {
  }

  public static BigDecimal getAsBigDecimal( final Number number ) {
    if ( number == null ) {
      throw new NullPointerException();
    }

    if ( number instanceof BigDecimal ) {
      return (BigDecimal) number;
    } else {
      return new BigDecimal( number.toString() );
    }
  }

  /**
   * Performs a rounding to get a more reliable (int) cast. This makes sure that nearly exact values like
   * 0.9999999..9999 are correctly interpreted as 1 while exact values like 0.99 are interpreted as 0.
   *
   * @param n
   * @return
   */
  public static BigDecimal performIntRounding( BigDecimal n ) {
    try {
      // no need to go further if the value is already an integer
      return n.setScale( 0 );
    } catch ( ArithmeticException e ) {
      //ignore and continue
    }

    final BigDecimal round;
    if ( n.signum() < 0 ) {
      n = n.subtract( DELTA );
      return n.setScale( 0, BigDecimal.ROUND_UP );
    } else {
      n = n.add( DELTA );
      round = n.setScale( 1, BigDecimal.ROUND_DOWN );
      return round.setScale( 0, BigDecimal.ROUND_DOWN );
    }
  }

  /**
   * Performs a rounding to get a more reliable (int) cast for minute function {@link
   * org.pentaho.reporting.libraries.formula.function.datetime.MinuteFunction}. See {@link
   * org.pentaho.reporting.libraries.formula.util.NumberUtilTest#testPerformMinuteRounding()} for more information.
   *
   * @param n value of the {@code BigDecimal} to be rounded
   * @return a {@code BigDecimal} rounded value
   * @see org.pentaho.reporting.libraries.formula.function.datetime.MinuteFunctionTest.java
   */
  public static BigDecimal performMinuteRounding( BigDecimal n ) {
    try {
      // no need to go further if the value is already an integer
      return n.setScale( 0 );
    } catch ( ArithmeticException e ) {
      //ignore and continue
    }
    n = n.add( MINUTE_ROUNDING_DELTA );
    return n.setScale( 0, BigDecimal.ROUND_DOWN );
  }

  public static BigDecimal performTuneRounding( BigDecimal n ) {
    try {
      // no need to go further if the value is already an integer
      n.setScale( ROUND_SCALE );
      return n;
    } catch ( ArithmeticException e ) {
      //ignore and continue
    }

    final BigDecimal round;
    if ( n.signum() < 0 ) {
      n = n.subtract( INT_TEST_DELTA );
      round = n.setScale( ROUND_SCALE, BigDecimal.ROUND_UP );
    } else {
      n = n.add( INT_TEST_DELTA );
      round = n.setScale( ROUND_SCALE, BigDecimal.ROUND_DOWN );
    }
    if ( n.compareTo( round ) == 0 ) {
      return n;
    }
    return NumberUtil.removeTrailingZeros( round );
  }


  public static BigDecimal removeTrailingZeros( final BigDecimal bd ) {
    if ( bd.signum() == 0 ) {
      return bd.setScale( 0 );
    }

    final String text = bd.toPlainString(); // get non-logarithm representation
    int scale = bd.scale();
    for ( int i = text.length() - 1; i >= 0; i-- ) {
      final char c = text.charAt( i );
      if ( c != '0' ) {
        break;
      }
      scale -= 1;
    }
    return bd.setScale( scale );
  }

  public static BigDecimal divide( final BigDecimal bd1, final BigDecimal bd2 )
    throws EvaluationException {
    if ( bd2.signum() == 0 ) {
      // prevent a division by zero ..
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARITHMETIC_VALUE );
    }
    final BigDecimal divide = bd1.divide( bd2, LibFormulaBoot.GLOBAL_SCALE, BigDecimal.ROUND_HALF_UP );
    return NumberUtil.removeTrailingZeros( divide );
  }
}
