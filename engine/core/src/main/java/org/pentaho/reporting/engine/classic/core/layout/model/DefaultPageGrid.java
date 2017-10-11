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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Creation-Date: 05.04.2007, 16:15:32
 *
 * @author Thomas Morgner
 */
public class DefaultPageGrid implements PageGrid {
  private long[] horizontalBreaks;
  private long[] verticalBreaks;
  private long[] horizontalBreaksFull;
  private long[] verticalBreaksFull;
  private PageFormat[][] pageMapping;

  public DefaultPageGrid( final PageDefinition pageDefinition ) {
    final Rectangle2D[] pagePositions = pageDefinition.getPagePositions();

    final TreeSet horizontalPositions = new TreeSet();
    final TreeSet verticalPositions = new TreeSet();

    final int pagePosCount = pagePositions.length;
    for ( int i = 0; i < pagePosCount; i++ ) {
      final Rectangle2D pagePosition = pagePositions[i];

      final double minX = pagePosition.getMinX();
      final double maxX = pagePosition.getMaxX();
      final double minY = pagePosition.getMinY();
      final double maxY = pagePosition.getMaxY();

      if ( minX == maxX || maxY == minY ) {
        throw new IllegalArgumentException( "This page format is invalid, it has no imageable area." );
      }
      horizontalPositions.add( new Double( minX ) );
      horizontalPositions.add( new Double( maxX ) );
      verticalPositions.add( new Double( minY ) );
      verticalPositions.add( new Double( maxY ) );
    }

    horizontalBreaksFull = new long[horizontalPositions.size()];
    int pos = 0;
    for ( Iterator iterator = horizontalPositions.iterator(); iterator.hasNext(); ) {
      final Double value = (Double) iterator.next();
      horizontalBreaksFull[pos] = StrictGeomUtility.toInternalValue( value.doubleValue() );
      pos += 1;
    }

    verticalBreaksFull = new long[verticalPositions.size()];
    pos = 0;
    for ( Iterator iterator = verticalPositions.iterator(); iterator.hasNext(); ) {
      final Double value = (Double) iterator.next();
      verticalBreaksFull[pos] = StrictGeomUtility.toInternalValue( value.doubleValue() );
      pos += 1;
    }

    horizontalPositions.remove( new Double( 0 ) );
    verticalPositions.remove( new Double( 0 ) );

    horizontalBreaks = new long[horizontalPositions.size()];
    pos = 0;
    for ( Iterator iterator = horizontalPositions.iterator(); iterator.hasNext(); ) {
      final Double value = (Double) iterator.next();
      horizontalBreaks[pos] = StrictGeomUtility.toInternalValue( value.doubleValue() );
      pos += 1;
    }

    verticalBreaks = new long[verticalPositions.size()];
    pos = 0;
    for ( Iterator iterator = verticalPositions.iterator(); iterator.hasNext(); ) {
      final Double value = (Double) iterator.next();
      verticalBreaks[pos] = StrictGeomUtility.toInternalValue( value.doubleValue() );
      pos += 1;
    }

    final int hbreakLength = horizontalBreaksFull.length;
    final int vbreakLength = verticalBreaksFull.length;
    pageMapping = new PageFormat[vbreakLength - 1][hbreakLength - 1];
    for ( int col = 0; col < hbreakLength; col++ ) {
      final long xPosition = horizontalBreaksFull[col];
      for ( int row = 0; row < vbreakLength; row++ ) {
        final long yPosition = verticalBreaksFull[row];
        final int idx = findPageFormat( pagePositions, xPosition, yPosition );
        if ( idx >= 0 ) {
          pageMapping[row][col] = pageDefinition.getPageFormat( idx );
        }
      }
    }
  }

  private int findPageFormat( final Rectangle2D[] positions, final long xPosition, final long yPosition ) {
    final int posCount = positions.length;
    for ( int i = 0; i < posCount; i++ ) {
      final Rectangle2D rect = positions[i];
      if ( StrictGeomUtility.toInternalValue( rect.getMinY() ) == yPosition
          && StrictGeomUtility.toInternalValue( rect.getMinX() ) == xPosition ) {
        return i;
      }
    }
    return -1;
  }

  /**
   * In case of overlapping pageboxes, this method may return null.
   *
   * @param row
   * @param col
   * @return
   */
  public PhysicalPageBox getPage( final int row, final int col ) {
    final long offsetX = horizontalBreaksFull[col];
    final long offsetY = verticalBreaksFull[row];

    final PageFormat format = pageMapping[row][col];
    return new PhysicalPageBox( format, offsetX, offsetY );
  }

  public long[] getHorizontalBreaks() {
    return (long[]) horizontalBreaks.clone();
  }

  public long[] getVerticalBreaks() {
    return (long[]) verticalBreaks.clone();
  }

  public int getRowCount() {
    return verticalBreaks.length;
  }

  public int getColumnCount() {
    return horizontalBreaks.length;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public long getMaximumPageWidth() {
    return horizontalBreaks[horizontalBreaks.length - 1];
  }

  public long getMaximumPageHeight() {
    return verticalBreaks[verticalBreaks.length - 1];
  }
}
