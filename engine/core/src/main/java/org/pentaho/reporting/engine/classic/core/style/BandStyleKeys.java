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

package org.pentaho.reporting.engine.classic.core.style;

/**
 * A band style sheet. Defines some base StyleKeys for all Bands.
 *
 * @author Thomas Morgner
 */
public class BandStyleKeys {
  /**
   * A key for the band's 'page break before' flag.
   */
  public static final StyleKey PAGEBREAK_BEFORE = StyleKey
      .getStyleKey( "pagebreak-before", Boolean.class, false, false );

  /**
   * A key for the band's 'page break after' flag.
   */
  public static final StyleKey PAGEBREAK_AFTER = StyleKey.getStyleKey( "pagebreak-after", Boolean.class, false, false );

  /**
   * A key for the band's 'display on first page' flag.
   */
  public static final StyleKey DISPLAY_ON_FIRSTPAGE = StyleKey.getStyleKey( "display-on-firstpage", Boolean.class,
      false, false );

  /**
   * A key for the band's 'display on last page' flag.
   */
  public static final StyleKey DISPLAY_ON_LASTPAGE = StyleKey.getStyleKey( "display-on-lastpage", Boolean.class, false,
      false );

  /**
   * A key for the band's 'repeat header' flag.
   */
  public static final StyleKey REPEAT_HEADER = StyleKey.getStyleKey( "repeat-header", Boolean.class, false, false );

  /**
   * A key for the band's 'print on bottom' flag.
   */
  public static final StyleKey FIXED_POSITION = StyleKey.getStyleKey( "fixed-position", Float.class, false, false );

  public static final StyleKey BOOKMARK = StyleKey.getStyleKey( "bookmark", String.class, false, false );

  public static final StyleKey STICKY = StyleKey.getStyleKey( "sticky", Boolean.class, false, false );

  /**
   * One of 'inline', 'block', 'canvas' or 'row'
   */
  public static final StyleKey LAYOUT = StyleKey.getStyleKey( "layout", String.class, false, false );

  public static final String LAYOUT_CANVAS = "canvas";
  public static final String LAYOUT_BLOCK = "block";
  public static final String LAYOUT_INLINE = "inline";
  public static final String LAYOUT_ROW = "row";
  public static final String LAYOUT_AUTO = "auto";
  public static final String LAYOUT_TABLE = "table";
  public static final String LAYOUT_TABLE_CELL = "table-cell";
  public static final String LAYOUT_TABLE_ROW = "table-row";
  public static final String LAYOUT_TABLE_BODY = "table-body";
  public static final String LAYOUT_TABLE_HEADER = "table-header";
  public static final String LAYOUT_TABLE_FOOTER = "table-footer";
  public static final String LAYOUT_TABLE_COL = "table-col";
  public static final String LAYOUT_TABLE_COL_GROUP = "table-col-group";

  /**
   * An internal carrier key that is used to store the computed sheetname for a given band.
   */
  public static final StyleKey COMPUTED_SHEETNAME = StyleKey.getStyleKey( "computed-sheetname", String.class, true,
      false );

  public static final StyleKey TABLE_LAYOUT = StyleKey.getStyleKey( "table-layout", TableLayout.class, false, false );

  /**
   * Creates a new band style-sheet.
   */
  private BandStyleKeys() {
  }
}
