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


package org.pentaho.reporting.engine.classic.core.function.strings;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.AbstractExpression;

/**
 * A expression that transforms all letters of a given string into lower-case letters.
 *
 * @author Thomas Morgner
 * @deprecated This can be solved with a formula
 */
public class ToLowerCaseStringExpression extends AbstractExpression {
  /**
   * The field name from where to read the string that should be converted to lower case.
   */
  private String field;

  /**
   * Default Constructor.
   */
  public ToLowerCaseStringExpression() {
  }

  /**
   * Returns the name of the datarow-column from where to read the string value.
   *
   * @return the field.
   */
  public String getField() {
    return field;
  }

  /**
   * Defines the name of the datarow-column from where to read the string value.
   *
   * @param field
   *          the field.
   */
  public void setField( final String field ) {
    this.field = field;
  }

  /**
   * Transforms the string that has been read from the defined field.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    final Object raw = getDataRow().get( getField() );
    if ( raw == null ) {
      return null;
    }
    final String text = String.valueOf( raw );
    final ResourceBundleFactory rf = getResourceBundleFactory();
    if ( rf == null ) {
      return text.toLowerCase();
    } else {
      return text.toLowerCase( rf.getLocale() );
    }
  }
}
