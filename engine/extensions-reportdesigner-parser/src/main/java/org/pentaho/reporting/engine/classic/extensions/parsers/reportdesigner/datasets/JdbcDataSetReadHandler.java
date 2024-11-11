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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.datasets;

import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class JdbcDataSetReadHandler extends AbstractXmlReadHandler {
  private String userName;
  private String password;
  private String driverClass;
  private String sqlQuery;
  private String connectionString;
  private SQLReportDataFactory dataFactory;

  public JdbcDataSetReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    userName = attrs.getValue( getUri(), "userName" );
    password = attrs.getValue( getUri(), "password" );
    driverClass = attrs.getValue( getUri(), "driverClass" );
    sqlQuery = attrs.getValue( getUri(), "sqlQuery" );
    connectionString = attrs.getValue( getUri(), "connectionString" );
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
    return new IgnoreAnyChildReadHandler();
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final DriverConnectionProvider connectionProvider = new DriverConnectionProvider();
    connectionProvider.setDriver( driverClass );
    connectionProvider.setUrl( connectionString );
    connectionProvider.setProperty( "user", userName );
    connectionProvider.setProperty( "password", password );
    dataFactory = new SQLReportDataFactory( connectionProvider );
    dataFactory.setQuery( "default", sqlQuery, null, null );
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
}
