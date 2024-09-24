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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.truetype;

import org.pentaho.reporting.libraries.fonts.LibFontsDefaults;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

import java.io.IOException;


/**
 * This is the scalable backend for truetype fonts. To make any use of it, you have to apply the font size to these
 * metrics.
 *
 * @author Thomas Morgner
 */
public class ScalableTrueTypeFontMetrics {
  private TrueTypeFont font;
  private long ascent;
  private long descent;
  private long leading;
  private long xHeight;
  private long strikethroughPosition;

  private long maxAscent;
  private long maxDescent;

  private long underlinePosition;
  private long italicAngle;
  private long maxCharAdvance;

  public ScalableTrueTypeFontMetrics( final TrueTypeFont font )
    throws IOException {
    if ( font == null ) {
      throw new NullPointerException( "The font must not be null" );
    }
    this.font = font;
    final FontHeaderTable head = (FontHeaderTable) font.getTable( FontHeaderTable.TABLE_ID );
    if ( head == null ) {
      throw new IllegalStateException( "Font has no HEAD table and is not a usable font." );
    }
    final int unitsPerEm = head.getUnitsPerEm();
    final long strictScaleFactor = FontStrictGeomUtility.toInternalValue( 1 );
    maxAscent = ( strictScaleFactor * head.getyMax() ) / unitsPerEm;
    maxDescent = ( strictScaleFactor * -head.getyMin() ) / unitsPerEm;
    // prefer the mac table, as at least for the old version of Arial
    // I use, the mac table is consistent with the Java-Font-Metrics
    final HorizontalHeaderTable hhea = (HorizontalHeaderTable) font.getTable( HorizontalHeaderTable.TABLE_ID );
    if ( hhea == null ) {
      throw new IllegalStateException( "The font has no HHEA table and is not a valid font." );
    }
    // Mac metrics must always be present..
    createMacMetrics( hhea, unitsPerEm, strictScaleFactor );

    final OS2Table table = (OS2Table) font.getTable( OS2Table.TABLE_ID );
    if ( table != null ) {
      computeWindowsMetrics( table, unitsPerEm, strictScaleFactor );
    }

    final PostscriptInformationTable postTable =
      (PostscriptInformationTable) font.getTable( PostscriptInformationTable.TABLE_ID );
    if ( postTable != null ) {
      this.italicAngle = FontStrictGeomUtility.toInternalValue( postTable.getItalicAngle() );
      this.underlinePosition = getAscent() +
        ( strictScaleFactor * ( -postTable.getUnderlinePosition() + ( postTable.getUnderlineThickness() / 2 ) ) )
          / unitsPerEm;
    }

    font.dispose();
  }


  private void createMacMetrics( final HorizontalHeaderTable hhea,
                                 final int unitsPerEm,
                                 final long scaleFactor ) {
    this.maxCharAdvance = ( scaleFactor * hhea.getMaxAdvanceWidth() ) / unitsPerEm;
    this.ascent = ( scaleFactor * hhea.getAscender() ) / unitsPerEm;
    this.descent = ( scaleFactor * -hhea.getDescender() ) / unitsPerEm;
    this.leading = ( scaleFactor * hhea.getLineGap() ) / unitsPerEm;
    this.xHeight = (long) ( ascent * LibFontsDefaults.DEFAULT_XHEIGHT_SIZE / LibFontsDefaults.DEFAULT_ASCENT_SIZE );
    this.strikethroughPosition =
      getMaxAscent() - (long) ( this.xHeight * LibFontsDefaults.DEFAULT_STRIKETHROUGH_POSITION );
    this.italicAngle = FontStrictGeomUtility.toInternalValue
      ( -StrictMath.atan2( hhea.getCaretSlopeRun(), hhea.getCaretSlopeRise() ) * 180 / Math.PI );
  }

  private void computeWindowsMetrics( final OS2Table table,
                                      final int unitsPerEm,
                                      final long scaleFactor ) {
    final short xHeightRaw = table.getxHeight();
    if ( xHeightRaw != 0 ) {
      this.xHeight = ( scaleFactor * xHeightRaw ) / unitsPerEm;
    }

    final short strikethroughPosition = table.getyStrikeoutPosition();
    if ( strikethroughPosition != 0 ) {
      this.strikethroughPosition = getMaxAscent() - ( scaleFactor * strikethroughPosition / unitsPerEm );
    }
  }

  /**
   * From the baseline to the
   *
   * @return
   */
  public long getAscent() {
    return ascent;
  }

  public long getDescent() {
    return descent;
  }

  public long getLeading() {
    return leading;
  }

  public long getXHeight() {
    return xHeight;
  }

  public long getUnderlinePosition() {
    return underlinePosition;
  }

  public long getStrikeThroughPosition() {
    return strikethroughPosition;
  }

  public TrueTypeFont getFont() {
    return font;
  }

  public long getItalicAngle() {
    return italicAngle;
  }

  public long getMaxAscent() {
    return maxAscent;
  }

  public long getMaxDescent() {
    return maxDescent;
  }

  public long getMaxCharAdvance() {
    return maxCharAdvance;
  }
}
