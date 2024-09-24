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

package org.pentaho.reporting.designer.core.util.docking;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;

/**
 * A Panel used to display the title of a GradientBorderPanel.<p/>
 * <p/>
 * It uses a gradient with user defineable colors as background.
 *
 * @author schmm7
 */
public class GradientPanel extends JPanel {

  public enum Direction {
    DIRECTION_UP,
    DIRECTION_DOWN,
    DIRECTION_LEFT,
  }


  private Color[] gradientColors;

  private Direction direction;
  private boolean gradientSetByUser;


  /**
   * Creates a new GradientTitlePanel with a gradient from DARK_GREY to LIGHT_GREY. The default title string is set to a
   * one char width whitespace. This results in a correctly calculated preferred size of the title label.
   */
  public GradientPanel() {
    setLayout( new BorderLayout() );
    setOpaque( false );

    gradientColors = new Color[] { getBackground().darker(), getBackground() };
    direction = Direction.DIRECTION_DOWN;
    gradientSetByUser = false;
  }


  /**
   * Sets the colors used as gradient.<p/>
   * <p/>
   * The first color in the array is used as the leftmost color in the gradient.
   *
   * @param gradientColors the colors to use as gradient
   */
  public void setGradientColors( final Color[] gradientColors ) {
    //noinspection ConstantConditions
    if ( gradientColors == null || gradientColors.length == 0 ) {
      throw new IllegalArgumentException( "gradientColors must be array with more than zero elements" );
    }
    gradientSetByUser = true;
    if ( gradientColors.length == 1 ) {
      this.gradientColors = new Color[] { gradientColors[ 0 ], gradientColors[ 0 ] };
    } else {
      this.gradientColors = gradientColors.clone();
    }
    repaint();
  }


  public Color[] getGradientColors() {
    return gradientColors.clone();
  }


  public Direction getDirection() {
    return direction;
  }


  public void setDirection( final Direction direction ) {
    this.direction = direction;
  }


  @Override
  public void setBackground( final Color bg ) {
    super.setBackground( bg );
    if ( !gradientSetByUser ) {
      gradientColors = new Color[] { getBackground().darker(), getBackground() };
    }
  }


  /**
   * First paints the gradient, and delegates the call to its children. <p/> Usually you should use components that do
   * not draw their background on their own.
   *
   * @param g the GraphicsContext to paint on
   * @see JComponent#setOpaque(boolean)
   */
  @Override
  protected void paintComponent( final Graphics g ) {
    final Graphics2D graphics2D = (Graphics2D) g;

    if ( direction == Direction.DIRECTION_DOWN ) {
      final int heigthIncrement = getHeight() / ( gradientColors.length - 1 );

      for ( int i = 0; i < gradientColors.length - 1; i++ ) {
        final GradientPaint gradientPaint = new GradientPaint( new Point2D.Double( 0, heigthIncrement * i ),
          gradientColors[ i ], new Point2D.Double( 0, ( i + 1 ) * heigthIncrement ), gradientColors[ i + 1 ] );
        graphics2D.setPaint( gradientPaint );
        graphics2D.fillRect( 0, i * heigthIncrement, getWidth(), heigthIncrement );
      }
    } else if ( direction == Direction.DIRECTION_UP ) {
      final int heigthIncrement = getHeight() / ( gradientColors.length - 1 );

      for ( int i = 0; i < gradientColors.length - 1; i++ ) {
        final Color gc1 = gradientColors[ gradientColors.length - i - 1 ];
        final Color gc2 = gradientColors[ gradientColors.length - i - 2 ];
        final GradientPaint gradientPaint = new GradientPaint( new Point2D.Double( 0, heigthIncrement * i ), gc1,
          new Point2D.Double( 0, ( i + 1 ) * heigthIncrement ), gc2 );
        graphics2D.setPaint( gradientPaint );
        graphics2D.fillRect( 0, i * heigthIncrement, getWidth(), heigthIncrement );
      }
    } else if ( direction == Direction.DIRECTION_LEFT ) {
      final int widthIncrement = getWidth() / ( gradientColors.length - 1 );

      for ( int i = 0; i < gradientColors.length - 1; i++ ) {
        final GradientPaint gradientPaint = new GradientPaint( new Point2D.Double( widthIncrement * i, 0 ),
          gradientColors[ i ], new Point2D.Double( ( i + 1 ) * widthIncrement, 0 ), gradientColors[ i + 1 ] );
        graphics2D.setPaint( gradientPaint );
        graphics2D.fillRect( i * widthIncrement, 0, widthIncrement, getHeight() );
      }
    }

    super.paintComponent( g );
  }


}

