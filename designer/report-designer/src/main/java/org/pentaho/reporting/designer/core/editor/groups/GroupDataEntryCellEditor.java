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
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.UndoableEditListener;
import javax.swing.table.TableCellEditor;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.EventObject;

public class GroupDataEntryCellEditor extends JPanel implements TableCellEditor {
  private static final String POPUP_EDITOR = "popupEditor";


  private static class NonFilteringPlainDocument implements Document {
    private PlainDocument backend;

    private NonFilteringPlainDocument() {
      backend = new PlainDocument();
    }

    public int getLength() {
      return backend.getLength();
    }

    public void addDocumentListener( final DocumentListener listener ) {
      backend.addDocumentListener( listener );
    }

    public void removeDocumentListener( final DocumentListener listener ) {
      backend.removeDocumentListener( listener );
    }

    public void addUndoableEditListener( final UndoableEditListener listener ) {
      backend.addUndoableEditListener( listener );
    }

    public void removeUndoableEditListener( final UndoableEditListener listener ) {
      backend.removeUndoableEditListener( listener );
    }

    public Object getProperty( final Object key ) {
      return backend.getProperty( key );
    }

    public void putProperty( final Object key, final Object value ) {
      if ( "filterNewlines".equals( key ) ) // NON-NLS
      {
        return;
      }
      backend.putProperty( key, value );
    }

    public void remove( final int offs, final int len ) throws BadLocationException {
      backend.remove( offs, len );
    }

    public void insertString( final int offset, final String str, final AttributeSet a ) throws BadLocationException {
      backend.insertString( offset, str, a );
    }

    public String getText( final int offset, final int length ) throws BadLocationException {
      return backend.getText( offset, length );
    }

    public void getText( final int offset, final int length, final Segment txt ) throws BadLocationException {
      backend.getText( offset, length, txt );
    }

    public Position getStartPosition() {
      return backend.getStartPosition();
    }

    public Position getEndPosition() {
      return backend.getEndPosition();
    }

    public Position createPosition( final int offs ) throws BadLocationException {
      return backend.createPosition( offs );
    }

    public Element[] getRootElements() {
      return backend.getRootElements();
    }

    public Element getDefaultRootElement() {
      return backend.getDefaultRootElement();
    }

    public void render( final Runnable r ) {
      backend.render( r );
    }
  }

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
      final GroupDataEntry value = getGroupDataEntry();
      if ( value == null ) {
        return;
      }

      final Window window = LibSwingUtil.getWindowAncestor( GroupDataEntryCellEditor.this );
      final EditGroupDetailsDialog editorDialog;
      if ( window instanceof Frame ) {
        editorDialog = new EditGroupDetailsDialog( (Frame) window );
      } else if ( window instanceof Dialog ) {
        editorDialog = new EditGroupDetailsDialog( (Dialog) window );
      } else {
        editorDialog = new EditGroupDetailsDialog();
      }


      if ( editorDialog.editGroupData( value.getName(), value.getFields(), getReportContext() ) ) {
        setGroupDataEntry
          ( new GroupDataEntry( value.getInstanceID(), editorDialog.getGroupName(), editorDialog.getFields() ) );
      }
      stopCellEditing();
    }
  }

  private JTextField textField;
  private JButton ellipsisButton;
  private EventListenerList eventListenerList;
  private ReportDocumentContext reportContext;
  private GroupDataEntry groupDataEntry;

  public GroupDataEntryCellEditor() {
    setLayout( new BorderLayout() );

    this.eventListenerList = new EventListenerList();

    ellipsisButton = new JButton( "..." );
    ellipsisButton.setDefaultCapable( false );
    ellipsisButton.setMargin( new Insets( 0, 0, 0, 0 ) );
    ellipsisButton.addActionListener( new ExtendedEditorAction() );

    textField = new JTextField();
    textField.setDocument( new NonFilteringPlainDocument() );
    textField.getInputMap()
      .put( Messages.getOptionalKeyStroke( "AbstractStringValueCellEditor.Popup.Accelerator" ), POPUP_EDITOR );
    textField.getActionMap().put( POPUP_EDITOR, new ExtendedEditorAction() );
    textField.setBorder( BorderFactory.createEmptyBorder() );
    textField.setEditable( false );

    add( textField, BorderLayout.CENTER );
    add( ellipsisButton, BorderLayout.EAST );
  }

  protected GroupDataEntry getGroupDataEntry() {
    return groupDataEntry;
  }

  protected void setGroupDataEntry( final GroupDataEntry groupDataEntry ) {
    this.groupDataEntry = groupDataEntry;
    if ( groupDataEntry == null ) {
      textField.setText( "" );
      ellipsisButton.setEnabled( false );
    } else {
      textField.setText( this.groupDataEntry.getFieldsAsText() );
      ellipsisButton.setEnabled( true );
    }
  }

  public void requestFocus() {
    textField.requestFocus();
  }

  /**
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    return groupDataEntry;
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
      fireEditingStopped();
      textField.setText( null );
      return true;
    } catch ( Exception e ) {
      // exception ignored
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
    if ( value instanceof GroupDataEntry == false ) {
      setGroupDataEntry( null );
      textField.setText( null );
    } else {
      setGroupDataEntry( (GroupDataEntry) value );
    }
    return this;
  }

  public ReportDocumentContext getReportContext() {
    return reportContext;
  }

  public void setReportContext( final ReportDocumentContext reportContext ) {
    this.reportContext = reportContext;
  }
}
