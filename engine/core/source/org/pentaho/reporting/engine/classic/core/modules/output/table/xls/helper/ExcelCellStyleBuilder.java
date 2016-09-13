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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;

import java.awt.Color;


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

  public void withRotation( final TextRotation rotation ) {
    if ( rotation == null ) {
      return;
    }

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
      xls_BackgroundStyle( bg, styleKey );
    }
  }

  // default visibility for testing purposes
  void xls_BackgroundStyle( final CellBackground bg, final HSSFCellStyleProducer.HSSFCellStyleKey styleKey ) {
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
      hssfCellStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
    }
  }

  // default visibility for testing purposes
  void xlsx_backgroundStyle( final CellBackground bg, final HSSFCellStyleProducer.HSSFCellStyleKey styleKey ) {
    final XSSFCellStyle xssfCellStyle = (XSSFCellStyle) hssfCellStyle;
    if ( BorderStyle.NONE.equals( bg.getBottom().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderBottom( styleKey.getBorderStrokeBottom() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.BOTTOM, createXSSFColor( styleKey
        .getExtendedColorBottom() ) );
    }
    if ( BorderStyle.NONE.equals( bg.getTop().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderTop( styleKey.getBorderStrokeTop() );
      xssfCellStyle
        .setBorderColor( XSSFCellBorder.BorderSide.TOP, createXSSFColor( styleKey.getExtendedColorTop() ) );
    }
    if ( BorderStyle.NONE.equals( bg.getLeft().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderLeft( styleKey.getBorderStrokeLeft() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.LEFT, createXSSFColor( styleKey
        .getExtendedColorLeft() ) );
    }
    if ( BorderStyle.NONE.equals( bg.getRight().getBorderStyle() ) == false ) {
      hssfCellStyle.setBorderRight( styleKey.getBorderStrokeRight() );
      xssfCellStyle.setBorderColor( XSSFCellBorder.BorderSide.RIGHT, createXSSFColor( styleKey
        .getExtendedColorRight() ) );
    }
    if ( bg.getBackgroundColor() != null ) {
      xssfCellStyle.setFillForegroundColor( createXSSFColor( styleKey.getExtendedColor() ) );
      hssfCellStyle.setFillPattern( HSSFCellStyle.SOLID_FOREGROUND );
    }
  }

  public CellStyle build() {
    return this.hssfCellStyle;
  }

  // default visibility for testing purposes
  XSSFColor createXSSFColor( final Color clr ) {
    byte[] rgb = { (byte) 255, (byte) clr.getRed(), (byte) clr.getGreen(), (byte) clr.getBlue() };
    return new XSSFColor( rgb );
  }
}
