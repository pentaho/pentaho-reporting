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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.SQLException;

public interface DataSourceProvider extends Serializable {
  public DataSource getDataSource() throws SQLException;

  public Object getConnectionHash();
}
