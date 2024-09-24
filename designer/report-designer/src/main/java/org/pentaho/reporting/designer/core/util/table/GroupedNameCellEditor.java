/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.designer.core.Messages;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class GroupedNameCellEditor extends DefaultCellEditor {
  private transient GroupedName value;
  private transient GroupedName originalValue;

  public GroupedNameCellEditor() {
    super( new JTextField() );
    getComponent().setName( "Table.editor" ); // NON-NLS
  }

  public boolean stopCellEditing() {
    final String s = (String) super.getCellEditorValue();
    if ( "".equals( s ) ) {
      return super.stopCellEditing();
    }
    try {
      if ( originalValue != null ) {
        value = new GroupedName( originalValue.getMetaData(), s, originalValue.getGroupName() );
      } else {
        value = new GroupedName( null, s, "" );
      }

    } catch ( final Exception e ) {
      // ignore the exception
      final JComponent editorComponent = (JComponent) getComponent();
      editorComponent.setBorder( new LineBorder( Color.red ) );
      return false;
    }
    return super.stopCellEditing();
  }

  public Component getTableCellEditorComponent( final JTable table,
                                                final Object value,
                                                final boolean isSelected,
                                                final int row,
                                                final int column ) {
    this.value = null;
    if ( value instanceof GroupedName ) {
      this.originalValue = (GroupedName) value;

      // If user clicks on a cell that has the default parameter message ('Enter a parameter'), then clear out the
      // message
      if ( ( column == 0 ) && ( isSelected == true ) &&
        ( String.valueOf( this.originalValue.getName() )
          .compareTo( Messages.getString( "DrillDownParameterTable.Parameter.DefaultName" ) ) == 0 ) ) {
        this.originalValue.setName( "" );
      }

      return super.getTableCellEditorComponent( table, originalValue.getName(), isSelected, row, column );
    } else {
      this.originalValue = null;
      return super.getTableCellEditorComponent( table, null, isSelected, row, column );
    }

  }

  public Object getCellEditorValue() {
    return value;
  }
}
