/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.data;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.ExpressionReadHandler;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class DataDefinitionRootElementHandler extends AbstractXmlReadHandler {
  private MasterParameterDefinitionReadHandler parameterDefinitionHandler;

  private DataDefinition dataDefinition;
  private DataSourceElementHandler dataSourceElementHandler;
  private ArrayList<ExpressionReadHandler> expressionHandlers;

  public DataDefinitionRootElementHandler() {
    expressionHandlers = new ArrayList<ExpressionReadHandler>();
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

    if ( "parameter-definition".equals( tagName ) ) {
      parameterDefinitionHandler = new MasterParameterDefinitionReadHandler();
      return parameterDefinitionHandler;
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
    final DataFactory primaryDataFactory;
    if ( dataSourceElementHandler == null ) {
      primaryDataFactory = null;
      primaryQuery = null;
      primaryQueryLimit = 0;
      primaryQueryTimeout = 0;
    } else {
      primaryDataFactory = dataSourceElementHandler.getDataFactory();
      primaryQuery = dataSourceElementHandler.getQuery();
      primaryQueryLimit = dataSourceElementHandler.getQueryLimit();
      primaryQueryTimeout = dataSourceElementHandler.getQueryTimeout();
    }

    final ReportParameterDefinition reportParameterDefinition;
    if ( parameterDefinitionHandler != null ) {
      reportParameterDefinition = (ReportParameterDefinition) parameterDefinitionHandler.getObject();
    } else {
      reportParameterDefinition = null;
    }

    final ArrayList<Expression> expressionsList = new ArrayList<Expression>();
    for ( int i = 0; i < expressionHandlers.size(); i++ ) {
      final ExpressionReadHandler readHandler = expressionHandlers.get( i );
      if ( readHandler.getObject() != null ) {
        expressionsList.add( (Expression) readHandler.getObject() );
      }
    }
    final Expression[] expressions = expressionsList.toArray( new Expression[expressionHandlers.size()] );

    dataDefinition =
        new DataDefinition( reportParameterDefinition, primaryDataFactory, primaryQuery, primaryQueryLimit,
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
