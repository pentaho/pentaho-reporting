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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import java.awt.Color;
import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.filter.types.ElementTypeUtils;
import org.pentaho.reporting.engine.classic.core.function.ColumnAggregationExpression;
import org.pentaho.reporting.libraries.libsparklines.BarGraphDrawable;
import org.pentaho.reporting.libraries.libsparklines.LineGraphDrawable;
import org.pentaho.reporting.libraries.libsparklines.PieGraphDrawable;

public class SparklineExpression extends ColumnAggregationExpression {
  public static final Color DEFAULT_COLOR = Color.GRAY;
  public static final Color DEFAULT_HIGH_COLOR = Color.BLACK;
  public static final Color DEFAULT_MEDIUM_COLOR = Color.YELLOW;
  public static final Color DEFAULT_LOW_COLOR = Color.GREEN;
  public static final Color DEFAULT_LAST_COLOR = Color.RED;

  private String type;
  private Color color;
  private Color backgroundColor;
  private Color highColor;
  private Color mediumColor;
  private Color lowColor;
  private Color lastColor;
  private Double highSlice;
  private Double mediumSlice;
  private Double lowSlice;
  private int spacing;
  private boolean counterClockWise = false;
  private int startAngle = 0;

  private String rawDataField;

  public SparklineExpression() {
    spacing = 2;
    color = DEFAULT_COLOR;
    highColor = DEFAULT_HIGH_COLOR;
    lowColor = DEFAULT_LOW_COLOR;
    mediumColor = DEFAULT_MEDIUM_COLOR;
    lastColor = DEFAULT_LAST_COLOR;
    type = "bar";
  }

  public String getType() {
    return type;
  }

  /**
   * Type can be either "bar", "line" or "pie".
   *
   * @param type
   */
  public void setType( final String type ) {
    this.type = type;
  }

  public String getRawDataField() {
    return rawDataField;
  }

  public void setRawDataField( final String rawDataField ) {
    this.rawDataField = rawDataField;
  }

  public Color getColor() {
    return color;
  }

