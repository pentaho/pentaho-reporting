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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.HorizontalLineType;
import org.pentaho.reporting.engine.classic.core.filter.types.VerticalLineType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ColorConverter;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;
import java.util.Properties;

public class LineReportElementReadHandler extends AbstractReportElementReadHandler {
  private Element element;
  private StrokeStyleDefinitionReadHandler strokeStyleDefinitionReadHandler;

  public LineReportElementReadHandler() {
    element = new Element();
    element.setElementType( new HorizontalLineType() );
    element.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
    element.getStyle().setStyleProperty( ElementStyleKeys.DRAW_SHAPE, Boolean.TRUE );
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "lineDefinition".equals( tagName ) ) {
        strokeStyleDefinitionReadHandler = new StrokeStyleDefinitionReadHandler();
        return strokeStyleDefinitionReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( strokeStyleDefinitionReadHandler != null ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.STROKE, strokeStyleDefinitionReadHandler.getStroke() );
      element.getStyle().setStyleProperty( ElementStyleKeys.PAINT, strokeStyleDefinitionReadHandler.getColor() );
    }

    final Properties result1 = getResult();
    final String color = result1.getProperty( "color" );
    if ( color != null ) {
      final Color c = ColorConverter.getObject( color );
      element.getStyle().setStyleProperty( ElementStyleKeys.PAINT, c );
    }

    final String lineWidth = result1.getProperty( "lineWidth" );
    if ( lineWidth != null ) {
      final float lineWidthf = ParserUtil.parseFloat( lineWidth, "Failed to parse lineWidth", getLocator() );
      element.getStyle().setStyleProperty( ElementStyleKeys.STROKE, new BasicStroke( lineWidthf ) );
    }

    final String drawBorder = result1.getProperty( "direction" );
    if ( drawBorder != null ) {
      if ( "VERTICAL".equals( drawBorder ) ) {
        element.setElementType( new VerticalLineType() );
      }
    }
  }

  protected Element getElement() {
    return element;
  }
}
