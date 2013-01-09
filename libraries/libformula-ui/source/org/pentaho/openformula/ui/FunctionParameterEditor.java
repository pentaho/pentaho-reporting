package org.pentaho.openformula.ui;

import java.awt.Component;

public interface FunctionParameterEditor
{
  public void addParameterUpdateListener(ParameterUpdateListener parameterUpdateListener);

  public void removeParameterUpdateListener(ParameterUpdateListener parameterUpdateListener);

  public Component getEditorComponent();

  public void setFields(FieldDefinition[] fieldDefinitions);

  public void clearSelectedFunction();

  public void setSelectedFunction(FunctionParameterContext context);
}
