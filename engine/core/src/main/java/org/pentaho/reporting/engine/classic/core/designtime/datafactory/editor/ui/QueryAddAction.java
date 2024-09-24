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
