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


package org.pentaho.reporting.engine.classic.core.function;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * An expression that takes values from one or more fields and returns the average of them.
 *
 * @author Thomas Morgner
 * @deprecated this has been replaced by the ColumnAverageExpression.
 */
@SuppressWarnings( "deprecation" )
public class AverageExpression extends AbstractExpression {
  /**
   * An ordered list containing the fieldnames used in the expression.
   */
  private ArrayList fieldList;
  /**
   * The scale-property defines the precission of the divide-operation.
   */
  private int scale;
  /**
   * The rounding-property defines the precission of the divide-operation.
   */
  private int roundingMode;
  private static final BigDecimal ZERO = new BigDecimal( "0" );

  /**
   * Creates a new expression. The fields used by the expression are defined using properties named '0', '1', ... 'N'.
   * These fields should contain {@link Number} instances.
   */
  public AverageExpression() {
    this.fieldList = new ArrayList();
    scale = 14;
    roundingMode = BigDecimal.ROUND_HALF_UP;
  }

  /**
   * Returns the defined rounding mode. This influences the precision of the divide-operation.
   *
   * @return the rounding mode.
   * @see java.math.BigDecimal#divide(java.math.BigDecimal, int)
   */
  public int getRoundingMode() {
    return roundingMode;
  }

  /**
   * Defines the rounding mode. This influences the precision of the divide-operation.
   *
   * @param roundingMode
   *          the rounding mode.
   * @see java.math.BigDecimal#divide(java.math.BigDecimal, int)
   */
  public void setRoundingMode( final int roundingMode ) {
    this.roundingMode = roundingMode;
  }

  /**
   * Returns the scale for the divide-operation. The scale influences the precision of the division.
   *
   * @return the scale.
   */
  public int getScale() {
    return scale;
  }

  /**
   * Defines the scale for the divide-operation. The scale influences the precision of the division.
   *
   * @param scale
   *          the scale.
   */
  public void setScale( final int scale ) {
    this.scale = scale;
  }

  /**
   * Returns the average of the values.
   *
   * @return a BigDecimal instance.
   */
  public Object getValue() {
    final Number[] values = collectValues();
    BigDecimal total = ZERO;
    int count = 0;
    for ( int i = 0; i < values.length; i++ ) {
      final Number n = values[i];
      if ( n != null ) {
        total = total.add( new BigDecimal( n.toString() ) );
        count++;
      }
    }
    if ( count > 0 ) {
      return total.divide( new BigDecimal( String.valueOf( count ) ), scale, roundingMode );
    }
    return ZERO;
  }

  /**
   * collects the values of all fields defined in the fieldList.
   *
   * @return an Objectarray containing all defined values from the datarow
   */
  private Number[] collectValues() {
    final Number[] retval = new Number[this.fieldList.size()];
    for ( int i = 0; i < this.fieldList.size(); i++ ) {
      final String field = (String) this.fieldList.get( i );
      final Object o = getDataRow().get( field );
      if ( o instanceof Number ) {
        retval[i] = (Number) o;
      }
    }
    return retval;
  }

  /**
   * Clones the expression.
   *
   * @return A copy of this expression.
   */
  public Expression getInstance() {
    final AverageExpression ae = (AverageExpression) super.getInstance();
    ae.fieldList = (ArrayList) fieldList.clone();
    return ae;
  }

  /**
   * Returns the defined fields as array.
   *
   * @return the fields
   */
  public String[] getField() {
    return (String[]) fieldList.toArray( new String[fieldList.size()] );
  }

  /**
   * Defines all fields as array. This completely replaces any previously defined fields.
   *
   * @param fields
   *          the new list of fields.
   */
  public void setField( final String[] fields ) {
    this.fieldList.clear();
    this.fieldList.addAll( Arrays.asList( fields ) );
  }

  /**
   * Returns the defined field at the given index-position.
   *
   * @param index
   *          the position of the field name that should be queried.
   * @return the field name at the given position.
   */
  public String getField( final int index ) {
    return (String) this.fieldList.get( index );
  }

  /**
   * Defines the field in the field-list at the given index.
   *
   * @param index
   *          the position in the list, where the field should be defined.
   * @param field
   *          the name of the field.
   */
  public void setField( final int index, final String field ) {
    if ( this.fieldList.size() == index ) {
      this.fieldList.add( field );
    } else {
      this.fieldList.set( index, field );
    }
  }

  /**
   * Returns the number of fields defined in this expression.
   *
   * @return the number of fields.
   */
  public int getFieldCount() {
    return fieldList.size();
  }
}
