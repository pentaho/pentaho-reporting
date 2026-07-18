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



package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections;

import org.olap4j.OlapConnection;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Creation-Date: Dec 12, 2006, 1:53:44 PM
 *
 * @author Thomas Morgner
 */
public interface OlapConnectionProvider extends Serializable {
  public OlapConnection createConnection( final String user, final String password ) throws SQLException;

  public Object getConnectionHash();
}
