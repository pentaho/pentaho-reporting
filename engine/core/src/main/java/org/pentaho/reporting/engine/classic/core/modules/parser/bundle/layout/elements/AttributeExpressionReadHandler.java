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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionPropertiesReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;

public class AttributeExpressionReadHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( AttributeExpressionReadHandler.class );

  private Expression expression;
  private String name;
  private String namespace;
  private String originalExpressionClass;
  private String expressionClassName;

  public AttributeExpressionReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new SAXException( "Required attribute 'name' is missing." );
    }

    namespace = attrs.getValue( getUri(), "namespace" );
    if ( namespace == null ) {
      throw new SAXException( "Required attribute 'namespace' is missing." );
    }

    originalExpressionClass = attrs.getValue( getUri(), "class" );
    expressionClassName = CompatibilityMapperUtil.mapClassName( originalExpressionClass );
    final String formula = attrs.getValue( getUri(), "formula" );
    if ( expressionClassName == null ) {
      if ( formula != null ) {
        final FormulaExpression expression = new FormulaExpression();
        expression.setFormula( formula );
        this.expression = expression;
        this.expression.setName( name ); // doesnt matter anyway, but it feels good :)
      } else {
        logger.warn( "Required attribute 'class' is missing. Gracefully ignoring the error." + getLocator() );
      }
    }

    if ( expression == null && expressionClassName != null ) {

      expression = ObjectUtilities.loadAndInstantiate( expressionClassName, getClass(), Expression.class );
      if ( expression == null ) {
        throw new ParseException( "Expression '" + expressionClassName + "' is not valid.", getLocator() );
      }
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
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "properties".equals( tagName ) && expression != null ) {
      try {
        return new ExpressionPropertiesReadHandler( expression, originalExpressionClass, expressionClassName );
      } catch ( IntrospectionException e ) {
        throw new SAXException( "Unable to create Introspector for the specified expression." );
      }
    }
    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return expression;
  }

  public Expression getExpression() {
    return expression;
  }

  public String getName() {
    return name;
  }

  public String getNamespace() {
    return namespace;
  }
}
