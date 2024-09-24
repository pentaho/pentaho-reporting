/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactorySupport;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataFactoryDesignTimeSupport;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

import javax.swing.table.TableModel;
import java.util.HashMap;

public class CachingDataFactory extends AbstractDataFactory implements CompoundDataFactorySupport {
  private enum QueryStyle {
    General, Static, FreeForm
  }

  private static final Log logger = LogFactory.getLog( CachingDataFactory.class );

  private DataCache dataCache;
  private HashMap<DataCacheKey, TableModel> sessionCache;
  private CompoundDataFactory backend;
  private boolean closed;
  private boolean debugDataSources;
  private boolean profileDataSources;
  private boolean noClose;
  private static final String[] EMPTY_NAMES = new String[0];

  public CachingDataFactory( final DataFactory backend, final boolean dataCacheEnabled ) {
    this( backend, false, dataCacheEnabled );
  }

  public CachingDataFactory( final DataFactory backend, final boolean noClose, final boolean dataCacheEnabled ) {
    this( backend, noClose, produceDefault( dataCacheEnabled ) );
  }

  private static DataCache produceDefault( final boolean dataCacheEnabled ) {
    if ( dataCacheEnabled ) {
      return DataCacheFactory.getCache();
    } else {
      return null;
    }
  }

