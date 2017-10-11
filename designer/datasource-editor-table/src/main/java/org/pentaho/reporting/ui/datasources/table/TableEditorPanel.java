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

package org.pentaho.reporting.ui.datasources.table;

import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class TableEditorPanel extends JPanel {

  private class AddRowAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddRowAction() {
      setEnabled( false );
      final URL location = TableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/table/resources/AddRow.png" ); // NON-NLS
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getString( "TableDataSourceEditor.AddRow.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "TableDataSourceEditor.AddRow.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      table.addRow();
      updateComponents();
    }
  }

  private class AddColumnAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddColumnAction() {
      setEnabled( false );
      final URL location = TableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/table/resources/AddColumn.png" ); // NON-NLS
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getString( "TableDataSourceEditor.AddColumn.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "TableDataSourceEditor.AddColumn.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      table.addColumn( " " );
      updateComponents();
    }
  }

  private class RemoveColumnAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RemoveColumnAction() {
      setEnabled( false );
      final URL location = TableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/table/resources/RemoveColumn.png" ); // NON-NLS
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getString( "TableDataSourceEditor.RemoveColumn.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "TableDataSourceEditor.RemoveColumn.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      table.removeColumn();
      setEnabled( false );
    }
  }

  private class RemoveRowAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private RemoveRowAction() {
      setEnabled( false );
      final URL location = TableDataSourceEditor.class.getResource
        ( "/org/pentaho/reporting/ui/datasources/table/resources/RemoveRow.png" ); // NON-NLS
      if ( location != null ) {
        putValue( Action.SMALL_ICON, new ImageIcon( location ) );
      } else {
        putValue( Action.NAME, Messages.getString( "TableDataSourceEditor.RemoveRow.Name" ) );
      }
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "TableDataSourceEditor.RemoveRow.Description" ) );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      table.removeRow();
      setEnabled( false );
    }
  }


  private class TableSelectionHandler implements ListSelectionListener {
    private TableSelectionHandler() {
    }

    public void valueChanged( final ListSelectionEvent e ) {
      if ( selectingHeaderColumn ) {
        return;
      }

      final EditableHeader editableHeader = (EditableHeader) table.getTableHeader();

      removeColumnAction.setEnabled( false );
      removeRowAction.setEnabled( table.getSelectedRowCount() > 0 && table.getRowCount() > 1 );

      editableHeader.editingStopped( null );
      editableHeader.removeEditor();
    }
  }

  private class TableHeaderDataSourceMouseListener extends MouseAdapter {
    private TableHeaderDataSourceMouseListener() {
    }

    public void mousePressed( final MouseEvent event ) {
      final Object eventSource = event.getSource();
      if ( eventSource instanceof JTableHeader == false ) {
        return;
      }
      final JTableHeader tableHeader = (JTableHeader) eventSource;

      removeColumnAction.setEnabled( table.getColumnCount() > 1 );
      removeRowAction.setEnabled( false );

      final TableCellEditor theTableCellEditor = table.getCellEditor();
      if ( theTableCellEditor != null ) {
        table.getCellEditor().stopCellEditing();
      }

      selectColumn( tableHeader, event.getPoint() );
    }

    private void selectColumn( final JTableHeader aTableHeader, final Point aPoint ) {
      try {
        selectingHeaderColumn = true;

        final TableColumnModel columnModel = table.getColumnModel();
        final int columnIndex = aTableHeader.columnAtPoint( aPoint );
        if ( columnIndex <= 0 ) {
          return;
        }

        final TableColumn tableColumn = columnModel.getColumn( columnIndex );

        table.clearSelection();
        table.setColumnSelectionInterval( columnIndex, columnIndex );
        table.setSelectedColumn( tableColumn );
        if ( table.getRowCount() > 0 ) {
          table.addRowSelectionInterval( 0, table.getRowCount() - 1 );
        }
      } finally {
        selectingHeaderColumn = false;
      }
    }
  }

  private class TableAddEmptyRowAtEndHandler extends KeyAdapter {
    private TableAddEmptyRowAtEndHandler() {
    }

    public void keyTyped( final KeyEvent aEvt ) {
      final int key = aEvt.getKeyCode();
      if ( key == KeyEvent.VK_TAB ) {
        if ( table.getSelectedColumn() == ( table.getColumnCount() - 1 ) ) {
          if ( table.getSelectedRow() == ( table.getRowCount() - 1 ) ) {
            table.addRow();
          }
        }
      }
    }
  }

  private class TableUpdateHandler implements TableModelListener {
    private TableUpdateHandler() {
    }

    /**
     * This fine grain notification tells listeners the exact range of cells, rows, or columns that changed.
     */
    public void tableChanged( final TableModelEvent e ) {
      fireChangeEvent();
    }
  }

  private void fireChangeEvent() {
    final ChangeEvent changeEvent = new ChangeEvent( this );
    final ChangeListener[] listeners = listenerList.getListeners( ChangeListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final ChangeListener listener = listeners[ i ];
      listener.stateChanged( changeEvent );
    }
  }

  private TableEditor table;
  private AddRowAction addRowAction;
  private AddColumnAction addColumnAction;
  private RemoveRowAction removeRowAction;
  private RemoveColumnAction removeColumnAction;
  private boolean selectingHeaderColumn;

  public TableEditorPanel() {
    table = new TableEditor();
    table.setAutoResizeMode( JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS );
    table.addKeyListener( new TableAddEmptyRowAtEndHandler() );
    table.getSelectionModel().addListSelectionListener( new TableSelectionHandler() );
    table.getTableHeader().addMouseListener( new TableHeaderDataSourceMouseListener() );
    table.getModel().addTableModelListener( new TableUpdateHandler() );
    table.setColumnSelectionAllowed( true );

    addRowAction = new AddRowAction();
    addColumnAction = new AddColumnAction();
    removeRowAction = new RemoveRowAction();
    removeColumnAction = new RemoveColumnAction();

    final JPanel toolbar = new JPanel( new FlowLayout( FlowLayout.RIGHT ) );
    toolbar.add( new BorderlessButton( addRowAction ) );
    toolbar.add( new BorderlessButton( addColumnAction ) );
    toolbar.add( new BorderlessButton( removeRowAction ) );
    toolbar.add( new BorderlessButton( removeColumnAction ) );

    setLayout( new BorderLayout() );
    add( toolbar, BorderLayout.NORTH );
    setBorder( BorderFactory.createEmptyBorder( 0, 5, 5, 5 ) );
    add( new JScrollPane( table ), BorderLayout.CENTER );
  }

  public TableModel getTableEditorModel() {
    return table.getTableEditorModel();
  }

  public void applyLocaleSettings( final LocaleSettings localeSettings ) {
    table.applyLocaleSettings( localeSettings );
  }

  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    addRowAction.setEnabled( enabled );
    addColumnAction.setEnabled( enabled );

    removeRowAction.setEnabled( enabled && table.getSelectedRow() != -1 );
    removeColumnAction.setEnabled( enabled && table.getSelectedColumn() != -1 );
  }

  private void updateComponents() {
    final boolean enabled = isEnabled();
    removeRowAction.setEnabled( enabled && table.getSelectedRow() != -1 );
    removeColumnAction.setEnabled( enabled && table.getSelectedColumn() != -1 );
  }

  public void stopEditing() {
    table.stopEditing();
  }

  public void addChangeListener( ChangeListener listener ) {
    listenerList.add( ChangeListener.class, listener );
  }

  public void removeChangeListener( ChangeListener listener ) {
    listenerList.remove( ChangeListener.class, listener );
  }

  public void setTableEditorModel( final TableModel tableModel ) {
    stopEditing();
    this.table.setTableEditorModel( tableModel );
  }
}
