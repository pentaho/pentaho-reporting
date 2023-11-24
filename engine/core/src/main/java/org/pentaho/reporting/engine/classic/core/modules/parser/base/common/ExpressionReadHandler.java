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
 * Copyright (c) 2001 - 2023 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FormulaFunction;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;

public class ExpressionReadHandler extends AbstractPropertyXmlReadHandler {
  /**
   * The dependency level attribute.
   */
  public static final String DEPENCY_LEVEL_ATT = "deplevel";

  private String originalClassName;
  private String expressionClassName;
  private Expression expression;

  public ExpressionReadHandler() {
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
    final int depLevel = ParserUtil.parseInt( attrs.getValue( getUri(), ExpressionReadHandler.DEPENCY_LEVEL_ATT ), 0 );
    final String expressionName = attrs.getValue( getUri(), "name" );
    final String elementType = attrs.getValue( getUri(), "elementType" );
    final String className = attrs.getValue( getUri(), "class" );
    final String formula = attrs.getValue( getUri(), "formula" );
    final String failOnError = attrs.getValue( getUri(), "failOnError" );
    if ( className == null ) {
      final String initial = attrs.getValue( getUri(), "initial" );
      if ( initial != null ) {
        final FormulaFunction function = new FormulaFunction();
        function.setInitial( initial );
        function.setFormula( formula );
        this.expression = function;
        this.expression.setName( expressionName );
        this.expression.setDependencyLevel( depLevel );
        if ( this.expression instanceof FormulaExpression ) {
          ( ( FormulaExpression ) this.expression ).setElementType( elementType );
        }
        this.originalClassName = FormulaFunction.class.getName();
        this.expressionClassName = FormulaFunction.class.getName();
      } else {
        final FormulaExpression expression = new FormulaExpression();
        expression.setFormula( formula );
        if ( failOnError != null ) {
          expression.setFailOnError( Boolean.parseBoolean( failOnError ) );
        }
        this.expression = expression;
        this.expression.setName( expressionName );
        this.expression.setDependencyLevel( depLevel );
        if ( this.expression instanceof FormulaExpression ) {
          ( ( FormulaExpression ) this.expression ).setElementType( elementType );
        }
        this.originalClassName = FormulaExpression.class.getName();
        this.expressionClassName = FormulaExpression.class.getName();
      }
    }

    if ( expression == null && className != null ) {
      final String mappedName = CompatibilityMapperUtil.mapClassName( className );
      expression = (Expression) ObjectUtilities.loadAndInstantiate( mappedName, getClass(), Expression.class );
      if ( expression == null ) {
        throw new ParseException( "Expression '" + className + "' is not valid.", getLocator() );
      }
      expression.setName( expressionName );
      expression.setDependencyLevel( depLevel );
      if ( this.expression instanceof FormulaExpression ) {
        ( ( FormulaExpression ) this.expression ).setElementType( elementType );
      }
      this.originalClassName = className;
      this.expressionClassName = mappedName;
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

    if ( "properties".equals( tagName ) && expression != null ) {
      try {
        return new ExpressionPropertiesReadHandler( expression, originalClassName, expressionClassName );
      } catch ( IntrospectionException e ) {
        throw new ParseException( "Unable to create Introspector for the specified expression.", getLocator() );
      }
    }
    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Expression getObject() {
    return expression;
  }
}
