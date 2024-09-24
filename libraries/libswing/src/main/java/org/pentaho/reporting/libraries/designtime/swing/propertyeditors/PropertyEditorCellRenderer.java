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
