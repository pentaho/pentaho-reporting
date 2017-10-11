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

import org.pentaho.reporting.libraries.pixie.wmf.MfDcState;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.ROPConstants;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

public class MfCmdPatBlt extends MfCmd {
  private static final int RECORD_SIZE = 6;
  private static final int POS_ROP = 0;
  private static final int POS_HEIGHT = 2;
  private static final int POS_WIDTH = 3;
  private static final int POS_Y = 4;
  private static final int POS_X = 5;

  private int rop;
  private int height;
  private int width;
  private int x;
  private int y;
  private int scaled_x;
  private int scaled_y;
  private int scaled_width;
  private int scaled_height;

  public MfCmdPatBlt() {
  }

  public void replay( final WmfFile file ) {
    switch( rop ) {
      case ROPConstants.PATCOPY: {
        final MfDcState state = file.getCurrentState();
        state.preparePaint();
        final Graphics2D g2 = (Graphics2D) file.getGraphics2D().create();
        g2.setPaintMode();
        g2.fill( getScaledBounds() );
        state.postPaint();
        break;
      }
      case ROPConstants.PATINVERT: {
        final MfDcState state = file.getCurrentState();
        state.preparePaint();
        final Graphics2D g2 = (Graphics2D) file.getGraphics2D().create();
        g2.setXORMode( g2.getColor() );
        g2.fill( getScaledBounds() );
        state.postPaint();
        break;
      }
      case ROPConstants.DSTINVERT: {
        final Graphics2D g2 = (Graphics2D) file.getGraphics2D().create();
        g2.setXORMode( Color.white );
        g2.fill( getScaledBounds() );
        break;
      }
      case ROPConstants.BLACKNESS: {
        // todo implement me when Palettes are implemented
        break;
      }
      case ROPConstants.WHITENESS: {
        // todo implement me when Palettes are implemented
        break;
      }
    }
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdPatBlt();
  }

  public int getROP() {
    return rop;
  }

  public void setROP( final int rop ) {
    this.rop = rop;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int rop = record.getLongParam( POS_ROP );
    final int height = record.getParam( POS_HEIGHT );
    final int width = record.getParam( POS_WIDTH );
    final int top = record.getParam( POS_X );
    final int left = record.getParam( POS_Y );
    setBounds( left, top, width, height );
    setROP( rop );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( POS_ROP, getROP() );
    final Rectangle bounds = getBounds();
    record.setParam( POS_HEIGHT, bounds.height );
    record.setParam( POS_WIDTH, bounds.width );
    record.setParam( POS_Y, bounds.y );
    record.setParam( POS_X, bounds.x );
    return record;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[PAT_BLT] rop=" );
    b.append( getROP() );
    b.append( " bounds=" );
    b.append( getBounds() );
    return b.toString();
  }

  public Rectangle getBounds() {
    return new Rectangle( x, y, width, height );
  }

  public Rectangle getScaledBounds() {
    return new Rectangle( scaled_x, scaled_y, scaled_width, scaled_height );
  }

  public void setBounds( final int x, final int y, final int width, final int height ) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
    scaleXChanged();
    scaleYChanged();

  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleXChanged() {
    scaled_x = getScaledX( x );
    scaled_width = getScaledX( width );
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted.
   */
  protected void scaleYChanged() {
    scaled_y = getScaledY( y );
    scaled_height = getScaledY( height );
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.PAT_BLT;
  }
}
