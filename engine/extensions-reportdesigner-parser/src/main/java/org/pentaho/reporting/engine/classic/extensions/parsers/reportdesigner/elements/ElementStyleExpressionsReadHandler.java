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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.HashMap;

public class ElementStyleExpressionsReadHandler extends AbstractXmlReadHandler {
  private ArrayList styleExpressions;
  private HashMap expressions;

  public ElementStyleExpressionsReadHandler() {
    styleExpressions = new ArrayList();
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
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "styleExpression".equals( tagName ) ) {
      final ElementStyleExpressionReadHandler readHandler = new ElementStyleExpressionReadHandler();
      styleExpressions.add( readHandler );
      return readHandler;
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
    expressions = new HashMap();
    for ( int i = 0; i < styleExpressions.size(); i++ ) {
      final ElementStyleExpressionReadHandler handler = (ElementStyleExpressionReadHandler) styleExpressions.get( i );
      final FormulaExpression expression = new FormulaExpression();
      expression.setFormula( ReportDesignerParserUtil.normalizeFormula( handler.getFormula() ) );
      expressions.put( handler.getStyleKey(), expression );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return expressions;
  }
}
