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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.LinkedMap;

import javax.swing.table.TableModel;
import java.util.ArrayList;

/**
 * The compound data factory is a collection of data-factories. Each of the child datafactories is queried in the order
 * of their addition to the collection.
 *
 * @author Thomas Morgner
 */
public class CompoundDataFactory extends AbstractDataFactory implements CompoundDataFactorySupport {
  private ArrayList<DataFactory> dataFactories;

  public CompoundDataFactory() {
    dataFactories = new ArrayList<DataFactory>();
  }

  public void initialize( final DataFactoryContext dataFactoryContext ) throws ReportDataFactoryException {
    super.initialize( dataFactoryContext );
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      dataFactory.initialize( dataFactoryContext );
    }
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed for the query.
   * <p/>
   * The parameter-dataset may change between two calls, do not assume anything, and do not hold references to the
   * parameter-dataset or the position of the columns in the dataset.
   *
   * @param query
   *          the query string
   * @param parameters
   *          the parameters for the query
   * @return the result of the query as table model.
   * @throws ReportDataFactoryException
   *           if an error occured while performing the query.
   */
  public final TableModel queryData( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    ArgumentNullException.validate( "query", query );
    ArgumentNullException.validate( "parameters", parameters );

    final TableModel staticResult = queryStatic( query, parameters );
    if ( staticResult != null ) {
      return staticResult;
    }
    final TableModel freeFormResult = queryFreeForm( query, parameters );
    if ( freeFormResult != null ) {
      return freeFormResult;
    }
    return handleFallThrough( query );
  }

  public TableModel queryDesignTimeStructureFreeForm( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    return postProcess( query, parameters, queryDesignTimeStructFreeFormInternal( query, parameters ) );
  }

  private TableModel queryDesignTimeStructFreeFormInternal( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );

