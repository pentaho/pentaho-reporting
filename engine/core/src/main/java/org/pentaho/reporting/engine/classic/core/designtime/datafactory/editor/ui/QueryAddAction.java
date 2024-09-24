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
import java.text.MessageFormat;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;

import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.Query;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.editor.model.QueryDialogModel;

public class QueryAddAction<T> extends AbstractAction {
  private QueryDialogModel<T> model;

  public QueryAddAction( final QueryDialogModel<T> model ) {
    this.model = model;
    final URL location =
        QueryAddAction.class
            .getResource( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/resources/Add.png" ); // NON-NLS
    if ( location != null ) {
      putValue( Action.SMALL_ICON, new ImageIcon( location ) );
    } else {
      putValue( Action.NAME, Messages.getString( "QueryAddAction.Name" ) );
    }
    putValue( Action.SHORT_DESCRIPTION, Messages.getString( "QueryAddAction.Description" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final String queryName = generateQueryName();

    final Query<T> query = new Query<T>( queryName, null );
    model.addQuery( query );
  }

  private boolean containsQueryWithName( final String name ) {
    for ( Query<T> query : model ) {
      if ( name.equals( query.getName() ) ) {
        return true;
      }
    }
    return false;
  }

  private String generateQueryName() {
    final String queryNamePattern = Messages.getString( "QueryAddAction.QueryPattern" );
    for ( int i = 1; i < 1000; ++i ) {
      final String newQuery = MessageFormat.format( queryNamePattern, i );
      if ( containsQueryWithName( newQuery ) == false ) {
        return newQuery;
      }
    }
    return queryNamePattern;
  }

}
