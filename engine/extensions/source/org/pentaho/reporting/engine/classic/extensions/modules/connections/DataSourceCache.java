package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import javax.sql.DataSource;

public interface DataSourceCache
{
  void put(String name, DataSource pool);

  void clear();

  void remove(String dsName);

  DataSource get(String dsName);
}
