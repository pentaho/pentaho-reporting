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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.DriverDataSourceProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.LegacyBandedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.PmdDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.xpath.XPathDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Properties;

public class MultiDataSetReadHandler extends PropertiesReadHandler {
  private ArrayList queries;
  private SelectedJNDIDataSourceReadHandler selectedJNDIDataSourceReadHandler;
  private DataFactory dataFactory;

  public MultiDataSetReadHandler() {
    queries = new ArrayList();
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

    if ( "query".equals( tagName ) ) {
      final QueryReadHandler readHandler = new QueryReadHandler();
      queries.add( readHandler );
      return readHandler;
    }
    if ( "padding".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }
    if ( "selectedJNDIDataSource".equals( tagName ) ) {
      selectedJNDIDataSourceReadHandler = new SelectedJNDIDataSourceReadHandler();
      return selectedJNDIDataSourceReadHandler;
    }
    if ( "columnInfo".equals( tagName ) ) {
      return new IgnoreAnyChildReadHandler();
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Properties result = getResult();

    final String connectionType = result.getProperty( "connectionType" );
    final String xQueryDataFile = result.getProperty( "xQueryDataFile" );
    final String xmiDefinitionFile = result.getProperty( "xmiDefinitionFile" );
    final String mondrianCubeDefinition = result.getProperty( "mondrianCubeDefinitionFile" );
    final boolean useMondrianCubeDefinition = "true".equals( result.getProperty( "useMondrianCubeDefinition" ) );

    if ( "MQL".equals( connectionType ) ) {
      if ( xmiDefinitionFile == null ) {
        throw new ParseException( "Required property 'xmiDefinitionFile' is missing" );
      }

      final PmdDataFactory dataFactory = new PmdDataFactory();
      dataFactory.setXmiFile( xmiDefinitionFile );
      for ( int i = 0; i < queries.size(); i++ ) {
        final QueryReadHandler handler = (QueryReadHandler) queries.get( i );
        dataFactory.setQuery( handler.getQueryName(), handler.getQuery(), null, null );
      }

      final String queryNameProperty = result.getProperty( "queryString" );
      if ( queryNameProperty != null ) {
        dataFactory.setQuery( "default", queryNameProperty, null, null );
      }
      this.dataFactory = dataFactory;
    } else if ( "XQuery".equals( connectionType ) ) {
      if ( xQueryDataFile == null ) {
        throw new ParseException( "Required property 'xQueryDataFile' is missing" );
      }
      final XPathDataFactory dataFactory = new XPathDataFactory();
      dataFactory.setXqueryDataFile( xQueryDataFile );
      for ( int i = 0; i < queries.size(); i++ ) {
        final QueryReadHandler handler = (QueryReadHandler) queries.get( i );
        dataFactory.setQuery( handler.getQueryName(), handler.getQuery(), true );
      }

      final String queryNameProperty = result.getProperty( "queryString" );
      if ( queryNameProperty != null ) {
        dataFactory.setQuery( "default", queryNameProperty, true );
      }
      this.dataFactory = dataFactory;
    } else if ( "JNDI".equals( connectionType ) ) {
      if ( selectedJNDIDataSourceReadHandler == null ) {
        throw new ParseException( "Required element 'selectedJNDIDataSourceReadHandler' is missing" );
      }

      if ( useMondrianCubeDefinition ) {
        if ( mondrianCubeDefinition == null ) {
          throw new ParseException( "Required property 'mondrianCubeDefinitionFile' is missing" );
        }

        final LegacyBandedMDXDataFactory dataFactory = new LegacyBandedMDXDataFactory();

        // legacy report usecase
        final CubeFileProvider cubeFileProvider =
          ClassicEngineBoot.getInstance().getObjectFactory().get( CubeFileProvider.class );
        cubeFileProvider.setDesignTimeFile( mondrianCubeDefinition );

        dataFactory.setCubeFileProvider( cubeFileProvider );
        dataFactory.setJdbcUser( selectedJNDIDataSourceReadHandler.getUsername() );
        dataFactory.setJdbcPassword( selectedJNDIDataSourceReadHandler.getPassword() );
        dataFactory.setDesignTimeName( selectedJNDIDataSourceReadHandler.getJndiName() );

        final DriverDataSourceProvider driverDataSourceProvider = new DriverDataSourceProvider();
        driverDataSourceProvider.setDriver( selectedJNDIDataSourceReadHandler.getDriverClass() );
        driverDataSourceProvider.setUrl( selectedJNDIDataSourceReadHandler.getConnectionString() );
        dataFactory.setDataSourceProvider( driverDataSourceProvider );
        for ( int i = 0; i < queries.size(); i++ ) {
          final QueryReadHandler handler = (QueryReadHandler) queries.get( i );
          dataFactory.setQuery( handler.getQueryName(), handler.getQuery(), null, null );
        }

        final String queryNameProperty = result.getProperty( "queryString" );
        if ( queryNameProperty != null ) {
          dataFactory.setQuery( "default", queryNameProperty, null, null );
        }
        this.dataFactory = dataFactory;
      } else {
        final DriverConnectionProvider drc = new DriverConnectionProvider();
        drc.setUrl( selectedJNDIDataSourceReadHandler.getConnectionString() );
        drc.setDriver( selectedJNDIDataSourceReadHandler.getDriverClass() );
        drc.setProperty( "user", selectedJNDIDataSourceReadHandler.getUsername() );
        drc.setProperty( "password", selectedJNDIDataSourceReadHandler.getPassword() );
        drc.setProperty( "::pentaho-reporting::name", selectedJNDIDataSourceReadHandler.getJndiName() );

        final SQLReportDataFactory dataFactory = new SQLReportDataFactory( drc );
        for ( int i = 0; i < queries.size(); i++ ) {
          final QueryReadHandler handler = (QueryReadHandler) queries.get( i );
          dataFactory.setQuery( handler.getQueryName(), handler.getQuery(), null, null );
        }

        final String queryNameProperty = result.getProperty( "queryString" );
        if ( queryNameProperty != null ) {
          dataFactory.setQuery( "default", queryNameProperty, null, null );
        }
        this.dataFactory = dataFactory;
      }
    } else {
      throw new ParseException( "Required Property 'connectionType' is missing", getLocator() );
    }

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
