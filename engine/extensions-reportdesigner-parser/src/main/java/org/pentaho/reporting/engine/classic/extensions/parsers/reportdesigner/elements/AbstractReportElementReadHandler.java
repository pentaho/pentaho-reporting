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
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ColorConverter;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.DoubleDimensionConverter;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.Point2DConverter;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public abstract class AbstractReportElementReadHandler extends PropertiesReadHandler {
  private ElementStyleExpressionsReadHandler styleExpressions;

  public AbstractReportElementReadHandler() {
  }

  protected ElementStyleSheet getStyle() {
    return getElement().getStyle();
  }

  protected abstract Element getElement();

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
    if ( isSameNamespace( getUri() ) == false ) {
      return null;
    }
    if ( "styleExpressions".equals( tagName ) ) {
      styleExpressions = new ElementStyleExpressionsReadHandler();
      return styleExpressions;
    }
    if ( "padding".equals( tagName ) ) {
      return new ElementPaddingReadHandler( getStyle() );
    }
    if ( "elementBorder".equals( tagName ) ) {
      return new ElementBorderReadHandler( getStyle() );
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
    final Properties result1 = getResult();

    final String name = result1.getProperty( "name" );
    if ( name != null ) {
      getElement().setName( name );
    }

    final String positionText = result1.getProperty( "position" );
    if ( positionText != null ) {
      final Point2D pos = (Point2D) new Point2DConverter().convertFromString( positionText, getLocator() );
      getStyle().setStyleProperty( ElementStyleKeys.POS_X, new Float( pos.getX() ) );
      getStyle().setStyleProperty( ElementStyleKeys.POS_Y, new Float( pos.getY() ) );
    }

    final String minSizeText = result1.getProperty( "minimumSize" );
    if ( minSizeText != null ) {
      final Dimension2D size =
        (Dimension2D) new DoubleDimensionConverter().convertFromString( minSizeText, getLocator() );
      getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( size.getWidth() ) );
      getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( size.getHeight() ) );
    }

    final String prefSizeText = result1.getProperty( "preferredSize" );
    if ( prefSizeText != null ) {
      final Dimension2D size =
        (Dimension2D) new DoubleDimensionConverter().convertFromString( prefSizeText, getLocator() );
      getStyle().setStyleProperty( ElementStyleKeys.WIDTH, new Float( size.getWidth() ) );
      getStyle().setStyleProperty( ElementStyleKeys.HEIGHT, new Float( size.getHeight() ) );
    }

    final String maxSizeText = result1.getProperty( "maximumSize" );
    if ( maxSizeText != null ) {
      final Dimension2D size =
        (Dimension2D) new DoubleDimensionConverter().convertFromString( maxSizeText, getLocator() );
      getStyle().setStyleProperty( ElementStyleKeys.MAX_WIDTH, new Float( size.getWidth() ) );
      getStyle().setStyleProperty( ElementStyleKeys.MAX_HEIGHT, new Float( size.getHeight() ) );
    }

    final String bgColorText = result1.getProperty( "background" );
    if ( bgColorText != null ) {
      final Color c = ColorConverter.getObject( bgColorText );
      getStyle().setStyleProperty( ElementStyleKeys.BACKGROUND_COLOR, c );
    }
    final String dynContent = result1.getProperty( "dynamicContent" );
    if ( dynContent != null ) {
      if ( "true".equals( dynContent ) ) {
        getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.FALSE );
      }
    }

    final Map map = getStyleExpressions();
    final Iterator iterator = map.entrySet().iterator();
    while ( iterator.hasNext() ) {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final StyleKey key = (StyleKey) entry.getKey();
      final Expression expression = (Expression) entry.getValue();
      getElement().setStyleExpression( key, expression );
    }
  }

  protected Map getStyleExpressions() throws SAXException {
    if ( styleExpressions != null ) {
      return (Map) styleExpressions.getObject();
    }
    return Collections.emptyMap();
  }
}
