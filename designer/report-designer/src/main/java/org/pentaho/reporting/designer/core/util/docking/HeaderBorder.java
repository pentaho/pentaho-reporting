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


package org.pentaho.reporting.designer.core.util.docking;

import javax.swing.border.AbstractBorder;
import java.awt.*;

public class HeaderBorder extends AbstractBorder {

  public HeaderBorder() {
  }

  /**
   * Returns the insets used by this border.<p/> This border uses one pixel on the top and left side, and 4 pixels on
   * the bottom and right side.
   *
   * @param c the component for which this border insets value applies
   * @return the insets object initialized to (1,1,4,4)
   */
  @Override

  public Insets getBorderInsets( final Component c ) {
    return new Insets( 1, 1, 1, 0 );
  }


  @Override
  public boolean isBorderOpaque() {
    return false;
  }


  /**
   * Paints this border with a one pixel wide line on the top and left side, and a fading shadow on the bottom and right
   * side.
   *
   * @param c      the component for which this border is being painted
   * @param g      the paint graphics
   * @param x      the x position of the painted border
   * @param y      the y position of the painted border
   * @param width  the width of the painted border
   * @param height the height of the painted border
   */
  @Override
  public void paintBorder( final Component c,
                           final Graphics g,
                           final int x,
                           final int y,
                           final int width,
                           final int height ) {

    Color base = c.getBackground();
    if ( base == null ) {
      base = Color.LIGHT_GRAY;
    }
    final Color col1 = new Color( Math.max( 0, base.getRed() - 60 ), Math.max( 0, base.getGreen() - 60 ), Math.max( 0,
      base.getBlue() - 60 ) );
    final Color col2 =
      new Color( Math.min( 255, base.getRed() + 60 ), Math.min( 255, base.getGreen() + 60 ), Math.min( 255,
        base.getBlue() + 60 ) );

    g.setColor( col1 );
    g.drawLine( x, y + height - 1, x + width - 1, y + height - 1 );

    g.setColor( col2 );
    g.drawLine( x, y, x + width - 1, y );
    g.drawLine( x, y, x, y + height - 2 );


  }
}
