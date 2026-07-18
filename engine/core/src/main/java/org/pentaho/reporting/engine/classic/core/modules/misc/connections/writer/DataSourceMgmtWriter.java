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

package org.pentaho.reporting.engine.classic.core.modules.misc.connections.writer;

import org.pentaho.database.model.IDatabaseConnection;

import java.io.IOException;
import java.io.OutputStream;

public interface DataSourceMgmtWriter {
  void write( IDatabaseConnection[] connections, OutputStream out ) throws IOException;
}
