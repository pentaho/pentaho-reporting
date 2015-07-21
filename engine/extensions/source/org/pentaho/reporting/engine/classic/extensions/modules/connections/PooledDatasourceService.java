package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataSourceMgmtService;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataSourceService;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceMgmtServiceException;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceServiceException;
import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.base.boot.SingletonHint;

import javax.sql.DataSource;

@SingletonHint
public class PooledDatasourceService implements DataSourceService {
  private DataSourceCache cacheManager;

  public PooledDatasourceService() {
    final ObjectFactory objectFactory = ClassicEngineBoot.getInstance().getObjectFactory();
    final DataSourceCacheManager manager = objectFactory.get( DataSourceCacheManager.class );
    cacheManager = manager.getDataSourceCache();
  }

  protected DataSource retrieve( final String datasource ) throws DatasourceServiceException {
    try {
      final DataSourceMgmtService datasourceMgmtSvc =
        ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceMgmtService.class );
      try {
        final IDatabaseConnection databaseConnection = datasourceMgmtSvc.getDatasourceByName( datasource );
        return PooledDatasourceHelper.setupPooledDataSource( databaseConnection );
      } catch ( DatasourceMgmtServiceException daoe ) {
        return queryFallback( datasource );
      }
    } catch ( DatasourceMgmtServiceException daoe ) {
      throw new DatasourceServiceException
        ( Messages.getInstance().getString( "PooledDataSourceService.ERROR_0002_UNABLE_TO_GET_DATASOURCE", datasource ),
          daoe ); //$NON-NLS-1$
    }
  }

  protected DataSource queryFallback( final String dataSource ) {
    throw new DatasourceServiceException
      ( Messages.getInstance()
        .getString( "PooledDataSourceService.ERROR_0002_UNABLE_TO_GET_DATASOURCE", dataSource ) ); //$NON-NLS-1$
  }

  /**
   * This method clears the JNDI DS cache.  The need exists because after a JNDI connection edit the old DS must be
   * removed from the cache.
   */
  public void clearCache() {
    cacheManager.clear();
  }

  /**
   * This method clears the JNDI DS cache.  The need exists because after a JNDI connection edit the old DS must be
   * removed from the cache.
   */
  public void clearDataSource( final String dsName ) {
    cacheManager.remove( dsName );
  }

  /**
   * Since JNDI is supported different ways in different app servers, it's nearly impossible to have a ubiquitous way to
   * look up a datasource. This method is intended to hide all the lookups that may be required to find a jndi name.
   *
   * @param dsName The Datasource name
   * @return DataSource if there is one bound in JNDI
   */
  public DataSource getDataSource( final String dsName )
    throws DatasourceServiceException {
    if ( cacheManager != null ) {
      final DataSource foundDs = cacheManager.get( dsName );
      if ( foundDs != null ) {
        return foundDs;
      } else {
        return retrieve( dsName );
      }
    }
    return null;
  }


  /**
   * Since JNDI is supported different ways in different app servers, it's nearly impossible to have a ubiquitous way to
   * look up a datasource. This method is intended to hide all the lookups that may be required to find a jndi name, and
   * return the actual bound name.
   *
   * @param dsName The Datasource name (like SampleData)
   * @return The bound DS name if it is bound in JNDI (like "jdbc/SampleData")
   * @throws org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceServiceException
   */
  public String getDSBoundName( final String dsName ) throws DatasourceServiceException {
    return dsName;
  }
}
