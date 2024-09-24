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

package org.pentaho.reporting.libraries.designtime.swing;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * A cell-renderer that renders color objects along with their human readable name.
 */
public class ColorCellRenderer extends JLabel implements ListCellRenderer, TableCellRenderer {
  private Color color;

  /**
   * Creates a new renderer.
   */
  public ColorCellRenderer() {
  }

  /**
   * Return a component that has been configured to display the specified value. That component's <code>paint</code>
   * method is then called to "render" the cell.  If it is necessary to compute the dimensions of a list because the
   * list cells do not have a fixed size, this method is called to generate a component on which
   * <code>getPreferredSize</code> can be invoked.
   *
   * @param list         The JList we're painting.
   * @param value        The value returned by list.getModel().getElementAt(index).
   * @param index        The cells index.
   * @param isSelected   True if the specified cell was selected.
   * @param cellHasFocus True if the specified cell has the focus.
   * @return A component whose paint() method will render the specified value.
   * @see JList
   * @see javax.swing.ListSelectionModel
   * @see javax.swing.ListModel
   */
  public Component getListCellRendererComponent( final JList list,
                                                 final Object value,
                                                 final int index,
                                                 final boolean isSelected,
                                                 final boolean cellHasFocus ) {
    final Color item = (Color) value;
    if ( item != null ) {
      this.color = item;
      setBackground( item );
      setText( ColorUtility.toAttributeValue( item ) );
      return this;
    }

    this.color = null;
    setText( Messages.getInstance().getString( "ColorCellRenderer.Automatic" ) );
    setBackground( null );
    return this;
  }

  /**
   * Returns the component used for drawing the cell.  This method is used to configure the renderer appropriately
   * before drawing.
   *
   * @param  table    the <code>JTable</code> that is asking the renderer to draw; can be <code>null</code>
   * @param  value    the value of the cell to be rendered.  It is up to the specific renderer to interpret and draw the
   * value.  For example, if <code>value</code> is the string "true", it could be rendered as a string or it could be
   * rendered as a check box that is checked.  <code>null</code> is a valid value
   * @param  isSelected  true if the cell is to be rendered with the selection highlighted; otherwise false
   * @param  hasFocus  if true, render cell appropriately.  For example, put a special border on the cell, if the
   *                   cell can
   * be edited, render in the color used to indicate editing
   * @param  row   the row index of the cell being drawn.  When drawing the header, the value of <code>row</code> is -1
   * @param  column   the column index of the cell being drawn
   */
  public Component getTableCellRendererComponent( final JTable table,
                                                  final Object value,
                                                  final boolean isSelected,
                                                  final boolean hasFocus,
                                                  final int row,
                                                  final int column ) {
    try {
      final Color item;
      if ( value instanceof String ) {
        item = (Color) ColorUtility.toPropertyValue( (String) value );
      } else {
        item = (Color) value;
      }

      if ( item != null ) {
        this.color = item;
        setBackground( item );
        setText( ColorUtility.toAttributeValue( item ) );
        return this;
      }
    } catch ( IllegalArgumentException e ) {
      // ignore ..
    }

    this.color = null;
    setText( Messages.getInstance().getString( "ColorCellRenderer.Automatic" ) );
    setBackground( null );
    return this;
  }

  /**
   * Paints the current value along with the human readable name.
   *
   * @param graphics the graphics on which to paint.
   */
  protected void paintComponent( final Graphics graphics ) {
    if ( color != null ) {
      setBackground( color );
      graphics.setColor( color );
      graphics.fillRect( 0, 0, getWidth(), getHeight() );

      setForeground(
        ( ColorUtility.getBrightness( color ) > ColorUtility.BRIGHTNESS_THRESHOLD ) ? Color.BLACK : Color.WHITE
      );
    }
    super.paintComponent( graphics );
  }
}
