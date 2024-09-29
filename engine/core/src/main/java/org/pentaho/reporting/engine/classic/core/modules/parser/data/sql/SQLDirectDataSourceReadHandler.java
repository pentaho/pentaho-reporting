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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.sql;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SimpleSQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Creation-Date: 07.04.2006, 17:47:53
 *
 * @author Thomas Morgner
 */
public class SQLDirectDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ConnectionReadHandler connectionProviderReadHandler;
  private ConfigReadHandler configReadHandler;
  private DataFactory dataFactory;

  public SQLDirectDataSourceReadHandler() {
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
    final ConnectionReadHandlerFactory factory = ConnectionReadHandlerFactory.getInstance();
    final ConnectionReadHandler handler = (ConnectionReadHandler) factory.getHandler( uri, tagName );
    if ( handler != null ) {
      connectionProviderReadHandler = handler;
      return connectionProviderReadHandler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "config".equals( tagName ) ) {
      configReadHandler = new ConfigReadHandler();
      return configReadHandler;
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
    ConnectionProvider provider = null;
    if ( connectionProviderReadHandler != null ) {
      provider = (ConnectionProvider) connectionProviderReadHandler.getObject();
    }
    if ( provider == null ) {
      provider = (ConnectionProvider) getRootHandler().getHelperObject( "connection-provider" );
    }
    if ( provider == null ) {
      throw new SAXException( "Unable to create SQL Factory: No connection provider specified or recognized." );
    }

    final SimpleSQLReportDataFactory srdf = new SimpleSQLReportDataFactory( provider );
    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
