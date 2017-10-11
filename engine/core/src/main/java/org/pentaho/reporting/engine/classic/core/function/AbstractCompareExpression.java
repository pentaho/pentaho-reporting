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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import java.math.BigDecimal;

/**
 * The base class for all expressions that compare a value read from the datarow with another value. This expression and
 * all subclasses have been made obsolete by the Formula-Support.
 * <p/>
 * The given field name is always used to retrieve the left hand operand of the comparison. The value supplied by the
 * expression implementation is always the right hand operand.
 *
 * @author Thomas Morgner
 * @deprecated This can be better handled in a formula.
 */
public abstract class AbstractCompareExpression extends AbstractExpression {
  /**
   * A constant for an equals comparison.
   */
  public static final String EQUAL = "equal";
  /**
   * A constant for a not equals comparison.
   */
  public static final String NOT_EQUAL = "not-equal";
  /**
   * A constant for lower than comparison.
   */
  public static final String LOWER = "lower";
  /**
   * A constant for greater than comparison.
   */
  public static final String GREATER = "greater";
  /**
   * A constant for lower than or equal comparison.
   */
  public static final String LOWER_EQUAL = "lower-equal";
  /**
   * A constant for greater than or equal comparison.
   */
  public static final String GREATER_EQUAL = "greater-equal";

  /**
   * A field holding the compare method that should be used.
   */
  private String compareMethod;
  /**
   * The name of the field that should be read.
   */
  private String field;

  /**
   * The default constructor.
   */
  protected AbstractCompareExpression() {
  }

  /**
   * Compares the value read from the data-row with the value supplied by the expression itself. If the value is null or
   * no comparable or cannot be compared for other reasons, this expression returns Boolean.FALSE.
   *
   * @return Boolean.TRUE or Boolean.FALSE.
   */
  public Object getValue() {
    final Object o = getDataRow().get( getField() );
    if ( o instanceof Comparable == false ) {
      return Boolean.FALSE;
    }

    try {
      final Comparable c = (Comparable) o;
      final Comparable comparable = getComparable();
      if ( comparable == null ) {
        return Boolean.FALSE;
      }

      int result;
      try {
        // Comparing the easy way: Both values are the same type ..
        result = c.compareTo( comparable );
        // this results in a class-cast exception if they are not the same type.
      } catch ( Exception e ) {
        try {
          // invert it ..
          // Comparing the easy way: Both values are the same type ..
          result = -comparable.compareTo( c );
          // this results in a class-cast exception if they are not the same type.
        } catch ( Exception e2 ) {
          if ( c instanceof Number && comparable instanceof Number ) {
            final BigDecimal bd1 = new BigDecimal( String.valueOf( c ) );
            final BigDecimal bd2 = new BigDecimal( String.valueOf( comparable ) );
            result = bd1.compareTo( bd2 );
          } else {
            // not comparable ..
            return Boolean.FALSE;
          }
        }
      }

      final String method = getCompareMethod();
      if ( AbstractCompareExpression.EQUAL.equals( method ) ) {
        if ( result == 0 ) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if ( AbstractCompareExpression.NOT_EQUAL.equals( method ) ) {
        if ( result != 0 ) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if ( AbstractCompareExpression.LOWER.equals( method ) ) {
        if ( result < 0 ) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if ( AbstractCompareExpression.LOWER_EQUAL.equals( method ) ) {
        if ( result <= 0 ) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if ( AbstractCompareExpression.GREATER.equals( method ) ) {
        if ( result > 0 ) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
      if ( AbstractCompareExpression.GREATER_EQUAL.equals( method ) ) {
        if ( result >= 0 ) {
          return Boolean.TRUE;
        }
        return Boolean.FALSE;
      }
    } catch ( Exception e ) {
      // ignore ...
    }
    return Boolean.FALSE;
  }

  /**
   * Returns the name of the field from where to read the first operand.
   *
   * @return the field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the field from where to read the first operand.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns the compare method that should be used in the expression evaluation. Must be one of the constants declared
   * in this expression.
   *
   * @return the compare method.
   */
  public String getCompareMethod() {
    return compareMethod;
  }

  /**
   * Defines the compare method that should be used in the expression evaluation. Must be one of the constants declared
   * in this expression.
   *
   * @param compareMethod
   *          the compare method.
   */
  public void setCompareMethod( final String compareMethod ) {
    this.compareMethod = compareMethod;
  }

  /**
   * Returns the right-hand operand used in the comparison.
   *
   * @return the right-hand operand.
   */
  protected abstract Comparable getComparable();

}
