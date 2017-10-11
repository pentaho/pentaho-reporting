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

package org.pentaho.reporting.designer.core.settings.ui;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.settings.SettingsMessages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.table.ArrayTableModel;
import org.pentaho.reporting.designer.core.util.table.ElementMetaDataTable;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class FormatEditorDialog extends CommonDialog {
  private static class AddEntryAction extends AbstractAction {
    private ArrayTableModel tableModel;

    private AddEntryAction( final ArrayTableModel tableModel ) {
      this.tableModel = tableModel;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
      putValue( Action.SHORT_DESCRIPTION,
        SettingsMessages.getInstance().getString( "FormatEditorDialog.AddEntry.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      tableModel.add( null );
    }
  }

  private class RemoveEntryAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private ArrayTableModel tableModel;
    private JTable table;

    private RemoveEntryAction( final ArrayTableModel tableModel,
                               final ListSelectionModel selectionModel,
                               final JTable table ) {
      this.tableModel = tableModel;
      this.table = table;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );
      putValue( Action.SHORT_DESCRIPTION,
        SettingsMessages.getInstance().getString( "FormatEditorDialog.RemoveEntry.Description" ) );


      this.selectionModel = selectionModel;
      this.selectionModel.addListSelectionListener( this );
    }

    public void actionPerformed( final ActionEvent e ) {
      stopCellEditing();

      final int maxIdx = selectionModel.getMaxSelectionIndex();
      final ArrayList<Integer> list = new ArrayList<Integer>();
      for ( int i = selectionModel.getMinSelectionIndex(); i <= maxIdx; i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          list.add( 0, i );
        }
      }

      for ( int i = 0; i < list.size(); i++ ) {
        final Integer dataEntry = list.get( i );
        tableModel.remove( dataEntry );
      }
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    protected void stopCellEditing() {
      if ( table.getCellEditor() != null ) {
        table.getCellEditor().stopCellEditing();
      }
    }

  }

  public static class Result {
    private String[] dateFormats;
    private String[] numberFormats;

    public Result( final String[] dateFormats, final String[] numberFormats ) {
      this.dateFormats = dateFormats;
      this.numberFormats = numberFormats;
    }

    public String[] getDateFormats() {
      return dateFormats;
    }

    public String[] getNumberFormats() {
      return numberFormats;
    }
  }

  private ArrayTableModel dateFormatModel;
  private ArrayTableModel numberFormatModel;
  private ElementMetaDataTable dateFormatTable;
  private ElementMetaDataTable numberFormatTable;

  /**
   * Creates a new modal dialog.
   */
  public FormatEditorDialog() {
    init();
  }

  public FormatEditorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public FormatEditorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( SettingsMessages.getInstance().getString( "FormatEditorDialog.Title" ) );
    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.FormatEditor";
  }

  protected Component createContentPane() {
    dateFormatModel = new ArrayTableModel();
    dateFormatModel.setColumnTitle( SettingsMessages.getInstance().getString( "FormatEditorDialog.DateFormat" ) );
    numberFormatModel = new ArrayTableModel();
    numberFormatModel.setColumnTitle( SettingsMessages.getInstance().getString( "FormatEditorDialog.NumberFormat" ) );

    dateFormatTable = new ElementMetaDataTable();
    dateFormatTable.setModel( dateFormatModel );
    numberFormatTable = new ElementMetaDataTable();
    numberFormatTable.setModel( numberFormatModel );

    final JPanel panel = new JPanel( new GridLayout( 1, 2 ) );
    panel.add( createTablePanel( numberFormatTable, numberFormatModel ) );
    panel.add( createTablePanel( dateFormatTable, dateFormatModel ) );
    return panel;
  }

  private JPanel createTablePanel( final ElementMetaDataTable dateFormatTable,
                                   final ArrayTableModel dateFormatModel ) {
    final ListSelectionModel selectionModel = dateFormatTable.getSelectionModel();
    final Action addGroupAction = new AddEntryAction( dateFormatModel );
    final Action removeGroupAction = new RemoveEntryAction( dateFormatModel, selectionModel, dateFormatTable );

    final Action sortUpAction = new SortBulkUpAction( dateFormatModel, selectionModel, dateFormatTable );
    final Action sortDownAction = new SortBulkDownAction( dateFormatModel, selectionModel, dateFormatTable );

    final JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonsPanel.add( new BorderlessButton( sortUpAction ) );
    buttonsPanel.add( new BorderlessButton( sortDownAction ) );
    buttonsPanel.add( Box.createHorizontalStrut( 20 ) );
    buttonsPanel.add( new BorderlessButton( addGroupAction ) );
    buttonsPanel.add( new BorderlessButton( removeGroupAction ) );

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.setBorder( new EmptyBorder( 2, 2, 2, 2 ) );
    panel.add( new JScrollPane( dateFormatTable ), BorderLayout.CENTER );
    panel.add( buttonsPanel, BorderLayout.NORTH );
    return panel;
  }

  public Result editArray( final String[] dateFormats,
                           final String[] numberFormats ) {
    numberFormatModel.setType( String.class );
    numberFormatModel.setValueRole( AttributeMetaData.VALUEROLE_NUMBERFORMAT );
    numberFormatModel.setData( numberFormats, String.class );

    dateFormatModel.setType( String.class );
    dateFormatModel.setValueRole( AttributeMetaData.VALUEROLE_NUMBERFORMAT );
    dateFormatModel.setData( dateFormats, String.class );

    if ( performEdit() == false ) {
      return null;
    }

    if ( dateFormatTable.getCellEditor() != null ) {
      dateFormatTable.getCellEditor().stopCellEditing();
    }
    if ( numberFormatTable.getCellEditor() != null ) {
      numberFormatTable.getCellEditor().stopCellEditing();
    }

    final String[] dateFormatString = new String[ dateFormatModel.getSize() ];
    for ( int i = 0; i < dateFormatModel.getSize(); i++ ) {
      dateFormatString[ i ] = String.valueOf( dateFormatModel.get( i ) );
    }

    final String[] numberFormatString = new String[ numberFormatModel.getSize() ];
    for ( int i = 0; i < numberFormatModel.getSize(); i++ ) {
      numberFormatString[ i ] = String.valueOf( numberFormatModel.get( i ) );
    }

    // process the array ..
    return new Result( dateFormatString, numberFormatString );
  }

  public ReportDesignerContext getReportDesignerContext() {
    return dateFormatTable.getReportDesignerContext();
  }

  public void setReportDesignerContext( final ReportDesignerContext reportDesignerContext ) {
    if ( reportDesignerContext != null ) {
      this.dateFormatTable.setReportDesignerContext( reportDesignerContext );
      this.numberFormatTable.setReportDesignerContext( reportDesignerContext );
    } else {
      this.dateFormatTable.setReportDesignerContext( null );
      this.numberFormatTable.setReportDesignerContext( null );
    }
  }
}
