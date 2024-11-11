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

public class FormulaTextElement extends FormulaElement {
  private String text;

  public FormulaTextElement( final FormulaDocument document,
                             final FormulaRootElement parentElement,
                             final String text ) {
    super( document, parentElement );
    this.text = text;
  }

  public String getText() {
    return text;
  }

  /**
   * Fetches the name of the element.  If the element is used to represent some type of structure, this would be the
   * type name.
   *
   * @return the element name
   */
  public String getName() {
    return "content"; // NON-NLS
  }
}
