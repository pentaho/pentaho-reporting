/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import javax.sql.DataSource;

public interface DataSourceCache {
  void put( String name, DataSource pool );

  void clear();

  void remove( String dsName );

  DataSource get( String dsName );
}
