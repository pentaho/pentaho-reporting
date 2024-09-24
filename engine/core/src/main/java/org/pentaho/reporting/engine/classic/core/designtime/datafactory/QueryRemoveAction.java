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

package org.pentaho.reporting.engine.classic.core.designtime.datafactory;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;

public class QueryRemoveAction extends AbstractAction implements PropertyChangeListener {
  private NamedQueryModel queries;

  public QueryRemoveAction( final NamedQueryModel queries ) {
    this.queries = queries;
    this.queries.addPropertyChangeListener( NamedQueryModel.QUERY_SELECTED, this );

    final URL location =
        QueryRemoveAction.class
            .getResource( "/org/pentaho/reporting/engine/classic/core/designtime/datafactory/resources/Remove.png" ); // NON-NLS
    if ( location != null ) {
      putValue( Action.SMALL_ICON, new ImageIcon( location ) );
    } else {
      putValue( Action.NAME, Messages.getInstance().getString( "RemoveQuery.Name" ) );
    }
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "RemoveQuery.Description" ) );
    setEnabled( false );
  }

  public void actionPerformed( final ActionEvent e ) {
    final DefaultComboBoxModel comboBoxModel = queries.getQueries();
    comboBoxModel.removeElement( comboBoxModel.getSelectedItem() );
    comboBoxModel.setSelectedItem( null );
  }

  public void propertyChange( final PropertyChangeEvent evt ) {
    setEnabled( queries.isQuerySelected() );
  }
}
