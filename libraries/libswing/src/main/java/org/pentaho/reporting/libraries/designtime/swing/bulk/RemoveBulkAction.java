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

package org.pentaho.reporting.libraries.designtime.swing.bulk;

import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class RemoveBulkAction extends AbstractAction {
  private class SelectionUpdateHandler implements ListSelectionListener {
    private SelectionUpdateHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( listSelectionModel.isSelectionEmpty() == false );
    }
  }

  private BulkDataProvider tableModel;
  private ListSelectionModel listSelectionModel;
  private JTable editorTable;

  public RemoveBulkAction( final BulkDataProvider tableModel,
                           final ListSelectionModel listSelectionModel ) {
    this.tableModel = tableModel;
    this.listSelectionModel = listSelectionModel;
    this.listSelectionModel.addListSelectionListener( new SelectionUpdateHandler() );
    putValue( Action.SMALL_ICON, Messages.getInstance().getIcon( "Icons.REMOVE" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "Action.REMOVE" ) );
  }

  public RemoveBulkAction( final BulkDataProvider tableModel, final ListSelectionModel listSelectionModel,
                           final JTable editorTable ) {
    this( tableModel, listSelectionModel );
    this.editorTable = editorTable;
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    if ( listSelectionModel.isSelectionEmpty() ) {
      return;
    }

    if ( editorTable != null ) {
      final TableCellEditor cellEditor = editorTable.getCellEditor();
      if ( cellEditor != null ) {
        cellEditor.stopCellEditing();
      }
    }


    final Object[] data = tableModel.getBulkData();
    final ArrayList<Object> result = new ArrayList<Object>( data.length );
    for ( int i = 0; i < data.length; i++ ) {
      if ( listSelectionModel.isSelectedIndex( i ) == false ) {
        result.add( data[ i ] );
      }
    }

    tableModel.setBulkData( result.toArray() );
  }
}
