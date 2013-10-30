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

  //there is no need to pass parameters valuse in constructor,
  //as the parameterValues can be derived from
  //FunctionInformation - and if it is used as
  //marker for fields count. Usually  parameterValues is
  //functions set parameters + some empty positions to
  //illustrate UI empty fields.
  @Deprecated
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

  public FunctionParameterContext(final FunctionDescription function,
                                  final FunctionInformation fn,
                                  final boolean switchParameterEditor,
                                  final FormulaEditorModel editorModel)
  {
    this.function = function;
    this.parameterValues = fn.getParametes();
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
