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

import mondrian.olap.Connection;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.Properties;

public interface MondrianConnectionProvider extends Serializable {
  public Connection createConnection
    ( final Properties properties, final DataSource dataSource ) throws ReportDataFactoryException;

  Object getConnectionHash( final Properties properties ) throws ReportDataFactoryException;
}
