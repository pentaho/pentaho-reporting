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
