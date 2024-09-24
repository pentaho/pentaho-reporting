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
