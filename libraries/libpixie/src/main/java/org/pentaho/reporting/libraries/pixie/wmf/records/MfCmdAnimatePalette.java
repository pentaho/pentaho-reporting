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
import org.pentaho.reporting.libraries.pixie.wmf.MfRecord;
import org.pentaho.reporting.libraries.pixie.wmf.MfType;
import org.pentaho.reporting.libraries.pixie.wmf.WmfFile;

import java.awt.*;

/**
 * The AnimatePalette function replaces entries in the specified logical palette.
 * <p/>
 * <pre>
 * BOOL AnimatePalette(
 *  HPALETTE hpal,           // handle to logical palette
 *  UINT iStartIndex,        // first entry in logical palette
 *  UINT cEntries,           // number of entries
 *  CONST PALETTEENTRY *ppe  // first replacement
 * );
 * </pre>
 * <p/>
 * This function is not implemented. However, you can use this implementation to create a valid record. <table
 * border="1"> <tr> <th>offset</th> <th>length in bytes</th> <th>meaning</th> </tr> <tr> <td>0x0</td> <td>4</td>
 * <td>RecordSize (variable)</td> </tr> <tr> <td>0x4</td> <td>2</td> <td>record type (0x0436)</td> </tr> <tr>
 * <td>0x6</td> <td>2</td> <td>first palette entry to be animated</td> </tr> <tr> <td>0x8</td> <td>2</td> <td>number of
 * animated entries</td> </tr> <tr> <td>0xa</td> <td>n*4</td> <td>palette entry array with (1 byte red, 1 byte green, 1
 * byte blue, 1 byte flags)</td> </tr> </table>
 */
public final class MfCmdAnimatePalette extends MfCmd {
  /**
   * the position of the first entry within the palette that should be animated.
   */
  private static final int POS_START_ANIMATE_COLOR = 0;
  private static final int POS_CENTRIES = 1;
  private static final int POS_START_ENTRIES = 2;
  private int posStartAnimate;
  private Color[] colors;


  /**
   * DefaultConstructor.
   */
  public MfCmdAnimatePalette() {
  }

  /**
   * Returns the number of colors defined for the AnimatePalette command.
   *
   * @return the number of colors or 0 if no colors are defined.
   */
  public int getEntriesCount() {
    if ( colors == null ) {
      return 0;
    }

    return colors.length;
  }

  /**
   * Creates a new record based on the data stored in the MfCommand.
   *
   * @return the created record.
   */
  public MfRecord getRecord()
    throws RecordCreationException {
    final int cEntries = getEntriesCount();
    if ( cEntries == 0 ) {
      throw new RecordCreationException( "Empty AnimatePaletteRecord is not valid" );
    }

    final MfRecord record = new MfRecord( 2 + cEntries * 2 );
    record.setParam( POS_START_ANIMATE_COLOR, getPosStartAnimate() );
    record.setParam( POS_CENTRIES, cEntries );

    for ( int i = 0; i < cEntries; i++ ) {
      final Color c = colors[ i ];
      // a long parameter is 2 words long
      record.setLongParam( i * 2 + POS_START_ENTRIES, GDIColor.translateColor( c ) );
    }
    return record;
  }

  /**
   * Reads the command data from the given record and adjusts the internal parameters according to the data parsed.
   * <p/>
   * This method is not implemented, as a Palette implementation is still missing.
   *
   * @param record the record.
   */
  public void setRecord( final MfRecord record ) {
    // the handle to the palette object
    final int hPalette = record.getParam( POS_START_ANIMATE_COLOR );
    setPosStartAnimate( hPalette );
    // the number of defined entries ...
    final int cEntries = record.getParam( POS_CENTRIES );
    final Color[] colors = new Color[ cEntries ];

    for ( int i = 0; i < cEntries; i++ ) {
      final int cr = record.getLongParam( i * 2 + POS_START_ENTRIES );
      final GDIColor color = new GDIColor( cr );
      colors[ i ] = color;
    }
    this.colors = colors;
  }

  /**
   * Reads the function identifiert .Every record type is identified by a function number corresponding to one of the
   * Windows GDI functions used.
   *
   * @return MfType.ANIMATE_PALETTE.
   */
  public int getFunction() {
    return MfType.ANIMATE_PALETTE;
  }

  /**
   * Replays the command on the given WmfFile.
   *
   * @param file the meta file.
   */
  public void replay( final WmfFile file ) {
    // do nothing
    // System.out.println ("Animate Palette is not implemented");
  }

  /**
   * Returns a string representation of this command.
   *
   * @return the command as string.
   */
  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( "[ANIMATE_PALETTE] posStartAnimate=" );
    b.append( getPosStartAnimate() );
    b.append( " entriesCount=" );
    b.append( getEntriesCount() );
    return b.toString();
  }

  /**
   * Creates a empty unintialized copy of this command implementation.
   *
   * @return a new instance of the command.
   */
  public MfCmd getInstance() {
    return new MfCmdAnimatePalette();
  }

  /**
   * Returns the position of the first color that should be animated in the current palette.
   *
   * @return the position of the color.
   */
  public int getPosStartAnimate() {
    return posStartAnimate;
  }

  /**
   * Defines the position of the first color that should be animated in the current palette.
   *
   * @param hPalette the index of the color, not negative.
   */
  public void setPosStartAnimate( final int hPalette ) {
    if ( posStartAnimate < 0 ) {
      throw new IndexOutOfBoundsException( "Palette indices must be positive." );
    }
    this.posStartAnimate = hPalette;
  }

  /**
   * Returns the colors defined for this command.
   *
   * @return the colors
   */
  public Color[] getEntries() {
    return colors;
  }

  /**
   * Defines the colors that should be animated.
   *
   * @param colors the colors.
   */
  public void setEntries( final Color[] colors ) {
    this.colors = colors;
  }

  /**
   * Not implemented as no scaling needed for this operation.
   */
  protected void scaleXChanged() {
  }

  /**
   * Not implemented as no scaling needed for this operation.
   */
  protected void scaleYChanged() {
  }
}
