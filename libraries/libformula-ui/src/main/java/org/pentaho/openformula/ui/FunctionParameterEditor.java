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

import java.awt.*;

public interface FunctionParameterEditor {
  public void addParameterUpdateListener( ParameterUpdateListener parameterUpdateListener );

  public void removeParameterUpdateListener( ParameterUpdateListener parameterUpdateListener );

  public Component getEditorComponent();

  public void setFields( FieldDefinition[] fieldDefinitions );

  public void clearSelectedFunction();

  public void setSelectedFunction( FunctionParameterContext context );
}
