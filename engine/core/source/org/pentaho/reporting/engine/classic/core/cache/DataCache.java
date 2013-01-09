package org.pentaho.reporting.engine.classic.core.cache;

import javax.swing.table.TableModel;

public interface DataCache
{
  public TableModel get(DataCacheKey key);
  public TableModel put (DataCacheKey key, TableModel model);

  public DataCacheManager getCacheManager();
}
