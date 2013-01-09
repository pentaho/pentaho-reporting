package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.Serializable;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 * Todo: Document me!
 * <p/>
 * Date: 24.08.2009
 * Time: 19:00:58
 *
 * @author Thomas Morgner.
 */
public interface DataSourceProvider extends Serializable
{
  public DataSource getDataSource() throws SQLException;

  public Object getConnectionHash();
}
