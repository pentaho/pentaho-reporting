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
 * Copyright (c) 2001 - 2016 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

import java.awt.Color;
import java.util.HashMap;

/**
 * The cellstyle producer converts the JFreeReport content into excel cell styles. This class is able to use the POI 2.0
 * features to build data cells.
 *
 * @author Thomas Morgner
 */
public class HSSFCellStyleProducer implements CellStyleProducer {
  private static final Log logger = LogFactory.getLog( HSSFCellStyleProducer.class );

  static class HSSFCellStyleKey {
    /**
     * The cell background color.
     */
    private short color;
    private Color xColor;

    /**
     * The top border's size.
     */
    private short borderStrokeTop;

    /**
     * The bottom border's size.
     */
    private short borderStrokeBottom;

    /**
     * The left border's size.
     */
    private short borderStrokeLeft;

    /**
     * The right border's size.
     */
    private short borderStrokeRight;

    /**
     * The top border's color.
     */
    private short colorTop;
    private Color xColorTop;

    /**
     * The left border's color.
     */
    private short colorLeft;
    private Color xColorLeft;

    /**
     * The bottom border's color.
     */
    private short colorBottom;
    private Color xColorBottom;

    /**
     * The right border's color.
     */
    private short colorRight;
    private Color xColorRight;

    /**
     * A flag indicating whether to enable excels word wrapping.
     */
    private boolean wrapText;

    /**
     * the horizontal text alignment.
     */
    private short horizontalAlignment;

    /**
     * the vertical text alignment.
     */
    private short verticalAlignment;

    /**
     * the font definition for the cell.
     */
    private short font;

    /**
     * the data style.
     */
    private short dataStyle;

    /**
     * Indention level
     */
    private short indention;

    /**
     * Text rotation
     */
    private TextRotation textRotation;

    private Integer hashCode;

    /**
     * @param background
     *          can be null
     * @param contentStyle
     *          can be null
     */
    protected HSSFCellStyleKey( final CellBackground background, final StyleSheet contentStyle,
                                final DataFormat dataFormat, final ExcelFontFactory fontFactory,
                                final ExcelColorProducer colorProducer,
                                final ExcelColorProducer fontColorProducer ) {
      if ( dataFormat == null ) {
        throw new NullPointerException();
      }
      if ( fontFactory == null ) {
        throw new NullPointerException();
      }

      this.dataStyle = 0;
      this.color = HSSFColor.AUTOMATIC.index;
      this.colorBottom = HSSFColor.AUTOMATIC.index;
      this.colorLeft = HSSFColor.AUTOMATIC.index;
      this.colorRight = HSSFColor.AUTOMATIC.index;
      this.colorTop = HSSFColor.AUTOMATIC.index;

      if ( background != null ) {
        if ( background.getBackgroundColor() != null ) {
          this.color = colorProducer.getNearestColor( background.getBackgroundColor() );
          this.xColor = background.getBackgroundColor();
        }
        final BorderEdge bottom = background.getBottom();
        this.colorBottom = colorProducer.getNearestColor( bottom.getColor() );
        this.xColorBottom = bottom.getColor();
        this.borderStrokeBottom = HSSFCellStyleProducer.translateStroke( bottom.getBorderStyle(), bottom.getWidth() );

        final BorderEdge left = background.getLeft();
        this.colorLeft = colorProducer.getNearestColor( left.getColor() );
        this.xColorLeft = left.getColor();
        this.borderStrokeLeft = HSSFCellStyleProducer.translateStroke( left.getBorderStyle(), left.getWidth() );

        final BorderEdge top = background.getTop();
        this.colorTop = colorProducer.getNearestColor( top.getColor() );
        this.xColorTop = top.getColor();
        this.borderStrokeTop = HSSFCellStyleProducer.translateStroke( top.getBorderStyle(), top.getWidth() );

        final BorderEdge right = background.getRight();
        this.colorRight = colorProducer.getNearestColor( right.getColor() );
        this.xColorRight = right.getColor();
        this.borderStrokeRight = HSSFCellStyleProducer.translateStroke( right.getBorderStyle(), right.getWidth() );
      }

      if ( contentStyle != null ) {
        final Color textColor = (Color) contentStyle.getStyleProperty( ElementStyleKeys.PAINT );
        final HSSFFontWrapper wrapper =
            new HSSFFontWrapper( contentStyle, fontColorProducer.getNearestColor( textColor ) );
        final Font excelFont = fontFactory.getExcelFont( wrapper );
        this.font = excelFont.getIndex();

        final ElementAlignment horizontal =
            (ElementAlignment) contentStyle.getStyleProperty( ElementStyleKeys.ALIGNMENT );
        this.horizontalAlignment = HSSFCellStyleProducer.convertAlignment( horizontal );
        final ElementAlignment vertical =
            (ElementAlignment) contentStyle.getStyleProperty( ElementStyleKeys.VALIGNMENT );
        this.verticalAlignment = HSSFCellStyleProducer.convertAlignment( vertical );
        final String dataStyle = (String) contentStyle.getStyleProperty( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING );
        if ( dataStyle != null ) {
          this.dataStyle = dataFormat.getFormat( dataStyle );
        }
        this.wrapText = isWrapText( contentStyle );
        this.indention = getIndention( contentStyle );
        this.textRotation = (TextRotation) contentStyle.getStyleProperty( TextStyleKeys.TEXT_ROTATION, null );
      }
    }

