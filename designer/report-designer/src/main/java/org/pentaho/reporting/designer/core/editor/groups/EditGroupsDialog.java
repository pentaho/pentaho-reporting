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

package org.pentaho.reporting.designer.core.editor.groups;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.EditGroupUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EditGroupsDialog extends CommonDialog {
  private class AddGroupAction extends AbstractAction {
    private GroupDataTableModel tableModel;

    private AddGroupAction( final GroupDataTableModel tableModel ) {
      this.tableModel = tableModel;
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "EditGroupsDialog.AddGroup" ) );
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getAddIcon() );
    }

    public void actionPerformed( final ActionEvent e ) {
      final TableCellEditor cellEditor = table.getCellEditor();
      if ( cellEditor != null ) {
        cellEditor.stopCellEditing();
      }

      final EditGroupDetailsDialog dialog = new EditGroupDetailsDialog( EditGroupsDialog.this );
      final RelationalGroup group = new RelationalGroup();
      final EditGroupUndoEntry groupUndoEntry = dialog.editGroup( group, getReportRenderContext(), true );
      if ( groupUndoEntry != null ) {
        tableModel.add( new GroupDataEntry( null, groupUndoEntry.getNewName(), groupUndoEntry.getNewFields() ) );
      }
    }
  }

  private class RemoveGroupAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private GroupDataTableModel tableModel;

    private RemoveGroupAction( final GroupDataTableModel tableModel,
                               final ListSelectionModel selectionModel ) {
      this.tableModel = tableModel;
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "EditGroupsDialog.RemoveGroup" ) );
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getRemoveIcon() );

      this.selectionModel = selectionModel;
      this.selectionModel.addListSelectionListener( this );
    }

    public void actionPerformed( final ActionEvent e ) {
      final TableCellEditor cellEditor = table.getCellEditor();
      if ( cellEditor != null ) {
        cellEditor.stopCellEditing();
      }
      final int maxIdx = selectionModel.getMaxSelectionIndex();
      final ArrayList<GroupDataEntry> list = new ArrayList<GroupDataEntry>();
      for ( int i = selectionModel.getMinSelectionIndex(); i <= maxIdx; i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          list.add( tableModel.get( i ) );
        }
      }

      for ( int i = 0; i < list.size(); i++ ) {
        final GroupDataEntry dataEntry = list.get( i );
        tableModel.remove( dataEntry );
      }
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }
  }

  private class EditGroupAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private GroupDataTableModel tableModel;

    private EditGroupAction( final GroupDataTableModel tableModel,
                             final ListSelectionModel selectionModel ) {
      this.tableModel = tableModel;
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "EditGroupsDialog.EditGroup" ) );
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getEditIcon() );

      this.selectionModel = selectionModel;
      this.selectionModel.addListSelectionListener( this );
      setEnabled( this.selectionModel.isSelectionEmpty() == false );
    }

    public void actionPerformed( final ActionEvent e ) {
      final TableCellEditor cellEditor = table.getCellEditor();
      if ( cellEditor != null ) {
        cellEditor.stopCellEditing();
      }

      final int index = selectionModel.getLeadSelectionIndex();
      if ( index == -1 ) {
        return;
      }

      final GroupDataEntry groupDataEntry = tableModel.get( index );
      final EditGroupDetailsDialog dialog = new EditGroupDetailsDialog( EditGroupsDialog.this );
      if ( dialog.editGroupData( groupDataEntry.getName(), groupDataEntry.getFields(), getReportRenderContext() ) ) {
        tableModel.update( index, new GroupDataEntry( null, dialog.getGroupName(), dialog.getFields() ) );
      }

    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }
  }

  private JTable table;
  private GroupDataTableModel tableModel;
  private ReportDocumentContext reportRenderContext;
  private GroupDataEntryCellEditor groupDataEntryCellEditor;

  public EditGroupsDialog()
    throws HeadlessException {
    init();
  }

  public EditGroupsDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public EditGroupsDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "EditGroupsDialog.EditGroup" ) );
    setModal( true );

    groupDataEntryCellEditor = new GroupDataEntryCellEditor();
    tableModel = new GroupDataTableModel();

    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.EditGroups";
  }

  protected Component createContentPane() {
    table = new JTable( tableModel );
    table.setDefaultEditor( GroupDataEntry.class, groupDataEntryCellEditor );
    table.setDefaultRenderer( GroupDataEntry.class, new GroupDataEntryCellRenderer() );
    final ListSelectionModel selectionModel = table.getSelectionModel();

    final Action addGroupAction = new AddGroupAction( tableModel );
    final Action removeGroupAction = new RemoveGroupAction( tableModel, selectionModel );
    final Action editGroupAction = new EditGroupAction( tableModel, selectionModel );

    final Action sortUpAction = new SortBulkUpAction( tableModel, selectionModel, table );
    final Action sortDownAction = new SortBulkDownAction( tableModel, selectionModel, table );

    final JPanel buttonsPanel = new JPanel();
    buttonsPanel.setLayout( new FlowLayout( FlowLayout.RIGHT ) );
    buttonsPanel.add( new BorderlessButton( sortUpAction ) );
    buttonsPanel.add( new BorderlessButton( sortDownAction ) );
    buttonsPanel.add( Box.createHorizontalStrut( 20 ) );
    buttonsPanel.add( new BorderlessButton( editGroupAction ) );
    buttonsPanel.add( new BorderlessButton( addGroupAction ) );
    buttonsPanel.add( new BorderlessButton( removeGroupAction ) );

    final JPanel panel = new JPanel();
    panel.setLayout( new BorderLayout() );
    panel.add( new JScrollPane( table ), BorderLayout.CENTER );
    panel.add( buttonsPanel, BorderLayout.NORTH );

    return panel;
  }

  protected ReportDocumentContext getReportRenderContext() {
    return reportRenderContext;
  }

  protected GroupDataEntry[] editGroups( final ReportDocumentContext reportRenderContext,
                                         final GroupDataEntry[] groupData ) {
    if ( reportRenderContext == null ) {
      throw new NullPointerException();
    }

    this.reportRenderContext = reportRenderContext;
    this.groupDataEntryCellEditor.setReportContext( reportRenderContext );
    try {
      tableModel.clear();
      for ( int i = 0; i < groupData.length; i++ ) {
        final GroupDataEntry dataEntry = groupData[ i ];
        tableModel.add( dataEntry );
      }

      if ( performEdit() == false ) {
        return null;
      } else {
        final TableCellEditor cellEditor = table.getCellEditor();
        if ( cellEditor != null ) {
          cellEditor.stopCellEditing();
        }

        final GroupDataEntry[] retval = new GroupDataEntry[ tableModel.getSize() ];
        for ( int i = 0; i < retval.length; i++ ) {
          retval[ i ] = tableModel.get( i );
        }
        return retval;
      }
    } finally {
      this.reportRenderContext = null;
      this.groupDataEntryCellEditor.setReportContext( null );
    }
  }

  public boolean editGroups( final ReportDocumentContext context ) {
    if ( context == null ) {
      throw new NullPointerException();
    }

    final AbstractReportDefinition abstractReportDefinition = context.getReportDefinition();
    final GroupDataEntry[] oldEntries = EditGroupsUndoEntry.buildGroupData( abstractReportDefinition );
    final GroupDataEntry[] newEntries = editGroups( context, oldEntries );
    if ( newEntries == null ) {
      return false;
    }

    EditGroupsUndoEntry.applyGroupData( abstractReportDefinition, newEntries );

    // do something with the groups
    return true;
  }

}
