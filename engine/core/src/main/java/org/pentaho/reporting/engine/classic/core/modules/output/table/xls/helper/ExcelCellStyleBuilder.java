/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

/**
 * Created by dima.prokopenko@gmail.com on 9/13/2016.
 */
public class ExcelCellStyleBuilder {

  private final Workbook workbook;
  private final CellStyle hssfCellStyle;

  private boolean isXLSX = false;

  public ExcelCellStyleBuilder( Workbook workbook ) {
    this.workbook = workbook;
    this.hssfCellStyle = workbook.createCellStyle();
    this.isXLSX = hssfCellStyle instanceof XSSFCellStyle;
  }

  public void withRotation( final StyleSheet element ) {
    if ( element == null ) {
      return;
    }
    Object raw =  element.getStyleProperty( TextStyleKeys.TEXT_ROTATION, null );
    if ( raw == null ) {
      return;
    }

    TextRotation rotation = TextRotation.class.cast( raw );
    if ( isXLSX ) {
      //xlsx has different rotation degree boundaries
      final short numericValue = rotation.getNumericValue();
      hssfCellStyle.setRotation( numericValue < 0 ? (short) ( 90 - numericValue ) : numericValue );
    } else {
      hssfCellStyle.setRotation( rotation.getNumericValue() );
    }
  }

  public void withElementStyle( final StyleSheet elementStyleSheet, final HSSFCellStyleProducer.HSSFCellStyleKey styleKey ) {
    if ( elementStyleSheet == null ) {
      return;
    }

    hssfCellStyle.setAlignment( styleKey.getHorizontalAlignment() );
    hssfCellStyle.setVerticalAlignment( styleKey.getVerticalAlignment() );
    hssfCellStyle.setFont( workbook.getFontAt( styleKey.getFont() ) );
    hssfCellStyle.setWrapText( styleKey.isWrapText() );
    hssfCellStyle.setIndention( styleKey.getIndention() );
    if ( styleKey.getDataStyle() >= 0 ) {
      hssfCellStyle.setDataFormat( styleKey.getDataStyle() );
    }
  }

  public void withBackgroundStyle( final CellBackground bg, final HSSFCellStyleProducer.HSSFCellStyleKey styleKey ) {
    if ( bg == null ) {
      return;
    }
    if ( isXLSX ) {
      xlsx_backgroundStyle( bg, styleKey );
    } else {
      xls_backgroundStyle( bg, styleKey );
    }
  }

  // default visibility for testing purposes
  void xls_backgroundStyle( final CellBackground bg, final HSSFCellStyleProducer.HSSFCellStyleKey styleKey ) {
    if ( BorderStyle.NONE.equals( bg.getBottom().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderBottom( styleKey.getBorderStrokeBottom() );
      hssfCellStyle.setBottomBorderColor( styleKey.getColorBottom() );
    }
    if ( BorderStyle.NONE.equals( bg.getTop().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderTop( styleKey.getBorderStrokeTop() );
      hssfCellStyle.setTopBorderColor( styleKey.getColorTop() );
    }
    if ( BorderStyle.NONE.equals( bg.getLeft().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderLeft( styleKey.getBorderStrokeLeft() );
      hssfCellStyle.setLeftBorderColor( styleKey.getColorLeft() );
    }
    if ( BorderStyle.NONE.equals( bg.getRight().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderRight( styleKey.getBorderStrokeRight() );
      hssfCellStyle.setRightBorderColor( styleKey.getColorRight() );
    }
    if ( bg.getBackgroundColor() != null ) {
      hssfCellStyle.setFillForegroundColor( styleKey.getColor() );
      hssfCellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
    }
  }

  // default visibility for testing purposes
  void xlsx_backgroundStyle( final CellBackground bg, final HSSFCellStyleProducer.HSSFCellStyleKey styleKey ) {
    final XSSFCellStyle xssfCellStyle = (XSSFCellStyle) hssfCellStyle;
    if ( BorderStyle.NONE.equals( bg.getBottom().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderBottom( styleKey.getBorderStrokeBottom() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.BOTTOM, new XSSFColor( styleKey.getExtendedColorBottom(), null ) );
    }
    if ( BorderStyle.NONE.equals( bg.getTop().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderTop( styleKey.getBorderStrokeTop() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.TOP, new XSSFColor( styleKey.getExtendedColorTop(), null ) );
    }
    if ( BorderStyle.NONE.equals( bg.getLeft().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderLeft( styleKey.getBorderStrokeLeft() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.LEFT, new XSSFColor( styleKey.getExtendedColorLeft(), null ) );
    }
    if ( BorderStyle.NONE.equals( bg.getRight().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderRight( styleKey.getBorderStrokeRight() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.RIGHT, new XSSFColor( styleKey.getExtendedColorRight(), null ) );
    }
    if ( bg.getBackgroundColor() != null ) {
      xssfCellStyle.setFillForegroundColor( new XSSFColor( styleKey.getExtendedColor(), null ) );
      hssfCellStyle.setFillPattern( FillPatternType.SOLID_FOREGROUND );
    }
  }

  public CellStyle build() {
    return this.hssfCellStyle;
  }
}
