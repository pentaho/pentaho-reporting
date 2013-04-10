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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.cache;

import java.util.HashMap;
import javax.swing.table.TableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactorySupport;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.MetaDataLookupException;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class CachingDataFactory extends AbstractDataFactory implements CompoundDataFactorySupport
{
  private enum QueryStyle
  {
    General, Static, FreeForm
  }

  private static final Log logger = LogFactory.getLog(CachingDataFactory.class);

  private static final Object NULL_INDICATOR = new Object();

  private DataCache dataCache;
  private HashMap<DataCacheKey, TableModel> sessionCache;
  private CompoundDataFactory backend;
  private boolean closed;
  private boolean debugDataSources;
  private boolean profileDataSources;
  private boolean noClose;
  private static final String[] EMPTY_NAMES = new String[0];

  public CachingDataFactory(final DataFactory backend, final boolean dataCacheEnabled)
  {
    this(backend, false, dataCacheEnabled);
  }

  public CachingDataFactory(final DataFactory backend, final boolean noClose, final boolean dataCacheEnabled)
  {
    if (backend == null)
    {
      throw new NullPointerException();
    }
    this.noClose = noClose;
    if (noClose)
    {
      this.backend = CompoundDataFactory.normalize(backend, false);
    }
    else
    {
      this.backend = CompoundDataFactory.normalize(backend, true);
    }

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    sessionCache = new HashMap<DataCacheKey, TableModel>();
    if (dataCacheEnabled)
    {
      this.dataCache = DataCacheFactory.getCache();
    }
    else
    {
      this.dataCache = null;
    }

    this.debugDataSources = "true".equals(configuration.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.DebugDataSources"));
    this.profileDataSources = "true".equals(configuration.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.ProfileDataSources"));

  }

  public void initialize(final DataFactoryContext dataFactoryContext) throws ReportDataFactoryException
  {
    super.initialize(dataFactoryContext);
    backend.initialize(dataFactoryContext);
  }

  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    if (query == null)
    {
      throw new NullPointerException();
    }
    if (parameters == null)
    {
      throw new NullPointerException();
    }

    if (backend.isQueryExecutable(query, parameters))
    {
      return true;
    }
    return false;
  }

  public boolean isFreeFormQueryExecutable(final String query, final DataRow parameter)
  {
    if (query == null)
    {
      throw new NullPointerException();
    }
    if (parameter == null)
    {
      throw new NullPointerException();
    }

    return backend.isFreeFormQueryExecutable(query, parameter);
  }

  private Object lastKey;

  public TableModel queryStatic(final String query, final DataRow parameters) throws ReportDataFactoryException
  {
    if (query == null)
    {
      throw new NullPointerException();
    }
    if (parameters == null)
    {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey(query, parameters);
    if (key != null)
    {
      TableModel model = sessionCache.get(key);
      if (model == null)
      {
        model = dataCache.get(key);
      }
      if (model != null)
      {
        if (model instanceof MetaTableModel)
        {
          return new IndexedMetaTableModel((MetaTableModel) model);
        }
        else
        {
          return new IndexedTableModel(model);
        }
      }
    }

    if (!backend.isStaticQueryExecutable(query, parameters))
    {
      throw new ReportDataFactoryException("The specified query '" + query + "' is not executable here.");
    }

    TableModel data = queryInternal(query, parameters, QueryStyle.Static);
    if (data == null)
    {
      return null;
    }

    if (key != null)
    {
      final TableModel newData = dataCache.put(key, data);
      if (newData != data && data instanceof CloseableTableModel)
      {
        final CloseableTableModel closeableTableModel = (CloseableTableModel) data;
        closeableTableModel.close();
      }
      sessionCache.put(key, newData);
      data = newData;
    }

    if (data instanceof MetaTableModel)
    {
      return new IndexedMetaTableModel((MetaTableModel) data);
    }
    else
    {
      return new IndexedTableModel(data);
    }
  }

  public TableModel queryFreeForm(final String query, final DataRow parameters) throws ReportDataFactoryException
  {
    if (query == null)
    {
      throw new NullPointerException();
    }
    if (parameters == null)
    {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey(query, parameters);
    if (key != null)
    {
      final TableModel model = dataCache.get(key);
      if (model != null)
      {
        if (model instanceof MetaTableModel)
        {
          return new IndexedMetaTableModel((MetaTableModel) model);
        }
        else
        {
          return new IndexedTableModel(model);
        }
      }
    }

    if (!backend.isFreeFormQueryExecutable(query, parameters))
    {
      throw new ReportDataFactoryException("The specified query '" + query + "' is not executable here.");
    }

    TableModel data = queryInternal(query, parameters, QueryStyle.FreeForm);
    if (data == null)
    {
      return null;
    }

    if (key != null)
    {
      final TableModel newData = dataCache.put(key, data);
      if (newData != data && data instanceof CloseableTableModel)
      {
        final CloseableTableModel closeableTableModel = (CloseableTableModel) data;
        closeableTableModel.close();
      }
      data = newData;
    }

    if (data instanceof MetaTableModel)
    {
      return new IndexedMetaTableModel((MetaTableModel) data);
    }
    else
    {
      return new IndexedTableModel(data);
    }
  }

  public boolean isStaticQueryExecutable(final String query, final DataRow parameters)
  {
    if (query == null)
    {
      throw new NullPointerException();
    }
    if (parameters == null)
    {
      throw new NullPointerException();
    }

    return backend.isStaticQueryExecutable(query, parameters);
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
  public TableModel queryData(final String query, final DataRow parameters)
      throws ReportDataFactoryException
  {
    if (query == null)
    {
      throw new NullPointerException();
    }
    if (parameters == null)
    {
      throw new NullPointerException();
    }

    final DataCacheKey key = createCacheKey(query, parameters);
    if (key != null)
    {
      final TableModel model = dataCache.get(key);
      if (model != null)
      {
        if (model instanceof MetaTableModel)
        {
          return new IndexedMetaTableModel((MetaTableModel) model);
        }
        else
        {
          return new IndexedTableModel(model);
        }
      }
    }

    if (backend.isQueryExecutable(query, parameters))
    {
      TableModel data = queryInternal(query, parameters, QueryStyle.General);
      if (data != null)
      {
        if (key != null)
        {
          final TableModel newData = dataCache.put(key, data);
          if (newData != data && data instanceof CloseableTableModel)
          {
            final CloseableTableModel closeableTableModel = (CloseableTableModel) data;
            closeableTableModel.close();
          }
          data = newData;
        }

        if (data instanceof MetaTableModel)
        {
          return new IndexedMetaTableModel((MetaTableModel) data);
        }
        else
        {
          return new IndexedTableModel(data);
        }
      }
    }
    throw new ReportDataFactoryException("The specified query '" + query + "' is not executable here.");
  }

  private DataCacheKey createCacheKey(final String query,
                                      final DataRow parameters)
  {
    try
    {
      final DataFactory dataFactoryForQuery = backend.getDataFactoryForQuery(query);
      final DataCacheKey key;
      if (dataFactoryForQuery != null && dataCache != null)
      {
        // search the datafactory that executes a query
        // metadata: query fields that are used
        // metadata: get a query-string hash-object (or the raw query)
        final DataFactoryMetaData metaData = dataFactoryForQuery.getMetaData();
        final String[] referencedFields = metaData.getReferencedFields(dataFactoryForQuery, query, parameters);
        if (referencedFields != null)
        {
          final Object queryHash = metaData.getQueryHash(dataFactoryForQuery, query, parameters);
          if (queryHash == null)
          {
            key = null;
          }
          else
          {
            key = new DataCacheKey();
            for (int i = 0; i < referencedFields.length; i++)
            {
              final String field = referencedFields[i];
              key.addParameter(field, parameters.get(field));
            }

            key.addAttribute(DataCacheKey.QUERY_CACHE, queryHash);

            // The data cache maps are immutable - make sure of it.
            key.makeReadOnly();
          }
        }
        else
        {
          key = null;
        }
      }
      else
      {
        key = null;
      }
      return key;
    }
    catch (MetaDataLookupException mle)
    {
      logger.error
          ("Data-source used for query '" + query + "' does not provide metadata. Caching will be disabled.", mle);
      return null;
    }
  }

  private TableModel queryInternal(final String query,
                                   final DataRow parameters,
                                   final QueryStyle queryStyle)
      throws ReportDataFactoryException
  {
    if (profileDataSources && CachingDataFactory.logger.isDebugEnabled())
    {
      CachingDataFactory.logger.debug(System.identityHashCode(
          Thread.currentThread()) + ": Query processing time: Starting");
    }
    final long startTime = System.currentTimeMillis();
    try
    {
      final StaticDataRow params = new StaticDataRow(parameters);
      final TableModel dataFromQuery;
      switch (queryStyle)
      {
        case FreeForm:
          dataFromQuery = backend.queryFreeForm(query, params);
          break;
        case Static:
          dataFromQuery = backend.queryStatic(query, params);
          break;
        case General:
          dataFromQuery = backend.queryData(query, params);
          break;
        default:
          throw new IllegalStateException();
      }
      if (dataFromQuery == null)
      {
        //final DefaultTableModel value = new DefaultTableModel();
        if (debugDataSources && CachingDataFactory.logger.isDebugEnabled())
        {
          CachingDataFactory.logger.debug("Query failed for query '" + query + '\'');
        }
        return null;
      }
      else
      {
        if (debugDataSources && CachingDataFactory.logger.isDebugEnabled())
        {
          CachingDataFactory.printTableModelContents(dataFromQuery);
        }
        // totally new query here.
        return dataFromQuery;
      }
    }
    finally
    {
      final long queryTime = System.currentTimeMillis();
      if (profileDataSources && CachingDataFactory.logger.isDebugEnabled())
      {
        CachingDataFactory.logger.debug(System.identityHashCode(
            Thread.currentThread()) + ": Query processing time: " + ((queryTime - startTime) / 1000.0));
      }
    }
  }

  /**
   * Closes the report data factory and all report data instances that have been returned by this instance.
   */
  public void close()
  {

    if (closed == false)
    {
      for (final TableModel map : sessionCache.values())
      {
        if (map instanceof CloseableTableModel == false)
        {
          continue;
        }

        final CloseableTableModel ct = (CloseableTableModel) map;
        ct.close();
      }
      sessionCache.clear();
      if (noClose == false)
      {
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
   * @throws UnsupportedOperationException as this class is not derivable.
   */
  public DataFactory derive()
  {
    // If you see that exception, then you've probably tried to use that
    // datafactory from outside of the report processing. You deserve the
    // exception in that case ..
    throw new UnsupportedOperationException("The CachingReportDataFactory cannot be derived.");
  }

  /**
   * Prints a table model to standard output.
   *
   * @param mod the model.
   */
  public static void printTableModelContents(final TableModel mod)
  {
    if (mod == null)
    {
      throw new NullPointerException();
    }

    logger.debug("Tablemodel contains " + mod.getRowCount() + " rows."); //$NON-NLS-1$ //$NON-NLS-2$
    for (int i = 0; i < mod.getColumnCount(); i++)
    {
      logger.debug("Column: " + i + " Name = " + mod.getColumnName(
          i) + "; DataType = " //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
          + mod.getColumnClass(i));
    }

    logger.debug("Checking the data inside"); //$NON-NLS-1$
    for (int rows = 0; rows < mod.getRowCount(); rows++)
    {
      for (int i = 0; i < mod.getColumnCount(); i++)
      {
        final Object value = mod.getValueAt(rows, i);
        logger.debug("ValueAt (" + rows + ", " + i + ") is " + value); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      }
    }
  }

  public String[] getQueryNames()
  {
    return EMPTY_NAMES;
  }

  public void cancelRunningQuery()
  {
  }

  public CachingDataFactory clone()
  {
    final CachingDataFactory cdf = (CachingDataFactory) super.clone();
    cdf.backend = (CompoundDataFactory) backend.clone();
    cdf.sessionCache = (HashMap<DataCacheKey, TableModel>) sessionCache.clone();
    return cdf;
  }

  public DataFactory getDataFactoryForQuery(final String queryName, final boolean freeform)
  {
    return backend.getDataFactoryForQuery(queryName, freeform);
  }
}
