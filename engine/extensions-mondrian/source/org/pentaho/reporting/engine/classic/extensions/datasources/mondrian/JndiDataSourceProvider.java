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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.LFUMap;

public class JndiDataSourceProvider implements DataSourceProvider
{
  private String connectionPath;
  private static InitialContext initialContext;
  private static LFUMap cachedDataSources;

  protected static synchronized InitialContext getInitialContext() throws NamingException
  {
    if (initialContext == null)
    {
      initialContext = new InitialContext();
    }
    return initialContext;
  }

  public JndiDataSourceProvider(final String connectionPath)
  {
    if (connectionPath == null)
    {
      throw new NullPointerException();
    }
    this.connectionPath = connectionPath;
  }

  public String getConnectionPath()
  {
    return connectionPath;
  }

  public boolean equals(final Object o)
  {
    if (this == o)
    {
      return true;
    }
    if (o == null || getClass() != o.getClass())
    {
      return false;
    }

    final JndiDataSourceProvider that = (JndiDataSourceProvider) o;

    if (connectionPath != null ? !connectionPath.equals(that.connectionPath) : that.connectionPath != null)
    {
      return false;
    }

    return true;
  }

  public int hashCode()
  {
    return connectionPath != null ? connectionPath.hashCode() : 0;
  }

  public DataSource getDataSource() throws SQLException
  {
    if (connectionPath == null)
    {
      throw new SQLException("JNDI DataSource is invalid; no connection path is defined.");
    }
    try
    {
      final Context ctx = getInitialContext();
      return findDataSource(ctx, connectionPath);
    }
    catch (NamingException ne)
    {
      throw new SQLException("Failed to access the JNDI system", ne);
    }
  }

  /**
   * @noinspection SynchronizationOnLocalVariableOrMethodParameter
   */
  private DataSource findDataSource(final Context initialContext, final String connectionPath) throws SQLException
  {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();

    final LFUMap map = getDataSourceCache();
    final boolean cacheEnabled = "true".equals(config.getConfigProperty
        ("org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CacheJndiDataSources"));
    if (cacheEnabled)
    {
      synchronized (map)
      {
        final DataSource o1 = (DataSource) map.get(connectionPath);
        if (o1 != null)
        {
          return o1;
        }
      }
    }
    try
    {
      final Object o = initialContext.lookup(connectionPath);
      if (o instanceof DataSource)
      {
        if (cacheEnabled)
        {
          synchronized (map)
          {
            map.put(connectionPath, o);
          }
        }
        return (DataSource) o;
      }
    }
    catch (NamingException e)
    {
      // ignored ..
    }

    final Iterator keys = config.findPropertyKeys
        ("org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.jndi-prefix.");
    while (keys.hasNext())
    {
      final String key = (String) keys.next();
      final String prefix = config.getConfigProperty(key);
      try
      {
        final Object o = initialContext.lookup(prefix + connectionPath);
        if (o instanceof DataSource)
        {
          if (cacheEnabled)
          {
            synchronized (map)
            {
              map.put(connectionPath, o);
            }
          }
          return (DataSource) o;
        }
      }
      catch (NamingException e)
      {
        // ignored ..
      }
    }

    throw new SQLException("Failed to access the JNDI system");
  }

  public static synchronized LFUMap getDataSourceCache()
  {
    if (cachedDataSources == null)
    {
      cachedDataSources = new LFUMap(40);
    }
    return cachedDataSources;
  }

  public Object getConnectionHash()
  {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add(getClass().getName());
    list.add(connectionPath);
    return list;
  }
}
