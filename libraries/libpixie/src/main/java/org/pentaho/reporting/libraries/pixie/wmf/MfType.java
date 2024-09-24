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

package org.pentaho.reporting.libraries.pixie.wmf;


/**
 * Predefined types of Windows metafile records.
 */
public class MfType {

  // NOT YET IMPLEMENTED

  // Needs Bitmap-Implementation to work correctly
  public static final int CREATE_DIB_PATTERN_BRUSH = 0x0142;

  // Needs Bitmap-Implementation to work correctly
  public static final int CREATE_PATTERN_BRUSH = 0x01F9;
  public static final int SET_DIBITS_TO_DEVICE = 0x0d33;

  public static final int ANIMATE_PALETTE = 0x0436;
  public static final int ARC = 0x0817;
  public static final int BIT_BLT = 0x0940;
  public static final int CHORD = 0x0830;
  public static final int CREATE_BRUSH_INDIRECT = 0x02fc;
  public static final int CREATE_FONT_INDIRECT = 0x02fb;
  public static final int CREATE_PALETTE = 0x00f7;
  public static final int CREATE_PEN_INDIRECT = 0x02fa;
  public static final int CREATE_REGION = 0x06ff;
  public static final int DELETE_OBJECT = 0x01f0;
  public static final int ELLIPSE = 0x0418;
  public static final int ESCAPE = 0x0626;
  public static final int EXCLUDE_CLIP_RECT = 0x0415;
  public static final int EXT_FLOOD_FILL = 0x0548;
  public static final int EXT_TEXT_OUT = 0x0a32;
  public static final int FLOOD_FILL = 0x0419;
  public static final int FILL_REGION = 0x0228;
  public static final int FRAME_REGION = 0x0429;
  public static final int INTERSECT_CLIP_RECT = 0x0416;
  public static final int INVERT_REGION = 0x012a;
  public static final int LINE_TO = 0x0213;
  public static final int MOVE_TO = 0x0214;
  public static final int OFFSET_CLIP_RGN = 0x0220;
  public static final int OFFSET_VIEWPORT_ORG = 0x0211;
  public static final int OFFSET_WINDOW_ORG = 0x020f;
  public static final int PAINTREGION = 0x012b;
  public static final int PAT_BLT = 0x061d;
  public static final int PIE = 0x081a;
  public static final int POLYGON = 0x0324;
  public static final int POLYLINE = 0x0325;
  public static final int POLY_POLYGON = 0x0538;
  public static final int REALISE_PALETTE = 0x0035;
  public static final int RECTANGLE = 0x041b;
  public static final int RESIZE_PALETTE = 0x0139;
  public static final int RESTORE_DC = 0x0127;
  public static final int ROUND_RECT = 0x061c;
  public static final int SAVE_DC = 0x001e;
  public static final int SCALE_VIEWPORT_EXT = 0x0412;
  public static final int SCALE_WINDOW_EXT = 0x0400;
  public static final int SELECT_CLIP_REGION = 0x012c;
  public static final int SELECT_OBJECT = 0x012d;
  public static final int SELECT_PALETTE = 0x0234;
  public static final int SET_BK_COLOR = 0x0201;
  public static final int SET_BK_MODE = 0x0102;
  public static final int SET_MAP_MODE = 0x0103;
  public static final int SET_MAPPER_FLAGS = 0x0231;
  public static final int SET_PALETTE_ENTRIES = 0x0037;
  public static final int SET_PIXEL = 0x041f;
  public static final int SET_POLY_FILL_MODE = 0x0106;
  public static final int SET_ROP2 = 0x0104;
  public static final int SET_STRETCH_BLT_MODE = 0x0107;
  public static final int SET_TEXT_ALIGN = 0x012e;
  public static final int SET_TEXT_CHAR_EXTRA = 0x0108;
  public static final int SET_TEXT_COLOR = 0x0209;
  public static final int SET_TEXT_JUSTIFICATION = 0x020a;
  public static final int SET_VIEWPORT_EXT = 0x020e;
  public static final int SET_VIEWPORT_ORG = 0x020d;
  public static final int SET_WINDOW_EXT = 0x020c;
  public static final int SET_WINDOW_ORG = 0x020b;
  public static final int STRETCH_BLT = 0x0b41;
  public static final int STRETCH_DIBITS = 0x0f43;
  public static final int TEXT_OUT = 0x0521;
  public static final int END_OF_FILE = 0x0000;

