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

import java.awt.event.ActionEvent;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.Query;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModel;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModelEvent;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModelListener;

public class QueryRemoveAction<T> extends AbstractAction implements QueryDialogModelListener<T> {
  private QueryDialogModel<T> queries;

  public QueryRemoveAction( final QueryDialogModel<T> queries ) {
    this.queries = queries;
    this.queries.addQueryDialogModelListener( this );

    final URL location =
        QueryRemoveAction.class
            .getResource( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/resources/Remove.png" ); // NON-NLS
    if ( location != null ) {
      putValue( Action.SMALL_ICON, new ImageIcon( location ) );
    } else {
      putValue( Action.NAME, Messages.getString( "QueryRemoveAction.Name" ) );
    }
    putValue( Action.SHORT_DESCRIPTION, Messages.getString( "QueryRemoveAction.Description" ) );
    setEnabled( false );
  }

  public void actionPerformed( final ActionEvent e ) {
    Query<T> selectedQuery = queries.getSelectedQuery();
    if ( selectedQuery != null ) {
      queries.removeQuery( selectedQuery );
    }
  }

  public void globalScriptChanged( final QueryDialogModelEvent<T> event ) {

  }

  public void queryAdded( final QueryDialogModelEvent<T> event ) {

  }

  public void queryRemoved( final QueryDialogModelEvent<T> event ) {

  }

  public void queryUpdated( final QueryDialogModelEvent<T> event ) {

  }

  public void queryDataChanged( final QueryDialogModelEvent<T> event ) {

  }

  public void selectionChanged( final QueryDialogModelEvent<T> event ) {
    setEnabled( queries.isQuerySelected() );
  }
}
