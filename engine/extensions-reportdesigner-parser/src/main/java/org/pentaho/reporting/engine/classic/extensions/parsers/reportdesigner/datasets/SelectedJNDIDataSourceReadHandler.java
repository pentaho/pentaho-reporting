/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
