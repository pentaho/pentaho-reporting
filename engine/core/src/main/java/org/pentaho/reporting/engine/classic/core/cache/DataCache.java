/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.cache;

import javax.swing.table.TableModel;

public interface DataCache {
  public TableModel get( DataCacheKey key );

  public TableModel put( DataCacheKey key, TableModel model );

  public DataCacheManager getCacheManager();
}
