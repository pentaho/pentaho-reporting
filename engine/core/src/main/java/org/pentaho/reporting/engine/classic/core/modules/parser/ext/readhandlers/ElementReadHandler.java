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

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.AbstractPropertyXmlReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.StyleExpressionHandler;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class ElementReadHandler extends AbstractPropertyXmlReadHandler {
  private ArrayList styleExpressionHandlers;
  private XmlReadHandler dataSourceHandler;
  private Element element;

  public ElementReadHandler( final Element element ) {
    if ( element == null ) {
      throw new NullPointerException( "Element given must not be null." );
    }
    this.element = element;
    this.styleExpressionHandlers = new ArrayList();
  }

  protected Element getElement() {
    return element;
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    final String name = attrs.getValue( getUri(), "name" );
    if ( name != null ) {
      element.setName( name );
    }

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
      return new StyleReadHandler( element.getStyle() );
    }
    if ( "style-expression".equals( tagName ) ) {
      final StyleExpressionHandler styleExpressionHandler = new StyleExpressionHandler();
      styleExpressionHandlers.add( styleExpressionHandler );
      return styleExpressionHandler;
    } else if ( "datasource".equals( tagName ) ) {
      dataSourceHandler = new DataSourceReadHandler();
      return dataSourceHandler;
    } else if ( "template".equals( tagName ) ) {
      dataSourceHandler = new TemplateReadHandler( false );
      return dataSourceHandler;
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
    if ( dataSourceHandler != null ) {
      element.setDataSource( (DataSource) dataSourceHandler.getObject() );
    }

    for ( int i = 0; i < styleExpressionHandlers.size(); i++ ) {
      final StyleExpressionHandler handler = (StyleExpressionHandler) styleExpressionHandlers.get( i );
      final StyleKey key = handler.getKey();
      if ( handler.getKey() != null ) {
        final Expression expression = handler.getExpression();
        element.setStyleExpression( key, expression );
      }
    }

    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, getRootHandler().getSource() );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return element;
  }
}
