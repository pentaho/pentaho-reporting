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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class DataFactoriesReadHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( DataFactoriesReadHandler.class );
  private ArrayList<DataFactoryReadHandler> elements;
  private DataFactoryMetaDataCollection typeCollection;

  public DataFactoriesReadHandler() {
    elements = new ArrayList<DataFactoryReadHandler>();
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
    if ( getUri().equals( uri ) == false ) {
      return null;
    }
    if ( "data-factory".equals( tagName ) ) {
      final DataFactoryReadHandler readHandler = new DataFactoryReadHandler();
      elements.add( readHandler );
      return readHandler;
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
    final DataFactoryMetaData[] result = new DataFactoryMetaData[elements.size()];
    for ( int i = 0; i < elements.size(); i++ ) {
      final DataFactoryReadHandler handler = elements.get( i );
      result[i] = (DataFactoryMetaData) handler.getObject();
    }

    typeCollection = new DataFactoryMetaDataCollection( result );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return typeCollection;
  }
}
