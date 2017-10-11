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
import org.pentaho.reporting.designer.core.editor.ReportDataChangeListener;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.EditGroupUndoEntry;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.bulk.DefaultBulkListModel;
import org.pentaho.reporting.libraries.designtime.swing.bulk.RemoveBulkAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class EditGroupDetailsDialog extends CommonDialog implements ReportDataChangeListener {
  private class AddSelectionAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddSelectionAction( final ListSelectionModel selectionModel ) {
      this.selectionModel = selectionModel;
      putValue( Action.SMALL_ICON, IconLoader.getInstance().getFowardArrowIcon() );
      putValue( Action.SHORT_DESCRIPTION, Messages.getString( "EditGroupDetailsDialog.AddColumn" ) );
      selectionModel.addListSelectionListener( this );
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    public void valueChanged( final ListSelectionEvent e ) {
      setEnabled( selectionModel.isSelectionEmpty() == false );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final DefaultListModel data = getGroupFieldsModel();
      final DefaultListModel fields = getAvailableFieldsModel();
      for ( int i = 0; i < fields.getSize(); i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          data.addElement( fields.getElementAt( i ) );
        }
      }
    }
  }

  private class DoubleClickHandler extends MouseAdapter {
    private ListSelectionModel selectionModel;

    private DoubleClickHandler( final ListSelectionModel selectionModel ) {
      this.selectionModel = selectionModel;
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 ) {
        final DefaultListModel data = getGroupFieldsModel();
        final DefaultListModel fields = getAvailableFieldsModel();
        for ( int i = 0; i < fields.getSize(); i++ ) {
          if ( selectionModel.isSelectedIndex( i ) ) {
            data.addElement( fields.getElementAt( i ) );
          }
        }
      }
    }
  }

  private class ListTransferHandler extends TransferHandler {
    private JList targetList;
    private DefaultBulkListModel listModel;

    private ListTransferHandler( final JList targetList,
                                 final DefaultBulkListModel listModel ) {
      this.targetList = targetList;
      this.listModel = listModel;
    }

    public boolean importData( final TransferSupport support ) {
      if ( support.isDataFlavorSupported( DataFlavor.stringFlavor ) == false ) {
        return false;
      }

      if ( support.isDrop() == false ) {
        return false;
      }
      try {
        final String transferData = (String) support.getTransferable().getTransferData( DataFlavor.stringFlavor );
        if ( transferData == null ) {
          return false;
        }

        final CSVTokenizer tokenizer = new CSVTokenizer( transferData, ",", "\"" );
        final ArrayList<String> items = new ArrayList<String>();
        while ( tokenizer.hasMoreElements() ) {
          items.add( tokenizer.nextToken() );
        }

        final DropLocation dropLocation = support.getDropLocation();
        final Point point = dropLocation.getDropPoint();
        final int idx = targetList.locationToIndex( point );
        if ( idx == -1 ) {
          for ( int i = 0; i < items.size(); i++ ) {
            final String item = items.get( i );
            listModel.addElement( item );
          }
        } else {
          for ( int i = items.size() - 1; i >= 0; i -= 1 ) {
            final String item = items.get( i );
            listModel.add( idx, item );
          }
        }
      } catch ( Exception e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }

      return super.importData( support );
    }

    public boolean canImport( final TransferSupport support ) {
      if ( support.isDrop() == false ) {
        return false;
      }
      return ( support.isDataFlavorSupported( DataFlavor.stringFlavor ) );
    }

    public int getSourceActions( final JComponent c ) {
      return TransferHandler.COPY;
    }

    protected Transferable createTransferable( final JComponent c ) {
      final JList lcomp = (JList) c;
      final StringBuilder b = new StringBuilder();
      final CSVQuoter quoter = new CSVQuoter( ',', '"' );
      final Object[] selectedValues = lcomp.getSelectedValues();
      for ( int i = 0; i < selectedValues.length; i++ ) {
        if ( i != 0 ) {
          b.append( ',' );
        }
        final Object value = selectedValues[ i ];
        b.append( quoter.doQuoting( String.valueOf( value ) ) );
      }

      return new StringSelection( b.toString() );
    }
  }

  private DefaultBulkListModel availableFieldsModel;
  private DefaultBulkListModel groupFieldsModel;

  private JTextField nameTextField;
  private JList availableFields;
  private JList groupFields;

  public EditGroupDetailsDialog()
    throws HeadlessException {
    init();
  }

  public EditGroupDetailsDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public EditGroupDetailsDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getString( "EditGroupDetailsDialog.Title" ) );
    setModal( true );

    availableFieldsModel = new DefaultBulkListModel();
    groupFieldsModel = new DefaultBulkListModel();
    nameTextField = new JTextField( 25 );

    availableFields = new JList( availableFieldsModel );
    availableFields.setDragEnabled( true );
    availableFields.setTransferHandler( new ListTransferHandler( availableFields, availableFieldsModel ) );
    availableFields.setDropMode( DropMode.ON );
    availableFields.addMouseListener( new DoubleClickHandler( availableFields.getSelectionModel() ) );

    groupFields = new JList( groupFieldsModel );
    groupFields.setTransferHandler( new ListTransferHandler( groupFields, groupFieldsModel ) );
    groupFields.setDragEnabled( true );
    groupFields.setDropMode( DropMode.ON );


    super.init();
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.EditGroupDetails";
  }

  protected Component createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.add( createNamePanel(), BorderLayout.NORTH );
    contentPane.add( createSelectionPane(), BorderLayout.CENTER );
    return contentPane;
  }

  protected DefaultBulkListModel getAvailableFieldsModel() {
    return availableFieldsModel;
  }

  protected DefaultBulkListModel getGroupFieldsModel() {
    return groupFieldsModel;
  }

  public String getGroupName() {
    if ( nameTextField.getText().length() == 0 ) {
      return null;
    }
    return nameTextField.getText();
  }

  public void setGroupName( final String name ) {
    this.nameTextField.setText( name );
  }

  public String[] getFields() {
    final DefaultListModel data = this.getGroupFieldsModel();
    final String[] fields = new String[ data.size() ];
    for ( int i = 0; i < data.size(); i++ ) {
      fields[ i ] = (String) data.get( i );
    }
    return fields;
  }

  public void setFields( final String[] groupFields ) {
    final DefaultListModel data = getGroupFieldsModel();
    data.clear();
    for ( int i = 0; i < groupFields.length; i++ ) {
      data.addElement( groupFields[ i ] );
    }
  }

  public void dataModelChanged( final ReportDocumentContext context ) {
    final String[] columnNames = context.getReportDataSchemaModel().getColumnNames();
    final DefaultListModel availableFieldsModel = getAvailableFieldsModel();
    availableFieldsModel.clear();
    for ( int i = 0; i < columnNames.length; i++ ) {
      availableFieldsModel.addElement( columnNames[ i ] );
    }
  }

  public boolean editGroupData( final String name,
                                final String[] groupFields,
                                final ReportDocumentContext reportRenderContext ) {
    if ( reportRenderContext == null ) {
      throw new NullPointerException();
    }

    setGroupName( name );
    setFields( groupFields );

    try {
      nameTextField.setText( name );
      reportRenderContext.addReportDataChangeListener( this );
      dataModelChanged( reportRenderContext );

      if ( performEdit() == false ) {
        return false;
      }

      return true;
    } finally {
      reportRenderContext.removeReportDataChangeListener( this );
    }
  }

  public EditGroupUndoEntry editGroup( final RelationalGroup group,
                                       final ReportDocumentContext reportRenderContext,
                                       final boolean addGroup ) {
    if ( addGroup ) {
      setTitle( Messages.getString( "EditGroupDetailsDialog.AddTitle" ) );
    } else {
      setTitle( Messages.getString( "EditGroupDetailsDialog.Title" ) );
    }
    final String oldName = group.getName();
    final String[] oldFields = group.getFieldsArray();
    if ( editGroupData( oldName, oldFields, reportRenderContext ) ) {
      return new EditGroupUndoEntry( group.getObjectID(), oldName, getGroupName(), oldFields, getFields() );
    }
    return null;
  }

  private JPanel createNamePanel() {
    final JPanel theNamePanel = new JPanel( new FlowLayout( FlowLayout.LEFT, 5, 5 ) );
    theNamePanel.setBorder( BorderFactory.createEmptyBorder( 5, 0, 5, 5 ) );
    theNamePanel.add( new JLabel( Messages.getString( "EditGroupDetailsDialog.Name" ) ) );
    theNamePanel.add( nameTextField );
    return theNamePanel;
  }

  private JPanel createSelectionPane() {
    final JButton columnsAdd = new BorderlessButton( new AddSelectionAction( availableFields.getSelectionModel() ) );
    final JLabel columnsLabel = new JLabel( Messages.getString( "EditGroupDetailsDialog.SelectedItems" ) );

    final ListSelectionModel columnsSelectionModel = groupFields.getSelectionModel();
    final JButton columnsSortUp =
      new BorderlessButton( new SortBulkUpAction( groupFieldsModel, columnsSelectionModel ) );
    final JButton columnsSortDown =
      new BorderlessButton( new SortBulkDownAction( groupFieldsModel, columnsSelectionModel ) );
    final JButton columnsRemove =
      new BorderlessButton( new RemoveBulkAction( groupFieldsModel, columnsSelectionModel ) );

    final JPanel tablesPane = new JPanel();
    tablesPane.setLayout( new GridBagLayout() );

    final JLabel tablesColumnsLabel = new JLabel( Messages.getString( "EditGroupDetailsDialog.AvailableFields" ) );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    gbc.anchor = GridBagConstraints.WEST;
    tablesPane.add( tablesColumnsLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.weighty = 5;
    gbc.gridheight = 1;
    gbc.weightx = 2;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.insets = new Insets( 0, 5, 5, 5 );
    final JScrollPane comp = new JScrollPane
      ( availableFields, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    tablesPane.add( comp, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( columnsLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 3;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( columnsSortUp, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( columnsSortDown, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( columnsRemove, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 4;
    gbc.insets = new Insets( 0, 5, 5, 0 );
    tablesPane.add( new JScrollPane
      ( groupFields, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    tablesPane.add( columnsAdd, gbc );

    return tablesPane;
  }
}
