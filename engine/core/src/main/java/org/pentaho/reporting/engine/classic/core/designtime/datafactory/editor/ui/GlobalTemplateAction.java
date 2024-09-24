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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.ui;

import java.awt.Component;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModel;

public class GlobalTemplateAction<T> extends ScriptTemplateAction {
  private final Component parentComponent;
  private final QueryDialogModel<T> dialogModel;

  public GlobalTemplateAction( final Component parentComponent, final QueryDialogModel<T> dialogModel ) {
    super( true );
    this.parentComponent = parentComponent;
    this.dialogModel = dialogModel;
  }

  protected Component getParentComponent() {
    return parentComponent;
  }

  protected String getText() {
    return dialogModel.getGlobalScript();
  }

  protected void setText( final String text ) {
    dialogModel.setGlobalScripting( dialogModel.getGlobalScriptLanguage(), text );
  }
}
