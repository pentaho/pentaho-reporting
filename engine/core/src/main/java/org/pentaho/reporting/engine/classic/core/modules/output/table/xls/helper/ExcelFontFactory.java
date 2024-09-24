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
 * Copyright (c) 2001 - 2024 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;
import java.util.HashMap;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * This class keeps track of all fonts that we have used so far in our Excel file.
 * <p/>
 * Excel fonts should never be created directly, as excel does not like the idea of having too many font definitions.
 *
 * @author Heiko Evermann
 */
public class ExcelFontFactory {
  /**
   * The list of fonts that we have used so far.
   */
  private HashMap<HSSFFontWrapper, Font> fonts;

  /**
   * The workbook that is used to create the font.
   */
  private final Workbook workbook;

  /**
   * Constructor for ExcelFontFactory.
   *
   * @param workbook
   *          the workbook.
   */
  public ExcelFontFactory( final Workbook workbook, final ExcelColorProducer colorProducer ) {
    if ( workbook == null ) {
      throw new NullPointerException();
    }
    if ( colorProducer == null ) {
      throw new NullPointerException();
    }

    this.fonts = new HashMap<HSSFFontWrapper, Font>();
    this.workbook = workbook;

    // read the fonts from the workbook ...
    // Funny one: Please note that the layout will be broken if the first
    // font is not 'Arial 10'.
    final int numberOfFonts = this.workbook.getNumberOfFonts();
    for ( int i = 0; i < numberOfFonts; i++ ) {
      final Font font = workbook.getFontAt( (short) i );
      this.fonts.put( new HSSFFontWrapper( font ), font );
    }

    // add the default font
    // this MUST be the first one, that is created.
    // oh, I hate Excel ...
    final HSSFFontWrapper wrapper =
        new HSSFFontWrapper( "Arial", (short) 10, false, false, false, false, colorProducer
            .getNearestColor( Color.black ) );
    getExcelFont( wrapper );
  }

  /**
   * Creates a HSSFFont. The created font is cached and reused later, if a similiar font is requested.
   *
   * @param wrapper
   *          the font information that should be used to produce the excel font
   * @return the created or a cached HSSFFont instance
   */
  public Font getExcelFont( final HSSFFontWrapper wrapper ) {
    if ( wrapper == null ) {
      throw new NullPointerException();
    }

    if ( fonts.containsKey( wrapper ) ) {
      return fonts.get( wrapper );
    }

    // ok, we need a new one ...
    final Font excelFont = createFont( wrapper );
    fonts.put( wrapper, excelFont );
    return excelFont;
  }

  /**
   * Returns the excel font stored in this wrapper.
   *
   * @param wrapper
   *          the font wrapper that holds all font information from the repagination.
   * @return the created font.
   */
  private Font createFont( final HSSFFontWrapper wrapper ) {
    final Font font = workbook.createFont();
    font.setBold( wrapper.isBold() );
    font.setColor( wrapper.getColorIndex() );
    font.setFontName( wrapper.getFontName() );
    font.setFontHeightInPoints( (short) wrapper.getFontHeight() );
    font.setItalic( wrapper.isItalic() );
    font.setStrikeout( wrapper.isStrikethrough() );
    if ( wrapper.isUnderline() ) {
      font.setUnderline( Font.U_SINGLE );
    } else {
      font.setUnderline( Font.U_NONE );
    }
    return font;
  }

}
