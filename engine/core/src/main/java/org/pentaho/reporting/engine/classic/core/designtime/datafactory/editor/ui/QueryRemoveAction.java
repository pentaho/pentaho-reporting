/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