  public CachingDataFactory( final DataFactory backend, final boolean noClose, final DataCache dataCache ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    this.noClose = noClose;
    if ( noClose ) {
      this.backend = CompoundDataFactory.normalize( backend, false );
    } else {
      this.backend = CompoundDataFactory.normalize( backend, true );
    }

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    this.sessionCache = new HashMap<DataCacheKey, TableModel>();
    this.dataCache = dataCache;

    this.debugDataSources =
        "true".equals( configuration.getConfigProperty( "org.pentaho.reporting.engine.classic.core.DebugDataSources" ) );
    this.profileDataSources =
        "true"
            .equals( configuration.getConfigProperty( "org.pentaho.reporting.engine.classic.core.ProfileDataSources" ) );

  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    super.initialize( dataFactoryContext );
    backend.initialize( dataFactoryContext );
  }

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    if ( backend.isQueryExecutable( query, parameters ) ) {
      return true;
    }
    return false;
  }

  public boolean isFreeFormQueryExecutable( final String query, final DataRow parameter ) {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameter == null ) {
      throw new NullPointerException();
    }

    return backend.isFreeFormQueryExecutable( query, parameter );
  }

  public TableModel queryStatic( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey( query, parameters, false );
    if ( key != null ) {
      TableModel model = sessionCache.get( key );
      if ( model == null ) {
        model = dataCache.get( key );
      }
      if ( model != null ) {
        logger.debug( "Returning cached data for static query '" + query + "'." );
        return wrapAsIndexed( model );
      }
    }

    if ( !backend.isStaticQueryExecutable( query, parameters ) ) {
      throw new ReportDataFactoryException( "The specified query '" + query + "' is not executable here." );
    }

    TableModel data = queryInternal( query, parameters, QueryStyle.Static );
    if ( data == null ) {
      return null;
    }

    data = putInCache( key, data );
    return wrapAsIndexed( data );
  }

  public TableModel queryDesignTimeStructureStatic( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey( query, parameters, false );
    if ( key != null ) {
      TableModel model = sessionCache.get( key );
      if ( model == null ) {
        model = dataCache.get( key );
      }
      if ( model != null ) {
        logger.debug( "Returning cached data for design-time query '" + query + "'." );
        return wrapAsIndexed( model );
      }
    }

    if ( !backend.isStaticQueryExecutable( query, parameters ) ) {
      throw new ReportDataFactoryException( "The specified query '" + query + "' is not executable here." );
    }

    TableModel data = backend.queryDesignTimeStructureStatic( query, parameters );
    if ( data == null ) {
      return null;
    }

    data = putInCache( key, data );
    return wrapAsIndexed( data );
  }

  public TableModel queryFreeForm( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey( query, parameters, false );
    if ( key != null ) {
      TableModel model = sessionCache.get( key );
      if ( model == null ) {
        model = dataCache.get( key );
      }
      if ( model != null ) {
        logger.debug( "Returning cached data for freeform query '" + query + "'." );
        return wrapAsIndexed( model );
      }
    }

    if ( !backend.isFreeFormQueryExecutable( query, parameters ) ) {
      throw new ReportDataFactoryException( "The specified query '" + query + "' is not executable here." );
    }

    TableModel data = queryInternal( query, parameters, QueryStyle.FreeForm );
    if ( data == null ) {
      return null;
    }

    data = putInCache( key, data );
    return wrapAsIndexed( data );
  }

  public TableModel queryDesignTimeStructureFreeForm( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey( query, parameters, false );
    if ( key != null ) {
      TableModel model = sessionCache.get( key );
      if ( model == null ) {
        model = dataCache.get( key );
      }
      if ( model != null ) {
        logger.debug( "Returning cached data for free-form design time query '" + query + "'." );
        return wrapAsIndexed( model );
      }
    }

    if ( !backend.isFreeFormQueryExecutable( query, parameters ) ) {
      throw new ReportDataFactoryException( "The specified query '" + query + "' is not executable here." );
    }

    TableModel data = backend.queryDesignTimeStructureFreeForm( query, parameters );
    if ( data == null ) {
      return null;
    }

    data = putInCache( key, data );
    return wrapAsIndexed( data );
  }

  public boolean isStaticQueryExecutable( final String query, final DataRow parameters ) {
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( parameters == null ) {
      throw new NullPointerException();
    }

    return backend.isStaticQueryExecutable( query, parameters );
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    ArgumentNullException.validate( "query", query );
    ArgumentNullException.validate( "parameters", parameters );

    final DataCacheKey key = createCacheKey( query, parameters, false );
    if ( key != null ) {
      TableModel model = sessionCache.get( key );
      if ( model == null ) {
        model = dataCache.get( key );
      }
      if ( model != null ) {
        logger.debug( "Returning cached data for query '" + query + "'." );
        return wrapAsIndexed( model );
      }
    }

    if ( backend.isQueryExecutable( query, parameters ) ) {
      TableModel data = queryInternal( query, parameters, QueryStyle.General );
      if ( data != null ) {
        data = putInCache( key, data );
        return wrapAsIndexed( data );
      }
    }
    throw new ReportDataFactoryException( "The specified query '" + query + "' is not executable here." );
  }

  public TableModel queryDesignTimeStructure( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    ArgumentNullException.validate( "query", query );
    ArgumentNullException.validate( "parameters", parameters );

    final DataCacheKey key = createCacheKey( query, parameters, true );
    if ( key != null ) {
      final TableModel model = dataCache.get( key );
      if ( model != null ) {
        logger.debug( "Returning cached data for design-time query '" + query + "'." );
        return wrapAsIndexed( model );
      }
    }

    if ( backend.isQueryExecutable( query, parameters ) ) {
      TableModel data = queryDesignTimeStructureInternal( query, parameters );
      if ( data != null ) {
        data = putInCache( key, data );
        return wrapAsIndexed( data );
      }
    }
    throw new ReportDataFactoryException( "The specified query '" + query + "' is not executable here." );
  }

  private TableModel putInCache( final DataCacheKey key, TableModel data ) {
    if ( key != null ) {
      final TableModel newData = dataCache.put( key, data );
      if ( newData != data && data instanceof CloseableTableModel ) {
        final CloseableTableModel closeableTableModel = (CloseableTableModel) data;
        closeableTableModel.close();
      }
      sessionCache.put( key, newData );
      data = newData;
    }
    return data;
  }

  private TableModel wrapAsIndexed( final TableModel data ) {
    if ( data instanceof MetaTableModel ) {
      return new IndexedMetaTableModel( (MetaTableModel) data );
    } else {
      return new IndexedTableModel( data );
    }
  }

  private DataCacheKey createCacheKey( final String query, final DataRow parameters, final boolean designTime ) {
    try {
      if ( dataCache == null ) {
        return null;
      }

      final DataCacheKey key;
      DataFactoryMetaData metaData = backend.getMetaData();

      final String[] referencedFields = metaData.getReferencedFields( backend, query, parameters );
      if ( referencedFields != null ) {
        final Object queryHash = metaData.getQueryHash( backend, query, parameters );
        if ( queryHash == null ) {
          logger.debug( "Query hash is null, caching is disabled for query '" + query + "'." );
          key = null;
        } else {
          key = new DataCacheKey();
          for ( int i = 0; i < referencedFields.length; i++ ) {
            final String field = referencedFields[i];
            key.addParameter( field, parameters.get( field ) );
          }

          key.addAttribute( DataCacheKey.QUERY_CACHE, queryHash );
          key.addAttribute( DataFactoryDesignTimeSupport.DESIGN_TIME, designTime );

          // The data cache maps are immutable - make sure of it.
          key.makeReadOnly();
        }
      } else {
        logger.debug( "No Referenced fields, caching is disabled for query '" + query + "'." );
        key = null;
      }
      return key;
    } catch ( final MetaDataLookupException mle ) {
      logger.error( String.format(
          "Data-source used for query '%s' does not provide metadata. Caching will be disabled.", query ), mle );
      return null;
    }
  }

  private TableModel queryInternal( final String query, final DataRow parameters, final QueryStyle queryStyle )
    throws ReportDataFactoryException {
    if ( profileDataSources && CachingDataFactory.logger.isDebugEnabled() ) {
      CachingDataFactory.logger.debug( System.identityHashCode( Thread.currentThread() )
          + ": Query processing time: Starting" );
    }
    final long startTime = System.currentTimeMillis();
    try {
      final StaticDataRow params = new StaticDataRow( parameters );
      final TableModel dataFromQuery;
      switch ( queryStyle ) {
        case FreeForm:
          dataFromQuery = backend.queryFreeForm( query, params );
          break;
        case Static:
          dataFromQuery = backend.queryStatic( query, params );
          break;
        case General:
          dataFromQuery = backend.queryData( query, params );
          break;
        default:
          throw new IllegalStateException();
      }
      if ( dataFromQuery == null ) {
        // final DefaultTableModel value = new DefaultTableModel();
        if ( debugDataSources && CachingDataFactory.logger.isDebugEnabled() ) {
          CachingDataFactory.logger.debug( "Query failed for query '" + query + '\'' );
        }
        return null;
      } else {
        if ( debugDataSources && CachingDataFactory.logger.isDebugEnabled() ) {
          CachingDataFactory.printTableModelContents( dataFromQuery );
        }
        // totally new query here.
        CachingDataFactory.logger.debug( "Query returned a data-set for query '" + query + '\'' );
        return dataFromQuery;
      }
    } finally {
      final long queryTime = System.currentTimeMillis();
      if ( profileDataSources && CachingDataFactory.logger.isDebugEnabled() ) {
        CachingDataFactory.logger.debug( System.identityHashCode( Thread.currentThread() )
            + ": Query processing time: " + ( ( queryTime - startTime ) / 1000.0 ) );
      }
    }
  }

  private TableModel queryDesignTimeStructureInternal( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    if ( profileDataSources && CachingDataFactory.logger.isDebugEnabled() ) {
      CachingDataFactory.logger.debug( System.identityHashCode( Thread.currentThread() )
          + ": Query processing time: Starting" );
    }
    final long startTime = System.currentTimeMillis();
    try {
      return backend.queryDesignTimeStructure( query, parameters );
    } finally {
      final long queryTime = System.currentTimeMillis();
      if ( profileDataSources && CachingDataFactory.logger.isDebugEnabled() ) {
        CachingDataFactory.logger.debug( System.identityHashCode( Thread.currentThread() )
            + ": Query processing time: " + ( ( queryTime - startTime ) / 1000.0 ) );
      }
    }
  }

  /**
   * Closes the report data factory and all report data instances that have been returned by this instance.
   */
  public void close() {

    if ( closed == false ) {
      for ( final TableModel map : sessionCache.values() ) {
        if ( map instanceof CloseableTableModel == false ) {
          continue;
        }

        final CloseableTableModel ct = (CloseableTableModel) map;
        ct.close();
      }
      sessionCache.clear();
      if ( noClose == false ) {
        backend.close();
      }
      closed = true;
    }
  }

  /**
   * Derives a freshly initialized report data factory, which is independend of the original data factory. Opening or
   * Closing one data factory must not affect the other factories.
   *
   * @return nothing, the method dies instead.
   * @throws UnsupportedOperationException
   *           as this class is not derivable.
   */
  public DataFactory derive() {
    // If you see that exception, then you've probably tried to use that
    // datafactory from outside of the report processing. You deserve the
    // exception in that case ..
    throw new UnsupportedOperationException( "The CachingReportDataFactory cannot be derived." );
  }

  /**
   * Prints a table model to standard output.
   *
   * @param mod
   *          the model.
   */
  public static void printTableModelContents( final TableModel mod ) {
    if ( mod == null ) {
      throw new NullPointerException();
    }

    logger.debug( "Tablemodel contains " + mod.getRowCount() + " rows." ); //$NON-NLS-1$ //$NON-NLS-2$
    for ( int i = 0; i < mod.getColumnCount(); i++ ) {
      logger.debug( "Column: " + i + " Name = " + mod.getColumnName( i ) + "; DataType = " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          + mod.getColumnClass( i ) );
    }

    logger.debug( "Checking the data inside" ); //$NON-NLS-1$
    for ( int rows = 0; rows < mod.getRowCount(); rows++ ) {
      for ( int i = 0; i < mod.getColumnCount(); i++ ) {
        final Object value = mod.getValueAt( rows, i );
        logger.debug( "ValueAt (" + rows + ", " + i + ") is " + value ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
    }
  }

  public String[] getQueryNames() {
    return EMPTY_NAMES;
  }

  public void cancelRunningQuery() {
  }

  public CachingDataFactory clone() {
    final CachingDataFactory cdf = (CachingDataFactory) super.clone();
    cdf.backend = (CompoundDataFactory) backend.clone();
    cdf.sessionCache = (HashMap<DataCacheKey, TableModel>) sessionCache.clone();
    return cdf;
  }

  public DataFactory getDataFactoryForQuery( final String queryName, final boolean freeform ) {
    return backend.getDataFactoryForQuery( queryName, freeform );
  }
}
