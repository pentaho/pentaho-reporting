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

/**
 * User: Martin Date: 03.02.2006 Time: 08:13:47
 */
public class ShadowBorder extends AbstractBorder {
  private static final int OFFSET_1 = 20;
  private static final int OFFSET_2 = 30;
  private static final int OFFSET_3 = 40;

  public ShadowBorder() {
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
    return new Insets( 1, 1, 4, 4 );
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
      base = SystemColor.controlShadow;
    }

    final Color col3 = new Color( base.getRed() - OFFSET_1, base.getGreen() - OFFSET_1, base.getBlue() - OFFSET_1 );
    final Color col2 = new Color( base.getRed() - OFFSET_2, base.getGreen() - OFFSET_2, base.getBlue() - OFFSET_2 );
    final Color col1 = new Color( base.getRed() - OFFSET_3, base.getGreen() - OFFSET_3, base.getBlue() - OFFSET_3 );

    g.setColor( col3 );
    g.drawLine( x + width - 4, y, x + width - 3, y );
    g.drawLine( x, y + height - 4, x, y + height - 3 );
    g.drawLine( x + 1, y + height - 2, x + width - 3, y + height - 2 );
    g.drawLine( x + width - 3, y + height - 3, x + width - 3, y + height - 3 );
    g.drawLine( x + width - 2, y + 1, x + width - 2, y + height - 3 );

    g.setColor( col2 );
    g.drawLine( x + width - 5, y, x + width - 4, y );
    g.drawLine( x, y + height - 5, x, y + height - 4 );
    g.drawLine( x + 1, y + height - 3, x + width - 4, y + height - 3 );
    g.drawLine( x + width - 3, y + 1, x + width - 3, y + height - 4 );

    g.setColor( col1 );
    g.drawLine( x, y, x + width - 5, y );
    g.drawLine( x, y, x, y + height - 5 );
    g.drawLine( x + 1, y + height - 4, x + width - 4, y + height - 4 );
    g.drawLine( x + width - 4, y + 1, x + width - 4, y + height - 4 );
  }
}
