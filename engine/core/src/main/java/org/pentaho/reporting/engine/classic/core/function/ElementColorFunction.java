/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.function;

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A function that alternates between true and false for each item within a group. The functions value affects a defined
 * elements color. If the function evaluates to true, the named element is painted with the colorTrue, else the element
 * is painted with colorFalse.
 * <p/>
 * Use the property <code>element</code> to name an element contained in the ItemBand whose color should be affected by
 * this function. All colors have the color 'black' by default.
 *
 * @author Thomas Morgner
 * @deprecated add a style expression for the 'paint' style instead
 */
public class ElementColorFunction extends AbstractElementFormatFunction {
  /**
   * the color if the field is TRUE.
   */
  private Color colorTrue;
  /**
   * the color if the field is FALSE.
   */
  private Color colorFalse;

  /**
   * The field from where to read the Boolean value.
   */
  private String field;

  /**
   * Default constructor.
   */
  public ElementColorFunction() {
  }

  /**
   * Returns the field used by the function. The field name corresponds to a column name in the report's data-row.
   *
   * @return The field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Sets the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Sets the color for true values.
   *
   * @param colorTrue
   *          the color.
   */
  public void setColorTrue( final Color colorTrue ) {
    this.colorTrue = colorTrue;
  }

  /**
   * Sets the color for false values.
   *
   * @param colorFalse
   *          the color.
   */
  public void setColorFalse( final Color colorFalse ) {
    this.colorFalse = colorFalse;
  }

  /**
   * Returns the color for true values.
   *
   * @return A color.
   */
  public Color getColorTrue() {
    return colorTrue;
  }

  /**
   * Returns the color for false values.
   *
   * @return A color.
   */
  public Color getColorFalse() {
    return colorFalse;
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      final boolean value = isValueTrue();
      final Color color;
      if ( value ) {
        color = getColorTrue();
      } else {
        color = getColorFalse();
      }
      e.getStyle().setStyleProperty( ElementStyleKeys.PAINT, color );
      return true;
    }
    return false;
  }

  /**
   * Computes the boolean value. This method returns true only if the value is a java.lang.Boolean with the value true.
   *
   * @return true if the datarow column contains Boolean.TRUE.
   */
  protected boolean isValueTrue() {
    final Object o = getDataRow().get( getField() );
    return Boolean.TRUE.equals( o );
  }
}
