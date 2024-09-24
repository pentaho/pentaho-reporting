/*
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
* Copyright (c) 2008 - 2009 Larry Ogrodnek, Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.libsparklines;

import org.pentaho.reporting.libraries.libsparklines.util.GraphUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * A very fast and very simple bar-graph drawable. This code is based on the BarGraph class writen by Larry Ogrodnek but
 * instead of producing a low-resolution image, this class writes the content into a Graphics2D context.
 *
 * @author Thomas Morgner
 */
public class BarGraphDrawable {
  private static final int DEFAULT_SPACING = 2;

  private static final Color DEFAULT_COLOR = Color.gray;
  private static final Color DEFAULT_HIGH_COLOR = Color.black;
  private static final Color DEFAULT_LAST_COLOR = Color.red;
  private static final Number[] EMPTY = new Number[ 0 ];

  private Number[] data;
  private Color color;
  private Color highColor;
  private Color lastColor;
  private Color background;
  private int spacing;

  /**
   * Creates a default bargraph drawable with some sensible default colors and spacings.
   */
  public BarGraphDrawable() {
    this.highColor = BarGraphDrawable.DEFAULT_HIGH_COLOR;
    this.lastColor = BarGraphDrawable.DEFAULT_LAST_COLOR;
    this.color = BarGraphDrawable.DEFAULT_COLOR;
    this.spacing = BarGraphDrawable.DEFAULT_SPACING;
    this.data = EMPTY;
  }

  /**
   * Returns the numeric data for the drawable or null, if the drawable has no data.
   *
   * @return the data.
   */
  public Number[] getData() {
    return (Number[]) data.clone();
  }

  /**
   * Defines the numeric data for the drawable or null, if the drawable has no data.
   *
   * @param data the data (can be null).
   */
  public void setData( final Number[] data ) {
    this.data = (Number[]) data.clone();
  }

  /**
   * Returns the main color for the bars.
   *
   * @return the main color for the bars, never null.
   */
  public Color getColor() {
    return color;
  }

  /**
   * Defines the main color for the bars.
   *
   * @param color the main color for the bars, never null.
   */
  public void setColor( final Color color ) {
    if ( color == null ) {
      throw new NullPointerException();
    }
    this.color = color;
  }

  /**
   * Returns the color for the highest bars. This property is optional and the high-color can be null.
   *
   * @return the color for the highest bars, or null if high bars should not be marked specially.
   */
  public Color getHighColor() {
    return highColor;
  }

  /**
   * Defines the color for the highest bars. This property is optional and the high-color can be null.
   *
   * @param highColor the color for the highest bars, or null if high bars should not be marked specially.
   */
  public void setHighColor( final Color highColor ) {
    this.highColor = highColor;
  }

  /**
   * Returns the color for the last bar. This property is optional and the last-bar-color can be null.
   *
   * @return the color for the last bar in the graph, or null if last bars should not be marked specially.
   */
  public Color getLastColor() {
    return lastColor;
  }

  /**
   * Defines the color for the last bar. This property is optional and the last-bar-color can be null.
   *
   * @param lastColor the color for the last bar in the graph, or null if last bars should not be marked specially.
   */
  public void setLastColor( final Color lastColor ) {
    this.lastColor = lastColor;
  }

  /**
   * Returns the color for the background of the graph. This property can be null, in which case the bar will have a
   * transparent background.
   *
   * @return color for the background or null, if the graph has a transparent background color.
   */
  public Color getBackground() {
    return background;
  }

  /**
   * Defines the color for the background of the graph. This property can be null, in which case the bar will have a
   * transparent background.
   *
   * @param background the background or null, if the graph has a transparent background color.
   */
  public void setBackground( final Color background ) {
    this.background = background;
  }

  /**
   * Returns the spacing between the bars.
   *
   * @return the spacing between the bars.
   */
  public int getSpacing() {
    return spacing;
  }

  /**
   * Defines the spacing between the bars.
   *
   * @param spacing the spacing between the bars.
   */
  public void setSpacing( final int spacing ) {
    this.spacing = spacing;
  }

  /**
   * Draws the bar-graph into the given Graphics2D context in the given area. This method will not draw a graph if the
   * data given is null or empty.
   *
   * @param g2       the graphics context on which the bargraph should be rendered.
   * @param drawArea the area on which the bargraph should be drawn.
   */
  public void draw( Graphics2D g2, Rectangle2D drawArea ) {
    if ( g2 == null ) {
      throw new NullPointerException();
    }
    if ( drawArea == null ) {
      throw new NullPointerException();
    }

    final int height = (int) drawArea.getHeight();
    if ( height <= 0 ) {
      return;
    }

    final Graphics2D g = (Graphics2D) g2.create();
    g.translate( drawArea.getX(), drawArea.getY() );
    if ( background != null ) {
      g.setBackground( background );
      g.clearRect( 0, 0, (int) drawArea.getWidth(), height );
    }

    if ( data == null || data.length == 0 ) {
      g.dispose();
      return;
    }

    final float scale = GraphUtils.getDivisor( data, height );
    final float axe = GraphUtils.getAxe( data ) / scale;
    final float avg = computeAverage( data );

    final float spacing1 = getSpacing();
    final float barWidth = (float) ( ( drawArea.getWidth() - ( spacing1 * data.length ) ) / (float) data.length );

    float x = 0;

    final double canvasHeight = drawArea.getHeight();
    final Rectangle2D.Double bar = new Rectangle2D.Double();
    for ( int index = 0; index < data.length; index++ ) {
      final Number value = data[ index ];
      if ( value == null ) {
        x += ( barWidth + spacing1 );
        continue;
      }

      final float h = (int) ( value.doubleValue() / scale );

      final float intVal = value.floatValue();
      if ( index == ( data.length - 1 ) && lastColor != null ) {
        g.setPaint( lastColor );
      } else if ( intVal < avg || ( highColor == null ) ) {
        g.setPaint( color );
      } else {
        g.setPaint( highColor );
      }

      if ( axe == 0 ) {
        // only positive values, bottom aligned
        bar.setRect( x, ( canvasHeight - h ), barWidth, h );
      } else if ( axe < 0 ) {
        // only negative values, top aligned
        bar.setRect( x, 0, barWidth, h < 0 ? -h : h );
      } else {
        // mixed values, middle aligned
        //{-1|-2|-3|-4|0|1|2|3|4|5}
        bar.setRect( x, h < 0 ? axe : axe - h, barWidth, h < 0 ? -h : h );
      }
      g.fill( bar );
      x += ( barWidth + spacing1 );
    }

    g.dispose();
  }

  /**
   * Computes the average for all numbers in the array.
   *
   * @param data the numbers for which the average should be computed.
   * @return the average.
   */
  private static float computeAverage( final Number[] data ) {
    int total = 0;
    int length = 0;
    for ( int index = 0; index < data.length; index++ ) {
      final Number i = data[ index ];
      if ( i == null ) {
        continue;
      }

      total += i.floatValue();
      length += 1;
    }

    return ( total / length );
  }

}
