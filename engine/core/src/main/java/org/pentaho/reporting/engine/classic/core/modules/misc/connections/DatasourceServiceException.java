package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public class DatasourceServiceException extends RuntimeException {
  public DatasourceServiceException() {
  }

  public DatasourceServiceException( final String message ) {
    super( message );
  }

  public DatasourceServiceException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public DatasourceServiceException( final Throwable cause ) {
    super( cause );
  }
}
