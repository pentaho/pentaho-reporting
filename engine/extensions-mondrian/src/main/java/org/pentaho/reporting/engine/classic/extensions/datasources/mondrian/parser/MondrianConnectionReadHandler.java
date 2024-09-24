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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PasswordEncryptionService;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DriverDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.JndiDataSourceProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class MondrianConnectionReadHandler extends AbstractXmlReadHandler {
  private static final Log logger = LogFactory.getLog( MondrianConnectionReadHandler.class );

  private String driverClass;
  private String connectionString;
  private String mondrianCubeDefinitionFile;
  private String dataSourceName;
  private PropertiesReadHandler propertiesReadHandler;

  private String jdbcPassword;
  private String jdbcPasswordField;
  private String jdbcUser;
  private String jdbcUserField;
  private String designTimeName;
  private String role;
  private String roleField;

  private DataSourceProviderReadHandler dataSourceProviderHandler;
  private CubeFileProviderReadHandler cubeFileProviderReadHandler;

  public MondrianConnectionReadHandler() {
  }

  public String getJdbcPassword() {
    return jdbcPassword;
  }

  public String getJdbcUser() {
    return jdbcUser;
  }

  public String getRole() {
    return role;
  }

  public String getRoleField() {
    return roleField;
  }

  public String getDesignTimeName() {
    return designTimeName;
  }

  public CubeFileProvider getCubeFileProvider() {
    if ( cubeFileProviderReadHandler != null ) {
      return cubeFileProviderReadHandler.getProvider();
    }

    if ( mondrianCubeDefinitionFile != null ) {
      // legacy report usecase
      final CubeFileProvider cubeFileProvider =
        ClassicEngineBoot.getInstance().getObjectFactory().get( CubeFileProvider.class );
      cubeFileProvider.setDesignTimeFile( mondrianCubeDefinitionFile );
      return cubeFileProvider;
    }
    return null;
  }

  public DataSourceProvider getDataSourceProvider() {
    if ( dataSourceProviderHandler != null ) {
      return dataSourceProviderHandler.getProvider();
    }
    if ( dataSourceName != null ) {
      return new JndiDataSourceProvider( dataSourceName );
    }
    if ( connectionString != null ) {
      final DriverDataSourceProvider driverDataSourceProvider = new DriverDataSourceProvider();
      driverDataSourceProvider.setDriver( driverClass );
      driverDataSourceProvider.setUrl( connectionString );
      if ( propertiesReadHandler != null ) {
        final Properties p = propertiesReadHandler.getResult();
        final Iterator it = p.entrySet().iterator();
        while ( it.hasNext() ) {
          final Map.Entry entry = (Map.Entry) it.next();
          driverDataSourceProvider.setProperty( (String) entry.getKey(), (String) entry.getValue() );
        }
      }
      return driverDataSourceProvider;
    }
    return null;
  }

  public String getJdbcPasswordField() {
    return jdbcPasswordField;
  }

  public String getJdbcUserField() {
    return jdbcUserField;
  }

  /**
   * Starts parsing.
   *
   * @param attrs the attributes.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );

    dataSourceName = attrs.getValue( getUri(), "datasource-name" );
    connectionString = attrs.getValue( getUri(), "connection-string" );
    driverClass = attrs.getValue( getUri(), "jdbc-driver" );
    mondrianCubeDefinitionFile = attrs.getValue( getUri(), "mondrian-cube-definition" );

    designTimeName = attrs.getValue( getUri(), "design-time-name" );

    jdbcPassword = PasswordEncryptionService.getInstance().decrypt
      ( getRootHandler(), attrs.getValue( getUri(), "jdbc-password" ) );
    jdbcPasswordField = attrs.getValue( getUri(), "jdbc-password-field" );

    jdbcUser = attrs.getValue( getUri(), "jdbc-user" );
    jdbcUserField = attrs.getValue( getUri(), "jdbc-user-field" );

    role = attrs.getValue( getUri(), "role" );
    roleField = attrs.getValue( getUri(), "role-field" );
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
    final DataSourceProviderReadHandlerFactory dataSourceProviderReadHandlerFactory =
      DataSourceProviderReadHandlerFactory.getInstance();
    final XmlReadHandler dataSourceHandler = dataSourceProviderReadHandlerFactory.getHandler( uri, tagName );
    if ( dataSourceHandler instanceof DataSourceProviderReadHandler ) {
      dataSourceProviderHandler = (DataSourceProviderReadHandler) dataSourceHandler;
      return dataSourceProviderHandler;
    }

    final CubeFileProviderReadHandlerFactory cubeFileProviderReadHandlerFactory =
      CubeFileProviderReadHandlerFactory.getInstance();
    final XmlReadHandler cubeHandler = cubeFileProviderReadHandlerFactory.getHandler( uri, tagName );
    if ( cubeHandler instanceof CubeFileProviderReadHandler ) {
      cubeFileProviderReadHandler = (CubeFileProviderReadHandler) cubeHandler;
      return cubeFileProviderReadHandler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "properties".equals( tagName ) ) {
      propertiesReadHandler = new PropertiesReadHandler();
      return propertiesReadHandler;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( cubeFileProviderReadHandler != null && dataSourceProviderHandler != null ) {
      return;
    }

    if ( cubeFileProviderReadHandler == null ) {
      if ( mondrianCubeDefinitionFile == null ) {
        throw new ParseException( "Mondrian-schema file is not defined.", getLocator() );
      }
    }

    if ( dataSourceProviderHandler == null ) {
      if ( dataSourceName == null && connectionString == null ) {
        throw new ParseException( "No Connection Information found. This is a invalid datasource.", getLocator() );
      }
    }

    logger.warn( "This is a obsolete modrian-datasource definition. " +
      "Load and save the report in PRD-3.5 to migrate it to the final version." );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return null;
  }

  public void configure( final AbstractMDXDataFactory dataFactory ) {
    dataFactory.setCubeFileProvider( getCubeFileProvider() );
    dataFactory.setDataSourceProvider( getDataSourceProvider() );
    dataFactory.setJdbcPassword( getJdbcPassword() );
    dataFactory.setJdbcUser( getJdbcUser() );
    dataFactory.setJdbcPasswordField( getJdbcPasswordField() );
    dataFactory.setJdbcUserField( getJdbcUserField() );
    dataFactory.setRole( getRole() );
    dataFactory.setRoleField( getRoleField() );
    dataFactory.setDesignTimeName( getDesignTimeName() );
  }


}
