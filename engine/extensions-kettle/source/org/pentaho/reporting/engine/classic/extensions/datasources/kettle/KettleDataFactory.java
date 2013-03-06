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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Fires a Kettle-Query by executing a Kettle-Transformation.
 *
 * @author Thomas Morgner
 */
public class KettleDataFactory extends AbstractDataFactory
{
  private static final long serialVersionUID = 3378733681824193349L;
  
  private LinkedHashMap<String, KettleTransformationProducer> queries;
  private transient KettleTransformationProducer currentlyRunningQuery;

  public KettleDataFactory()
  {
    queries = new LinkedHashMap<String, KettleTransformationProducer>();
  }

  public void setQuery(final String name, final KettleTransformationProducer value)
  {
    if (value == null)
    {
      queries.remove(name);
    }
    else
    {
      queries.put(name, value);
    }
  }

  public KettleTransformationProducer getQuery(final String name)
  {
    return queries.get(name);
  }

  public String[] getQueryNames()
  {
    return queries.keySet().toArray(new String[queries.size()]);
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
  public TableModel queryData(final String query, final DataRow parameters) throws ReportDataFactoryException
  {
    final Object queryLimitRaw = parameters.get(DataFactory.QUERY_LIMIT);
    final int queryLimit;
    if (queryLimitRaw instanceof Number)
    {
      final Number queryLimitNum = (Number) queryLimitRaw;
      queryLimit = queryLimitNum.intValue();
    }
    else
    {
      queryLimit = 0;
    }

    final KettleTransformationProducer producer = queries.get(query);
    if (producer == null)
    {
      throw new ReportDataFactoryException("There is no such query defined: " + query);
    }

    try
    {
      currentlyRunningQuery = producer;
      return producer.performQuery(parameters, queryLimit, getResourceManager(), getContextKey());
    }
    catch (ReportDataFactoryException rdfe)
    {
      throw rdfe;
    }
    catch (Throwable e)
    {
      throw new ReportDataFactoryException("Caught Kettle Exception: Check your configuration", e);
    }
    finally
    {
      currentlyRunningQuery = null;
    }
  }

  @SuppressWarnings("unchecked")
  public KettleDataFactory clone()
  {
    final KettleDataFactory df = (KettleDataFactory) super.clone();
    df.queries = (LinkedHashMap<String, KettleTransformationProducer>) queries.clone();
    df.currentlyRunningQuery = null;
    for (final Map.Entry<String, KettleTransformationProducer> entry : df.queries.entrySet())
    {
      final KettleTransformationProducer value = entry.getValue();
      entry.setValue((KettleTransformationProducer) value.clone());
    }
    return df;
  }

  /**
   * Closes the data factory and frees all resources held by this instance.
   */
  public void close()
  {
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full query.
   *
   * @param query      the query, never null.
   * @param parameters the parameters, never null.
   * @return true, if the query would be executable, false if the query is not recognized.
   */
  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    return queries.containsKey(query);
  }

  public void cancelRunningQuery()
  {
    final KettleTransformationProducer producer = this.currentlyRunningQuery;
    if (producer != null)
    {
      producer.cancelQuery();
      this.currentlyRunningQuery = null;
    }
  }

  public Object getQueryHash(final String queryName)
  {
    final KettleTransformationProducer transformationProducer = getQuery(queryName);
    if (transformationProducer == null)
    {
      return null;
    }
    return transformationProducer.getQueryHash(getResourceManager(), getContextKey());
  }
}
