/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.openformula.ui;

import java.util.Arrays;

import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;

public class FunctionParameterContext
{
  private FunctionDescription function;
  private String[] parameterValues;
  private FunctionInformation functionInformation;
  private boolean switchParameterEditor;
  private FormulaEditorModel editorModel;

  public FunctionParameterContext()
  {
    switchParameterEditor = true;
  }

  public FunctionParameterContext(final FunctionDescription function,
                                  final String[] parameterValues,
                                  final FunctionInformation fn,
                                  final boolean switchParameterEditor,
                                  final FormulaEditorModel editorModel)
  {
    this.function = function;
    this.parameterValues = parameterValues;
    this.functionInformation = fn;
    this.switchParameterEditor = switchParameterEditor;
    this.editorModel = editorModel;
  }

  public FormulaEditorModel getEditorModel()
  {
    return editorModel;
  }

  public FunctionInformation getFunctionInformation()
  {
    return functionInformation;
  }

  public FunctionDescription getFunction()
  {
    return function;
  }

  public int getFunctionParameterStartPosition()
  {
    if (functionInformation == null)
    {
      return -1;
    }
    return functionInformation.getFunctionParameterStart();
  }

  public String[] getParameterValues()
  {
    return parameterValues;
  }

  public boolean isSwitchParameterEditor()
  {
    return switchParameterEditor;
  }

  public void setSwitchParameterEditor(final boolean switchParameterEditor)
  {
    this.switchParameterEditor = switchParameterEditor;
  }

  public static boolean isSameFunctionDescription(final FunctionDescription d1,
                                                   final FunctionDescription d2)
  {
    if (d1 == null || d2 == null)
    {
      return false;
    }
    if (d1.getClass().equals(d2.getClass()))
    {
      return true;
    }
    return false;
  }

  public String toString()
  {
    final StringBuffer sb = new StringBuffer();
    sb.append("FunctionParameterContext");
    sb.append("{function=").append(function);
    sb.append(", parameterValues=").append(parameterValues == null ? "null" : Arrays.asList(parameterValues).toString());
    sb.append(", functionInformation=").append(functionInformation);
    sb.append('}');
    return sb.toString();
  }
}
