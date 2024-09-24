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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * A very fast and very simple line-graph drawable. This code is based on the LineGraph class writen by Larry Ogrodnek
 * but instead of producing a low-resolution image, this class writes the content into a Graphics2D context.
 *
 * @author Thomas Morgner
 */
public class LineGraphDrawable {
  private static final int DEFAULT_SPACING = 2;
  private static final Number[] EMPTY = new Number[ 0 ];
  private static final float LAST_POINT_RADIUS = 2.5f;
  private static final float LAST_POINT_DIAMETER = LAST_POINT_RADIUS * 2;

  private int spacing;
  private Color color;
  private Color background;
  private Color lastColor;
  private Number[] data;

  /**
   * Creates a default bargraph drawable with some sensible default colors and spacings.
   */
  public LineGraphDrawable() {
    this.color = Color.black;
    this.spacing = DEFAULT_SPACING;
    this.data = EMPTY;
  }

  /**
   * Returns the numeric data for the drawable or null, if the drawable has no data.
   *
   * @return the data.
   */
  public Number[] getData() {
    return data.clone();
  }

  /**
   * Defines the numeric data for the drawable or null, if the drawable has no data.
   *
   * @param data the data (can be null).
   */
  public void setData( final Number[] data ) {
    this.data = data.clone();
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

  public Color getLastColor() {
    return lastColor;
  }

  public void setLastColor( final Color lastColor ) {
    this.lastColor = lastColor;
  }

  /**
   * Draws the bar-graph into the given Graphics2D context in the given area. This method will not draw a graph if the
   * data given is null or empty.
   *
   * @param graphics the graphics context on which the bargraph should be rendered.
   * @param drawArea the area on which the bargraph should be drawn.
   */
  public void draw( final Graphics2D graphics, final Rectangle2D drawArea ) {
    if ( graphics == null ) {
      throw new NullPointerException();
    }
    if ( drawArea == null ) {
      throw new NullPointerException();
    }

    final float lastPointDiameter;
    final float lastPointRadius;
    if ( lastColor == null ) {
      lastPointDiameter = 0;
      lastPointRadius = 0;
    } else {
      lastPointDiameter = LAST_POINT_DIAMETER;
      lastPointRadius = LAST_POINT_RADIUS;
    }

    final int height = (int) ( drawArea.getHeight() - lastPointDiameter );
    if ( height <= 0 ) {
      return;
    }

    final Graphics2D g2 = (Graphics2D) graphics.create();
    if ( background != null ) {
      g2.setPaint( background );
      g2.draw( drawArea );
    }

    if ( data.length == 0 ) {
      g2.dispose();
      return;
    }

    g2.translate( drawArea.getX(), drawArea.getY() + lastPointRadius );

    float scale = GraphUtils.getDivisor( data, height );
    final int spacing = getSpacing();
    final float usableWidth = (float) ( drawArea.getWidth() - lastPointRadius );
    final float width = ( usableWidth - spacing * ( data.length - 1 ) ) / ( data.length - 1 );

    float min = computeMin();

    int x = 0;
    int y = -1;

    if ( scale == 0.0 ) {
      //special case -- a horizontal straight line
      scale = 1.0f;
      y = -height / 2;
    }

    double lastX = 0;
    double lastY = 0;
    final Line2D.Double line = new Line2D.Double();
    g2.setPaint( color );
    for ( int i = 0; i < data.length - 1; i++ ) {
      final int px1 = x;
      x += ( width + spacing );
      final int px2 = x;

      final Number number = data[ i ];
      final Number nextNumber = data[ i + 1 ];
      if ( number == null && nextNumber == null ) {
        final float delta = height - ( ( 0 - min ) / scale );
        line.setLine( px1, y + delta, px2, y + delta );
      } else if ( number == null ) {
        line.setLine( px1, y + ( height - ( ( 0 - min ) / scale ) ),
          px2, y + ( height - ( ( nextNumber.floatValue() - min ) / scale ) ) );
      } else if ( nextNumber == null ) {
        line.setLine( px1, y + ( height - ( ( number.floatValue() - min ) / scale ) ),
          px2, y + ( height - ( ( 0 - min ) / scale ) ) );
      } else {
        line.setLine( px1, y + ( height - ( ( number.floatValue() - min ) / scale ) ),
          px2, y + ( height - ( ( nextNumber.floatValue() - min ) / scale ) ) );
      }
      lastX = line.getX2();
      lastY = line.getY2();
      g2.draw( line );

    }

    if ( lastColor != null ) {
      g2.setColor( lastColor );
      g2.fill( new Ellipse2D.Double
        ( lastX - LAST_POINT_RADIUS, lastY - LAST_POINT_RADIUS, LAST_POINT_DIAMETER, LAST_POINT_DIAMETER ) );
    }
    g2.dispose();

  }

  private float computeMin() {
    float min = Float.MAX_VALUE;
    for ( int index = 0; index < data.length; index++ ) {
      final Number i = data[ index ];
      if ( i == null ) {
        continue;
      }
      final float value = i.floatValue();
      if ( value < min ) {
        min = value;
      }
    }
    return min;
  }
}
