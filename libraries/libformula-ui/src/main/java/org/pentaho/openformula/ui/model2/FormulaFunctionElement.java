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

package org.pentaho.openformula.ui.model2;

public class FormulaFunctionElement extends FormulaTextElement {
  private String normalizedFunctionName;

  public FormulaFunctionElement( final FormulaDocument document,
                                 final FormulaRootElement parentElement,
                                 final String functionName ) {
    super( document, parentElement, functionName );
    this.normalizedFunctionName = functionName.trim();
  }

  public String getNormalizedFunctionName() {
    return normalizedFunctionName;
  }

  /**
   * Fetches the name of the element.  If the element is used to represent some type of structure, this would be the
   * type name.
   *
   * @return the element name
   */
  public String getName() {
    return "functionName"; // NON-NLS
  }
}