    private boolean isWrapText( final StyleSheet styleSheet ) {
      final Object excelWrap = styleSheet.getStyleProperty( ElementStyleKeys.EXCEL_WRAP_TEXT );
      if ( excelWrap != null ) {
        return Boolean.TRUE.equals( excelWrap );
      }
      return TextWrap.WRAP.equals( styleSheet.getStyleProperty( TextStyleKeys.TEXT_WRAP, TextWrap.WRAP ) );
    }

    private Short getIndention( final StyleSheet styleSheet ) {
      Short indention = (Short) styleSheet.getStyleProperty( ElementStyleKeys.EXCEL_INDENTION );
      return indention == null ? 0 : indention;
    }

    protected HSSFCellStyleKey( final XSSFCellStyle style ) {
      this.color = style.getFillForegroundColor();
      this.colorTop = style.getTopBorderColor();
      this.colorLeft = style.getLeftBorderColor();
      this.colorBottom = style.getBottomBorderColor();
      this.colorRight = style.getRightBorderColor();

      this.xColor = createColor( style.getFillBackgroundXSSFColor() );
      this.xColorTop = createColor( style.getTopBorderXSSFColor() );
      this.xColorLeft = createColor( style.getLeftBorderXSSFColor() );
      this.xColorBottom = createColor( style.getBottomBorderXSSFColor() );
      this.xColorRight = createColor( style.getRightBorderXSSFColor() );

      this.borderStrokeTop = style.getBorderTop();
      this.borderStrokeLeft = style.getBorderLeft();
      this.borderStrokeBottom = style.getBorderBottom();
      this.borderStrokeRight = style.getBorderRight();

      this.dataStyle = style.getDataFormat();
      this.font = style.getFontIndex();
      this.horizontalAlignment = style.getAlignment();
      this.verticalAlignment = style.getVerticalAlignment();
      this.wrapText = style.getWrapText();
      this.textRotation = TextRotation.getInstance( style.getRotation() );
    }

    private static Color createColor( final XSSFColor color ) {
      if ( color == null ) {
        return null;
      }
      final byte[] rgb = color.getRgb();
      return new Color( 0xFF & rgb[0], 0xFF & rgb[1], 0xFF & rgb[2] );
    }

