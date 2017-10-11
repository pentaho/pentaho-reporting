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

package org.pentaho.reporting.engine.classic.core;

import java.awt.Insets;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

/**
 * A simple page definition defines a logical page, for which all physical pages have the same size.
 * <p/>
 * The page area is a rectangle.
 *
 * @author Thomas Morgner
 */
public class SimplePageDefinition implements PageDefinition {
  /**
   * The page format.
   */
  private transient PageFormat format;
  /**
   * The page positions of the physical pages.
   */
  private transient Rectangle2D[] pagePositions;
  /**
   * The number of columns in the page grid.
   */
  private int pageCountHorizontal;
  /**
   * The number of rows in the page grid.
   */
  private int pageCountVertical;

  /**
   * Creates a new SimplePageDefinition object.
   *
   * @param format
   *          the pageformat used as base.
   * @param x
   *          the number of physical pages in a row.
   * @param y
   *          the number of physical pages in a column.
   */
  public SimplePageDefinition( final PageFormat format, final int x, final int y ) {
    if ( format == null ) {
      throw new NullPointerException( "Format must not be null" );
    }
    if ( x < 1 ) {
      throw new IllegalArgumentException( "PageCount must be greater or equal to 1" );
    }
    if ( y < 1 ) {
      throw new IllegalArgumentException( "PageCount must be greater or equal to 1" );
    }
    this.format = (PageFormat) format.clone();
    this.pageCountHorizontal = x;
    this.pageCountVertical = y;
    this.pagePositions = new Rectangle2D[pageCountHorizontal * pageCountVertical];

    final float width = (float) format.getImageableWidth();
    final float height = (float) format.getImageableHeight();
    float pageStartY = 0;
    for ( int vert = 0; vert < pageCountVertical; vert++ ) {
      float pageStartX = 0;
      for ( int hor = 0; hor < pageCountHorizontal; hor++ ) {
        final Rectangle2D rect = new Rectangle2D.Float( pageStartX, pageStartY, width, height );
        pagePositions[vert * pageCountHorizontal + hor] = rect;
        pageStartX += width;
      }
      pageStartY += height;
    }
  }

  /**
   * Creates a 1x1 page defintion. The physical page size is equal to the logical page size.
   *
   * @param format
   *          the pageformat.
   */
  public SimplePageDefinition( final PageFormat format ) {
    this( format, 1, 1 );
  }

  /**
   * Creates a 1x1 page defintion. The physical page size is equal to the logical page size.
   */
  public SimplePageDefinition( final PageSize papersize, final int orientation, final Insets margins ) {
    this( PageFormatFactory.create( papersize, orientation, margins ), 1, 1 );
  }

  /**
   * Creates a 1x1 page defintion. The physical page size is equal to the logical page size.
   */
  public SimplePageDefinition( final PageSize papersize ) {
    this( PageFormatFactory.create( papersize, PageFormat.PORTRAIT, new Insets( 0, 0, 0, 0 ) ), 1, 1 );
  }

  /**
   * Returns the number of physical pages in this page definition.
   *
   * @return the number of pages.
   */
  public int getPageCount() {
    return pageCountHorizontal * pageCountVertical;
  }

  /**
   * Returns the physical page format for the given position.
   *
   * @param pos
   *          the position in the page grid.
   * @return a clone of the pageformat at the specified positon.
   */
  public PageFormat getPageFormat( final int pos ) {
    if ( pos < 0 || pos > getPageCount() ) {
      throw new IndexOutOfBoundsException( "Index is invalid" );
    }
    return (PageFormat) format.clone();
  }

  /**
   * Returns the printable area within the logical page area covered by the physical page at the given position.
   *
   * @param index
   *          the positon.
   * @return the printable area for the page.
   */
  public Rectangle2D getPagePosition( final int index ) {
    if ( index < 0 || index > getPageCount() ) {
      throw new IndexOutOfBoundsException( "Index is invalid" );
    }
    return pagePositions[index].getBounds2D();
  }

  /**
   * Returns all page position known to this page definition.
   *
   * @return the page positions.
   */
  public Rectangle2D[] getPagePositions() {
    final int length = pagePositions.length;
    final Rectangle2D[] rects = new Rectangle2D[length];
    for ( int i = 0; i < length; i++ ) {
      rects[i] = pagePositions[i].getBounds2D();
    }
    return rects;
  }

