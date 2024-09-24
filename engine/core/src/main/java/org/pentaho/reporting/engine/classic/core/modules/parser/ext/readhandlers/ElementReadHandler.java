/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
