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
