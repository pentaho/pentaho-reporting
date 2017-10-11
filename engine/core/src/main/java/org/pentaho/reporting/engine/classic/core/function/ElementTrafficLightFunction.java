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

import java.awt.Color;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A function that performs basic traffic lighting based on a range of values and a given set of colors to use. The
 * default colors are for red for values smaller than 50, yellow for values smaller than 75 and green for all values
 * greater than 75. The default behaviour will be applied if no other limits and colors are defined. This function
 * respects absolute values when flagged.
 * <p/>
 * By default the given limits specify the lower boundary of an range. That means a value lower than the first given
 * limit will return the default color, a value lower than the second value will return the first color and so on.
 * <p/>
 * That logic can be inversed using the 'useOppositeLogic' flag. In that case the limits specify an upper boundary. If
 * the value read from the datarow is greater than the last limit specified, the default is returned. If the value is
 * greater than the second last limit, the last color is used, and so on.
 *
 * @author Michael D'Amour
 * @deprecated This function can be safely replaced by a formula.
 */
@SuppressWarnings( "deprecation" )
public class ElementTrafficLightFunction extends AbstractElementFormatFunction {
  /**
   * An internal immutable helper class that bundles the color and limit. This class corresponds to one entry in the
   * list of colors and limits.
   */
  private static class LightDefinition implements Comparable, Serializable, Cloneable {
    /**
     * The color of the entry.
     */
    private Color color;
    /**
     * The numeric limit of the entry.
     */
    private Number limit;

    /**
     * Creates a definition with the given limit and color.
     *
     * @param limit
     *          the limit that activates the color.
     * @param color
     *          the color for the limit.
     */
    protected LightDefinition( final Number limit, final Color color ) {
      this.limit = limit;
      this.color = color;
    }

    /**
     * Returns the color for the entry.
     *
     * @return the color.
     */
    public Color getColor() {
      return color;
    }

    /**
     * Returns the numerical limit for the entry.
     *
     * @return the limit.
     */
    public Number getLimit() {
      return limit;
    }

    /**
     * Compares the object for equality.
     *
     * @return true, if the given object is a LightDefinition where both color and limit match, false otherwise.
     */
    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final LightDefinition that = (LightDefinition) o;

      if ( ObjectUtilities.equal( color, that.color ) == false ) {
        return false;
      }
      if ( ObjectUtilities.equal( limit, that.limit ) == false ) {
        return false;
      }

      return true;
    }

    /**
     * Computes a hashcode for the LightDefinition object.
     *
     * @return the hashcode.
     */
    public int hashCode() {
      int result = 0;
      if ( color != null ) {
        result += color.hashCode();
      }
      if ( limit != null ) {
        result = 31 * result + limit.hashCode();
      } else {
        result = 31 * result;
      }
      return result;
    }

    /**
     * Compares this LightDefinition with another LightDefinition. This will happily crash if the given object is no
     * LightDefinition object.
     *
     * @param o
     *          the other object.
     * @return -1, 0 or -1 depending on whether this object is less, equal or greater than the given object.
     * @throws ClassCastException
     *           if the given object is no LightDefinition.
     */
    public int compareTo( final Object o ) {
      final LightDefinition ldef = (LightDefinition) o;
      final Number myLimit = this.getLimit();
      final Number otherLimit = ldef.getLimit();
      if ( myLimit == null && otherLimit == null ) {
        return 0;
      }
      if ( myLimit == null ) {
        return +1;
      }
      if ( otherLimit == null ) {
        return -1;
      }
      final double myValue = myLimit.doubleValue();
      final double otherValue = otherLimit.doubleValue();
      if ( myValue < otherValue ) {
        return -1;
      }
      if ( myValue > otherValue ) {
        return +1;
      }
      return 0;
    }

