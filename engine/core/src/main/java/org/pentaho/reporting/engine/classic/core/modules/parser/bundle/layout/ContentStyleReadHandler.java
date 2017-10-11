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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import java.awt.Stroke;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ContentStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private ElementStyleSheet styleSheet;
  private ColorValueConverter colorValueConverter;

  public ContentStyleReadHandler() {
    colorValueConverter = new ColorValueConverter();
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  /**
   * Starts parsing.
   * <p/>
   * draw-shape="true" fill-shape="false" scale="false" keep-aspect-ratio="true" fill-color="#ff00aa" stroke-width="1"
   * paint="#aa0033"
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String antiAliasing = attrs.getValue( getUri(), "anti-aliasing" );
    if ( antiAliasing != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.ANTI_ALIASING, "true".equals( antiAliasing ) );
    }
    final String drawShape = attrs.getValue( getUri(), "draw-shape" );
    if ( drawShape != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.DRAW_SHAPE, "true".equals( drawShape ) );
    }

    final String fillShape = attrs.getValue( getUri(), "fill-shape" );
    if ( fillShape != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.FILL_SHAPE, "true".equals( fillShape ) );
    }

    final String dynamicHeight = attrs.getValue( getUri(), "dynamic-height" );
    if ( dynamicHeight != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, "true".equals( dynamicHeight ) );
    }

    final String scale = attrs.getValue( getUri(), "scale" );
    if ( scale != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.SCALE, "true".equals( scale ) );
    }

    final String keepAspectRatio = attrs.getValue( getUri(), "keep-aspect-ratio" );
    if ( keepAspectRatio != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, "true".equals( keepAspectRatio ) );
    }

    final Stroke stroke = readStroke( attrs );
    if ( stroke != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.STROKE, stroke );
    }

    final String excelCellFormat = attrs.getValue( getUri(), "excel-cell-format" );
    if ( excelCellFormat != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING, excelCellFormat );
    }

    final String color = attrs.getValue( getUri(), "color" );
    if ( color != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.PAINT, ReportParserUtil.parseColor( color, null ) );
    }
    final String fillColor = attrs.getValue( getUri(), "fill-color" );
    if ( fillColor != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.FILL_COLOR, ReportParserUtil.parseColor( fillColor, null ) );
    }

  }

  private Stroke readStroke( final Attributes atts ) throws ParseException {
    final String strokeStyle = atts.getValue( getUri(), "stroke-style" );
    final String weightAttr = atts.getValue( getUri(), "stroke-weight" );
    if ( strokeStyle == null && weightAttr == null ) {
      return null;
    }

    float weight = 1;
    if ( weightAttr != null ) {
      weight = ParserUtil.parseFloat( weightAttr, "Weight is given, but not a number.", getLocator() );
    }

    // "dashed | solid | dotted | dot-dot-dash | dot-dash"
    return ReportParserUtil.parseStroke( strokeStyle, weight );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return styleSheet;
  }
}
