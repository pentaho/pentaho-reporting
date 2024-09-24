/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionPropertyReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.PropertyStringReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.UserDefinedExpressionPropertyReadHandler;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class ExpressionMapPropertyReadHandler extends PropertyStringReadHandler
    implements UserDefinedExpressionPropertyReadHandler {

  private BeanUtility expression;
  private String originalExpressionClass;
  private String expressionClass;
  private String expressionName;
  private List<ExpressionReadHandler> expressionHandlers;
  private String propertyName;

  @Override
  public void init( BeanUtility expression, String originalExpressionClass, String expressionClass, String expressionName ) {

    this.expression = expression;
    this.originalExpressionClass = originalExpressionClass;
    this.expressionClass = expressionClass;
    this.expressionName = expressionName;
    this.expressionHandlers = new ArrayList<>(  );
  }

  @Override
  public void startParsing( PropertyAttributes attrs ) throws SAXException {
    propertyName = attrs.getValue( getUri(), ExpressionPropertyReadHandler.NAME_ATT );
    if ( propertyName == null ) {
      throw new ParseException( "Required attribute 'name' is null.", getLocator() );
    }

  }

  @Override
  protected XmlReadHandler getHandlerForChild( String uri, String tagName, PropertyAttributes attrs ) throws SAXException {
    if ( "expression".equals( tagName ) ) {
      final ExpressionReadHandler readHandler = new ExpressionReadHandler();
      expressionHandlers.add( readHandler );
      return readHandler;
    }
    return null;
  }

  @Override
  public void doneParsing() throws SAXException {
    try {
      LinkedHashMap<String, Expression> map = new LinkedHashMap<>();
      for ( ExpressionReadHandler handler : expressionHandlers ) {
        final Expression object = handler.getObject();
        if ( object != null ) {
          final String name = object.getName();
          // Style/attribute expressions have no explicit name.
          // The parser sets the name because we have a name-attribute in the XML.
          if ( name != null ) {
            object.setName( null );
            map.put( name, object );
          }
        }
      }
      expression.setProperty( propertyName, map );
    }
    catch ( BeanException e ) {
      throw new ParseException( "Unable to set property" , e, getLocator());
    }
  }
}
