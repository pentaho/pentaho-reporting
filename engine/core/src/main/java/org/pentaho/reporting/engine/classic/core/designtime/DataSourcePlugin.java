/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.designtime;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;

/**
 * Represents a DataFactory-Editor plugin. The plugin must be stateless.
 *
 * @author Thomas Morgner
 */
public interface DataSourcePlugin {
  public DataFactory performEdit( DesignTimeContext context, DataFactory input, String selectedQueryName,
      DataFactoryChangeRecorder changeRecorder );

  public boolean canHandle( DataFactory dataFactory );

  public DataFactoryMetaData getMetaData();
}
