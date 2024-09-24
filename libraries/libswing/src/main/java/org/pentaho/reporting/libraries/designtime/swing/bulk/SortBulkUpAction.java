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

import org.pentaho.reporting.libraries.base.util.BulkDataUtility;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.event.ActionEvent;

public class SortBulkUpAction extends AbstractAction {
  private BulkDataProvider tableModel;
  private ListSelectionModel listSelectionModel;
  private JTable editorTable;

  /**
   * Defines an <code>Action</code> object with a default description string and default icon.
   */
  public SortBulkUpAction( final BulkDataProvider tableModel, final ListSelectionModel listSelectionModel ) {
    if ( tableModel == null ) {
      throw new NullPointerException();
    }
    if ( listSelectionModel == null ) {
      throw new NullPointerException();
    }

    this.tableModel = tableModel;
    this.listSelectionModel = listSelectionModel;

    putValue( Action.SMALL_ICON, Messages.getInstance().getIcon( "Icons.MOVE_UP" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "Action.MOVE_UP" ) );
  }

  public SortBulkUpAction( final BulkDataProvider tableModel, final ListSelectionModel listSelectionModel,
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
    if ( listSelectionModel.getMinSelectionIndex() == 0 ) {
      // already the first entry ...
      return;
    }

    if ( editorTable != null ) {
      final TableCellEditor cellEditor = editorTable.getCellEditor();
      if ( cellEditor != null ) {
        cellEditor.stopCellEditing();
      }
    }


    final Object[] data = tableModel.getBulkData();
    final Object[] result = (Object[]) data.clone();
    final boolean[] selections = new boolean[ result.length ];
    for ( int i = listSelectionModel.getMinSelectionIndex(); i <= listSelectionModel.getMaxSelectionIndex(); i++ ) {
      selections[ i ] = listSelectionModel.isSelectedIndex( i );
    }

    BulkDataUtility.pushUp( result, selections );
    tableModel.setBulkData( result );

    listSelectionModel.setValueIsAdjusting( true );
    listSelectionModel.removeSelectionInterval( 0, selections.length );
    for ( int i = 0; i < selections.length; i++ ) {
      final boolean selection = selections[ i ];
      if ( selection ) {
        listSelectionModel.addSelectionInterval( i, i );
      }
    }
    listSelectionModel.setValueIsAdjusting( false );
  }
}
