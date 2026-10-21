/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
  private static final Log logger = LogFactory.getLog( ExpressionReadHandler.class );

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
    if ( className == null ) {
      parseFormulaExpression( attrs, expressionName, elementType, depLevel );
    } else {
      parseClassExpression( className, expressionName, elementType, depLevel );
    }
  }

  private void parseFormulaExpression( final PropertyAttributes attrs, final String expressionName,
                                       final String elementType, final int depLevel ) {
    final String formula = attrs.getValue( getUri(), "formula" );
    final String initial = attrs.getValue( getUri(), "initial" );
    if ( initial != null ) {
      final FormulaFunction function = new FormulaFunction();
      function.setInitial( initial );
      function.setFormula( formula );
      function.setName( expressionName );
      function.setDependencyLevel( depLevel );
      this.expression = function;
      this.originalClassName = FormulaFunction.class.getName();
      this.expressionClassName = FormulaFunction.class.getName();
    } else {
      final String failOnError = attrs.getValue( getUri(), "failOnError" );
      final FormulaExpression formulaExpression = new FormulaExpression();
      formulaExpression.setFormula( formula );
      if ( failOnError != null ) {
        formulaExpression.setFailOnError( Boolean.parseBoolean( failOnError ) );
      }
      formulaExpression.setName( expressionName );
      formulaExpression.setDependencyLevel( depLevel );
      formulaExpression.setElementType( elementType );
      this.expression = formulaExpression;
      this.originalClassName = FormulaExpression.class.getName();
      this.expressionClassName = FormulaExpression.class.getName();
    }
  }

  private void parseClassExpression( final String className, final String expressionName, final String elementType,
                                     final int depLevel ) throws ParseException {
    final String mappedName = CompatibilityMapperUtil.mapClassName( className );
    if ( CompatibilityMapperUtil.isIgnoredExpression( mappedName ) ) {
      logger.warn( "Expression '" + expressionName + "' of class '" + className
          + "' is no longer supported and has been ignored. " + getLocator() );
      return;
    }
    expression = (Expression) ObjectUtilities.loadAndInstantiate( mappedName, getClass(), Expression.class );
    if ( expression == null ) {
      throw new ParseException( "Expression '" + className + "' is not valid.", getLocator() );
    }
    expression.setName( expressionName );
    expression.setDependencyLevel( depLevel );
    if ( expression instanceof FormulaExpression ) {
      ( (FormulaExpression) expression ).setElementType( elementType );
    }
    this.originalClassName = className;
    this.expressionClassName = mappedName;
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
