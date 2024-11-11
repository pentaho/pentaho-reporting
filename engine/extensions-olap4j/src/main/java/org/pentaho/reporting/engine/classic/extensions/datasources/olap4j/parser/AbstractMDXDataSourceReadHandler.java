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


package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.parser;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractMDXDataSourceReadHandler extends AbstractXmlReadHandler
  implements DataFactoryReadHandler {
  private AbstractMDXDataFactory dataFactory;
  private OlapConnectionReadHandler connectionProviderReadHandler;
  private StringReadHandler roleField;
  private StringReadHandler jdbcUserField;
  private StringReadHandler jdbcPasswordField;

  public AbstractMDXDataSourceReadHandler() {
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {

    final OlapConnectionReadHandlerFactory factory = OlapConnectionReadHandlerFactory.getInstance();
    final XmlReadHandler handler = factory.getHandler( uri, tagName );
    if ( handler instanceof OlapConnectionReadHandler ) {
      connectionProviderReadHandler = (OlapConnectionReadHandler) handler;
      return connectionProviderReadHandler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "role-field".equals( tagName ) ) {
      roleField = new StringReadHandler();
      return roleField;
    }
    if ( "jdbc-user-field".equals( tagName ) ) {
      jdbcUserField = new StringReadHandler();
      return jdbcUserField;
    }
    if ( "jdbc-password-field".equals( tagName ) ) {
      jdbcPasswordField = new StringReadHandler();
      return jdbcPasswordField;
    }
    return null;

  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    OlapConnectionProvider provider = null;
    if ( connectionProviderReadHandler != null ) {
      provider = (OlapConnectionProvider) connectionProviderReadHandler.getObject();
    }
    if ( provider == null ) {
      provider = (OlapConnectionProvider) getRootHandler().getHelperObject( "olap-connection-provider" );
    }
    if ( provider == null ) {
      throw new SAXException(
        "Unable to create OLAP4J Factory: No connection provider specified or recognized." );
    }

    dataFactory = createDataFactory( provider );
    if ( roleField != null ) {
      dataFactory.setRoleField( roleField.getResult() );
    }
    if ( jdbcUserField != null ) {
      dataFactory.setJdbcUserField( jdbcUserField.getResult() );
    }
    if ( jdbcPasswordField != null ) {
      dataFactory.setJdbcPasswordField( jdbcPasswordField.getResult() );
    }
  }

  protected abstract AbstractMDXDataFactory createDataFactory( OlapConnectionProvider connectionProvider );


  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }
}
