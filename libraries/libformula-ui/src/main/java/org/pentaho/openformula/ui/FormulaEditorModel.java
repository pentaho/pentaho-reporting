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

package org.pentaho.openformula.ui;

import org.pentaho.openformula.ui.model2.FormulaDocument;
import org.pentaho.openformula.ui.model2.FormulaElement;
import org.pentaho.openformula.ui.model2.FunctionInformation;

public class FormulaEditorModel {
  private FormulaDocument document;
  private int caretPosition;

  public FormulaEditorModel() {
    document = new FormulaDocument();
    caretPosition = 0;
  }

  public int getLength() {
    return document.getLength();
  }

  public FunctionInformation getCurrentFunction() {
    return document.getFunctionForPosition( caretPosition );
  }

  public String getFormulaText() {
    return document.getText();
  }

  public void setFormulaText( final String text ) {
    document.setText( text );
  }

  public int getCaretPosition() {
    return caretPosition;
  }

  public void setCaretPosition( final int carretPosition ) {
    this.caretPosition = carretPosition;
  }

  public FormulaElement getFormulaElementAt( final int index ) {
    return document.getElementAtPosition( index );
  }

  public void revalidateStructure() {
    document.revalidateStructure();
  }
}
