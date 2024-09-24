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

package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.util.NumberUtil;

import java.math.BigDecimal;

/**
 * Creation-Date: 03.11.2006, 16:15:28
 *
 * @author Thomas Morgner
 */
public class DefaultComparator implements ExtendedComparator {
  private FormulaContext context;

  private static final int LESS = -1;
  private static final int EQUAL = 0;
  private static final int MORE = 1;

  public DefaultComparator() {
  }

  public void inititalize( final FormulaContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }
    this.context = context;
  }

  public boolean isEqual( final Type type1, final Object value1,
                          final Type type2, final Object value2 ) {
    // this is rather easy. If at least one of the types is a numeric,
    // try to compare them as numbers. (And here it gets messy.)
    if ( ObjectUtilities.equal( value1, value2 ) ) {
      return true;
    }

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    if ( type1.isFlagSet( Type.NUMERIC_TYPE ) || value1 instanceof Number ||
      type2.isFlagSet( Type.NUMERIC_TYPE ) || value2 instanceof Number ) {
      try {
        final Number number1 = typeRegistry.convertToNumber( type1, value1 );
        final Number number2 = typeRegistry.convertToNumber( type2, value2 );
        final BigDecimal bd1 = NumberUtil.getAsBigDecimal( number1 );
        final BigDecimal bd2 = NumberUtil.getAsBigDecimal( number2 );
        if ( bd1.signum() != bd2.signum() ) {
          return false;
        }

        return bd1.compareTo( bd2 ) == 0;
      } catch ( EvaluationException nfe ) {
        // ignore ..
      }
    }

    if ( type1.isFlagSet( Type.TEXT_TYPE ) || type2.isFlagSet( Type.TEXT_TYPE ) ) {
      String text1 = null;
      String text2 = null;
      try {
        // Convert both values to text ..
        text1 = typeRegistry.convertToText( type1, value1 );
        text2 = typeRegistry.convertToText( type2, value2 );
      } catch ( EvaluationException nfe ) {
        // ignore ..
      }

      if ( text1 == null && text2 == null ) {
        return true;
      }
      if ( text1 == null || text2 == null ) {
        return false;
      }
      return ObjectUtilities.equal( text1, text2 );

    }

    // Fall back to Java's equals method and hope the best ..
    return ( ObjectUtilities.equal( value1, value2 ) );
  }

  /**
   * Returns null, if the types are not comparable and are not convertible at all.
   *
   * @param type1
   * @param value1
   * @param type2
   * @param value2
   * @return
   */
  public int compare( final Type type1, final Object value1,
                      final Type type2, final Object value2 ) {
    // this is rather easy. If at least one of the types is a numeric,
    // try to compare them as numbers. (And here it gets messy.)
    if ( value1 == null && value2 == null ) {
      return DefaultComparator.EQUAL;
    }
    if ( value1 == null ) {
      return DefaultComparator.LESS;
    }
    if ( value2 == null ) {
      return DefaultComparator.MORE;
    }

    // First, we try to compare both types directly. This is the least-expensive
    // solution, as it does
    // not include any conversion operations ..
    if ( type1.isFlagSet( Type.SCALAR_TYPE ) && type2.isFlagSet( Type.SCALAR_TYPE ) ) {
      // this is something else
      if ( value1 instanceof Comparable && value2 instanceof Comparable ) {
        final Comparable c1 = (Comparable) value1;
        try {
          final int result = c1.compareTo( value2 );
          if ( result == 0 ) {
            return DefaultComparator.EQUAL;
          } else if ( result > 0 ) {
            return DefaultComparator.MORE;
          } else {
            return DefaultComparator.LESS;
          }
        } catch ( Exception e ) {
          // ignore any exception ..
        }
      }
    }

    // Next, we check the types on a numeric level.
    final TypeRegistry typeRegistry = context.getTypeRegistry();
    if ( type1.isFlagSet( Type.NUMERIC_TYPE ) || value1 instanceof Number ||
      type2.isFlagSet( Type.NUMERIC_TYPE ) || value2 instanceof Number ) {
      try {
        final Number number1 = typeRegistry.convertToNumber( type1, value1 );
        final Number number2 = typeRegistry.convertToNumber( type2, value2 );
        final BigDecimal bd1 = NumberUtil.getAsBigDecimal( number1 );
        final BigDecimal bd2 = NumberUtil.getAsBigDecimal( number2 );

        if ( bd1.signum() != bd2.signum() ) {
          if ( bd1.signum() < 0 ) {
            return DefaultComparator.LESS;
          } else if ( bd1.signum() > 0 ) {
            return DefaultComparator.MORE;
          }
        }

        final int result = bd1.compareTo( bd2 );
        if ( result == 0 ) {
          return DefaultComparator.EQUAL;
        }
        if ( result > 0 ) {
          return DefaultComparator.MORE;
        }
        return DefaultComparator.LESS;
      } catch ( EvaluationException nfe ) {
        // Ignore ..
      }
    }

    // And finally convert them to text and compare the text values ..
    // Convert both values to text ..
    String text1 = null;
    String text2 = null;
    try {
      text1 = typeRegistry.convertToText( type1, value1 );
      text2 = typeRegistry.convertToText( type2, value2 );
    } catch ( EvaluationException e ) {
      // failure here can be ignored.
    }

    if ( text1 == null && text2 == null ) {
      return DefaultComparator.EQUAL;
    }
    if ( text1 == null ) {
      return DefaultComparator.LESS;
    }
    if ( text2 == null ) {
      return DefaultComparator.MORE;
    }

    final int result = text1.compareTo( text2 );
    if ( result == 0 ) {
      return DefaultComparator.EQUAL;
    } else if ( result > 0 ) {
      return DefaultComparator.MORE;
    } else {
      return DefaultComparator.LESS;
    }
  }
}
