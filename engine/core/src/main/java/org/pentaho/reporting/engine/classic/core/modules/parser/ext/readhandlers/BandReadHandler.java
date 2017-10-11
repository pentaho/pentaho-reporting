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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.StyleExpressionHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.elements.ElementFactoryCollector;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class BandReadHandler extends ElementReadHandler {
  private static final Log logger = LogFactory.getLog( BandReadHandler.class );
  private ArrayList elementHandlers;
  private ArrayList styleExpressionHandlers;

  public BandReadHandler( final Band element ) {
    super( element );
    elementHandlers = new ArrayList();
    this.styleExpressionHandlers = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "style".equals( tagName ) ) {
      return new StyleReadHandler( getElement().getStyle() );
    } else if ( "default-style".equals( tagName ) ) {
      BandReadHandler.logger.warn( "Tag <default-style> is deprecated. All definitions "
          + "have been mapped into the bands primary style sheet." );
      return new StyleReadHandler( getElement().getStyle() );
    }
    if ( "style-expression".equals( tagName ) ) {
      final StyleExpressionHandler styleExpressionHandler = new StyleExpressionHandler();
      styleExpressionHandlers.add( styleExpressionHandler );
      return styleExpressionHandler;
    } else if ( "element".equals( tagName ) ) {
      // type is not really used anymore. We always return org.pentaho.reporting.engine.classic.core.Element
      final String type = atts.getValue( getUri(), "type" );

      final ElementFactoryCollector fc =
          (ElementFactoryCollector) getRootHandler().getHelperObject( ReportDefinitionReadHandler.ELEMENT_FACTORY_KEY );
      final Element element = fc.getElementForType( type );
      if ( element == null ) {
        throw new ParseException( "There is no factory for elements of type '" + type + '\'', getLocator() );
      }

      final XmlReadHandler readHandler = new ElementReadHandler( element );
      elementHandlers.add( readHandler );
      return readHandler;
    } else if ( "band".equals( tagName ) ) {
      final XmlReadHandler readHandler = new BandReadHandler( new Band() );
      elementHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Band band = (Band) getElement();
    for ( int i = 0; i < elementHandlers.size(); i++ ) {
      final ElementReadHandler readHandler = (ElementReadHandler) elementHandlers.get( i );
      band.addElement( readHandler.getElement() );
    }
    for ( int i = 0; i < styleExpressionHandlers.size(); i++ ) {
      final StyleExpressionHandler handler = (StyleExpressionHandler) styleExpressionHandlers.get( i );
      final StyleKey key = handler.getKey();
      if ( handler.getKey() != null ) {
        final Expression expression = handler.getExpression();
        band.setStyleExpression( key, expression );
      }
    }
  }
}
