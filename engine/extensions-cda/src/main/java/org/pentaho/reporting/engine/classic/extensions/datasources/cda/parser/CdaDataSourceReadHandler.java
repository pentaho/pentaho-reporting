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

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.CdaQueryEntry;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class CdaDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ConfigReadHandler configReadHandler;
  private ArrayList<XmlReadHandler> queries;
  private CdaDataFactory dataFactory;

  public CdaDataSourceReadHandler() {
    queries = new ArrayList<XmlReadHandler>();
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
    if ( "config".equals( tagName ) ) {
      configReadHandler = new ConfigReadHandler();
      return configReadHandler;
    }

    if ( "query".equals( tagName ) ) {
      final QueryReadHandler readHandler = new QueryReadHandler();
      queries.add( readHandler );
      return readHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final CdaDataFactory srdf = new CdaDataFactory();
    if ( configReadHandler == null ) {
      throw new ParseException( "Required element 'config' is missing.", getLocator() );
    }

    srdf.setBaseUrl( configReadHandler.getBaseUrl() );
    srdf.setBaseUrlField( configReadHandler.getBaseUrlField() );
    srdf.setFile( configReadHandler.getFile() );
    srdf.setPath( configReadHandler.getPath() );
    srdf.setSolution( configReadHandler.getSolution() );
    srdf.setUsername( configReadHandler.getUsername() );
    srdf.setPassword( configReadHandler.getPassword() );
    srdf.setUseLocalCall( configReadHandler.isUseLocalCall() );
    srdf.setSugarMode( configReadHandler.isSugarMode() );

    for ( int i = 0; i < queries.size(); i++ ) {
      final QueryReadHandler handler = (QueryReadHandler) queries.get( i );
      final CdaQueryEntry cdaqueryentry = new CdaQueryEntry( handler.getQueryName(), handler.getQueryId() );
      cdaqueryentry.setParameters( handler.getParameters() );
      srdf.setQueryEntry( handler.getQueryName(), cdaqueryentry );
    }
    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
