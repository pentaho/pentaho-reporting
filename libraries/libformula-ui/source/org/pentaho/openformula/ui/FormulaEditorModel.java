/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.openformula.ui;

import org.pentaho.openformula.ui.model2.FormulaDocument;
import org.pentaho.openformula.ui.model2.FunctionInformation;

public class FormulaEditorModel
{
  private FormulaDocument document;
  private int carretPosition;

  public FormulaEditorModel()
  {
    document = new FormulaDocument();
    carretPosition = 0;
  }

  public int getLength()
  {
    return document.getLength();
  }

  public FunctionInformation getCurrentFunction()
  {
    return document.getFunctionForPosition(carretPosition);
  }

  public String getFormulaText()
  {
    return document.getText();
  }

  public void setFormulaText(final String text)
  {
    document.setText(text);
  }

  public int getCarretPosition()
  {
    return carretPosition;
  }

  public void setCarretPosition(final int carretPosition)
  {
    this.carretPosition = carretPosition;
  }

  public void updateParameterText(final int start, final int end, final String newText, final boolean hasDummyParams)
  {
    document.updateParameterText(start, end, newText, hasDummyParams);
  }

  public void revalidateStructure()
  {
    document.revalidateStructure();
  }
}
