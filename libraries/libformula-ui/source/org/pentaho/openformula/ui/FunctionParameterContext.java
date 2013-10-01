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
