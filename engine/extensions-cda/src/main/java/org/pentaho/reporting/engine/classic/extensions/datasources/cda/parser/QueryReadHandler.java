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


package org.pentaho.reporting.engine.classic.extensions.datasources.cda.parser;

import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class QueryReadHandler extends PropertyReadHandler {
  private String queryName;
  private String queryId;
  private ArrayList<VariableReadHandler> variables;
  private ParameterMapping[] queryParameters;
  private boolean legacyParsing;

  public QueryReadHandler() {
    variables = new ArrayList<VariableReadHandler>();
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  @Override
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    queryName = attrs.getValue( getUri(), "name" );
    queryId = queryName;
    if ( queryName == null ) {
      throw new ParseException( "Required attribute 'name' is not defined" );
    }

    queryId = attrs.getValue( getUri(), "query" );
    if ( StringUtils.isEmpty( queryId ) ) {
      legacyParsing = true;
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

  @Override
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "variable".equals( tagName ) ) {
      final VariableReadHandler readHandler = new VariableReadHandler();
      variables.add( readHandler );
      return readHandler;
    }

    return null;
  }

  @Override
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( legacyParsing ) {
      queryId = super.getResult();
    }

    queryParameters = new ParameterMapping[ variables.size() ];
    for ( int i = 0; i < variables.size(); i++ ) {
      final VariableReadHandler handler = variables.get( i );
      final ParameterMapping queryParameter =
        new ParameterMapping( handler.getDataRowName(), handler.getVariableName() );
      queryParameters[ i ] = queryParameter;
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  @Override
  public Object getObject() {
    return new ParameterMapping( queryName, queryId );
  }

  public String getQueryId() {
    return queryId;
  }

  public String getQueryName() {
    return queryName;
  }

  public ParameterMapping[] getParameters() {
    return queryParameters.clone();
  }
}