    protected HSSFCellStyleKey( final CellStyle style ) {
      this.color = style.getFillForegroundColor();
      this.colorTop = style.getTopBorderColor();
      this.colorLeft = style.getLeftBorderColor();
      this.colorBottom = style.getBottomBorderColor();
      this.colorRight = style.getRightBorderColor();
      this.borderStrokeTop = style.getBorderTop();
      this.borderStrokeLeft = style.getBorderLeft();
      this.borderStrokeBottom = style.getBorderBottom();
      this.borderStrokeRight = style.getBorderRight();

      this.dataStyle = style.getDataFormat();
      this.font = style.getFontIndex();
      this.horizontalAlignment = style.getAlignment();
      this.verticalAlignment = style.getVerticalAlignment();
      this.wrapText = style.getWrapText();
      this.textRotation = TextRotation.getInstance( style.getRotation() );
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final HSSFCellStyleKey that = (HSSFCellStyleKey) o;

      if ( borderStrokeBottom != that.borderStrokeBottom ) {
        return false;
      }
      if ( borderStrokeLeft != that.borderStrokeLeft ) {
        return false;
      }
      if ( borderStrokeRight != that.borderStrokeRight ) {
        return false;
      }
      if ( borderStrokeTop != that.borderStrokeTop ) {
        return false;
      }
      if ( color != that.color ) {
        return false;
      }
      if ( colorBottom != that.colorBottom ) {
        return false;
      }
      if ( colorLeft != that.colorLeft ) {
        return false;
      }
      if ( colorRight != that.colorRight ) {
        return false;
      }
      if ( colorTop != that.colorTop ) {
        return false;
      }
      if ( dataStyle != that.dataStyle ) {
        return false;
      }
      if ( font != that.font ) {
        return false;
      }
      if ( horizontalAlignment != that.horizontalAlignment ) {
        return false;
      }
      if ( verticalAlignment != that.verticalAlignment ) {
        return false;
      }
      if ( wrapText != that.wrapText ) {
        return false;
      }
      if ( xColor == null ) {
        if ( that.xColor != null ) {
          return false;
        }
      } else {
        if ( xColor.equals( that.xColor ) == false ) {
          return false;
        }
      }

      if ( xColorRight == null ) {
        if ( that.xColorRight != null ) {
          return false;
        }
      } else {
        if ( xColorRight.equals( that.xColorRight ) == false ) {
          return false;
        }
      }
      if ( xColorLeft == null ) {
        if ( that.xColorLeft != null ) {
          return false;
        }
      } else {
        if ( xColorLeft.equals( that.xColorLeft ) == false ) {
          return false;
        }
      }
      if ( xColorTop == null ) {
        if ( that.xColorTop != null ) {
          return false;
        }
      } else {
        if ( xColorTop.equals( that.xColorTop ) == false ) {
          return false;
        }
      }
      if ( xColorBottom == null ) {
        if ( that.xColorBottom != null ) {
          return false;
        }
      } else {
        return xColorBottom.equals( that.xColorBottom );
      }
      if ( textRotation == null ) {
        if ( that.textRotation != null ) {
          return false;
        }
      } else {
        return textRotation.equals( that.textRotation );
      }

      return true;
    }

    public int hashCode() {
      if ( hashCode == null ) {
        int result = (int) color;
        result = 29 * result + (int) borderStrokeTop;
        result = 29 * result + (int) borderStrokeBottom;
        result = 29 * result + (int) borderStrokeLeft;
        result = 29 * result + (int) borderStrokeRight;
        result = 29 * result + (int) colorTop;
        result = 29 * result + (int) colorLeft;
        result = 29 * result + (int) colorBottom;
        result = 29 * result + (int) colorRight;
        result = 29 * result + ( wrapText ? 1 : 0 );
        result = 29 * result + (int) horizontalAlignment;
        result = 29 * result + (int) verticalAlignment;
        result = 29 * result + (int) font;
        result = 29 * result + (int) dataStyle;
        result = 29 * result + (int) indention;
        result = 29 * result + ( ( xColor == null ) ? 0 : xColor.hashCode() );
        result = 29 * result + ( ( xColorTop == null ) ? 0 : xColorTop.hashCode() );
        result = 29 * result + ( ( xColorLeft == null ) ? 0 : xColorLeft.hashCode() );
        result = 29 * result + ( ( xColorBottom == null ) ? 0 : xColorBottom.hashCode() );
        result = 29 * result + ( ( xColorRight == null ) ? 0 : xColorRight.hashCode() );
        result = 29 * result + ( ( textRotation == null ) ? 0 : textRotation.hashCode() );
        hashCode = result;
      }
      return hashCode;
    }

    public short getColor() {
      return color;
    }

    public short getBorderStrokeTop() {
      return borderStrokeTop;
    }

    public short getBorderStrokeBottom() {
      return borderStrokeBottom;
    }

    public short getBorderStrokeLeft() {
      return borderStrokeLeft;
    }

    public short getBorderStrokeRight() {
      return borderStrokeRight;
    }

    public short getColorTop() {
      return colorTop;
    }

