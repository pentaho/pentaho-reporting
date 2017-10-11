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

import org.pentaho.reporting.libraries.pixie.wmf.GDIColor;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogPen;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * The CreatePenIndirect function creates a logical cosmetic pen that has the style, width, and color specified in a
 * structure.
 * <p/>
 * <pre>
 * typedef struct tagLOGPEN {
 * UINT     lopnStyle;
 * POINT    lopnWidth;
 * COLORREF lopnColor;
 * } LOGPEN, *PLOGPEN;
 * </pre>
 */
public class MfCmdCreatePen extends MfCmd {
  private static final int RECORD_SIZE = 4;
  private static final int POS_STYLE = 0;
  private static final int POS_WIDTH = 1;
  private static final int POS_COLOR = 2;

  private int style;
  private Color color;
  private int width;
  private int scaled_width;

  public MfCmdCreatePen() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final MfLogPen lpen = new MfLogPen();
    lpen.setStyle( getStyle() );
    lpen.setColor( getColor() );
    lpen.setWidth( getScaledWidth() );

    file.getCurrentState().setLogPen( lpen );
    file.storeObject( lpen );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdCreatePen();
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.CREATE_PEN_INDIRECT;
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( POS_STYLE, getStyle() );
    record.setParam( POS_WIDTH, getWidth() );
    record.setLongParam( POS_COLOR, GDIColor.translateColor( getColor() ) );
    return record;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int style = record.getParam( POS_STYLE );
    final int width = record.getParam( POS_WIDTH );
    final int color = record.getLongParam( POS_COLOR );

    setStyle( style );
    setWidth( width );
    setColor( new GDIColor( color ) );
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[CREATE_PEN] style=" );
    b.append( getStyle() );
    b.append( " width=" );
    b.append( getWidth() );
    b.append( " color=" );
    b.append( getColor() );
    return b.toString();
  }


  public int getStyle() {
    return style;
  }

  public void setStyle( final int style ) {
    this.style = style;
  }

  public int getScaledWidth() {
    return scaled_width;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth( final int width ) {
    this.width = width;
    scaleXChanged();
  }

  public Color getColor() {
    return color;
  }

  public void setColor( final Color c ) {
    this.color = c;
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_width = getScaledX( width );
  }

}
