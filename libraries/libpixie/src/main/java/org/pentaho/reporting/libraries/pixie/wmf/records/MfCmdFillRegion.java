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


package org.pentaho.reporting.libraries.pixie.wmf.records;

import org.pentaho.reporting.libraries.pixie.wmf.MfDcState;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogBrush;
import org.pentaho.reporting.libraries.pixie.wmf.MfLogRegion;
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The FillRgn function fills a region by using the specified brush.
 */
public class MfCmdFillRegion extends MfCmd {
  private static final int RECORD_SIZE = 2;
  private static final int POS_REGION = 0;
  private static final int POS_BRUSH = 1;

  private int brushObjectNr;
  private int regionObjectNr;

  public MfCmdFillRegion() {
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    final MfLogBrush brush = file.getBrushObject( brushObjectNr );
    final MfLogRegion regio = file.getRegionObject( regionObjectNr );

    final MfDcState state = file.getCurrentState();
    state.setLogRegion( regio );
    state.setLogBrush( brush );

    final Graphics2D graph = file.getGraphics2D();
    final Rectangle rec = scaleRect( regio.getBounds() );

    final Rectangle2D rect = new Rectangle2D.Double();
    rect.setFrame( rec.x, rec.y, rec.width, rec.height );

    if ( brush.isVisible() ) {
      state.preparePaint();
      graph.fill( rect );
      state.postPaint();
    }
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdFillRegion();
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * After the raw record was read from the datasource, the record is parsed by the concrete implementation.
   *
   * @param record the raw data that makes up the record.
   */
  public void setRecord( final MfRecord record ) {
    final int regio = record.getParam( POS_REGION );
    final int brush = record.getParam( POS_BRUSH );
    setBrush( brush );
    setRegion( regio );
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord() {
    final MfRecord record = new MfRecord( RECORD_SIZE );
    record.setParam( POS_REGION, getRegion() );
    record.setParam( POS_BRUSH, getBrush() );
    return record;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[FILL_REGION] brush=" );
    b.append( getBrush() );
    b.append( " region=" );
    b.append( getRegion() );
    return b.toString();
  }

  public int getBrush() {
    return brushObjectNr;
  }

  public void setBrush( final int brush ) {
    this.brushObjectNr = brush;
  }

  public int getRegion() {
    return regionObjectNr;
  }

  public void setRegion( final int region ) {
    regionObjectNr = region;
  }

  /**
   * Reads the function identifier. Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return the function identifier.
   */
  public int getFunction() {
    return MfType.FILL_REGION;
  }

  /**
   * A callback function to inform the object, that the x scale has changed and the internal coordinate values have to
   * be adjusted. Not used.
   */
  protected void scaleXChanged() {
  }

  /**
   * A callback function to inform the object, that the y scale has changed and the internal coordinate values have to
   * be adjusted. Not used.
   */
  protected void scaleYChanged() {
  }
}
