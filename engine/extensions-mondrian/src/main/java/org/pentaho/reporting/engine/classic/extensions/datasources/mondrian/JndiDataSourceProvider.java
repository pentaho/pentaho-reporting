package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataSourceService;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceServiceException;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.SQLException;
import java.util.ArrayList;

public class JndiDataSourceProvider implements DataSourceProvider {
  private String connectionPath;
  private transient DataSourceService dataSourceService;

  public JndiDataSourceProvider( final String connectionPath ) {
    if ( connectionPath == null ) {
      throw new NullPointerException();
    }
    this.dataSourceService = ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceService.class );
    this.connectionPath = connectionPath;
  }

  public String getConnectionPath() {
    return connectionPath;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final JndiDataSourceProvider that = (JndiDataSourceProvider) o;

    if ( connectionPath != null ? !connectionPath.equals( that.connectionPath ) : that.connectionPath != null ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return connectionPath != null ? connectionPath.hashCode() : 0;
  }

  public DataSource getDataSource() throws SQLException {
    if ( connectionPath == null ) {
      throw new SQLException( "JNDI DataSource is invalid; no connection path is defined." );
    }
    try {
      return dataSourceService.getDataSource( connectionPath );
    } catch ( DatasourceServiceException ne ) {
      throw new SQLException( "Failed to access the JNDI system", ne );
    }
  }

  public Object getConnectionHash() {
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( connectionPath );
    return list;
  }

  private void readObject( final ObjectInputStream stream )
    throws IOException, ClassNotFoundException {
    stream.defaultReadObject();
    dataSourceService = ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceService.class );
  }
}
