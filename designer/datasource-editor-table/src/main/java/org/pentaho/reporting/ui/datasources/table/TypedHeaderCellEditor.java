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

import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.designtime.swing.EllipsisButton;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.EventObject;

public class TypedHeaderCellEditor implements TableCellEditor {
  private class DefaultTextActionHandler implements ActionListener {
    private DefaultTextActionHandler() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      stopCellEditing();
    }
  }

  private class SelectTypeAction extends AbstractAction {
    private Class type;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private SelectTypeAction( final Class type ) {
      this.type = type;
      putValue( Action.NAME, type.getName() );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      TypedHeaderCellEditor.this.type = this.type;
      stopCellEditing();
    }
  }

  public class ShowPopupAction extends AbstractAction {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public ShowPopupAction() {
      putValue( Action.NAME, ".." );
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final JPopupMenu menu = new JPopupMenu();
      menu.add( new SelectTypeAction( String.class ) );
      menu.add( new SelectTypeAction( Boolean.class ) );
      menu.add( new SelectTypeAction( Number.class ) );
      menu.add( new SelectTypeAction( BigDecimal.class ) );
      menu.add( new SelectTypeAction( BigInteger.class ) );
      menu.add( new SelectTypeAction( Double.class ) );
      menu.add( new SelectTypeAction( Float.class ) );
      menu.add( new SelectTypeAction( Integer.class ) );
      menu.add( new SelectTypeAction( Long.class ) );
      menu.add( new SelectTypeAction( Short.class ) );
      menu.add( new SelectTypeAction( Byte.class ) );
      menu.add( new SelectTypeAction( Date.class ) );
      menu.add( new SelectTypeAction( java.sql.Date.class ) );
      menu.add( new SelectTypeAction( Time.class ) );
      menu.add( new SelectTypeAction( Timestamp.class ) );
      menu.add( new SelectTypeAction( Object.class ) );

      final Object source = e.getSource();
      if ( source instanceof Component ) {
        final Component c = (Component) source;
        menu.show( c, 0, c.getHeight() );
      } else {
        final Component parent = TypedHeaderCellEditor.this.typePopupButton;
        menu.show( parent, 0, 0 );
      }
    }
  }

  private EventListenerList eventListenerList;
  private JPanel editorPanel;
  private JTextField nameField;
  private JButton typePopupButton;
  private TypedHeaderInformation value;
  private Class type;
  private JTable table;
  private int editingColumn;

  public TypedHeaderCellEditor() {
    nameField = new JTextField();
    nameField.addActionListener( new DefaultTextActionHandler() );
    typePopupButton = new EllipsisButton( new ShowPopupAction() );

    editorPanel = new JPanel( new BorderLayout() );
    editorPanel.add( nameField, BorderLayout.CENTER );
    editorPanel.add( typePopupButton, BorderLayout.EAST );

    eventListenerList = new EventListenerList();
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
    editingColumn = column;
    this.table = table;
    if ( value instanceof TypedHeaderInformation ) {
      final TypedHeaderInformation headerInformation = (TypedHeaderInformation) value;
      nameField.setText( headerInformation.getName() );
      this.type = headerInformation.getType();
      this.value = headerInformation;
    } else {
      this.value = null;
      this.type = Object.class;
      nameField.setText( null );
    }
    return editorPanel;
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    if ( value == null ) {
      return "<null>";
    }
    return value;
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
   * @see #shouldSelectCell
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
   * @see #isCellEditable
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
    value = new TypedHeaderInformation( type, nameField.getText() );
    int proceedConversion = JOptionPane.YES_OPTION;
    if ( !isTypeConversionSafe() ) {
      proceedConversion = JOptionPane.showConfirmDialog
        ( table, Messages.getString( "TypedHeaderCellEditor.TypeConversionMessage" ),
          Messages.getString( "TypedHeaderCellEditor.Warning" ), JOptionPane.YES_NO_OPTION );
    }
    if ( proceedConversion == JOptionPane.YES_OPTION ) {
      fireEditingStopped();
    }
    return true;
  }

  private boolean isTypeConversionSafe() {
    boolean result = true;
    for ( int rowIndex = 0; rowIndex < table.getModel().getRowCount(); rowIndex++ ) {
      final Object currentValue = table.getModel().getValueAt( rowIndex, editingColumn );
      if ( currentValue == null ) {
        continue;
      }

      final Object newValue = ConverterRegistry.convert( currentValue, type, null );
      if ( newValue == null ) {
        result = false;
        break;
      }
    }
    return result;
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
