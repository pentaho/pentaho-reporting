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

package org.pentaho.reporting.libraries.designtime.swing.table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.designtime.swing.BorderlessButton;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.GenericTransferable;
import org.pentaho.reporting.libraries.designtime.swing.Messages;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkDownAction;
import org.pentaho.reporting.libraries.designtime.swing.bulk.SortBulkUpAction;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ArrayCellEditorDialog extends CommonDialog {
  private static class AddEntryAction extends AbstractAction {
    private ArrayTableModel tableModel;

    private AddEntryAction( final ArrayTableModel tableModel ) {
      this.tableModel = tableModel;
      putValue( Action.SMALL_ICON, Messages.getInstance().getIcon( "Icons.Add" ) );
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getInstance().getString( "ArrayCellEditorDialog.AddEntry.Description" ) );
    }

    public void actionPerformed( final ActionEvent e ) {
      tableModel.add( null );
    }
  }

  private class RemoveEntryAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;
    private ArrayTableModel tableModel;

    private RemoveEntryAction( final ArrayTableModel tableModel,
                               final ListSelectionModel selectionModel ) {
      this.tableModel = tableModel;
      putValue( Action.SMALL_ICON, Messages.getInstance().getIcon( "Icons.Remove" ) );
      putValue( Action.SHORT_DESCRIPTION,
        Messages.getInstance().getString( "ArrayCellEditorDialog.RemoveEntry.Description" ) );


      this.selectionModel = selectionModel;
      this.selectionModel.addListSelectionListener( this );
    }

    public void actionPerformed( final ActionEvent e ) {
      table.stopEditing();

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
  }


  private class DoubleClickHandler extends MouseAdapter {
    private ListSelectionModel selectionModel;

    private DoubleClickHandler( final ListSelectionModel selectionModel ) {
      this.selectionModel = selectionModel;
    }

    public void mouseClicked( final MouseEvent e ) {
      if ( e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 ) {
        final ArrayTableModel data = getTableModel();
        final ArrayTableModel fields = getPaletteListModel();
        for ( int i = 0; i < fields.getSize(); i++ ) {
          if ( selectionModel.isSelectedIndex( i ) ) {
            data.add( fields.get( i ) );
          }
        }
      }
    }
  }

  private class ListTransferHandler extends TransferHandler {
    private ListTransferHandler() {
    }

    public boolean importData( final TransferSupport support ) {
      final DataFlavor dataFlavor = getDataFlavor();
      if ( support.isDataFlavorSupported( dataFlavor ) == false ) {
        return false;
      }

      if ( support.isDrop() == false ) {
        return false;
      }
      try {
        final Object[] transferData = (Object[]) support.getTransferable().getTransferData( dataFlavor );
        if ( transferData == null ) {
          return false;
        }

        final ArrayList<Object> items = new ArrayList<Object>();
        for ( final Object item : transferData ) {
          items.add( item );
        }

        final DropLocation dropLocation = support.getDropLocation();
        final Point point = dropLocation.getDropPoint();

        final int idx = table.rowAtPoint( point );
        if ( idx == -1 ) {
          for ( int i = 0; i < items.size(); i++ ) {
            tableModel.add( items.get( i ) );
          }
        } else {
          for ( int i = items.size() - 1; i >= 0; i -= 1 ) {
            tableModel.add( idx, items.get( i ) );
          }
        }
      } catch ( Exception e ) {
        logger.error( "Unable to transfer data in drag-and-drop operation", e ); // NON-NLS
        return false;
      }

      return super.importData( support );
    }

    public boolean canImport( final TransferSupport support ) {
      if ( support.isDrop() == false ) {
        return false;
      }
      return ( support.isDataFlavorSupported( getDataFlavor() ) );
    }

    public int getSourceActions( final JComponent c ) {
      return TransferHandler.COPY;
    }

    protected Transferable createTransferable( final JComponent c ) {
      final JList lcomp = (JList) c;
      final Object[] selectedValues = lcomp.getSelectedValues();
      return new GenericTransferable( selectedValues );
    }
  }

  private class AddSelectionAction extends AbstractAction implements ListSelectionListener {
    private ListSelectionModel selectionModel;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private AddSelectionAction( final ListSelectionModel selectionModel ) {
      this.selectionModel = selectionModel;
      putValue( Action.SMALL_ICON, Messages.getInstance().getIcon( "Icons.ForwardArrow" ) );
      putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "ArrayCellEditorDialog.AddRow" ) );
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
      final ArrayTableModel data = getTableModel();
      final ArrayTableModel fields = getPaletteListModel();
      for ( int i = 0; i < fields.getSize(); i++ ) {
        if ( selectionModel.isSelectedIndex( i ) ) {
          data.add( fields.get( i ) );
        }
      }
    }
  }

  private static final Log logger = LogFactory.getLog( ArrayCellEditorDialog.class );
  private ArrayTableModel tableModel;
  private PropertyTable table;
  private PropertyTable paletteList;
  private ArrayTableModel paletteListModel;
  private DataFlavor dataFlavor;
  private JPanel contentPane;
  private boolean defaultSize;

  public ArrayCellEditorDialog()
    throws HeadlessException {
    init();
  }

  public ArrayCellEditorDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public ArrayCellEditorDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( Messages.getInstance().getString( "ArrayCellEditorDialog.Title" ) );

    tableModel = new ArrayTableModel();

    table = new PropertyTable();
    table.setModel( tableModel );

    paletteListModel = new ArrayTableModel();
    paletteListModel.setEditable( false );

    paletteList = new PropertyTable();
    paletteList.setModel( paletteListModel );
    paletteList.setDragEnabled( true );
    paletteList.setTransferHandler( new ListTransferHandler() );
    paletteList.setDropMode( DropMode.ON );
    paletteList.addMouseListener( new DoubleClickHandler( paletteList.getSelectionModel() ) );

    contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    super.init();
  }

  protected void performInitialResize() {
    super.performInitialResize();
    defaultSize = true;
  }

  protected String getDialogId() {
    return "LibSwing.ArrayCellEditor";// NON-NLS
  }

  protected ArrayTableModel getPaletteListModel() {
    return paletteListModel;
  }

  protected ArrayTableModel getTableModel() {
    return tableModel;
  }

  protected Component createContentPane() {
    return contentPane;
  }

  public Object editArray( Object data,
                           final Class arrayType,
                           final Class propertyEditorType ) {
    if ( arrayType == null ) {
      throw new NullPointerException();
    }
    if ( arrayType.isArray() == false ) {
      throw new IllegalArgumentException( "Expect an array class, not a primitive data-type" );
    }
    final Class componentType = arrayType.getComponentType();
    if ( ArrayAccessUtility.isArray( data ) == false ) {
      data = Array.newInstance( componentType, 0 );
    }

    if ( componentType.isArray() ) {
      dataFlavor = null;
    } else {
      dataFlavor = GenericTransferable.ELEMENT_FLAVOR;
    }

    tableModel.setType( componentType );
    tableModel.setPropertyEditorType( propertyEditorType );
    tableModel.setData( ArrayAccessUtility.normalizeArray( data ), componentType );

    paletteListModel.setType( componentType );
    paletteListModel.setPropertyEditorType( propertyEditorType );
    paletteListModel.setData( ArrayAccessUtility.normalizeArray( data ), componentType );

    if ( dataFlavor != null ) {
      paletteListModel.clear();
      final Object[] selection = getSelection( arrayType, propertyEditorType );
      if ( selection != null && selection.length != 0 ) {
        for ( final Object s : selection ) {
          paletteListModel.add( s );
        }
        configurePanelWithSelection();
      } else {
        configurePanelWithoutSelection();
      }
    } else {
      configurePanelWithoutSelection();
    }

    if ( defaultSize ) {
      performInitialResize();
      defaultSize = false;
    }

    if ( performEdit() == false ) {
      return null;
    }

    table.stopEditing();

    // process the array ..
    final Object[] objects = tableModel.toArray();
    return ArrayAccessUtility.normalizeNative( objects, componentType );
  }

  public DataFlavor getDataFlavor() {
    return dataFlavor;
  }

  private Object[] getSelection( final Class arrayType,
                                 final Class propertyEditorType ) {
    if ( String[].class.equals( arrayType ) ) {
      if ( propertyEditorType != null && PropertyEditor.class.isAssignableFrom( propertyEditorType ) ) {
        try {
          final PropertyEditor editor = (PropertyEditor) propertyEditorType.newInstance();
          return editor.getTags();
        } catch ( Throwable e ) {
          logger.error( "Unable to instantiate property editor.", e );// NON-NLS
        }
      }
    } else if ( Color[].class.equals( arrayType ) ) {
      return ColorUtility.getPredefinedExcelColors();
    }

    return null;
  }

  private void configurePanelWithSelection() {
    final ListSelectionModel selectionModel = table.getSelectionModel();
    final JLabel columnsLabel = new JLabel( Messages.getInstance().getString( "ArrayCellEditorDialog.SelectedItems" ) );

    final Action addGroupAction = new AddEntryAction( tableModel );
    final Action removeGroupAction = new RemoveEntryAction( tableModel, selectionModel );
    final Action sortUpAction = new SortBulkUpAction( tableModel, selectionModel, table );
    final Action sortDownAction = new SortBulkDownAction( tableModel, selectionModel, table );

    final JPanel tablesPane = new JPanel();
    tablesPane.setLayout( new GridBagLayout() );

    final JLabel tablesColumnsLabel =
      new JLabel( Messages.getInstance().getString( "ArrayCellEditorDialog.AvailableSelection" ) );
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
      ( paletteList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
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
    tablesPane.add( new BorderlessButton( sortUpAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new BorderlessButton( sortDownAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new BorderlessButton( addGroupAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new BorderlessButton( removeGroupAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 5;
    gbc.insets = new Insets( 0, 5, 5, 0 );
    tablesPane.add( new JScrollPane
      ( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.insets = new Insets( 5, 5, 5, 0 );
    tablesPane.add( new BorderlessButton( new AddSelectionAction( paletteList.getSelectionModel() ) ), gbc );

    contentPane.removeAll();
    contentPane.add( tablesPane );
    contentPane.invalidate();
    contentPane.revalidate();
    contentPane.repaint();
  }


  private void configurePanelWithoutSelection() {
    final JLabel columnsLabel = new JLabel( Messages.getInstance().getString( "ArrayCellEditorDialog.SelectedItems" ) );
    final ListSelectionModel selectionModel = table.getSelectionModel();

    final Action addGroupAction = new AddEntryAction( tableModel );
    final Action removeGroupAction = new RemoveEntryAction( tableModel, selectionModel );
    final Action sortUpAction = new SortBulkUpAction( tableModel, selectionModel, table );
    final Action sortDownAction = new SortBulkDownAction( tableModel, selectionModel, table );

    final JPanel tablesPane = new JPanel();
    tablesPane.setLayout( new GridBagLayout() );

    GridBagConstraints gbc = new GridBagConstraints();
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
    tablesPane.add( new BorderlessButton( sortUpAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 4;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new BorderlessButton( sortDownAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 5;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new BorderlessButton( addGroupAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 6;
    gbc.gridy = 2;
    gbc.insets = new Insets( 5, 5, 5, 5 );
    tablesPane.add( new BorderlessButton( removeGroupAction ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.gridwidth = 5;
    gbc.insets = new Insets( 0, 5, 5, 0 );
    tablesPane.add( new JScrollPane
      ( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED ), gbc );

    contentPane.removeAll();
    contentPane.add( tablesPane );
    contentPane.invalidate();
    contentPane.revalidate();
    contentPane.repaint();
  }
}
