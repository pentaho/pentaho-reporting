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
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * Currently i have no clue, how this is implemented. <p/> From The WINE-Sources: <p/>
 * <pre>
 * 	The layout of the record looks something like this:
 * <p/>
 * 	 rdParm	meaning
 * 	 0		Always 0?
 * 	 1		Always 6?
 * 	 2		Looks like a handle? - not constant
 * 	 3		0 or 1 ??
 * 	 4		Total number of bytes
 * 	 5		No. of separate bands = n [see below]
 * 	 6		Largest number of x co-ords in a band
 * 	 7-10		Bounding box x1 y1 x2 y2
 * 	 11-...		n bands
 * <p/>
 * 	 Regions are divided into bands that are uniform in the
 * 	 y-direction. Each band consists of pairs of on/off x-coords and is
 * 	 written as
 * 		m y0 y1 x1 x2 x3 ... xm m
 * 	 into successive rdParm[]s.
 * <p/>
 * 	 This is probably just a dump of the internal RGNOBJ?
 * </pre>
 * <p/>
 * <pre>
 * static BOOL MF_Play_MetaCreateRegion( METARECORD *mr, HRGN hrgn )
 * {
 * WORD band, pair;
 * WORD *start, *end;
 * INT16 y0, y1;
 * HRGN hrgn2 = CreateRectRgn( 0, 0, 0, 0 );
 * <p/>
 * for(band  = 0, start = &(mr->rdParm[11]);
 * band < mr->rdParm[5];
 * band++, start = end + 1)
 * {
 * if(*start / 2 != (*start + 1) / 2)
 * {
 * WARN("Delimiter not even.\n");
 * DeleteObject( hrgn2 );
 * return FALSE;
 * }
 * <p/>
 * end = start + *start + 3;
 * if(end > (WORD *)mr + mr->rdSize)
 * {
 * WARN("End points outside record.\n");
 * DeleteObject( hrgn2 );
 * return FALSE;
 * }
 * <p/>
 * if(*start != *end)
 * {
 * WARN("Mismatched delimiters.\n");
 * DeleteObject( hrgn2 );
 * return FALSE;
 * }
 * <p/>
 * y0 = *(INT16 *)(start + 1);
 * y1 = *(INT16 *)(start + 2);
 * for(pair = 0; pair < *start / 2; pair++)
 * {
 * SetRectRgn( hrgn2, *(INT16 *)(start + 3 + 2*pair), y0,
 * (INT16 *)(start + 4 + 2*pair), y1 );
 * CombineRgn(hrgn, hrgn, hrgn2, RGN_OR);
 * }
 * }
 * DeleteObject( hrgn2 );
 * return TRUE;
 * }
 * </pre>
 * </p>
 */
public class MfCmdCreateRegion extends MfCmd {
  private int regionX;
  private int regionY;
  private int regionWidth;
  private int regionHeight;
  private Rectangle[] rects;

  public MfCmdCreateRegion() {
  }

  public void setRecord( final MfRecord record ) {
    // System.out.println("Create Region is not implemented.");

  }

  /**
   * Writer function
   */
  public MfRecord getRecord() {

    final MfRecord record = new MfRecord( 0 );
    record.setParam( 0, 0 );
    record.setParam( 1, 6 );
    record.setParam( 2, 0x1234 );
    record.setParam( 3, 0 );
    record.setParam( 4, 0 ); // Length
    record.setParam( 5, 0 ); // Bands
    record.setParam( 6, 0 ); // Max-Bands
    record.setParam( 7, regionX );
    record.setParam( 8, regionY );
    record.setParam( 9, regionX + regionWidth );
    record.setParam( 10, regionY + regionHeight );

    // some more data ... a array of rectangles (16bit x 4)
    // which makes up the defined region
    // the rectangles are sorted and seem to be packed in some way
    // todo: Not complete ..
    return record;
  }

  public String toString() {
    final StringBuffer b = new StringBuffer();
    b.append( "[CREATE_REGION] " );
    b.append( " no internals known (see WINE for details)" );
    return b.toString();
  }


  public int getFunction() {
    return MfType.CREATE_REGION;
  }

  public void replay( final WmfFile file ) {
  }

  public MfCmd getInstance() {
    return new MfCmdCreateRegion();
  }

  protected void scaleXChanged() {
  }

  protected void scaleYChanged() {
  }

  public int getRegionX() {
    return regionX;
  }

  public void setRegionX( final int regionX ) {
    this.regionX = regionX;
  }

  public int getRegionY() {
    return regionY;
  }

  public void setRegionY( final int regionY ) {
    this.regionY = regionY;
  }

  public int getRegionWidth() {
    return regionWidth;
  }

  public void setRegionWidth( final int regionWidth ) {
    this.regionWidth = regionWidth;
  }

  public int getRegionHeight() {
    return regionHeight;
  }

  public void setRegionHeight( final int regionHeight ) {
    this.regionHeight = regionHeight;
  }

  public Rectangle[] getRects() {
    return rects;
  }

  public void setRects( final Rectangle[] rects ) {
    this.rects = rects;
  }
}
