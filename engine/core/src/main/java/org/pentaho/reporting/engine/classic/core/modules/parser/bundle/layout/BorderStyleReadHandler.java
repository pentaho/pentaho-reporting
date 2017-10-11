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

import java.awt.Color;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BorderStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private ElementStyleSheet styleSheet;

  public BorderStyleReadHandler() {
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes atts ) throws SAXException {
    final String bgColor = atts.getValue( getUri(), "background-color" );
    if ( bgColor != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, ReportParserUtil.parseColor( bgColor, null ) );
    }

    final String padding = atts.getValue( getUri(), "padding" );
    if ( padding != null ) {
      final Float value = ReportParserUtil.parseFloat( padding, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_TOP, value );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, value );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_LEFT, value );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, value );
    }
    final String paddingTop = atts.getValue( getUri(), "padding-top" );
    if ( paddingTop != null ) {
      final Float value = ReportParserUtil.parseFloat( paddingTop, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_TOP, value );
    }
    final String paddingLeft = atts.getValue( getUri(), "padding-left" );
    if ( paddingLeft != null ) {
      final Float value = ReportParserUtil.parseFloat( paddingLeft, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_LEFT, value );
    }
    final String paddingBottom = atts.getValue( getUri(), "padding-bottom" );
    if ( paddingBottom != null ) {
      final Float value = ReportParserUtil.parseFloat( paddingBottom, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_BOTTOM, value );
    }
    final String paddingRight = atts.getValue( getUri(), "padding-right" );
    if ( paddingRight != null ) {
      final Float value = ReportParserUtil.parseFloat( paddingRight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.PADDING_RIGHT, value );
    }

    final String borderColor = atts.getValue( getUri(), "border-color" );
    if ( borderColor != null ) {
      final Color value = ReportParserUtil.parseColor( borderColor, null );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, value );
    }
    final String borderColorTop = atts.getValue( getUri(), "border-top-color" );
    if ( borderColorTop != null ) {
      final Color value = ReportParserUtil.parseColor( borderColorTop, null );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, value );
    }
    final String borderColorLeft = atts.getValue( getUri(), "border-left-color" );
    if ( borderColorLeft != null ) {
      final Color value = ReportParserUtil.parseColor( borderColorLeft, null );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, value );
    }
    final String borderColorBottom = atts.getValue( getUri(), "border-bottom-color" );
    if ( borderColorBottom != null ) {
      final Color value = ReportParserUtil.parseColor( borderColorBottom, null );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, value );
    }
    final String borderColorRight = atts.getValue( getUri(), "border-right-color" );
    if ( borderColorRight != null ) {
      final Color value = ReportParserUtil.parseColor( borderColorRight, null );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, value );
    }

    final String borderColorBreak = atts.getValue( getUri(), "border-break-color" );
    if ( borderColorBreak != null ) {
      final Color value = ReportParserUtil.parseColor( borderColorBreak, null );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BREAK_COLOR, value );
    }

    final String borderWidth = atts.getValue( getUri(), "border-width" );
    if ( borderWidth != null ) {
      final Float value = ReportParserUtil.parseFloat( borderWidth, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, value );
    }
    final String borderWidthTop = atts.getValue( getUri(), "border-top-width" );
    if ( borderWidthTop != null ) {
      final Float value = ReportParserUtil.parseFloat( borderWidthTop, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, value );
    }
    final String borderWidthLeft = atts.getValue( getUri(), "border-left-width" );
    if ( borderWidthLeft != null ) {
      final Float value = ReportParserUtil.parseFloat( borderWidthLeft, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, value );
    }
    final String borderWidthBottom = atts.getValue( getUri(), "border-bottom-width" );
    if ( borderWidthBottom != null ) {
      final Float value = ReportParserUtil.parseFloat( borderWidthBottom, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, value );
    }
    final String borderWidthRight = atts.getValue( getUri(), "border-right-width" );
    if ( borderWidthRight != null ) {
      final Float value = ReportParserUtil.parseFloat( borderWidthRight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, value );
    }
    final String borderWidthBreak = atts.getValue( getUri(), "border-break-width" );
    if ( borderWidthBreak != null ) {
      final Float value = ReportParserUtil.parseFloat( borderWidthBreak, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BREAK_WIDTH, value );
    }

    final String borderStyle = atts.getValue( getUri(), "border-style" );
    if ( borderStyle != null ) {
      final BorderStyle value = parseBorderStyle( borderStyle );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, value );
    }
    final String borderStyleTop = atts.getValue( getUri(), "border-top-style" );
    if ( borderStyleTop != null ) {
      final BorderStyle value = parseBorderStyle( borderStyleTop );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, value );
    }
    final String borderStyleLeft = atts.getValue( getUri(), "border-left-style" );
    if ( borderStyleLeft != null ) {
      final BorderStyle value = parseBorderStyle( borderStyleLeft );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, value );
    }
    final String borderStyleBottom = atts.getValue( getUri(), "border-bottom-style" );
    if ( borderStyleBottom != null ) {
      final BorderStyle value = parseBorderStyle( borderStyleBottom );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, value );
    }
    final String borderStyleRight = atts.getValue( getUri(), "border-right-style" );
    if ( borderStyleRight != null ) {
      final BorderStyle value = parseBorderStyle( borderStyleRight );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, value );
    }
    final String borderStyleBreak = atts.getValue( getUri(), "border-break-style" );
    if ( borderStyleBreak != null ) {
      final BorderStyle value = parseBorderStyle( borderStyleBreak );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BREAK_STYLE, value );
    }

    final String borderRadiusWidth = atts.getValue( getUri(), "border-radius-width" );
    if ( borderRadiusWidth != null ) {
      final Float value = ReportParserUtil.parseFloat( borderRadiusWidth, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, value );
    }
    final String borderRadiusHeight = atts.getValue( getUri(), "border-radius-height" );
    if ( borderRadiusHeight != null ) {
      final Float value = ReportParserUtil.parseFloat( borderRadiusHeight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, value );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, value );
    }
    final String borderTopLeftRadiusWidth = atts.getValue( getUri(), "border-top-left-radius-width" );
    if ( borderTopLeftRadiusWidth != null ) {
      final Float value = ReportParserUtil.parseFloat( borderTopLeftRadiusWidth, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, value );
    }
    final String borderTopLeftRadiusHeight = atts.getValue( getUri(), "border-top-left-radius-height" );
    if ( borderTopLeftRadiusHeight != null ) {
      final Float value = ReportParserUtil.parseFloat( borderTopLeftRadiusHeight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, value );
    }
    final String borderTopRightRadiusWidth = atts.getValue( getUri(), "border-top-right-radius-width" );
    if ( borderTopRightRadiusWidth != null ) {
      final Float value = ReportParserUtil.parseFloat( borderTopRightRadiusWidth, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, value );
    }
    final String borderTopRightRadiusHeight = atts.getValue( getUri(), "border-top-right-radius-height" );
    if ( borderTopRightRadiusHeight != null ) {
      final Float value = ReportParserUtil.parseFloat( borderTopRightRadiusHeight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, value );
    }
    final String borderBottomLeftRadiusWidth = atts.getValue( getUri(), "border-bottom-left-radius-width" );
    if ( borderBottomLeftRadiusWidth != null ) {
      final Float value = ReportParserUtil.parseFloat( borderBottomLeftRadiusWidth, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, value );
    }
    final String borderBottomLeftRadiusHeight = atts.getValue( getUri(), "border-bottom-left-radius-height" );
    if ( borderBottomLeftRadiusHeight != null ) {
      final Float value = ReportParserUtil.parseFloat( borderBottomLeftRadiusHeight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, value );
    }
    final String borderBottomRightRadiusWidth = atts.getValue( getUri(), "border-bottom-right-radius-width" );
    if ( borderBottomRightRadiusWidth != null ) {
      final Float value = ReportParserUtil.parseFloat( borderBottomRightRadiusWidth, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, value );
    }
    final String borderBottomRightRadiusHeight = atts.getValue( getUri(), "border-bottom-right-radius-height" );
    if ( borderBottomRightRadiusHeight != null ) {
      final Float value = ReportParserUtil.parseFloat( borderBottomRightRadiusHeight, getLocator() );
      styleSheet.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, value );
    }
  }

  private BorderStyle parseBorderStyle( final String value ) {
    if ( BorderStyle.DASHED.toString().equals( value ) ) {
      return BorderStyle.DASHED;
    }
    if ( BorderStyle.DOT_DASH.toString().equals( value ) ) {
      return BorderStyle.DOT_DASH;
    }
    if ( BorderStyle.DOT_DOT_DASH.toString().equals( value ) ) {
      return BorderStyle.DOT_DOT_DASH;
    }
    if ( BorderStyle.DOTTED.toString().equals( value ) ) {
      return BorderStyle.DOTTED;
    }
    if ( BorderStyle.DOUBLE.toString().equals( value ) ) {
      return BorderStyle.DOUBLE;
    }
    if ( BorderStyle.GROOVE.toString().equals( value ) ) {
      return BorderStyle.GROOVE;
    }
    if ( BorderStyle.HIDDEN.toString().equals( value ) ) {
      return BorderStyle.HIDDEN;
    }
    if ( BorderStyle.INSET.toString().equals( value ) ) {
      return BorderStyle.INSET;
    }
    if ( BorderStyle.OUTSET.toString().equals( value ) ) {
      return BorderStyle.OUTSET;
    }
    if ( BorderStyle.RIDGE.toString().equals( value ) ) {
      return BorderStyle.RIDGE;
    }
    if ( BorderStyle.SOLID.toString().equals( value ) ) {
      return BorderStyle.SOLID;
    }
    // if (BorderStyle.NONE.toString().equals(value))
    // {
    // return BorderStyle.NONE;
    // }
    return BorderStyle.NONE;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
