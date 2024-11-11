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

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * This function hides all elements with a given name, as long as the defined <code>field</code> does <b>not</b> contain
 * the element name.
 *
 * @author Thomas Morgner
 * @deprecated Use a formula
 */
public class HideElementByNameFunction extends AbstractElementFormatFunction {
  /**
   * The name of the data-row column that is checked for null-values.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public HideElementByNameFunction() {
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
   * Defines the field name for the function. The field name corresponds to a column name in the report's data-row.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  protected boolean evaluateElement( final ReportElement e ) {
    if ( ObjectUtilities.equal( e.getName(), getElement() ) ) {
      final boolean visible = ObjectUtilities.equal( getElement(), getDataRow().get( getField() ) );
      e.getStyle().setStyleProperty( ElementStyleKeys.VISIBLE, visible );
      return true;
    }
    return false;
  }
}
