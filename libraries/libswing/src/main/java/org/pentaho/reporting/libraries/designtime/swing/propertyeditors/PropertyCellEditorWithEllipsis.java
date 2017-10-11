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

package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.Messages;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyEditor;
import java.util.EventObject;

public class PropertyCellEditorWithEllipsis extends JPanel implements TableCellEditor {
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
      if ( propertyEditor == null ) {
        return;
      }

      if ( propertyEditor.supportsCustomEditor() ) {
        final Window window = LibSwingUtil.getWindowAncestor( PropertyCellEditorWithEllipsis.this );

        final CustomPropertyEditorDialog editorDialog;
        if ( window instanceof Frame ) {
          editorDialog = new CustomPropertyEditorDialog( (Frame) window );
        } else if ( window instanceof Dialog ) {
          editorDialog = new CustomPropertyEditorDialog( (Dialog) window );
        } else {
          editorDialog = new CustomPropertyEditorDialog();
        }
        if ( editorDialog.performEdit( propertyEditor ) ) {
          textField.setText( propertyEditor.getAsText() );
          stopCellEditing();
        }
      } else {
        final BasicTextPropertyEditorDialog editorDialog = createTextEditorDialog();
        if ( editorDialog.performEdit( propertyEditor ) ) {
          textField.setText( propertyEditor.getAsText() );
          stopCellEditing();
        }
      }
    }
  }

  private JTextField textField;
  private JButton ellipsisButton;
  private EventListenerList eventListenerList;
  private boolean nullable;
  private PropertyEditor propertyEditor;

  public PropertyCellEditorWithEllipsis() {
    setLayout( new BorderLayout() );

    this.eventListenerList = new EventListenerList();

    ellipsisButton = new EllipsisButton( "..." );
    ellipsisButton.addActionListener( new ExtendedEditorAction() );

    textField = new JTextField();
    textField.getInputMap().put
      ( Messages.getInstance().getKeyStroke( "PropertyCellEditorWithEllipsis.PopupEditor.Accelerator" ), POPUP_EDITOR );
    textField.getActionMap().put( POPUP_EDITOR, new ExtendedEditorAction() );
    textField.setBorder( BorderFactory.createEmptyBorder() );

    add( textField, BorderLayout.CENTER );
    add( ellipsisButton, BorderLayout.EAST );

    nullable = false;
  }

  protected BasicTextPropertyEditorDialog createTextEditorDialog() {
    final Window window = LibSwingUtil.getWindowAncestor( PropertyCellEditorWithEllipsis.this );

    final BasicTextPropertyEditorDialog editorDialog;
    if ( window instanceof Frame ) {
      editorDialog = new BasicTextPropertyEditorDialog( (Frame) window );
    } else if ( window instanceof Dialog ) {
      editorDialog = new BasicTextPropertyEditorDialog( (Dialog) window );
    } else {
      editorDialog = new BasicTextPropertyEditorDialog();
    }
    return editorDialog;
  }

  public PropertyEditor getPropertyEditor() {
    return propertyEditor;
  }

  public void setPropertyEditor( final PropertyEditor propertyEditor ) {
    this.propertyEditor = propertyEditor;
    if ( propertyEditor instanceof AdvancedPropertyEditor ) {
      final AdvancedPropertyEditor advancedPropertyEditor = (AdvancedPropertyEditor) propertyEditor;
      textField.setEditable( advancedPropertyEditor.supportsText() );
    } else {
      textField.setEditable( true );
    }
  }

  public boolean isNullable() {
    return nullable;
  }

  public void setNullable( final boolean nullable ) {
    this.nullable = nullable;
  }

  public void requestFocus() {
    textField.requestFocus();
  }

  public JTextField getTextField() {
    return textField;
  }

  public JButton getEllipsisButton() {
    return ellipsisButton;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    return propertyEditor.getValue();
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
    try {
      propertyEditor.setAsText( textField.getText() );
      fireEditingStopped();
      textField.setText( null );
      return true;
    } catch ( Exception e ) {
      fireEditingCanceled();
      textField.setText( null );
      return true;
    }

  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    textField.setText( null );
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
    propertyEditor.setValue( value );
    if ( value == null ) {
      textField.setText( null );
    } else {
      textField.setText( propertyEditor.getAsText() );
    }
    return this;
  }
}
