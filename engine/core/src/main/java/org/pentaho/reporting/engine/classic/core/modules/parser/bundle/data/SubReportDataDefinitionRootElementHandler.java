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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class SubReportDataDefinitionRootElementHandler extends AbstractXmlReadHandler {
  private ParameterMappingsReadHandler parameterMappingsHandler;
  private SubReportDataDefinition dataDefinition;
  private DataSourceElementHandler dataSourceElementHandler;
  private ArrayList expressionHandlers;
  private static final ParameterMapping[] EMPTY_PARAMETERMAPPINGS = new ParameterMapping[0];

  public SubReportDataDefinitionRootElementHandler() {
    expressionHandlers = new ArrayList();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
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

    if ( "parameter-mapping".equals( tagName ) ) {
      parameterMappingsHandler = new ParameterMappingsReadHandler();
      return parameterMappingsHandler;
    }

    if ( "expression".equals( tagName ) ) {
      final ExpressionReadHandler readHandler = new ExpressionReadHandler();
      expressionHandlers.add( readHandler );
      return readHandler;
    }

    if ( "data-source".equals( tagName ) ) {
      dataSourceElementHandler = new DataSourceElementHandler();
      return dataSourceElementHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final String primaryQuery;
    final int primaryQueryLimit;
    final int primaryQueryTimeout;
    final DataFactory dataFactory;
    if ( dataSourceElementHandler == null ) {
      primaryQuery = null;
      primaryQueryLimit = 0;
      primaryQueryTimeout = 0;
      dataFactory = null;
    } else {
      primaryQuery = dataSourceElementHandler.getQuery();
      primaryQueryLimit = dataSourceElementHandler.getQueryLimit();
      primaryQueryTimeout = dataSourceElementHandler.getQueryTimeout();
      dataFactory = dataSourceElementHandler.getDataFactory();
    }

    final ParameterMapping[] importMappings;
    final ParameterMapping[] exportMappings;
    if ( parameterMappingsHandler == null ) {
      importMappings = SubReportDataDefinitionRootElementHandler.EMPTY_PARAMETERMAPPINGS;
      exportMappings = SubReportDataDefinitionRootElementHandler.EMPTY_PARAMETERMAPPINGS;
    } else {
      importMappings = parameterMappingsHandler.getInputMapping();
      exportMappings = parameterMappingsHandler.getExportMapping();
    }

    final ArrayList<Expression> expressionsList = new ArrayList<Expression>();
    for ( int i = 0; i < expressionHandlers.size(); i++ ) {
      final ExpressionReadHandler readHandler = (ExpressionReadHandler) expressionHandlers.get( i );
      expressionsList.add( (Expression) readHandler.getObject() );
    }

    final Expression[] expressions = expressionsList.toArray( new Expression[expressionHandlers.size()] );
    dataDefinition =
        new SubReportDataDefinition( importMappings, exportMappings, dataFactory, primaryQuery, primaryQueryLimit,
            primaryQueryTimeout, expressions );

  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return dataDefinition;
  }
}
