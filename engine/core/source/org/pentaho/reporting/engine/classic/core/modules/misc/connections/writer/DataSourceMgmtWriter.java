package org.pentaho.reporting.engine.classic.core.modules.misc.connections.writer;

import java.io.IOException;
import java.io.OutputStream;

import org.pentaho.database.model.IDatabaseConnection;

public interface DataSourceMgmtWriter
{
  void write(IDatabaseConnection[] connections, OutputStream out) throws IOException;
}