  public void setColor( final Color color ) {
    this.color = color;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor( final Color backgroundColor ) {
    this.backgroundColor = backgroundColor;
  }

  /**
   * Returns the color for the highest bars (for bar graphs) or the color for the highest slice (for pie graphs)
   *
   * @return The High bar/slice color.
   */
  public Color getHighColor() {
    return highColor;
  }

  /**
   * Sets the color for the highest bars (for bar graphs) or the color for the highest slice (for pie graphs)
   *
   * @param highColor
   *          The color to use.
   */
  public void setHighColor( final Color highColor ) {
    this.highColor = highColor;
  }

  /**
   * Returns the color of the last bar (for bar graphs).
   *
   * @return The last bar color
   */
  public Color getLastColor() {
    return lastColor;
  }

  /**
   * Sets the color of the last bar (for bar graphs)
   *
   * @param lastColor
   *          The color to use.
   */
  public void setLastColor( final Color lastColor ) {
    this.lastColor = lastColor;
  }

  /**
   * Returns the color for the medium slice (for pie graphs)
   *
   * @return The Medium color.
   */
  public Color getMediumColor() {
    return mediumColor;
  }

  /**
   * Sets the color for the medium slice (for pie graphs)
   *
   * @param mediumColor
   *          The color to use.
   */
  public void setMediumColor( Color mediumColor ) {
    this.mediumColor = mediumColor;
  }

  /**
   * Returns the color for the lower slice (for pie graphs)
   *
   * @return The Low color.
   */
  public Color getLowColor() {
    return lowColor;
  }

  /**
   * Sets the color for the lower slice (for pie graphs)
   *
   * @param lowColor
   *          The color to use.
   */
  public void setLowColor( Color lowColor ) {
    this.lowColor = lowColor;
  }

  /**
   * Returns the percentage from which to end the higher slice (for pie graphs). It should be "1.0".
   *
   * @return The slice percentage
   */
  public Double getHighSlice() {
    return highSlice;
  }

  /**
   * Sets the percentage from which to end the higher slice (for pie graphs). It should be "1.0".
   *
   * @param highSlice
   *          The begin in percentage.
   */
  public void setHighSlice( Double highSlice ) {
    this.highSlice = highSlice;
  }

  /**
   * Returns the percentage from which to end the medium slice (for pie graphs).
   *
   * @return The slice percentage
   */
  public Double getMediumSlice() {
    return mediumSlice;
  }

  /**
   * Sets the percentage from which to end the medium slice (for pie graphs).
   *
   * @param mediumSlice
   *          The begin in percentage.
   */
  public void setMediumSlice( Double mediumSlice ) {
    this.mediumSlice = mediumSlice;
  }

  /**
   * Returns the percentage from which to end the lower slice (for pie graphs).
   *
   * @return The slice percentage
   */
  public Double getLowSlice() {
    return lowSlice;
  }

  /**
   * Sets the percentage from which to end the lower slice (for pie graphs).
   *
   * @param lowSlice
   *          The begin in percentage.
   */
  public void setLowSlice( Double lowSlice ) {
    this.lowSlice = lowSlice;
  }

  /**
   * Returns if the graph should be drawn in counter clockwise or not (for pie graphs).
   *
   * @return true if counter clockwise or false.
   */
  public boolean isCounterClockWise() {
    return counterClockWise;
  }

  /**
   * Sets the graph drawing orientation (for pie graphs). <code>true</code> means counter clockwise.
   *
   * @param counterClockWise
   *          The new clockwise value.
   */
  public void setCounterClockWise( boolean counterClockWise ) {
    this.counterClockWise = counterClockWise;
  }

  /**
   * Returns the angle from which the graph should start drawing (for pie graphs). 0 means 12 o'clock.
   *
   * @return The angle in degrees.
   */
  public int getStartAngle() {
    return startAngle;
  }

  /**
   * Sets the angle from which the graph should start drawing (for pie graphs). 0 means 12 o'clock.
   *
   * @param startAngle
   *          The new angle in degrees.
   */
  public void setStartAngle( int startAngle ) {
    this.startAngle = startAngle;
  }

  /**
   * Returns the spacing between each datapoint.
   *
   * @return The spacing.
   */
  public int getSpacing() {
    return spacing;
  }

  /**
   * Sets the spacing between each datapoint.
   *
   * @param spacing
   *          The new spacing value to use.
   */
  public void setSpacing( final int spacing ) {
    this.spacing = spacing;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Number[] data = getData();
    if ( "line".equals( type ) ) {
      final LineGraphDrawable drawable = new LineGraphDrawable();
      drawable.setBackground( backgroundColor );
      drawable.setColor( color );
      drawable.setSpacing( spacing );
      drawable.setData( data );
      return drawable;
    } else if ( "bar".equals( type ) ) {
      final BarGraphDrawable drawable = new BarGraphDrawable();
      drawable.setBackground( backgroundColor );
      drawable.setColor( color );
      drawable.setHighColor( highColor );
      drawable.setLastColor( lastColor );
      drawable.setData( data );
      drawable.setSpacing( spacing );
      return drawable;
    } else if ( "pie".equals( type ) ) {
      final PieGraphDrawable drawable = new PieGraphDrawable();
      if ( data.length < 1 ) {
        return null;
      }
      drawable.setValue( data[0] );
      drawable.setColor( color );
      drawable.setBackground( backgroundColor );
      drawable.setLowColor( lowColor );
      drawable.setHighColor( highColor );
      drawable.setMediumColor( mediumColor );
      drawable.setCounterClockWise( counterClockWise );
      drawable.setStartAngle( startAngle );
      if ( lowSlice != null ) {
        drawable.setLowSlice( lowSlice );
      }
      if ( mediumSlice != null ) {
        drawable.setMediumSlice( mediumSlice );
      }
      if ( highSlice != null ) {
        drawable.setHighSlice( highSlice );
      }

      return drawable;
    }
    return null;
  }

  protected Number[] getData() {

    if ( rawDataField != null ) {
      final Object o = getDataRow().get( rawDataField );
      final Number[] retval = ElementTypeUtils.getData( o );
      if ( retval != null ) {
        return retval;
      }
    }

    final ArrayList numbers = new ArrayList();

    final Object[] values = getFieldValues();
    for ( int i = 0; i < values.length; i++ ) {
      final Object value = values[i];
      if ( value instanceof Number ) {
        final Number n = (Number) value;
        numbers.add( n );
      }
    }
    return (Number[]) numbers.toArray( new Number[numbers.size()] );
  }

}
