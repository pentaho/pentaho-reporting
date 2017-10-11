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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.pixie.wmf.BrushConstants;
import org.pentaho.reporting.libraries.pixie.wmf.GDIColor;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogBrush;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * The CreateBrushIndirect function creates a logical brush that has the specified style, color, and pattern.
 * <p/>
 * The style is one of the BS_* constants defined in {@link org.pentaho.reporting.libraries.pixie.wmf.BrushConstants}.
 * The hatch is one of the HS_* constants defined in {@link org.pentaho.reporting.libraries.pixie.wmf.BrushConstants}.
 * <p/>
 * The record size is variable. First parameter defines the style, n next parameters define the color table for the
 * brush and the last parameter defines the hatch.
 * <p/>
 * todo reimplement this record type for all brushes..
 */
public class MfCmdCreateBrush extends MfCmd {
  private static final Log logger = LogFactory.getLog( MfCmdCreateBrush.class );

  private static final int PARAM_STYLE = 0;
  private static final int PARAM_COLOR = 1;
  private static final int PARAM_HATCH = 3;

  private static final int RECORD_SIZE = 4;


  private int style;
  private Color color;
  private int hatch;

  public MfCmdCreateBrush() {
  }

  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( "[CREATE_BRUSH] style=" );
    b.append( getStyle() );
    b.append( " color=" );
    b.append( getColor() );
    b.append( " hatch=" );
    b.append( getHatch() );
    return b.toString();
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final MfLogBrush lbrush = new MfLogBrush();
    lbrush.setStyle( getStyle() );
    lbrush.setColor( getColor() );
    lbrush.setHatchedStyle( getHatch() );

    file.getCurrentState().setLogBrush( lbrush );
    file.storeObject( lbrush );
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdCreateBrush();
  }

  public void setStyle( final int style ) {
    this.style = style;
  }

  public int getStyle() {
    return style;
  }

  public void setHatch( final int hatch ) {
    if ( hatch != BrushConstants.BS_DIBPATTERN &&
      hatch != BrushConstants.BS_DIBPATTERN8X8 &&
      hatch != BrushConstants.BS_DIBPATTERNPT &&
      hatch != BrushConstants.BS_HATCHED &&
      hatch != BrushConstants.BS_HOLLOW &&
      hatch != BrushConstants.BS_INDEXED &&
      hatch != BrushConstants.BS_MONOPATTERN &&
      // hatch != BrushConstants.BS_NULL &&  // BS_NULL is the same as BS_HOLLOW
      hatch != BrushConstants.BS_PATTERN &&
      hatch != BrushConstants.BS_PATTERN8X8 &&
      hatch != BrushConstants.BS_SOLID ) {
      throw new IllegalArgumentException( "The specified pattern is invalid" );
    }

    this.hatch = hatch;
  }

  public int getHatch() {
    return hatch;
  }

  public void setColor( final Color c ) {
    this.color = c;
  }

  public Color getColor() {
    return color;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.CREATE_BRUSH_INDIRECT;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    if ( record.getLength() != 14 ) {
      logger.warn( "Unknown type of CreateBrushIndirect encountered." );
    }
    final int style = record.getParam( PARAM_STYLE );
    final int color = record.getLongParam( PARAM_COLOR );
    final int hatch = record.getParam( PARAM_HATCH );
    setStyle( style );
    setColor( new GDIColor( color ) );
    setHatch( hatch );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( PARAM_STYLE, getStyle() );
    record.setLongParam( PARAM_COLOR, GDIColor.translateColor( getColor() ) );
    record.setParam( PARAM_HATCH, getHatch() );
    return record;
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted. This method is not used.
   */
  protected void scaleXChanged() {
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted. The method is not used.
   */
  protected void scaleYChanged() {
  }

}
