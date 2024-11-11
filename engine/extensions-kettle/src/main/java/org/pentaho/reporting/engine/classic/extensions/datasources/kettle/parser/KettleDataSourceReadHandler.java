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


package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class KettleDataSourceReadHandler extends AbstractXmlReadHandler
  implements DataFactoryReadHandler {
  private ArrayList<KettleTransformationProducerReadHandler> queries;
  private DataFactory dataFactory;

  public KettleDataSourceReadHandler() {
    queries = new ArrayList<KettleTransformationProducerReadHandler>();
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    final KettleTransformationProducerReadHandlerFactory instance =
      KettleTransformationProducerReadHandlerFactory.getInstance();
    final KettleTransformationProducerReadHandler queryReadHandler = instance.getHandler( uri, tagName );
    if ( queryReadHandler != null ) {
      queries.add( queryReadHandler );
      return queryReadHandler;
    }
    return null;
  }

  protected void addQueryHandler( final KettleTransformationProducerReadHandler readHandler ) {
    queries.add( readHandler );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final KettleDataFactory srdf = new KettleDataFactory();
    for ( int i = 0; i < queries.size(); i++ ) {
      final KettleTransformationProducerReadHandler handler = queries.get( i );
      srdf.setQuery( handler.getName(), handler.getTransformationProducer() );
    }

    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
