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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;

public class ExpressionPropertiesReadHandler extends AbstractPropertyXmlReadHandler {
  private BeanUtility beanUtility;
  private Expression expression;

  private String originalExpressionClass;
  private String expressionClass;

  public ExpressionPropertiesReadHandler( final Expression expression, final String originalExpressionClass,
      final String expressionClass ) throws IntrospectionException {
    this.originalExpressionClass = originalExpressionClass;
    this.expressionClass = expressionClass;
    this.beanUtility = new BeanUtility( expression );
    this.expression = expression;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes attrs )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "property".equals( tagName ) ) {
      final String propertyName = attrs.getValue( getUri(), ExpressionPropertyReadHandler.NAME_ATT );
      return createReadHandler( propertyName );
    }
    return null;
  }

  private XmlReadHandler createReadHandler( String propertyName ) throws ParseException {
    try {
      final ExpressionMetaData expressionMetaData = ExpressionRegistry.getInstance().getExpressionMetaData( expressionClass );
      final ExpressionPropertyMetaData propertyDescription = expressionMetaData.getPropertyDescription( propertyName );
      if ( propertyDescription != null ) {
        final Class<? extends UserDefinedExpressionPropertyReadHandler> propertyReadHandler = propertyDescription.getPropertyReadHandler();
        if ( propertyReadHandler != null ) {
          final UserDefinedExpressionPropertyReadHandler xmlReadHandler = propertyReadHandler.newInstance();
          xmlReadHandler.init( beanUtility, originalExpressionClass, expressionClass, expression.getName() );
          return xmlReadHandler;
        }
      }
      return new ExpressionPropertyReadHandler( beanUtility, originalExpressionClass,
          expressionClass, expression.getName() );
    }
    catch ( MetaDataLookupException e ) {
      return new ExpressionPropertyReadHandler( beanUtility, originalExpressionClass,
          expressionClass, expression.getName() );
    }
    catch ( Exception e ) {
      throw new ParseException( "Unable to read metadata for property '" + propertyName + "'.", getLocator());
    }

  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return this;
  }
}
