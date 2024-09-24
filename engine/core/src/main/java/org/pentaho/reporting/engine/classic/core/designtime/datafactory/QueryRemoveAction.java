/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
