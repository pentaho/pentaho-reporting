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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.FormulaFunction;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.compat.CompatibilityMapperUtil;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserUtil;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.beans.IntrospectionException;

public class ReportFunctionReadHandler extends AbstractXmlReadHandler {
  private Expression expression;
  private BeanUtility beanUtility;

  public ReportFunctionReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    final String type = attrs.getValue( getUri(), "type" );
    if ( type.endsWith( "_DesignerWrapper" ) == false ) {
      throw new ParseException( "Magic-Token not found" );
    }

    final String realType = CompatibilityMapperUtil.mapClassName
      ( type.substring( 0, type.length() - "_DesignerWrapper".length() ) );
    final Object expression = ObjectUtilities.loadAndInstantiate
      ( realType, ReportFunctionReadHandler.class, Expression.class );
    if ( expression == null ) {
      throw new ParseException( "Specified expression does not exist" );
    }

    try {
      this.expression = (Expression) expression;
      this.beanUtility = new BeanUtility( expression );
    } catch ( IntrospectionException e ) {
      throw new ParseException( "Specified expression cannot be beaned" );
    }
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
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "padding".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }
    if ( "property".equals( tagName ) ) {
      return new DesignerExpressionPropertyReadHandler( beanUtility, expression.getName() );
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( expression instanceof FormulaExpression ) {
      final FormulaExpression formulaExpression = (FormulaExpression) expression;
      formulaExpression.setFormula( ReportDesignerParserUtil.normalizeFormula( formulaExpression.getFormula() ) );
    } else if ( expression instanceof FormulaFunction ) {
      final FormulaFunction formulaFunction = (FormulaFunction) expression;
      formulaFunction.setFormula( ReportDesignerParserUtil.normalizeFormula( formulaFunction.getFormula() ) );
      formulaFunction.setInitial( ReportDesignerParserUtil.normalizeFormula( formulaFunction.getInitial() ) );
    }

  }

  public Expression getExpression() {
    return expression;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return expression;
  }
}
