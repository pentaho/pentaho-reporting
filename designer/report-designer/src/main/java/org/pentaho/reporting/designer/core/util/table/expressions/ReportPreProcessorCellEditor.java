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

package org.pentaho.reporting.designer.core.util.table.expressions;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.table.CellEditorUtility;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorRegistry;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.SmartComboBox;
import org.pentaho.reporting.libraries.designtime.swing.ValuePassThroughCellEditor;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

public class ReportPreProcessorCellEditor implements TableCellEditor {
  private static final String POPUP_EDITOR = "popupEditor";

  private class ExtendedEditorAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private ExtendedEditorAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final Window window = LibSwingUtil.getWindowAncestor( carrierPanel );
      final Object selectedItem = expressionEditor.getSelectedItem();
      if ( selectedItem instanceof ReportPreProcessor ) {
        final ReportPreProcessorPropertiesDialog optionPane;
        if ( window instanceof JFrame ) {
          optionPane = new ReportPreProcessorPropertiesDialog( (JFrame) window );
        } else if ( window instanceof JDialog ) {
          optionPane = new ReportPreProcessorPropertiesDialog( (JDialog) window );
        } else {
          optionPane = new ReportPreProcessorPropertiesDialog();
        }
        final ReportPreProcessor expression = optionPane.editExpression( (ReportPreProcessor) selectedItem );
        if ( expression != selectedItem ) {
          expressionEditor.setSelectedItem( expression );
        }
        stopCellEditing();
      } else if ( selectedItem instanceof ReportPreProcessorMetaData ) {
        try {
          final ReportPreProcessorMetaData emd = (ReportPreProcessorMetaData) selectedItem;
          final ReportPreProcessor expression = (ReportPreProcessor) emd.getPreProcessorType().newInstance();

          final ReportPreProcessorPropertiesDialog optionPane;
          if ( window instanceof JFrame ) {
            optionPane = new ReportPreProcessorPropertiesDialog( (JFrame) window );
          } else if ( window instanceof JDialog ) {
            optionPane = new ReportPreProcessorPropertiesDialog( (JDialog) window );
          } else {
            optionPane = new ReportPreProcessorPropertiesDialog();
          }

          final ReportPreProcessor resultexpression = optionPane.editExpression( expression );
          if ( resultexpression != expression ) {
            expressionEditor.setSelectedItem( resultexpression );
          }
          stopCellEditing();
        } catch ( Throwable e1 ) {
          UncaughtExceptionsModel.getInstance().addException( e1 );
        }
      }

    }

  }

  private class SelectionAction implements ActionListener {
    private SelectionAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      stopCellEditing();
    }
  }

  private JPanel carrierPanel;
  private JComboBox expressionEditor;
  private EventListenerList eventListenerList;
  private ReportDocumentContext renderContext;

  public ReportPreProcessorCellEditor() {
    eventListenerList = new EventListenerList();

    final EllipsisButton ellipsisButton = new EllipsisButton( "..." );
    ellipsisButton.addActionListener( new ExtendedEditorAction() );

    final DefaultComboBoxModel model = new DefaultComboBoxModel();
    final ReportPreProcessorMetaData[] datas =
      ReportPreProcessorRegistry.getInstance().getAllReportPreProcessorMetaDatas();
    model.addElement( null );
    for ( int i = 0; i < datas.length; i++ ) {
      final ReportPreProcessorMetaData metaData = datas[ i ];
      if ( metaData.isAutoProcessor() ) {
        continue;
      }
      if ( metaData.isHidden() ) {
        continue;
      }
      if ( !WorkspaceSettings.getInstance().isVisible( metaData ) ) {
        continue;
      }
      model.addElement( metaData );
    }
    expressionEditor = new SmartComboBox<ReportPreProcessorMetaData>( model );
    expressionEditor.addActionListener( new SelectionAction() );
    expressionEditor
      .setEditor( new ValuePassThroughCellEditor( expressionEditor, new ReportPreProcessorListCellRenderer() ) );
    expressionEditor.setRenderer( new ReportPreProcessorListCellRenderer() );
    expressionEditor.getInputMap().put( UtilMessages.getInstance().getKeyStroke
      ( "PropertyCellEditorWithEllipsis.PopupEditor.Accelerator" ), POPUP_EDITOR );
    expressionEditor.getActionMap().put( POPUP_EDITOR, new ExtendedEditorAction() );
    expressionEditor.setBorder( BorderFactory.createEmptyBorder() );

    carrierPanel = new JPanel( new BorderLayout() );
    carrierPanel.add( expressionEditor, BorderLayout.CENTER );
    carrierPanel.add( ellipsisButton, BorderLayout.EAST );

  }

  public void setRenderContext( final ReportDocumentContext renderContext ) {
    this.renderContext = renderContext;
  }

  public FieldDefinition[] getFields() {
    return CellEditorUtility.getFields( renderContext, new String[ 0 ] );
  }

  /**
   * Sets an initial <code>value</code> for the editor.  This will cause the editor to <code>stopEditing</code> and lose
   * any partially edited value if the editor is editing when this method is called. <p>
   * <p/>
   * Returns the component that should be added to the client's <code>Component</code> hierarchy.  Once installed in the
   * client's hierarchy this component will then be able to draw and receive user input.
   *
   * @param table      the <code>JTable</code> that is asking the editor to edit; can be <code>null</code>
   * @param value      the value of the cell to be edited; it is up to the specific editor to interpret and draw the
   *                   value.  For example, if value is the string "true", it could be rendered as a string or it could
   *                   be rendered as a check box that is checked.  <code>null</code> is a valid value
   * @param isSelected true if the cell is to be rendered with highlighting
   * @param row        the row of the cell being edited
   * @param column     the column of the cell being edited
   * @return the component for editing
   */
  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    final ReportPreProcessor value1;
    if ( value instanceof ReportPreProcessor ) {
      value1 = (ReportPreProcessor) value;
    } else {
      value1 = null;
    }

    this.expressionEditor.setSelectedItem( value1 );
    return carrierPanel;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    final Object o = this.expressionEditor.getSelectedItem();
    if ( o instanceof ReportPreProcessorMetaData ) {
      try {
        final ReportPreProcessorMetaData emd = (ReportPreProcessorMetaData) o;
        return emd.getPreProcessorType().newInstance();
      } catch ( Throwable t ) {
        UncaughtExceptionsModel.getInstance().addException( t );
        return null;
      }

    } else if ( o instanceof ReportPreProcessor ) {
      return o;
    } else {
      return null;
    }
  }

  /**
   * Asks the editor if it can start editing using <code>anEvent</code>. <code>anEvent</code> is in the invoking
   * component coordinate system. The editor can not assume the Component returned by
   * <code>getCellEditorComponent</code> is installed.  This method is intended for the use of client to avoid the cost
   * of setting up and installing the editor component if editing is not possible. If editing can be started this method
   * returns true.
   *
   * @param anEvent the event the editor should use to consider whether to begin editing or not
   * @return true if editing can be started
   */
  public boolean isCellEditable( final EventObject anEvent ) {
    if ( anEvent instanceof MouseEvent ) {
      final MouseEvent mouseEvent = (MouseEvent) anEvent;
      return mouseEvent.getClickCount() >= 2 && mouseEvent.getButton() == MouseEvent.BUTTON1;
    }
    return true;
  }

  /**
   * Returns true if the editing cell should be selected, false otherwise. Typically, the return value is true, because
   * is most cases the editing cell should be selected.  However, it is useful to return false to keep the selection
   * from changing for some types of edits. eg. A table that contains a column of check boxes, the user might want to be
   * able to change those checkboxes without altering the selection.  (See Netscape Communicator for just such an
   * example) Of course, it is up to the client of the editor to use the return value, but it doesn't need to if it
   * doesn't want to.
   *
   * @param anEvent the event the editor should use to start editing
   * @return true if the editor would like the editing cell to be selected; otherwise returns false
   */
  public boolean shouldSelectCell( final EventObject anEvent ) {
    return true;
  }

  /**
   * Tells the editor to stop editing and accept any partially edited value as the value of the editor.  The editor
   * returns false if editing was not stopped; this is useful for editors that validate and can not accept invalid
   * entries.
   *
   * @return true if editing was stopped; false otherwise
   */
  public boolean stopCellEditing() {
    expressionEditor.actionPerformed( new ActionEvent( this, 0, "" ) );
    fireEditingStopped();
    return true;
  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    fireEditingCanceled();
  }


  protected void fireEditingCanceled() {
    final CellEditorListener[] listeners = eventListenerList.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingCanceled( event );
    }
  }


  protected void fireEditingStopped() {
    final CellEditorListener[] listeners = eventListenerList.getListeners( CellEditorListener.class );
    final ChangeEvent event = new ChangeEvent( this );
    for ( int i = 0; i < listeners.length; i++ ) {
      final CellEditorListener listener = listeners[ i ];
      listener.editingStopped( event );
    }
  }

  /**
   * Adds a listener to the list that's notified when the editor stops, or cancels editing.
   *
   * @param l the CellEditorListener
   */
  public void addCellEditorListener( final CellEditorListener l ) {
    eventListenerList.add( CellEditorListener.class, l );
  }

  /**
   * Removes a listener from the list that's notified
   *
   * @param l the CellEditorListener
   */
  public void removeCellEditorListener( final CellEditorListener l ) {
    eventListenerList.remove( CellEditorListener.class, l );
  }
}