    /**
     * Creates a copy of this entry.
     *
     * @return a copy of the LightDefinition.
     */
    public Object clone() {
      try {
        return super.clone();
      } catch ( CloneNotSupportedException n ) {
        throw new IllegalStateException( n );
      }
    }
  }

  /**
   * A flag indicating whether limits specify the lower or the upper boundary of a range.
   */
  private boolean useOppositeLogic;
  /**
   * A flag indicating whether the values read from the field should be made absolute before they are compared to the
   * limits.
   */
  private boolean useAbsoluteValue;
  /**
   * A flag indicating whether the color is applied to the foreground or background of the element.
   */
  private boolean defineBackground;
  /**
   * The name of the data-row column from where to read the number that is compared to the limits.
   */
  private String field;
  /**
   * The default color that is used if none of the limits applies.
   */
  private Color defaultColor;
  /**
   * The list of limit and color pairs.
   */
  private ArrayList<LightDefinition> limits;
  /**
   * A temporary sorted array that speeds up the comparison.
   */
  private transient LightDefinition[] lightDefArray;

  /**
   * Default constructor.
   */
  public ElementTrafficLightFunction() {
    limits = new ArrayList<LightDefinition>();
    defaultColor = Color.red;
  }

  /**
   * Configures the default behaviour. The function will behave like a traffic-light with red for values smaller than
   * 50, yellow for values smaller than 75 but greater than 50 and green for values greater than 75.
   */
  private void configureDefaultBehaviour() {
    if ( limits.isEmpty() ) {
      limits.add( new LightDefinition( new Integer( 50 ), Color.yellow ) );
      limits.add( new LightDefinition( new Integer( 75 ), Color.green ) );
      lightDefArray = null;
    }
  }

  /**
   * Defines, whether all negative limits should be made positive, by calling 'Math.abs'.
   *
   * @return Returns the useAbsoluteValue.
   */
  public boolean isUseAbsoluteValue() {
    return useAbsoluteValue;
  }

  /**
   * Defines, whether all negative limits should be made positive, by calling 'Math.abs'.
   *
   * @param useAbsoluteValue
   *          The useAbsoluteValue to set.
   */
  public void setUseAbsoluteValue( final boolean useAbsoluteValue ) {
    this.useAbsoluteValue = useAbsoluteValue;
  }

  /**
   * Returns whether limits specify the lower or the upper boundary of a range.
   *
   * @return true, if limits specify the upper boundaries, false otherwise.
   */
  public boolean isUseOppositeLogic() {
    return useOppositeLogic;
  }

  /**
   * Defines whether limits specify the lower or the upper boundary of a range. This property defaults to false, making
   * limits define the lower boundary.
   *
   * @param useOppositeLogic
   *          true, if limits specify the upper boundaries, false otherwise.
   */
  public void setUseOppositeLogic( final boolean useOppositeLogic ) {
    this.useOppositeLogic = useOppositeLogic;
  }

  /**
   * Updates the color at the given index in the list of LightDefinition entries.
   *
   * @param index
   *          the position of the entry that should be updated.
   * @param color
   *          the new color.
   */
  public void setColor( final int index, final Color color ) {
    if ( limits.size() == index ) {
      final LightDefinition ldef = new LightDefinition( null, color );
      limits.add( ldef );
      lightDefArray = null;
    } else {
      final LightDefinition ldef = limits.get( index );
      if ( ldef == null ) {
        final LightDefinition newdef = new LightDefinition( null, color );
        limits.set( index, newdef );
        lightDefArray = null;
      } else {
        final LightDefinition newdef = new LightDefinition( ldef.getLimit(), color );
        limits.set( index, newdef );
        lightDefArray = null;
      }
    }
  }

  /**
   * Returns the color at the given index in the list of LightDefinition entries.
   *
   * @param index
   *          the position of the entry that should be queried.
   * @return the color at the given position.
   */
  public Color getColor( final int index ) {
    final LightDefinition ldef = limits.get( index );
    if ( ldef == null ) {
      return null;
    }
    return ldef.getColor();
  }

  /**
   * Returns the number of LightDefinitions defined in this function.
   *
   * @return the number of entries.
   */
  public int getColorCount() {
    return limits.size();
  }

  /**
   * Returns all colors defined in this function mapped to their respective position.
   *
   * @return the colors as array.
   */
  public Color[] getColor() {
    final Color[] retval = new Color[limits.size()];
    for ( int i = 0; i < limits.size(); i++ ) {
      final LightDefinition definition = limits.get( i );
      retval[i] = definition.getColor();
    }
    return retval;
  }

  /**
   * Updates all colors defined in this function mapped to their respective position. If the color-array contains more
   * entries than the function has, new LightDefinitions will be added. If the given array contains fewer entries, the
   * extra LightDefinitions will be deleted.
   *
   * @param colors
   *          the colors as array.
   */
  public void setColor( final Color[] colors ) {
    for ( int i = 0; i < colors.length; i++ ) {
      final Color color = colors[i];
      setColor( i, color );
    }
    final int size = this.limits.size();
    if ( size > colors.length ) {
      for ( int i = size - 1; i >= colors.length; i-- ) {
        limits.remove( i );
      }
    }
    lightDefArray = null;
  }

  /**
   * Updates the numerical limit at the given index in the list of LightDefinition entries.
   *
   * @param index
   *          the position of the entry that should be updated.
   * @param value
   *          the new numerical limit.
   */
  public void setLimit( final int index, final Number value ) {
    if ( limits.size() == index ) {
      final LightDefinition ldef = new LightDefinition( value, null );
      limits.add( ldef );
    } else {
      final LightDefinition ldef = limits.get( index );
      if ( ldef == null ) {
        final LightDefinition newdef = new LightDefinition( value, null );
        limits.set( index, newdef );
      } else {
        final LightDefinition newdef = new LightDefinition( value, ldef.getColor() );
        limits.set( index, newdef );
      }
    }
    lightDefArray = null;
  }

  /**
   * Returns the numerical limit at the given index in the list of LightDefinition entries.
   *
   * @param index
   *          the position of the entry that should be queried.
   * @return the numerical limit at the given position.
   */
  public Number getLimit( final int index ) {
    final LightDefinition ldef = limits.get( index );
    if ( ldef == null ) {
      return null;
    }
    return ldef.getLimit();
  }

  /**
   * Returns the number of LightDefinitions defined in this function.
   *
   * @return the number of entries.
   */
  public int getLimitCount() {
    return limits.size();
  }

  /**
   * Returns all numerical limits defined in this function mapped to their respective position.
   *
   * @return the numerical limits as array.
   */
  public Number[] getLimit() {
    final Number[] retval = new Number[limits.size()];
    for ( int i = 0; i < limits.size(); i++ ) {
      final LightDefinition definition = limits.get( i );
      retval[i] = definition.getLimit();
    }
    return retval;
  }

  /**
   * Updates all numerical limits defined in this function mapped to their respective position. If the numerical
   * limits-array contains more entries than the function has, new LightDefinitions will be added. If the given array
   * contains fewer entries, the extra LightDefinitions will be deleted.
   *
   * @param limits
   *          the numerical limits as array.
   */
  public void setLimit( final Number[] limits ) {
    for ( int i = 0; i < limits.length; i++ ) {
      final Number limit = limits[i];
      setLimit( i, limit );
    }
    final int size = this.limits.size();
    if ( size > limits.length ) {
      for ( int i = size - 1; i >= limits.length; i-- ) {
        this.limits.remove( i );
      }
    }
    lightDefArray = null;
  }

  /**
   * Returns the default color that is used if none of the limits applies.
   *
   * @return the default color.
   */
  public Color getDefaultColor() {
    return defaultColor;
  }

  /**
   * Defines the default color that is used if none of the limits applies.
   *
   * @param defaultColor
   *          the default color.
   */
  public void setDefaultColor( final Color defaultColor ) {
    this.defaultColor = defaultColor;
  }

  /**
   * Returns whether the computed color is applied to the foreground or background of the element.
   *
   * @return true, if the color is applied as background, false if the color is applied as foreground.
   */
  public boolean isDefineBackground() {
    return defineBackground;
  }

  /**
   * Defines whether the computed color is applied to the foreground or background of the element.
   *
   * @param defineBackground
   *          true, if the color is applied as background, false if the color is applied as foreground.
   */
  public void setDefineBackground( final boolean defineBackground ) {
    this.defineBackground = defineBackground;
  }

  /**
   * Returns the field used by the function.
   * <p/>
   * The field name corresponds to a column name in the data-row.
   *
   * @return The field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field name for the function.
   * <p/>
   * The field name corresponds to a column name in the data-row.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  protected boolean evaluateElement( final ReportElement e ) {
    // only if needed ...
    configureDefaultBehaviour();

    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      final Color color = computeColor();
      if ( defineBackground ) {
        e.getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, color );
      } else {
        e.getStyle().setStyleProperty( ElementStyleKeys.PAINT, color );
      }
      return true;
    }
    return false;
  }

  /**
   * Computes the color that corresponds to the LightDefinition entry for which the limits match the value read from
   * field.
   *
   * @return the computed color.
   */
  private Color computeColor() {
    if ( field == null ) {
      return defaultColor;
    }

    final Object o = getDataRow().get( field );
    if ( o instanceof Number == false ) {
      return defaultColor;
    }

    final Number n = (Number) o;
    final Number value;
    if ( useAbsoluteValue ) {
      if ( n instanceof BigDecimal ) {
        final BigDecimal td = (BigDecimal) n;
        value = td.abs();
      } else {
        final BigDecimal td = new BigDecimal( n.toString() );
        value = td.abs();
      }
    } else {
      value = n;
    }

    if ( lightDefArray == null ) {
      lightDefArray = limits.toArray( new LightDefinition[limits.size()] );
      Arrays.sort( lightDefArray );
    }

    if ( useOppositeLogic ) {
      // Inverse logic. The first interval ranging from '-INF' to the first limit will use the
      // first color. If the value is in the range 'limit[i]' and 'limit[i+1]', the color[i+1]
      // will be used. If the value is greater than the last limit, the default color is used.

      if ( limits.isEmpty() ) {
        return defaultColor;
      }

      Color returnColor = defaultColor;
      for ( int i = lightDefArray.length - 1; i >= 0; i-- ) {
        final LightDefinition definition = lightDefArray[i];
        if ( definition == null ) {
          continue;
        }
        final Number limit = definition.getLimit();
        if ( limit == null ) {
          continue;
        }
        if ( value.doubleValue() < limit.doubleValue() ) {
          returnColor = definition.getColor();
        }
      }
      if ( returnColor == null ) {
        return defaultColor;
      }
      return returnColor;
    } else {
      // Standard logic. The first interval from '-INF' to the first limit uses the default color.
      // from there, the color for the first limit that is greater than the given value is used.
      // For the interval ranging from the last limit to '+INF', the last color is used.
      // If there are no limits defined, the default color is always used.

      Color returnColor = defaultColor;
      for ( int i = 0; i < lightDefArray.length; i++ ) {
        final LightDefinition definition = lightDefArray[i];
        if ( definition == null ) {
          continue;
        }
        final Number limit = definition.getLimit();
        if ( limit == null ) {
          continue;
        }
        if ( value.doubleValue() >= limit.doubleValue() ) {
          returnColor = definition.getColor();
        }
      }
      if ( returnColor == null ) {
        return defaultColor;
      }
      return returnColor;
    }
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public ElementTrafficLightFunction getInstance() {
    final ElementTrafficLightFunction elf = (ElementTrafficLightFunction) super.getInstance();
    elf.limits = (ArrayList<LightDefinition>) limits.clone();
    for ( int i = 0; i < limits.size(); i++ ) {
      final LightDefinition definition = limits.get( i );
      elf.limits.set( i, (LightDefinition) definition.clone() );
    }
    if ( lightDefArray != null ) {
      elf.lightDefArray = lightDefArray.clone();
    }
    return elf;
  }
}