  /**
   * Returns the total height of the logical page.
   *
   * @return the height of the page.
   */
  public float getHeight() {
    return (float) ( format.getImageableHeight() * pageCountVertical );
  }

  /**
   * Returns the total width of the logical page.
   *
   * @return the width of the page.
   */
  public float getWidth() {
    return (float) ( format.getImageableWidth() * pageCountHorizontal );
  }

  /**
   * Deserizalize the report and restore the pageformat.
   *
   * @param out
   *          the objectoutput stream
   * @throws java.io.IOException
   *           if errors occur
   */
  private void writeObject( final ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();
    final SerializerHelper instance = SerializerHelper.getInstance();
    instance.writeObject( format, out );
    final int length = pagePositions.length;
    out.writeInt( length );
    for ( int i = 0; i < length; i++ ) {
      instance.writeObject( pagePositions[i], out );
    }
  }

  /**
   * Resolve the pageformat, as PageFormat is not serializable.
   *
   * @param in
   *          the input stream.
   * @throws java.io.IOException
   *           if there is an IO problem.
   * @throws ClassNotFoundException
   *           if there is a class problem.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    final SerializerHelper instance = SerializerHelper.getInstance();
    format = (PageFormat) instance.readObject( in );
    final int length = in.readInt();
    pagePositions = new Rectangle2D[length];
    for ( int i = 0; i < length; i++ ) {
      pagePositions[i] = (Rectangle2D) instance.readObject( in );
    }
  }

  /**
   * Creates a copy of this page definition.
   *
   * @return a clone of this page definition object.
   * @throws CloneNotSupportedException
   */
  public Object clone() throws CloneNotSupportedException {
    final SimplePageDefinition pdef = (SimplePageDefinition) super.clone();
    pdef.format = (PageFormat) format.clone();
    return pdef;
  }

  /**
   * Checks whether this page definition object is equal to the given object.
   *
   * @param obj
   *          the other object.
   * @return true, if that object is the same as this object, false otherwise.
   */
  public boolean equals( final Object obj ) {
    if ( this == obj ) {
      return true;
    }
    if ( !( obj instanceof SimplePageDefinition ) ) {
      return false;
    }

    final SimplePageDefinition simplePageDefinition = (SimplePageDefinition) obj;

    if ( pageCountHorizontal != simplePageDefinition.pageCountHorizontal ) {
      return false;
    }
    if ( pageCountVertical != simplePageDefinition.pageCountVertical ) {
      return false;
    }
    if ( !PageFormatFactory.isEqual( format, simplePageDefinition.format ) ) {
      return false;
    }
    if ( !Arrays.equals( pagePositions, simplePageDefinition.pagePositions ) ) {
      return false;
    }

    return true;
  }

  /**
   * Returns the number of horizontal pages in this page definition. That number is always greater than zero.
   *
   * @return the horizontal page count.
   */
  public int getPageCountHorizontal() {
    return pageCountHorizontal;
  }

  /**
   * Returns the number of vertical pages in this page definition. That number is always greater than zero.
   *
   * @return the vertical page count.
   */
  public int getPageCountVertical() {
    return pageCountVertical;
  }

  /**
   * Computes a hashcode for this page definition.
   *
   * @return the hashcode.
   */
  public int hashCode() {
    int result = format.hashCode();
    result = 29 * result + pageCountHorizontal;
    result = 29 * result + pageCountVertical;
    return result;
  }

  public PageFormat getPageFormat() {
    return format;
  }

  public String toString() {
    final StringBuilder sb = new StringBuilder();
    sb.append( "SimplePageDefinition" );
    sb.append( "{format=" ).append( PageFormatFactory.printPageFormat( format ) );
    sb.append( ", pagePositions=" ).append( pagePositions == null ? "null" : Arrays.asList( pagePositions ).toString() );
    sb.append( ", pageCountHorizontal=" ).append( pageCountHorizontal );
    sb.append( ", pageCountVertical=" ).append( pageCountVertical );
    sb.append( '}' );
    return sb.toString();
  }
}
