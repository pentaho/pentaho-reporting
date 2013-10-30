package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import java.util.HashMap;
import javax.sql.DataSource;

import org.pentaho.reporting.libraries.base.boot.SingletonHint;

@SingletonHint
public class StaticDataSourceCacheManager implements DataSourceCacheManager, DataSourceCache
{
  private HashMap<String, DataSource> backend;

  public StaticDataSourceCacheManager()
  {
    backend = new HashMap<String, DataSource>();
  }

  public void put(final String name, final DataSource pool)
  {
    backend.put(name, pool);
  }

  public void clear()
  {
    backend.clear();
  }

  public void remove(final String dsName)
  {
    backend.remove(dsName);
  }

  public DataSource get(final String dsName)
  {
    return backend.get(dsName);
  }

  public DataSourceCache getDataSourceCache()
  {
    return this;
  }
}
