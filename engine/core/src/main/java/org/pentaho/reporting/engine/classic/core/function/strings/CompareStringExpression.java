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


package org.pentaho.reporting.engine.classic.core.function.strings;

import org.pentaho.reporting.engine.classic.core.function.AbstractCompareExpression;

/**
 * Compares a given static string with a string read from a field.
 *
 * @author Thomas Morgner
 * @deprecated This can be done a lot easier using a simple formula.
 */
@SuppressWarnings( "deprecation" )
public class CompareStringExpression extends AbstractCompareExpression {
  /**
   * The static text.
   */
  private String text;

  /**
   * Default Constructor.
   */
  public CompareStringExpression() {
  }

  /**
   * Returns the static text to which the field's value should be compared to.
   *
   * @return the text.
   */
  public String getText() {
    return text;
  }

  /**
   * Defines the static text to which the field's value should be compared to.
   *
   * @param text
   *          the text.
   */
  public void setText( final String text ) {
    this.text = text;
  }

  /**
   * Returns the text.
   *
   * @return the text.
   */
  protected Comparable getComparable() {
    return text;
  }
}
