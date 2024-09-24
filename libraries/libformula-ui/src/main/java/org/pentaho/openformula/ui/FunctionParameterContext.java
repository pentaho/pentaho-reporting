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