    public short getColorLeft() {
      return colorLeft;
    }

    public short getColorBottom() {
      return colorBottom;
    }

    public short getColorRight() {
      return colorRight;
    }

    public Color getExtendedColor() {
      return xColor;
    }

    public Color getExtendedColorTop() {
      return xColorTop;
    }

    public Color getExtendedColorLeft() {
      return xColorLeft;
    }

    public Color getExtendedColorBottom() {
      return xColorBottom;
    }

    public Color getExtendedColorRight() {
      return xColorRight;
    }

    public boolean isWrapText() {
      return wrapText;
    }

    public short getHorizontalAlignment() {
      return horizontalAlignment;
    }

    public short getVerticalAlignment() {
      return verticalAlignment;
    }

    public short getFont() {
      return font;
    }

    public short getDataStyle() {
      return dataStyle;
    }

    public short getIndention() {
      return indention;
    }
  }

  /**
   * the font factory is used to create excel fonts.
   */
  private ExcelFontFactory fontFactory;

  /**
   * The workbook, which creates all cells and styles.
   */
  private Workbook workbook;

  /**
   * The data format is used to create format strings.
   */
  private DataFormat dataFormat;

  /**
   * The cache for all generated styles.
   */
  private HashMap<HSSFCellStyleKey, CellStyle> styleCache;

  private boolean warningDone;
  private boolean hardLimit;
  private ExcelColorProducer colorProducer;
  private ExcelColorProducer fontColorProducer;

  /**
   * The class does the dirty work of creating the HSSF-objects.
   *
   * @param workbook
   *          the workbook for which the styles should be created.
   */
  public HSSFCellStyleProducer( final Workbook workbook, final boolean hardLimit,
                                final ExcelColorProducer colorProducer, final ExcelColorProducer fontColorProducer ) {
    this.fontColorProducer = fontColorProducer;
    if ( workbook == null ) {
      throw new NullPointerException();
    }
    if ( colorProducer == null ) {
      throw new NullPointerException();
    }
    this.colorProducer = colorProducer;
    this.styleCache = new HashMap<HSSFCellStyleKey, CellStyle>();
    this.workbook = workbook;
    this.fontFactory = new ExcelFontFactory( workbook, fontColorProducer );
    this.dataFormat = workbook.createDataFormat();
    this.hardLimit = hardLimit;

    if ( workbook instanceof XSSFWorkbook ) {
      final XSSFWorkbook xssfWorkbook = (XSSFWorkbook) workbook;
      final short predefinedStyles = workbook.getNumCellStyles();
      for ( short i = 0; i < predefinedStyles; i++ ) {
        final XSSFCellStyle cellStyleAt = xssfWorkbook.getCellStyleAt( i );
        this.styleCache.put( new HSSFCellStyleKey( cellStyleAt ), cellStyleAt );
      }
    } else {
      // Read in the styles ...
      final short predefinedStyles = workbook.getNumCellStyles();
      for ( short i = 0; i < predefinedStyles; i++ ) {
        final CellStyle cellStyleAt = workbook.getCellStyleAt( i );
        this.styleCache.put( new HSSFCellStyleKey( cellStyleAt ), cellStyleAt );
      }
    }
  }

  /**
   * Creates a HSSFCellStyle based on the given ExcelDataCellStyle. If a similiar cell style was previously generated,
   * then reuse that cached result.
   *
   * @param element
   *          can be null for background only cells.
   * @param bg
   *          the optional background style for the table cell.
   * @return the generated or cached HSSFCellStyle.
   */
  public CellStyle createCellStyle( final InstanceID id, final StyleSheet element, final CellBackground bg ) {
    // check, whether that style is already created
    final HSSFCellStyleKey styleKey =
        new HSSFCellStyleKey( bg, element, dataFormat, fontFactory, colorProducer, fontColorProducer );
    if ( styleCache.containsKey( styleKey ) ) {
      return styleCache.get( styleKey );
    }

    if ( ( styleCache.size() ) > 4000 ) {
      if ( warningDone == false ) {
        HSSFCellStyleProducer.logger.warn( "HSSFCellStyleProducer has reached the limit of 4000 created styles." );
        warningDone = true;
      }
      if ( hardLimit ) {
        HSSFCellStyleProducer.logger
            .warn( "HSSFCellStyleProducer will not create more styles. New cells will not have any style." );
        return null;
      }
    }

    ExcelCellStyleBuilder builder = new ExcelCellStyleBuilder( this.workbook );

    builder.withRotation( element );
    builder.withElementStyle( element, styleKey );
    builder.withBackgroundStyle( bg, styleKey );

    final CellStyle hssfCellStyle = builder.build();

    styleCache.put( styleKey, hssfCellStyle );
    return hssfCellStyle;
  }

