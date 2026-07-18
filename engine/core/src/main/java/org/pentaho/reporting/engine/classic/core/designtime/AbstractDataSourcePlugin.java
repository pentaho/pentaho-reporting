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



package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.DataFactory;

public abstract class AbstractDataSourcePlugin implements DataSourcePlugin {
  protected AbstractDataSourcePlugin() {
  }

  public DataFactory performEdit( final DesignTimeContext context, final DataFactory input,
      final String selectedQueryName, final DataFactoryChangeRecorder changeRecorder ) {
    return null;
  }

  public boolean canHandle( final DataFactory dataFactory ) {
    if ( dataFactory == null ) {
      return false;
    }
    if ( getMetaData().getName().equals( dataFactory.getClass().getName() ) ) {
      return true;
    }
    return false;
  }
}
