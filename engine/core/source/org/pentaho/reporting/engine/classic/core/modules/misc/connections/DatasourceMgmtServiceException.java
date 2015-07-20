package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

public class DatasourceMgmtServiceException extends RuntimeException {
  public DatasourceMgmtServiceException() {
  }

  public DatasourceMgmtServiceException( final String message ) {
    super( message );
  }

  public DatasourceMgmtServiceException( final String message, final Throwable cause ) {
    super( message, cause );
  }

  public DatasourceMgmtServiceException( final Throwable cause ) {
    super( cause );
  }
}
