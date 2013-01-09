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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.hibernate;

import java.util.LinkedHashMap;
import javax.swing.table.TableModel;

import org.hibernate.Session;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Creation-Date: Jan 12, 2007, 5:41:02 PM
 *
 * @author Thomas Morgner
 */
public class HQLDataFactory extends SimpleHQLDataFactory
{
  private LinkedHashMap<String,String> querymappings;

  public HQLDataFactory(final Session connection)
  {
    super(connection);
    querymappings = new LinkedHashMap<String,String>();
  }

  public HQLDataFactory(final SessionProvider connectionProvider)
  {
    super(connectionProvider);
    querymappings = new LinkedHashMap<String,String>();
  }

  /**
   * Checks whether the query would be executable by this datafactory. This performs a rough check, not a full
   * query.
   *
   * @param query
   * @param parameters
   * @return
   */
  public boolean isQueryExecutable(final String query, final DataRow parameters)
  {
    return querymappings.containsKey(query);
  }


  public void setQuery(final String name, final String queryString)
  {
    if (queryString == null)
    {
      querymappings.remove(name);
    }
    else
    {
      querymappings.put(name, queryString);
    }
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The
   * Parameterset given here may contain more data than actually needed.
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
      throw new NullPointerException("Query is null.");
    }
    final String realQuery = getQuery(query);
    if (realQuery == null)
    {
      throw new ReportDataFactoryException("Query '" + query + "' is not recognized.");
    }
    return super.queryData(realQuery, parameters);
  }

  public String getQuery(final String name)
  {
    return querymappings.get(name);
  }

  public String[] getQueryNames()
  {
    return querymappings.keySet().toArray(new String[querymappings.size()]);
  }

  public DataFactory derive()
  {
    final HQLDataFactory df = (HQLDataFactory) super.derive();
    df.querymappings = (LinkedHashMap<String, String>) querymappings.clone();
    return df;
  }
}