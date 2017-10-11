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
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.beans.PropertyEditor;

public class PropertyEditorCellRenderer implements TableCellRenderer, ListCellRenderer {
  private class PropertyPainter extends JComponent {
    private PropertyPainter() {
      setOpaque( false );
    }

    protected void paintComponent( final Graphics g ) {
      final PropertyEditor propertyEditor = getPropertyEditor();
      if ( propertyEditor == null ) {
        return;
      }
      if ( propertyEditor.isPaintable() == false ) {
        return;
      }
      propertyEditor.paintValue( g, new Rectangle( 0, 0, getWidth(), getHeight() ) );
    }
  }

  private static final Border NO_FOCUS_BORDER = new EmptyBorder( 1, 1, 1, 1 );
  private PropertyEditor propertyEditor;
  private DefaultTableCellRenderer fallbackRenderer;
  private PropertyPainter propertyPainter;

  public PropertyEditorCellRenderer() {
    fallbackRenderer = new DefaultTableCellRenderer();
    fallbackRenderer.putClientProperty( "html.disable", Boolean.TRUE );
    propertyPainter = new PropertyPainter();
  }

  public Component getListCellRendererComponent( final JList table,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean hasFocus ) {
    if ( propertyEditor == null ) {
      throw new IllegalStateException();
    }
    try {
      propertyEditor.setValue( value );
      if ( value != null ) {
        if ( propertyEditor.isPaintable() ) {
          return propertyPainter;
        }

        fallbackRenderer.setText( propertyEditor.getAsText() );
      } else {
        fallbackRenderer.setText( "" );
      }
    } catch ( Exception e ) {
      // exception ignored
      fallbackRenderer.setText( propertyEditor.getAsText() );
    }
    if ( isSelected ) {
      fallbackRenderer.setForeground( table.getSelectionForeground() );
      fallbackRenderer.setBackground( table.getSelectionBackground() );
    } else {
      fallbackRenderer.setForeground( table.getForeground() );
      fallbackRenderer.setBackground( table.getBackground() );
    }

    if ( hasFocus ) {
      fallbackRenderer.setBorder( UIManager.getBorder( "Table.focusCellHighlightBorder" ) );
    } else {
      fallbackRenderer.setBorder( NO_FOCUS_BORDER );
    }
    return fallbackRenderer;

  }

  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    if ( propertyEditor == null ) {
      throw new IllegalStateException();
    }
    try {
      propertyEditor.setValue( value );
      if ( value != null ) {
        if ( propertyEditor.isPaintable() ) {
          return propertyPainter;
        }
      }
      fallbackRenderer.setText( propertyEditor.getAsText() );
    } catch ( Exception e ) {
      // exception ignored
      fallbackRenderer.setText( propertyEditor.getAsText() );
    }
    if ( isSelected ) {
      fallbackRenderer.setForeground( table.getSelectionForeground() );
      fallbackRenderer.setBackground( table.getSelectionBackground() );
    } else {
      fallbackRenderer.setForeground( table.getForeground() );
      fallbackRenderer.setBackground( table.getBackground() );
    }

    if ( hasFocus ) {
      fallbackRenderer.setBorder( UIManager.getBorder( "Table.focusCellHighlightBorder" ) );
    } else {
      fallbackRenderer.setBorder( NO_FOCUS_BORDER );
    }
    return fallbackRenderer;
  }

  public PropertyEditor getPropertyEditor() {
    return propertyEditor;
  }

  public void setPropertyEditor( final PropertyEditor propertyEditor ) {
    this.propertyEditor = propertyEditor;
  }
}
