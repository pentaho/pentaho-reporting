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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.parser;

import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultCell;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.layout.model.ResultTable;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;

/**
 * Creation-Date: 21.08.2007, 15:21:22
 *
 * @author Thomas Morgner
 */
public class CellReadHandler extends AbstractXmlReadHandler {
  private ResultTable table;
  private int row;
  private int column;

  public CellReadHandler( final ResultTable table, final int row, final int column ) {
    this.table = table;
    this.row = row;
    this.column = column;
  }

  protected void startParsing( final Attributes atts ) throws SAXException {
    final ResultCell resultCell = new ResultCell();
    resultCell.setName( atts.getValue( getUri(), "content-idref" ) );

    final String attr = atts.getValue( getUri(), "background-color" );
    resultCell.setBackgroundColor( ReportParserUtil.parseColor( attr, null ) );

    final String borderColorTopText = atts.getValue( getUri(), "border-top-color" );
    final Color borderColorTop = ReportParserUtil.parseColor( borderColorTopText, null );
    final String borderColorLeftText = atts.getValue( getUri(), "border-left-color" );
    final Color borderColorLeft = ReportParserUtil.parseColor( borderColorLeftText, null );
    final String borderColorBottomText = atts.getValue( getUri(), "border-bottom-color" );
    final Color borderColorBottom = ReportParserUtil.parseColor( borderColorBottomText, null );
    final String borderColorRightText = atts.getValue( getUri(), "border-right-color" );
    final Color borderColorRight = ReportParserUtil.parseColor( borderColorRightText, null );

    final String borderWidthTopText = atts.getValue( getUri(), "border-top-width" );
    final Float borderWidthTop = ReportParserUtil.parseFloat( borderWidthTopText, getLocator() );
    final String borderWidthLeftText = atts.getValue( getUri(), "border-left-width" );
    final Float borderWidthLeft = ReportParserUtil.parseFloat( borderWidthLeftText, getLocator() );
    final String borderWidthBottomText = atts.getValue( getUri(), "border-bottom-width" );
    final Float borderWidthBottom = ReportParserUtil.parseFloat( borderWidthBottomText, getLocator() );
    final String borderWidthRightText = atts.getValue( getUri(), "border-right-width" );
    final Float borderWidthRight = ReportParserUtil.parseFloat( borderWidthRightText, getLocator() );

    final String borderStyleTopText = atts.getValue( getUri(), "border-top-style" );
    final BorderStyle borderStyleTop = parseBorderStyle( borderStyleTopText );
    final String borderStyleLeftText = atts.getValue( getUri(), "border-left-style" );
    final BorderStyle borderStyleLeft = parseBorderStyle( borderStyleLeftText );
    final String borderStyleBottomText = atts.getValue( getUri(), "border-bottom-style" );
    final BorderStyle borderStyleBottom = parseBorderStyle( borderStyleBottomText );
    final String borderStyleRightText = atts.getValue( getUri(), "border-right-style" );
    final BorderStyle borderStyleRight = parseBorderStyle( borderStyleRightText );

    if ( borderWidthTop != null ) {
      resultCell.setTop( new BorderEdge( borderStyleTop, borderColorTop, StrictGeomUtility
          .toInternalValue( borderWidthTop.floatValue() ) ) );
    }
    if ( borderWidthLeft != null ) {
      resultCell.setLeft( new BorderEdge( borderStyleLeft, borderColorLeft, StrictGeomUtility
          .toInternalValue( borderWidthLeft.floatValue() ) ) );
    }
    if ( borderWidthBottom != null ) {
      resultCell.setBottom( new BorderEdge( borderStyleBottom, borderColorBottom, StrictGeomUtility
          .toInternalValue( borderWidthBottom.floatValue() ) ) );
    }
    if ( borderWidthRight != null ) {
      resultCell.setRight( new BorderEdge( borderStyleRight, borderColorRight, StrictGeomUtility
          .toInternalValue( borderWidthRight.floatValue() ) ) );
    }

    resultCell.setTopLeft( parseCornerRadius( "border-top-left", atts ) );
    resultCell.setTopRight( parseCornerRadius( "border-top-right", atts ) );
    resultCell.setBottomLeft( parseCornerRadius( "border-bottom-left", atts ) );
    resultCell.setBottomRight( parseCornerRadius( "border-bottom-right", atts ) );

    final int rowSpan = ParserUtil.parseInt( atts.getValue( getUri(), "rowspan" ), 1 );
    final int colSpan = ParserUtil.parseInt( atts.getValue( getUri(), "colspan" ), 1 );
    for ( int r = row; r < row + rowSpan; r++ ) {
      for ( int c = column; c < column + colSpan; c++ ) {
        table.setResultCell( r, c, resultCell );
      }
    }

  }

  private BorderCorner parseCornerRadius( final String keyPrefix, final Attributes attributes ) throws SAXException {
    final String borderRadiusX = attributes.getValue( getUri(), keyPrefix + "-x" );
    if ( borderRadiusX == null ) {
      return new BorderCorner( 0, 0 );
    }
    final String borderRadiusY = attributes.getValue( getUri(), keyPrefix + "-y" );
    if ( borderRadiusY == null ) {
      return new BorderCorner( 0, 0 );
    }

    final Float radiusX = ReportParserUtil.parseFloat( borderRadiusX, getLocator() );
    final Float radiusY = ReportParserUtil.parseFloat( borderRadiusY, getLocator() );
    if ( radiusX == null || radiusX.floatValue() <= 0 || radiusY == null || radiusY.floatValue() <= 0 ) {
      return new BorderCorner( 0, 0 );
    }
    return new BorderCorner( StrictGeomUtility.toInternalValue( radiusX.floatValue() ), StrictGeomUtility
        .toInternalValue( radiusY.floatValue() ) );
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
    if ( BorderStyle.NONE.toString().equals( value ) ) {
      return BorderStyle.NONE;
    }
    return BorderStyle.NONE;
  }

  public Object getObject() throws SAXException {
    return null;
  }
}
