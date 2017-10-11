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

package org.pentaho.reporting.libraries.pixie.wmf.records;

import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * This is the base class for all WMF-Records. A WMF record specifies a single command for drawing a element of the
 * image.
 */
public abstract class MfCmd {
  /**
   * The X-Scale for the command.
   */
  private float scaleX;
  /**
   * The Y-Scale for the command.
   */
  private float scaleY;

  /**
   * The default constructor, adjusts the scale to 1.
   */
  protected MfCmd() {
    scaleX = 1;
    scaleY = 1;
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   * @throws RecordCreationException if an error occured while generating the record.
   */
  public abstract MfRecord getRecord()
    throws RecordCreationException;

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public abstract void setRecord( MfRecord record );

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public abstract int getFunction();

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public abstract MfCmd getInstance();

  /**
   * Replays the command on the given WmfFile.
   *
   * @param metafile the meta file.
   */
  public abstract void replay( WmfFile metafile );

  /**
   * Set the scale for the command.
   *
   * @param scaleX the horizontal scale
   * @param scaleY the vertical scale
   */
  public void setScale( final float scaleX, final float scaleY ) {
    final float oldScaleX = this.scaleX;
    final float oldScaleY = this.scaleY;
    this.scaleX = scaleX;
    this.scaleY = scaleY;
    if ( oldScaleX != scaleX ) {
      scaleXChanged();
    }
    if ( oldScaleY != scaleY ) {
      scaleYChanged();
    }
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected abstract void scaleXChanged();

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected abstract void scaleYChanged();

  /**
   * Scales the given rectangle.
   *
   * @param r the source rectangle.
   * @return a new rectangle containing the scaled values.
   */
  protected Rectangle scaleRect( final Rectangle r ) {
    final Rectangle retval = new Rectangle();
    retval.x = getScaledX( r.x );
    retval.y = getScaledY( r.y );

    retval.width = getScaledWidth( r.width );
    retval.height = getScaledHeight( r.height );
    return retval;
  }

  /**
   * Scales the given horizontal length and makes sure that the lenght is at least 1.
   *
   * @param length the value that should be scaled.
   * @return the scaled value.
   */
  protected int getScaledWidth( int length ) {
    if ( length == 0 ) {
      return 1;
    }
    length = (int) ( length * scaleX + 0.5f );
    return ( length == 0 ) ? 1 : length;
  }

  /**
   * Scales the given vertical length and makes sure that the lenght is at least 1.
   *
   * @param length the value that should be scaled.
   * @return the scaled value.
   */
  protected int getScaledHeight( int length ) {
    if ( length == 0 ) {
      return 1;
    }
    length = (int) ( length * scaleY + 0.5f );
    return ( length == 0 ) ? 1 : length;
  }

  /**
   * Applies the new x-scaling to all values in the array n and places the values in the array dest. Additionally dest
   * is also returned as return value.
   *
   * @param n    the unscaled source values
   * @param dest the array to store the scaled values
   * @return dest.
   */
  protected int[] applyScaleX( final int[] n, int[] dest ) {
    if ( dest == null ) {
      dest = new int[ n.length ];
    } else if ( dest.length < n.length ) {
      dest = new int[ n.length ];
    }

    for ( int i = 0; i < n.length; i++ ) {
      dest[ i ] = (int) ( n[ i ] * scaleX + 0.5f );
    }
    return dest;
  }

  /**
   * Applies the new y-scaling to all values in the array n and places the values in the array dest. Additionally dest
   * is also returned as return value.
   *
   * @param n    the unscaled source values
   * @param dest the array to store the scaled values
   * @return dest.
   */
  protected int[] applyScaleY( final int[] n, int[] dest ) {
    if ( dest == null ) {
      dest = new int[ n.length ];
    } else if ( dest.length < n.length ) {
      dest = new int[ n.length ];
    }

    for ( int i = 0; i < n.length; i++ ) {
      dest[ i ] = (int) ( n[ i ] * scaleY + 0.5f );
    }
    return dest;
  }

  /**
   * Return integer scaled to output units.
   *
   * @param y the unscaled y
   * @return the scaled y value
   */
  public int getScaledY( final int y ) {
    return (int) ( y * scaleY + 0.5f );
  }

  /**
   * Return integer scaled to output units.
   *
   * @param x the unscaled x
   * @return the scaled x value
   */
  public int getScaledX( final int x ) {
    return (int) ( x * scaleX + 0.5f );
  }

}
