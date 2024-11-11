/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.designtime.swing.propertyeditors;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyEditor;

public class PropertyEditorCellEditor extends AbstractCellEditor implements TableCellEditor {
  private class SelectionAction implements ActionListener {
    private SelectionAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      if ( !usingCustomEditor ) {
        stopCellEditing();
      }
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

  private PropertyEditor propertyEditor;
  private JTextField defaultCellEditor;
  private boolean usingCustomEditor;
  private boolean usingTags;
  private JComboBox tagsCellEditor;

  public PropertyEditorCellEditor() {
    this.tagsCellEditor = new JComboBox();
    this.tagsCellEditor.addActionListener( new SelectionAction() );
    this.tagsCellEditor.putClientProperty( "JComboBox.isTableCellEditor", Boolean.TRUE );
    this.tagsCellEditor.getInputMap().put( KeyStroke.getKeyStroke( KeyEvent.VK_ESCAPE, 0 ), new CancelAction() );
    this.tagsCellEditor.setRenderer( new TagListComboBoxRenderer() );

    this.defaultCellEditor = new JTextField();
    this.defaultCellEditor.addActionListener( new SelectionAction() );
    this.defaultCellEditor.setBorder( BorderFactory.createEmptyBorder() );
  }

  public PropertyEditor getPropertyEditor() {
    return propertyEditor;
  }

  public void setPropertyEditor( final PropertyEditor propertyEditor ) {
    this.propertyEditor = propertyEditor;
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    propertyEditor.setValue( value );
    if ( propertyEditor.supportsCustomEditor() ) {
      usingCustomEditor = true;
      return propertyEditor.getCustomEditor();
    } else {
      final String[] tags = propertyEditor.getTags();
      if ( tags != null ) {
        usingCustomEditor = false;
        usingTags = true;
        tagsCellEditor.setModel( new DefaultComboBoxModel( tags ) );
        tagsCellEditor.setSelectedItem( propertyEditor.getAsText() );
        return tagsCellEditor;
      } else {
        usingCustomEditor = false;
        usingTags = false;
        defaultCellEditor.setText( propertyEditor.getAsText() );
        return defaultCellEditor;
      }
    }
  }

  public Object getCellEditorValue() {
    if ( propertyEditor == null ) {
      return null;
    }
    if ( usingCustomEditor ) {
      return propertyEditor.getValue();
    } else {
      try {
        if ( usingTags ) {
          final String text = (String) tagsCellEditor.getSelectedItem();
          propertyEditor.setAsText( text );
          return propertyEditor.getValue();
        } else {
          final String text = defaultCellEditor.getText();
          propertyEditor.setAsText( text );
          return propertyEditor.getValue();
        }
      } catch ( final Exception e ) {
        // exception ignored
        return null;
      }
    }
  }

  public boolean stopCellEditing() {
    if ( usingCustomEditor ) {
      fireEditingStopped();
      return true;
    }
    if ( usingTags ) {
      final String s = (String) tagsCellEditor.getSelectedItem();
      propertyEditor.setAsText( s );
      final boolean retval = ( propertyEditor.getValue() != null );
      fireEditingStopped();
      return retval;
    }
    try {
      propertyEditor.setAsText( defaultCellEditor.getText() );
      final boolean retval = ( propertyEditor.getValue() != null );
      fireEditingStopped();
      return retval;
    } catch ( final Exception e ) {
      // exception ignored
      fireEditingCanceled();
      return true;
    }
  }
}