  /**
   * Converts the given element alignment into one of the HSSFCellStyle-constants.
   *
   * @param e the JFreeReport element alignment.
   * @return the HSSFCellStyle-Alignment.
   * @throws IllegalArgumentException if an Unknown JFreeReport alignment is given.
   */
  protected static short convertAlignment( final ElementAlignment e ) {
    if ( ElementAlignment.LEFT.equals( e ) ) {
      return HSSFCellStyle.ALIGN_LEFT;
    }
    if ( ElementAlignment.RIGHT.equals( e ) ) {
      return HSSFCellStyle.ALIGN_RIGHT;
    }
    if ( ElementAlignment.JUSTIFY.equals( e ) ) {
      return HSSFCellStyle.ALIGN_JUSTIFY;
    }
    if ( ElementAlignment.CENTER.equals( e ) ) {
      return HSSFCellStyle.ALIGN_CENTER;
    }
    if ( ElementAlignment.TOP.equals( e ) ) {
      return HSSFCellStyle.VERTICAL_TOP;
    }
    if ( ElementAlignment.BOTTOM.equals( e ) ) {
      return HSSFCellStyle.VERTICAL_BOTTOM;
    }
    if ( ElementAlignment.MIDDLE.equals( e ) ) {
      return HSSFCellStyle.VERTICAL_CENTER;
    }

    throw new IllegalArgumentException( "Invalid alignment" );
  }

  /**
   * Tries to translate the given stroke width into one of the predefined excel border styles.
   *
   * @param widthRaw the AWT-Stroke-Width.
   * @return the translated excel border width.
   */
  protected static short translateStroke( final BorderStyle borderStyle, final long widthRaw ) {
    final double width = StrictGeomUtility.toExternalValue( widthRaw );
    if ( BorderStyle.NONE.equals( borderStyle ) ) {
      return HSSFCellStyle.BORDER_NONE;
    }
    if ( BorderStyle.DASHED.equals( borderStyle ) ) {
      if ( width <= 1.5 ) {
        return HSSFCellStyle.BORDER_DASHED;
      } else {
        return HSSFCellStyle.BORDER_MEDIUM_DASHED;
      }
    }
    if ( BorderStyle.DOT_DOT_DASH.equals( borderStyle ) ) {
      if ( width <= 1.5 ) {
        return HSSFCellStyle.BORDER_DASH_DOT_DOT;
      } else {
        return HSSFCellStyle.BORDER_MEDIUM_DASH_DOT_DOT;
      }
    }
    if ( BorderStyle.DOT_DASH.equals( borderStyle ) ) {
      if ( width <= 1.5 ) {
        return HSSFCellStyle.BORDER_DASH_DOT;
      } else {
        return HSSFCellStyle.BORDER_MEDIUM_DASH_DOT;
      }
    }
    if ( BorderStyle.DOTTED.equals( borderStyle ) ) {
      return HSSFCellStyle.BORDER_DOTTED;
    }
    if ( BorderStyle.DOUBLE.equals( borderStyle ) ) {
      return HSSFCellStyle.BORDER_DOUBLE;
    }

    if ( width == 0 ) {
      return HSSFCellStyle.BORDER_NONE;
    } else if ( width <= 0.5 ) {
      return HSSFCellStyle.BORDER_HAIR;
    } else if ( width <= 1 ) {
      return HSSFCellStyle.BORDER_THIN;
    } else if ( width <= 1.5 ) {
      return HSSFCellStyle.BORDER_MEDIUM;
      // }
      // else if (width <= 2)
      // {
      // return HSSFCellStyle.BORDER_DOUBLE;
    } else {
      return HSSFCellStyle.BORDER_THICK;
    }
  }

  public ExcelFontFactory getFontFactory() {
    return fontFactory;
  }
}
