package org.pentaho.reporting.engine.classic.core.modules.misc.connections.writer;

import org.pentaho.database.model.IDatabaseConnection;

import java.io.IOException;
import java.io.OutputStream;

public interface DataSourceMgmtWriter {
  void write( IDatabaseConnection[] connections, OutputStream out ) throws IOException;
}
