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

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SelectedJNDIDataSourceReadHandler extends AbstractXmlReadHandler {
  private String jndiName;
  private String driverClass;
  private String connectionString;
  private String username;
  private String password;

  public SelectedJNDIDataSourceReadHandler() {
  }

  public String getJndiName() {
    return jndiName;
  }

  public String getDriverClass() {
    return driverClass;
  }

  public String getConnectionString() {
    return connectionString;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    jndiName = attrs.getValue( getUri(), "jndiName" );
    driverClass = attrs.getValue( getUri(), "driverClass" );
    connectionString = attrs.getValue( getUri(), "connectionString" );
    username = attrs.getValue( getUri(), "userName" );
    password = attrs.getValue( getUri(), "password" );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
