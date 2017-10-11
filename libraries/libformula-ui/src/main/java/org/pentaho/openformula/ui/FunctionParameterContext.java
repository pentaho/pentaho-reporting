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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.openformula.ui;

import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.reporting.libraries.formula.function.FunctionDescription;

import java.util.Arrays;

public class FunctionParameterContext {
  private FunctionDescription function;
  private String[] parameterValues;
  private FunctionInformation functionInformation;
  private boolean switchParameterEditor;
  private FormulaEditorModel editorModel;

  public FunctionParameterContext() {
    switchParameterEditor = true;
  }

  @Deprecated
  public FunctionParameterContext( final FunctionDescription function,
                                   final String[] parameterValues,
                                   final FunctionInformation fn,
                                   final boolean switchParameterEditor,
                                   final FormulaEditorModel editorModel ) {
    // todo: remove me in next major release
    this( function, fn, switchParameterEditor, editorModel );
    this.parameterValues = parameterValues;
  }

  public FunctionParameterContext( final FunctionDescription function,
                                   final FunctionInformation fn,
                                   final boolean switchParameterEditor,
                                   final FormulaEditorModel editorModel ) {
    this.function = function;
    this.functionInformation = fn;
    this.switchParameterEditor = switchParameterEditor;
    this.editorModel = editorModel;
    this.parameterValues = fn.getParameters();
  }

  public FormulaEditorModel getEditorModel() {
    return editorModel;
  }

  public FunctionInformation getFunctionInformation() {
    return functionInformation;
  }

  public FunctionDescription getFunction() {
    return function;
  }

  public String[] getParameterValues() {
    return parameterValues;
  }

  public boolean isSwitchParameterEditor() {
    return switchParameterEditor;
  }

  public void setSwitchParameterEditor( final boolean switchParameterEditor ) {
    this.switchParameterEditor = switchParameterEditor;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "FunctionParameterContext" );
    sb.append( "{function=" ).append( function );
    sb.append( ", parameterValues=" )
      .append( parameterValues == null ? "null" : Arrays.asList( parameterValues ).toString() );
    sb.append( ", functionInformation=" ).append( functionInformation );
    sb.append( '}' );
    return sb.toString();
  }
}
