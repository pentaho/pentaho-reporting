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

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.Query;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModel;

public class QueryTemplateAction<T> extends ScriptTemplateAction {
  private Component parentComponent;
  private QueryDialogModel<T> dialogModel;

  public QueryTemplateAction( final Component parentComponent, final QueryDialogModel<T> dialogModel ) {
    super( false );
    this.parentComponent = parentComponent;
    this.dialogModel = dialogModel;
  }

  protected Component getParentComponent() {
    return parentComponent;
  }

  protected void setText( final String text ) {
    Query<T> selectedQuery = dialogModel.getSelectedQuery();
    if ( selectedQuery == null ) {
      throw new IllegalStateException();
    }

    dialogModel.updateSelectedQuery( selectedQuery.updateQueryScript( selectedQuery.getQueryLanguage(), text ) );
  }

  protected String getText() {
    Query<T> selectedQuery = dialogModel.getSelectedQuery();
    if ( selectedQuery == null ) {
      throw new IllegalStateException();
    }
    return selectedQuery.getQueryScript();
  }
}
