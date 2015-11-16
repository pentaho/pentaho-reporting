package org.pentaho.reporting.engine.classic.core.modules.misc.connections;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.SingletonHint;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Iterator;

@SingletonHint
public class JndiDataSourceService implements DataSourceService {
  private static class LookupResult {
    public DataSource dataSource;
    public String path;
  }

  private static final String JNDI_PREFIX_CONFIGURATION =
      "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.jndi-prefix.";

  private static final Log logger = LogFactory.getLog( JndiDataSourceService.class );
  private InitialContext initialContext;

  public JndiDataSourceService() {
  }

  private synchronized InitialContext getInitialContext() throws NamingException {
    if ( initialContext == null ) {
      initialContext = new InitialContext();
    }
    return initialContext;
  }

  public synchronized void clearCache() {
    initialContext = null;
  }

  public void clearDataSource( final String dsName ) {
  }

  public DataSource getDataSource( final String dsName ) throws DatasourceServiceException {
    final LookupResult result = findDataSource( dsName );
    return result.dataSource;
  }

  public String getDSBoundName( final String dsName ) throws DatasourceServiceException {
    final LookupResult result = findDataSource( dsName );
    return result.path;
  }

  private LookupResult findDataSource( final String connectionPath ) throws DatasourceServiceException {
    try {
      final Context initialContext = getInitialContext();
      final Object o = initialContext.lookup( connectionPath );
      if ( o instanceof DataSource ) {
        final LookupResult result = new LookupResult();
        result.dataSource = (DataSource) o;
        result.path = connectionPath;
        return result;
      }
    } catch ( NamingException e ) {
      logger.trace( "Failed to lookup JNDI name", e );
      // ignored ..
    }

    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator keys = config.findPropertyKeys( JNDI_PREFIX_CONFIGURATION );
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final String prefix = config.getConfigProperty( key );
      try {
        final Context initialContext = getInitialContext();
        final Object o = initialContext.lookup( prefix + connectionPath );
        if ( o instanceof DataSource ) {
          final LookupResult result = new LookupResult();
          result.dataSource = (DataSource) o;
          result.path = connectionPath;
          return result;
        }
      } catch ( NamingException e ) {
        logger.trace( "Failed to lookup JNDI name", e );
        // ignored ..
      }
    }

    throw new DatasourceServiceException( "Failed to access the JNDI system: Cannot find the requested datasource '"
        + connectionPath + "' anywhere in the JNDI system." );
  }
}
