package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public class NonExistingDatasourceException extends DatasourceMgmtServiceException {
  public NonExistingDatasourceException() {
  }

  public NonExistingDatasourceException( final String message ) {
    super( message );
  }

  public NonExistingDatasourceException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public NonExistingDatasourceException( final Throwable cause ) {
    super( cause );
  }
}
