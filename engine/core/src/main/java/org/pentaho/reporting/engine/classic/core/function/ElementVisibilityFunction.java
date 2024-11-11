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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Triggers the visiblity of an element based on the boolean value read from the defined field.
 *
 * @author Thomas Morgner
 * @deprecated add a style-expression for the visible style-key instead.
 */
public class ElementVisibilityFunction extends AbstractElementFormatFunction {
  /**
   * The field name of the data-row column from where to read the boolean value.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public ElementVisibilityFunction() {
  }

  /**
   * Returns the field name of the data-row column from where to read the boolean value.
   *
   * @return the field name.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines field name of the data-row column from where to read the boolean value.
   *
   * @param field
   *          the name of the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Returns whether the element will be visible or not.
   *
   * @return Boolean.TRUE or Boolean.FALSE.
   */
  public Object getValue() {
    if ( isVisible() ) {
      return Boolean.TRUE;
    } else {
      return Boolean.FALSE;
    }
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, isVisible() );
    }
    return true;
  }

  /**
   * Computes the visiblity of the element.
   *
   * @return true, if the field contains the Boolean.TRUE object, false otherwise.
   */
  protected boolean isVisible() {
    return Boolean.TRUE.equals( getDataRow().get( getField() ) );
  }
}
