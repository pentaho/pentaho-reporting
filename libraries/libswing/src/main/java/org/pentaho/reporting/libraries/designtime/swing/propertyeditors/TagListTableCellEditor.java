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

import org.pentaho.reporting.libraries.base.util.DebugLog;

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
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.EventObject;

public class TagListTableCellEditor extends JPanel implements TableCellEditor {
  protected static class NonFilteringPlainDocument implements Document {
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
      if ( "filterNewlines".equals( key ) ) {
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


  protected class SelectionAction implements ActionListener {
    protected SelectionAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( isFilterEvents() ) {
        return;
      }
      stopCellEditing();
    }
  }

  protected class CancelAction extends AbstractAction {
    protected CancelAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      cancelCellEditing();
    }
  }

  private static class TagListComboBoxRenderer extends DefaultListCellRenderer {
    private TagListComboBoxRenderer() {
    }

    public Component getListCellRendererComponent( final JList list,
                                                   final Object value,
                                                   final int index,
                                                   final boolean isSelected,
                                                   final boolean cellHasFocus ) {
      if ( value == null ) {
        return super.getListCellRendererComponent( list, "<undefined>", index, isSelected, cellHasFocus );
      }
      return super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
    }

  }

  private transient Object originalValue;
  private volatile boolean filterEvents;
  private JComboBox comboBox;
  private EventListenerList eventListenerList;
  private String[] tags;
  private static final String[] EMPTY_TAGS = new String[ 0 ];

  /**
   * Creates a new <code>JPanel</code> with a double buffer and a flow layout.
   */
  public TagListTableCellEditor() {
    setLayout( new BorderLayout() );

    tags = EMPTY_TAGS;
    eventListenerList = new EventListenerList();

    comboBox = new JComboBox();
    comboBox.addActionListener( new SelectionAction() );
    comboBox.putClientProperty( "JComboBox.isTableCellEditor", Boolean.TRUE );
    comboBox.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), new CancelAction() );
    comboBox.setEditable( true );
    comboBox.setRenderer( new TagListComboBoxRenderer() );

    comboBox.setModel( new DefaultComboBoxModel( getTags() ) );
    comboBox.setEditable( true );
    add( comboBox, BorderLayout.CENTER );
    comboBox.requestFocus();
  }

  public String[] getTags() {
    return tags.clone();
  }

  public void setTags( final String[] tags ) {
    this.tags = tags.clone();
    this.comboBox.setModel( new DefaultComboBoxModel( this.tags ) );
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
   * Returns the value contained in the editor.
   *
   * @return the value contained in the editor
   */
  public Object getCellEditorValue() {
    final Object selectedItem = comboBox.getSelectedItem();
    if ( "".equals( selectedItem ) ) {
      return null;
    }
    return selectedItem;
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
    try {
      // ugly hack to make the combobox editor commit any changes before we go out of focus.
      comboBox.actionPerformed( new ActionEvent( this, ActionEvent.ACTION_PERFORMED, comboBox.getActionCommand() ) );
      fireEditingStopped();
      return true;
    } catch ( final Exception e ) {
      DebugLog.log( "Error on Stop", e );
      // exception ignored
      fireEditingCanceled();
      return true;
    }

  }

  /**
   * Tells the editor to cancel editing and not accept any partially edited value.
   */
  public void cancelCellEditing() {
    try {
      filterEvents = true;
      comboBox.setSelectedItem( originalValue );
    } finally {
      filterEvents = false;
    }
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

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    try {
      filterEvents = true;
      comboBox.setSelectedItem( value );
    } finally {
      filterEvents = false;
    }
    this.originalValue = value;
    return this;
  }

  protected boolean isFilterEvents() {
    return filterEvents;
  }
}
