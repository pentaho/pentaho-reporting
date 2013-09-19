package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.Serializable;
import java.sql.SQLException;
import javax.sql.DataSource;

public interface DataSourceProvider extends Serializable
{
  public DataSource getDataSource() throws SQLException;

  public Object getConnectionHash();
}
