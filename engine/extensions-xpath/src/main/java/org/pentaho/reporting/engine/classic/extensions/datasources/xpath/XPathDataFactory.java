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

package org.pentaho.reporting.engine.classic.extensions.datasources.xpath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedHashMap;

public class XPathDataFactory extends AbstractDataFactory {
  public static class QueryDefinition implements Serializable {
    private boolean legacyQuery;
    private String xpathExpression;

    public QueryDefinition( final String xpathExpression, final boolean legacyQuery ) {
      this.xpathExpression = xpathExpression;
      this.legacyQuery = legacyQuery;
    }

    public boolean isLegacyQuery() {
      return legacyQuery;
    }

    public String getXpathExpression() {
      return xpathExpression;
    }
  }

  private static final Log logger = LogFactory.getLog( XPathDataFactory.class );

  private LinkedHashMap<String, QueryDefinition> queries;
  private String xqueryDataFile;

  public XPathDataFactory() {
    queries = new LinkedHashMap<String, QueryDefinition>();
  }

  public String getXqueryDataFile() {
    return xqueryDataFile;
  }

  public void setXqueryDataFile( final String xqueryDataFile ) {
    this.xqueryDataFile = xqueryDataFile;
  }

  public void setQuery( final String name, final String value, final boolean legacyQuery ) {
    if ( value == null ) {
      queries.remove( name );
    } else {
      queries.put( name, new QueryDefinition( value, legacyQuery ) );
    }
  }

  public QueryDefinition getQuery( final String name ) {
    return queries.get( name );
  }

  public String[] getQueryNames() {
    return queries.keySet().toArray( new String[ queries.size() ] );
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param query      the query string
   * @param parameters the parameters for the query
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException if an error occured while performing the query.
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    final QueryDefinition xpath = queries.get( query );
    if ( xpath == null ) {
      throw new ReportDataFactoryException( "No such query" );
    }

    final int queryLimitVal;
    final Object queryLimit = parameters.get( DataFactory.QUERY_LIMIT );
    if ( queryLimit instanceof Number ) {
      final Number i = (Number) queryLimit;
      queryLimitVal = i.intValue();
    } else {
      queryLimitVal = -1;
    }

    try {
      final ResourceManager resourceManager = getResourceManager();
      final ResourceData resource = load();
      if ( xpath.isLegacyQuery() ) {
        return new LegacyXPathTableModel( resource, resourceManager, xpath.getXpathExpression(), parameters,
          queryLimitVal );
      }
      return new XPathTableModel( resource, resourceManager, xpath.getXpathExpression(), parameters, queryLimitVal );
    } catch ( ResourceException re ) {
      throw new ReportDataFactoryException( "Failed to load XML data", re );
    }
  }

  private ResourceData load() throws ResourceException {
    try {
      final ResourceKey resourceKey;
      final ResourceKey contextKey = getContextKey();
      if ( contextKey != null ) {
        final ResourceManager resourceManager = getResourceManager();
        resourceKey = resourceManager.deriveKey( contextKey, getXqueryDataFile() );
        return resourceManager.load( resourceKey );
      }
    } catch ( ResourceException re ) {
      // failed to load from context
      logger.debug( "Failed to load datasource as derived path: " + re );
    }

    try {
      final ResourceManager resourceManager = getResourceManager();
      final ResourceKey resourceKey = resourceManager.createKey( new URL( getXqueryDataFile() ) );
      return resourceManager.load( resourceKey );
    } catch ( ResourceException re ) {
      logger.debug( "Failed to load datasource as URL: " + re );
    } catch ( MalformedURLException e ) {
      //
    }

    try {
      final ResourceManager resourceManager = getResourceManager();
      final ResourceKey resourceKey = resourceManager.createKey( new File( getXqueryDataFile() ) );
      return resourceManager.load( resourceKey );
    } catch ( ResourceException re ) {
      // failed to load from context
      logger.debug( "Failed to load datasource as file: " + re );
    }

    throw new ResourceException( "Unable to load the resource" );
  }

  public XPathDataFactory clone() {
    final XPathDataFactory dataFactory = (XPathDataFactory) super.clone();
    //noinspection unchecked
    dataFactory.queries = (LinkedHashMap<String, QueryDefinition>) queries.clone();
    return dataFactory;
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public XPathDataFactory derive() {
    return clone();
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {

  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queries.containsKey( query );
  }
}