      if ( dataFactory instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport support = (CompoundDataFactorySupport) dataFactory;
        if ( support.isFreeFormQueryExecutable( query, parameters ) ) {
          return support.queryDesignTimeStructureFreeForm( query, parameters );
        }
      } else if ( isFreeFormQueryDataFactory( dataFactory ) && dataFactory.isQueryExecutable( query, parameters ) ) {
        if ( dataFactory instanceof DataFactoryDesignTimeSupport ) {
          DataFactoryDesignTimeSupport dts = (DataFactoryDesignTimeSupport) dataFactory;
          return dts.queryDesignTimeStructure( query, parameters );
        } else {
          return dataFactory.queryData( query, new DataRowWrapper( parameters ) );
        }
      }
    }
    return null;
  }

  public TableModel queryFreeForm( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    return postProcess( query, parameters, queryFreeFormInternal( query, parameters ) );
  }

  private TableModel queryFreeFormInternal( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );

      if ( dataFactory instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport support = (CompoundDataFactorySupport) dataFactory;
        if ( support.isFreeFormQueryExecutable( query, parameters ) ) {
          return support.queryFreeForm( query, parameters );
        }
      } else if ( isFreeFormQueryDataFactory( dataFactory ) && dataFactory.isQueryExecutable( query, parameters ) ) {
        return dataFactory.queryData( query, parameters );
      }
    }
    return null;
  }

  public TableModel queryDesignTimeStructureStatic( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    return postProcess( query, parameters, queryDesignTimeStructStaticInternal( query, parameters ) );
  }

  private TableModel queryDesignTimeStructStaticInternal( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      if ( dataFactory instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport support = (CompoundDataFactorySupport) dataFactory;
        if ( support.isStaticQueryExecutable( query, parameters ) ) {
          return support.queryDesignTimeStructureStatic( query, parameters );
        }
      } else if ( ( isFreeFormQueryDataFactory( dataFactory ) == false )
          && dataFactory.isQueryExecutable( query, parameters ) ) {
        if ( dataFactory instanceof DataFactoryDesignTimeSupport ) {
          DataFactoryDesignTimeSupport dts = (DataFactoryDesignTimeSupport) dataFactory;
          return dts.queryDesignTimeStructure( query, parameters );
        } else {
          return dataFactory.queryData( query, new DataRowWrapper( parameters ) );
        }
      }
    }
    return null;
  }

  public TableModel queryStatic( final String query, final DataRow parameters ) throws ReportDataFactoryException {
    return postProcess( query, parameters, queryStaticInternal( query, parameters ) );
  }

  protected TableModel postProcess( final String query, final DataRow parameters, final TableModel tableModel ) {
    return tableModel;
  }

  private TableModel queryStaticInternal( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      if ( dataFactory instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport support = (CompoundDataFactorySupport) dataFactory;
        if ( support.isStaticQueryExecutable( query, parameters ) ) {
          return support.queryStatic( query, parameters );
        }
      } else if ( ( isFreeFormQueryDataFactory( dataFactory ) == false )
          && dataFactory.isQueryExecutable( query, parameters ) ) {
        return dataFactory.queryData( query, parameters );
      }
    }
    return null;
  }

  public TableModel queryDesignTimeStructure( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    ArgumentNullException.validate( "query", query );
    ArgumentNullException.validate( "parameters", parameters );

    final TableModel staticResult = queryDesignTimeStructureStatic( query, parameters );
    if ( staticResult != null ) {
      return staticResult;
    }
    final TableModel freeFormResult = queryDesignTimeStructureFreeForm( query, parameters );
    if ( freeFormResult != null ) {
      return freeFormResult;
    }
    return handleFallThrough( query );
  }

  protected TableModel handleFallThrough( final String query ) throws ReportDataFactoryException {
    throw new ReportDataFactoryException( "None of the data-factories was able to handle this query." );
  }

  private boolean isFreeFormQueryDataFactory( final DataFactory dataFactory ) {
    final DataFactoryMetaData metaData = dataFactory.getMetaData();
    if ( metaData.isFreeFormQuery() ) {
      return true;
    }

    return false;
  }

  /**
   * Returns a copy of the data factory that is not affected by its anchestor and holds no connection to the anchestor
   * anymore. A data-factory will be derived at the beginning of the report processing.
   *
   * @return a copy of the data factory.
   */
  public DataFactory derive() {
    final CompoundDataFactory cdf = deriveEmpty();

    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      cdf.dataFactories.add( dataFactory.derive() );
    }
    return cdf;
  }

  public CompoundDataFactory deriveEmpty() {
    final CompoundDataFactory cdf = (CompoundDataFactory) clone();
    cdf.dataFactories = (ArrayList<DataFactory>) dataFactories.clone();
    cdf.dataFactories.clear();
    return cdf;
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close() {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      dataFactory.close();
    }
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query
   *          query name.
   * @param parameters
   *          the parameters for the query.
   * @return true, if the query may be executable, false, if no datasource claims the query.
   */
  public final boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return isStaticQueryExecutable( query, parameters ) || isFreeFormQueryExecutable( query, parameters );
  }

  public boolean isFreeFormQueryExecutable( final String query, final DataRow parameters ) {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      if ( dataFactory instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport support = (CompoundDataFactorySupport) dataFactory;
        if ( support.isFreeFormQueryExecutable( query, parameters ) ) {
          return true;
        }
      } else if ( ( isFreeFormQueryDataFactory( dataFactory ) ) && dataFactory.isQueryExecutable( query, parameters ) ) {
        return true;
      }
    }
    return false;
  }

  public boolean isStaticQueryExecutable( final String query, final DataRow parameters ) {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      if ( dataFactory instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport support = (CompoundDataFactorySupport) dataFactory;
        if ( support.isStaticQueryExecutable( query, parameters ) ) {
          return true;
        }
      } else if ( ( isFreeFormQueryDataFactory( dataFactory ) == false )
          && dataFactory.isQueryExecutable( query, parameters ) ) {
        return true;
      }
    }
    return false;
  }

  protected void addRaw( final DataFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    dataFactories.add( factory );
  }

  public void add( final DataFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    final DataFactory derived = factory.derive();
    if ( derived == null ) {
      throw new IllegalStateException( "Deriving failed silently. Fix your implementation of " + factory.getClass() );
    }
    dataFactories.add( derived );
  }

  public void add( final int index, final DataFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    final DataFactory derived = factory.derive();
    if ( derived == null ) {
      throw new InvalidReportStateException( "Deriving failed silently. Fix your implementation of "
          + factory.getClass() );
    }
    dataFactories.add( index, derived );
  }

  public void set( final int index, final DataFactory factory ) {
    if ( factory == null ) {
      throw new NullPointerException();
    }
    final DataFactory derived = factory.derive();
    if ( derived == null ) {
      throw new InvalidReportStateException( "Deriving failed silently. Fix your implementation of "
          + factory.getClass() );
    }
    dataFactories.set( index, derived );
  }

  public void remove( final int index ) {
    dataFactories.remove( index );
  }

  public void remove( final DataFactory dataFactory ) {
    dataFactories.remove( dataFactory );
  }

  public int size() {
    return dataFactories.size();
  }

  public DataFactory get( final int idx ) {
    final DataFactory df = dataFactories.get( idx );
    return df.derive();
  }

  public int indexOfByReference( final DataFactory d ) {
    for ( int i = 0; i < size(); i++ ) {
      final DataFactory df = getReference( i );
      if ( df == d ) {
        return i;
      }
    }
    return -1;
  }

  public DataFactory getReference( final int idx ) {
    return dataFactories.get( idx );
  }

  public boolean isNormalized() {
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      if ( dataFactory instanceof CompoundDataFactory ) {
        return false;
      }
    }
    return true;
  }

  public static CompoundDataFactory normalize( final DataFactory dataFactory ) {
    return normalize( dataFactory, true );
  }

  protected CompoundDataFactory normalizeInternal( boolean derive ) {
    final CompoundDataFactory retval = deriveEmpty();
    final int size = size();
    for ( int i = 0; i < size; i++ ) {
      final DataFactory original = getReference( i );
      if ( original instanceof CompoundDataFactory ) {
        final CompoundDataFactory container = normalize( original, derive );
        final int containerSize = container.size();
        for ( int x = 0; x < containerSize; x++ ) {
          if ( derive ) {
            retval.add( container.getReference( x ) );
          } else {
            retval.addRaw( container.getReference( x ) );
          }
        }
      } else {
        if ( derive ) {
          retval.add( original );
        } else {
          retval.addRaw( original );
        }

      }
    }
    return retval;
  }

  public static CompoundDataFactory normalize( final DataFactory dataFactory, final boolean derive ) {
    if ( dataFactory == null ) {
      return new CompoundDataFactory();
    }

    if ( dataFactory instanceof CompoundDataFactory == false ) {
      final CompoundDataFactory retval = new CompoundDataFactory();
      if ( derive ) {
        retval.add( dataFactory );
      } else {
        retval.addRaw( dataFactory );
      }
      return retval;
    }

    final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
    if ( cdf.isNormalized() ) {
      if ( derive ) {
        return (CompoundDataFactory) cdf.derive();
      }
      return cdf;
    }

    return cdf.normalizeInternal( derive );
  }

  public String[] getQueryNames() {
    final LinkedMap nameSet = new LinkedMap();
    for ( int i = 0; i < dataFactories.size(); i++ ) {
      final DataFactory dataFactory = dataFactories.get( i );
      final String[] queryNames = dataFactory.getQueryNames();
      for ( int j = 0; j < queryNames.length; j++ ) {
        final String queryName = queryNames[j];
        nameSet.put( queryName, queryName );
      }
    }
    return (String[]) nameSet.keys( new String[nameSet.size()] );
  }

  public DataFactory getDataFactoryForQuery( final String queryName, final boolean freeform ) {
    final DataRow dr = new StaticDataRow();
    for ( int i = 0; i < size(); i++ ) {
      final DataFactory df = dataFactories.get( i );
      if ( df instanceof CompoundDataFactorySupport ) {
        final CompoundDataFactorySupport cdf = (CompoundDataFactorySupport) df;
        final DataFactory r = cdf.getDataFactoryForQuery( queryName, freeform );
        if ( r != null ) {
          return r;
        }
      }
      if ( ( isFreeFormQueryDataFactory( df ) == freeform ) && df.isQueryExecutable( queryName, dr ) ) {
        return df;
      }
    }
    return null;
  }

  public DataFactory getDataFactoryForQuery( final String queryName ) {
    final DataFactory nonFreeForm = getDataFactoryForQuery( queryName, false );
    if ( nonFreeForm != null ) {
      return nonFreeForm;
    }

    final DataFactory freeForm = getDataFactoryForQuery( queryName, true );
    if ( freeForm != null ) {
      return freeForm;
    }

    return null;
  }

}
