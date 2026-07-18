/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.openformula.ui.model2;

public class FormulaNumberElement extends FormulaTextElement {
  public FormulaNumberElement( final FormulaDocument document,
                               final FormulaRootElement parentElement,
                               final String text ) {
    super( document, parentElement, text );
  }

  /**
   * Fetches the name of the element.  If the element is used to represent some type of structure, this would be the
   * type name.
   *
   * @return the element name
   */
  public String getName() {
    return "number"; // NON-NLS
  }
}
