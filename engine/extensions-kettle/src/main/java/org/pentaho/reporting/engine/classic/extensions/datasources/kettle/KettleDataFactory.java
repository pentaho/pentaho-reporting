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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;

import javax.swing.table.TableModel;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Fires a Kettle-Query by executing a Kettle-Transformation.
 *
 * @author Thomas Morgner
 */
public class KettleDataFactory extends AbstractDataFactory {
  private static final long serialVersionUID = 3378733681824193349L;
  private static final Log logger = LogFactory.getLog( KettleDataFactory.class );

  private LinkedHashMap<String, KettleTransformationProducer> queries;
  private transient KettleTransformationProducer currentlyRunningQuery;

  /**
   * This attribute will only have a value when the KettleDataFactory is serving an embedded unified datasource, versus
   * the typical Kettle transformation datasource.
   */
  private DataFactoryMetaData metadata;

  public void setMetadata( final DataFactoryMetaData metadata ) {
    this.metadata = metadata;
  }

  public KettleDataFactory() {
    queries = new LinkedHashMap<String, KettleTransformationProducer>();
  }

  public void setQuery( final String name, final KettleTransformationProducer value ) {
    if ( value == null ) {
      queries.remove( name );
    } else {
      queries.put( name, value );
    }
  }

  public KettleTransformationProducer getQuery( final String name ) {
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
    int queryLimit = calculateQueryLimit( parameters );
    final KettleTransformationProducer producer = queries.get( query );
    if ( producer == null ) {
      throw new ReportDataFactoryException( "There is no such query defined: " + query );
    }

    try {
      currentlyRunningQuery = producer;
      return producer.performQuery( parameters, queryLimit, getDataFactoryContext() );
    } catch ( final ReportDataFactoryException rdfe ) {
      throw rdfe;
    } catch ( final Throwable e ) {
      throw new ReportDataFactoryException( "Caught Kettle Exception: Check your configuration", e );
    } finally {
      currentlyRunningQuery = null;
    }
  }

  @SuppressWarnings( "unchecked" )
  public KettleDataFactory clone() {
    final KettleDataFactory df = (KettleDataFactory) super.clone();
    df.queries = (LinkedHashMap<String, KettleTransformationProducer>) queries.clone();
    df.currentlyRunningQuery = null;
    for ( final Map.Entry<String, KettleTransformationProducer> entry : df.queries.entrySet() ) {
      final KettleTransformationProducer value = entry.getValue();
      entry.setValue( (KettleTransformationProducer) value.clone() );
    }
    return df;
  }

  public TableModel queryDesignTimeStructure( final String query,
                                              final DataRow parameter ) throws ReportDataFactoryException {
    final KettleTransformationProducer producer = queries.get( query );
    if ( producer == null ) {
      throw new ReportDataFactoryException( "There is no such query defined: " + query );
    }

    try {
      currentlyRunningQuery = producer;
      return producer.queryDesignTimeStructure( parameter, getDataFactoryContext() );
    } catch ( final ReportDataFactoryException rdfe ) {
      throw rdfe;
    } catch ( final Throwable e ) {
      throw new ReportDataFactoryException( "Caught Kettle Exception: Check your configuration", e );
    } finally {
      currentlyRunningQuery = null;
    }
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query      the query, never null.
   * @param parameters the parameters, never null.
   * @return true, if the query would be executable, false if the query is not recognized.
   */
  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return queries.containsKey( query );
  }

  public void cancelRunningQuery() {
    final KettleTransformationProducer producer = this.currentlyRunningQuery;
    if ( producer != null ) {
      producer.cancelQuery();
      this.currentlyRunningQuery = null;
    }
  }

  public Object getQueryHash( final String queryName ) {
    final KettleTransformationProducer transformationProducer = getQuery( queryName );
    if ( transformationProducer == null ) {
      return null;
    }
    return transformationProducer.getQueryHash( getResourceManager(), getContextKey() );
  }

  public boolean queriesAreHomogeneous() {

    if ( ( queries == null ) || ( queries.isEmpty() ) ) {
      return true;
    }

    KettleTransformationProducer key = null;
    for ( final KettleTransformationProducer producer : queries.values() ) {
      if ( key == null ) {
        key = producer;
        if ( !( key instanceof EmbeddedKettleTransformationProducer ) ) {
          return false;
        }
        continue;
      }
      if ( key.getClass() != producer.getClass() ) {
        return false;
      }
      if ( ( key instanceof EmbeddedKettleTransformationProducer ) &&
        ( producer instanceof EmbeddedKettleTransformationProducer ) ) {
        final EmbeddedKettleTransformationProducer k = (EmbeddedKettleTransformationProducer) key;
        final EmbeddedKettleTransformationProducer p = (EmbeddedKettleTransformationProducer) producer;
        if ( !k.getPluginId().equals( p.getPluginId() ) ) {
          return false;
        }
      }
      key = producer;
    }
    return true;
  }

  public DataFactoryMetaData getMetaData() {

    if ( metadata != null ) {
      return metadata;
    }

    if ( !queries.isEmpty() ) {
      // First query is acceptable; if the queries are "mixed", we are not using this metadata anyway
      final KettleTransformationProducer defaultProducer = queries.values().iterator().next();
      if ( defaultProducer instanceof EmbeddedKettleTransformationProducer ) {
        final EmbeddedKettleTransformationProducer producer = (EmbeddedKettleTransformationProducer) defaultProducer;
        final String pluginId = producer.getPluginId();
        try {
          metadata = DataFactoryRegistry.getInstance().getMetaData( pluginId );
          return metadata;
        } catch ( MetaDataLookupException e ) {
          // we are on the server... plugin metadata instances are not registered
          // Return the default Kettle datafactory metadata...
          if ( logger.isTraceEnabled() ) {
            logger.trace( "Failed to lookup metadata for plugin-id " + pluginId, e ); // NON-NLS
          }
        }
      }

    }
    return DataFactoryRegistry.getInstance().getMetaData( this.getClass().getName() );
  }

}
