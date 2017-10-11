/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

public class DynamicExcelColorProducer implements ExcelColorProducer {
  private short lastUsedColor = 0x8; // this magic constant stands for first color index in a palette

  private HSSFWorkbook workbook;
  private Map<String, HSSFColor> usedTripplets;

  public DynamicExcelColorProducer( final HSSFWorkbook workbook ) {
    if ( workbook == null ) {
      throw new NullPointerException();
    }
    this.workbook = workbook;
    this.usedTripplets = new HashMap<String, HSSFColor>();
  }

  public HSSFColor getColor( final short index ) {
    final HSSFPalette palette = workbook.getCustomPalette();
    return palette.getColor( index );
  }

  public short getNearestColor( final Color awtColor ) {
    if ( lastUsedColor > 64 ) {
      // we ran out of palette... try to get nearest color then
      return StaticExcelColorSupport.getNearestColor( awtColor, usedTripplets );
    }

    final HSSFPalette palette = workbook.getCustomPalette();
    final HSSFColor hssfColor =
        palette.findColor( (byte) awtColor.getRed(), (byte) awtColor.getGreen(), (byte) awtColor.getBlue() );

    if ( hssfColor != null && hssfColor.getIndex() < lastUsedColor ) {
      return hssfColor.getIndex();
    } else {
      palette.setColorAtIndex( lastUsedColor, (byte) awtColor.getRed(), (byte) awtColor.getGreen(), (byte) awtColor
          .getBlue() );
      final HSSFColor color = palette.getColor( lastUsedColor );
      usedTripplets.put( color.getHexString(), color );
      return lastUsedColor++;
    }
  }

}
