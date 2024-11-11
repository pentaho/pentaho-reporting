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


package org.pentaho.openformula.ui.model2;

public class FormulaClosingParenthesisElement extends FormulaElement {
  public static final String ELEMENT = ")";

  public FormulaClosingParenthesisElement( final FormulaDocument document,
                                           final FormulaRootElement parentElement ) {
    super( document, parentElement );
  }

  public String getText() {
    return ELEMENT;
  }

  /**
   * Fetches the name of the element.  If the element is used to represent some type of structure, this would be the
   * type name.
   *
   * @return the element name
   */
  public String getName() {
    return "close-paren"; // NON-NLS
  }
}