  public static final int OLD_STRETCH_BLT = 0x0b23;
  public static final int OLD_CREATE_PATTERN_BRUSH = 0x01f9;
  public static final int OLD_BIT_BLT = 0x0922;


  /**
   * Type bit flags.
   */
  public static final int STATE = 0x01;
  public static final int VECTOR = 0x02;
  public static final int RASTER = 0x04;
  public static final int MAPPING_MODE = 0x08;

  /**
   * All the known types. The last is the default.
   */
  private static MfType[] ntab =
    {
      new MfType( PAINTREGION, "MfPaintRegion", VECTOR ),
      new MfType( ARC, "MfArc", VECTOR ),
      new MfType( CHORD, "MfChord", VECTOR ),
      new MfType( ELLIPSE, "MfEllipse", VECTOR ),
      new MfType( EXCLUDE_CLIP_RECT, "MfExcludeClipRect", STATE ),
      new MfType( FLOOD_FILL, "MfFloodFill", VECTOR ),
      new MfType( INTERSECT_CLIP_RECT, "MfIntersectClipRect", STATE ),
      new MfType( LINE_TO, "MfLineTo", VECTOR ),
      new MfType( MOVE_TO, "MfMoveTo", STATE ),
      new MfType( OFFSET_CLIP_RGN, "MfOffsetclipRgn", STATE ),
      new MfType( OFFSET_VIEWPORT_ORG, "MfOffsetViewportOrg", STATE | MAPPING_MODE ),
      new MfType( OFFSET_WINDOW_ORG, "MfOffsetWindowOrg", STATE | MAPPING_MODE ),
      new MfType( PAT_BLT, "MfPatBlt", RASTER ),
      new MfType( PIE, "MfPie", VECTOR ),
      new MfType( REALISE_PALETTE, "MfRealisePalette", STATE ),
      new MfType( RECTANGLE, "MfRectangle", VECTOR ),
      new MfType( RESIZE_PALETTE, "MfResizePalette", STATE ),
      new MfType( RESTORE_DC, "MfRestoreDC", STATE | MAPPING_MODE ),
      new MfType( ROUND_RECT, "MfRoundRect", VECTOR ),
      new MfType( SAVE_DC, "MfSaveDC", STATE ),
      new MfType( SCALE_VIEWPORT_EXT, "MfScaleViewportExt", STATE | MAPPING_MODE ),
      new MfType( SCALE_WINDOW_EXT, "MfScaleWindowExt", STATE | MAPPING_MODE ),
      new MfType( SET_BK_COLOR, "MfSetBkColor", STATE ),
      new MfType( SET_BK_MODE, "MfSetBkMode", STATE ),
      new MfType( SET_MAP_MODE, "MfSetMapMode", STATE | MAPPING_MODE ),
      new MfType( SET_MAPPER_FLAGS, "MfSetMapperFlags", STATE ),
      new MfType( SET_PIXEL, "MfSetPixel", RASTER ),
      new MfType( SET_POLY_FILL_MODE, "MfSetPolyFillMode", STATE ),
      new MfType( SET_ROP2, "MfSetROP2", STATE ),
      new MfType( SET_STRETCH_BLT_MODE, "MfSetStretchBltMode", STATE ),
      new MfType( SET_TEXT_ALIGN, "MfSetTextAlign", STATE ),
      new MfType( SET_TEXT_CHAR_EXTRA, "MfSetTextCharExtra", STATE ),
      new MfType( SET_TEXT_COLOR, "MfSetTextColor", STATE ),
      new MfType( SET_TEXT_JUSTIFICATION, "MfSetTextJustification", STATE ),
      new MfType( SET_VIEWPORT_EXT, "MfSetViewportExt", STATE | MAPPING_MODE ),
      new MfType( SET_VIEWPORT_ORG, "MfSetViewportOrg", STATE | MAPPING_MODE ),
      new MfType( SET_WINDOW_EXT, "MfSetWindowExt", STATE | MAPPING_MODE ),
      new MfType( SET_WINDOW_ORG, "MfSetWindowOrg", STATE | MAPPING_MODE ),
      new MfType( ANIMATE_PALETTE, "MfAnimatePalette", STATE ),
      new MfType( BIT_BLT, "MfBitBlt", RASTER ),
      new MfType( OLD_BIT_BLT, "MfOldBitBlt", RASTER ),
      new MfType( CREATE_BRUSH_INDIRECT, "MfCreateBrush", STATE ),
      new MfType( CREATE_FONT_INDIRECT, "MfCreateFont", STATE ),
      new MfType( CREATE_PALETTE, "MfCreatePalette", STATE ),
      new MfType( OLD_CREATE_PATTERN_BRUSH, "MfOldCreatePatternBrush", STATE ),
      new MfType( CREATE_PATTERN_BRUSH, "MfCreatePatternBrush", STATE ),
      new MfType( CREATE_PEN_INDIRECT, "MfCreatePen", STATE ),
      new MfType( CREATE_REGION, "MfCreateRegion", STATE ),
      new MfType( DELETE_OBJECT, "MfDeleteObject", STATE ),
      //    new MfType( DRAW_TEXT, "MfDrawText", VECTOR ),
      new MfType( ESCAPE, "MfEscape", STATE ),
      new MfType( EXT_TEXT_OUT, "MfExtTextOut", VECTOR ),
      new MfType( POLYGON, "MfPolygon", VECTOR ),
      new MfType( POLY_POLYGON, "MfPolyPolygon", VECTOR ),
      new MfType( POLYLINE, "MfPolyline", VECTOR ),
      new MfType( SELECT_CLIP_REGION, "MfSelectClipRegion", STATE ),
      new MfType( SELECT_OBJECT, "MfSelectObject", STATE ),
      new MfType( SELECT_PALETTE, "MfSelectPalette", STATE ),
      new MfType( SET_DIBITS_TO_DEVICE, "MfSetDIBitsToDevice", RASTER ),
      new MfType( SET_PALETTE_ENTRIES, "MfSetPaletteEntries", STATE ),
      new MfType( OLD_STRETCH_BLT, "MfOldStretchBlt", RASTER ),
      new MfType( STRETCH_BLT, "MfStretchBlt", RASTER ),
      new MfType( STRETCH_DIBITS, "MfStretchDIBits", RASTER ),
      new MfType( TEXT_OUT, "MfTextOut", VECTOR ),
      new MfType( END_OF_FILE, "MfEndOfFile", STATE ),
      new MfType( -1, "MfUnknown", 0 )
    };

  /**
   * Map a 16-bit type id onto an object.
   */
  public static MfType get( final int id ) {
    for ( int i = 0; i < ntab.length; i++ ) {
      if ( ntab[ i ].id == id ) {
        return ntab[ i ];
      }
    }
    return ntab[ ntab.length - 1 ]; // Not found.
  }

  // Getter functionen
  private int id;
  private int type;
  private String name;

  public int getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public int getType() {
    return type;
  }

  /**
   * True if this record marks the screen.
   */
  public boolean doesMark() {
    return ( type & ( VECTOR | RASTER ) ) != 0;
  }

  /**
   * True if this record affects mapping modes.
   */
  public boolean isMappingMode() {
    return ( type & MAPPING_MODE ) != 0;
  }

  private MfType( final int id, final String name, final int type ) {
    this.id = id;
    this.name = name;
    this.type = type;
  }

}
