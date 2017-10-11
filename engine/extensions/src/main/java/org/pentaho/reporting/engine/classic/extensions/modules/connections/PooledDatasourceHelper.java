/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU General Public License, version 2 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/gpl-2.0.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * 
 * Copyright 2005 - 2017 Hitachi Vantara.  All rights reserved.
 *  
 * @created Jul 07, 2008 
 * @author rmansoor
 */
package org.pentaho.reporting.engine.classic.extensions.modules.connections;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.pentaho.database.DatabaseDialectException;
import org.pentaho.database.IDatabaseDialect;
import org.pentaho.database.IDriverLocator;
import org.pentaho.database.dialect.GenericDatabaseDialect;
import org.pentaho.database.model.IDatabaseConnection;
import org.pentaho.database.service.IDatabaseDialectService;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DataBaseConnectionAttributes;
import org.pentaho.reporting.engine.classic.core.modules.misc.connections.DatasourceServiceException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.sql.Driver;
import java.util.Map;
import java.util.Properties;

public class PooledDatasourceHelper {
  private static final Log logger = LogFactory.getLog( PooledDatasourceHelper.class );
  public static final String GENERIC = "GENERIC";

  private PooledDatasourceHelper() {
  }

  public static PoolingDataSource setupPooledDataSource( final IDatabaseConnection databaseConnection )
    throws DatasourceServiceException {
    try {
      final DataSourceCacheManager cacheManager =
          ClassicEngineBoot.getInstance().getObjectFactory().get( DataSourceCacheManager.class );
      final IDatabaseDialectService databaseDialectService =
          ClassicEngineBoot.getInstance().getObjectFactory().get( IDatabaseDialectService.class );
      final IDatabaseDialect dialect = databaseDialectService.getDialect( databaseConnection );

      final String driverClass;
      if ( GENERIC.equals( databaseConnection.getDatabaseType().getShortName() ) ) { //$NON-NLS-1$
        driverClass = databaseConnection.getAttributes().get( GenericDatabaseDialect.ATTRIBUTE_CUSTOM_DRIVER_CLASS );
      } else {
        driverClass = dialect.getNativeDriver();
      }

      String url;
      try {
        url = dialect.getURLWithExtraOptions( databaseConnection );
      } catch ( DatabaseDialectException e ) {
        url = null;
      }

      // Read default connection pooling parameter
      final String maxdleConn = getSystemSetting( "dbcp-defaults.max-idle-conn" ); //$NON-NLS-1$
      final String minIdleConn = getSystemSetting( "dbcp-defaults.min-idle-conn" ); //$NON-NLS-1$
      final String maxActConn = getSystemSetting( "dbcp-defaults.max-act-conn" ); //$NON-NLS-1$
      String validQuery = null;
      final String whenExhaustedAction = getSystemSetting( "dbcp-defaults.when-exhausted-action" ); //$NON-NLS-1$
      final String wait = getSystemSetting( "dbcp-defaults.wait" ); //$NON-NLS-1$
      final String testWhileIdleValue = getSystemSetting( "dbcp-defaults.test-while-idle" ); //$NON-NLS-1$
      final String testOnBorrowValue = getSystemSetting( "dbcp-defaults.test-on-borrow" ); //$NON-NLS-1$
      final String testOnReturnValue = getSystemSetting( "dbcp-defaults.test-on-return" ); //$NON-NLS-1$
      final boolean testWhileIdle =
          !StringUtils.isEmpty( testWhileIdleValue ) && Boolean.parseBoolean( testWhileIdleValue );
      final boolean testOnBorrow =
          !StringUtils.isEmpty( testOnBorrowValue ) && Boolean.parseBoolean( testOnBorrowValue );
      final boolean testOnReturn =
          !StringUtils.isEmpty( testOnReturnValue ) && Boolean.parseBoolean( testOnReturnValue );
      int maxActiveConnection = -1;
      long waitTime = -1;
      byte whenExhaustedActionType = -1;
      final int minIdleConnection = !StringUtils.isEmpty( minIdleConn ) ? Integer.parseInt( minIdleConn ) : -1;
      final int maxIdleConnection = !StringUtils.isEmpty( maxdleConn ) ? Integer.parseInt( maxdleConn ) : -1;

      final Map<String, String> attributes = databaseConnection.getAttributes();

      if ( attributes.containsKey( DataBaseConnectionAttributes.MAX_ACTIVE_KEY ) ) {
        maxActiveConnection = Integer.parseInt( attributes.get( DataBaseConnectionAttributes.MAX_ACTIVE_KEY ) );
      } else {
        if ( !StringUtils.isEmpty( maxActConn ) ) {
          maxActiveConnection = Integer.parseInt( maxActConn );
        }
      }
      if ( attributes.containsKey( DataBaseConnectionAttributes.MAX_WAIT_KEY ) ) {
        waitTime = Integer.parseInt( attributes.get( DataBaseConnectionAttributes.MAX_WAIT_KEY ) );
      } else {
        if ( !StringUtils.isEmpty( wait ) ) {
          waitTime = Long.parseLong( wait );
        }
      }
      if ( attributes.containsKey( DataBaseConnectionAttributes.QUERY_KEY ) ) {
        validQuery = attributes.get( DataBaseConnectionAttributes.QUERY_KEY );
      }
      if ( !StringUtils.isEmpty( whenExhaustedAction ) ) {
        whenExhaustedActionType = Byte.parseByte( whenExhaustedAction );
      } else {
        whenExhaustedActionType = GenericObjectPool.WHEN_EXHAUSTED_BLOCK;
      }

      final PoolingDataSource poolingDataSource = new PoolingDataSource();

      // As the name says, this is a generic pool; it returns basic Object-class objects.
      final GenericObjectPool pool = new GenericObjectPool( null );
      pool.setWhenExhaustedAction( whenExhaustedActionType );

      // Tuning the connection pool
      pool.setMaxActive( maxActiveConnection );
      pool.setMaxIdle( maxIdleConnection );
      pool.setMaxWait( waitTime );
      pool.setMinIdle( minIdleConnection );
      pool.setTestWhileIdle( testWhileIdle );
      pool.setTestOnReturn( testOnReturn );
      pool.setTestOnBorrow( testOnBorrow );
      pool.setTestWhileIdle( testWhileIdle );
      /*
       * ConnectionFactory creates connections on behalf of the pool. Here, we use the DriverManagerConnectionFactory
       * because that essentially uses DriverManager as the source of connections.
       */
      final Properties properties = new Properties();
      properties.setProperty( "user", databaseConnection.getUsername() );
      properties.setProperty( "password", databaseConnection.getPassword() );
      final ConnectionFactory factory =
        new DriverConnectionFactory( getDriver( dialect, driverClass, url ), url, properties );

      /*
       * Puts pool-specific wrappers on factory connections. For clarification: "[PoolableConnection]Factory," not
       * "Poolable[ConnectionFactory]."
       */
      // This declaration is used implicitly.
      // noinspection UnusedDeclaration
      final PoolableConnectionFactory pcf = new PoolableConnectionFactory( factory, // ConnectionFactory
          pool, // ObjectPool
          null, // KeyedObjectPoolFactory
          validQuery, // String (validation query)
          false, // boolean (default to read-only?)
          true // boolean (default to auto-commit statements?)
      );

      /*
       * initialize the pool to X connections
       */
      logger.debug( "Pool defaults to " + maxActiveConnection + " max active/" + maxIdleConnection + "max idle"
          + "with " + waitTime + "wait time"//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
          + " idle connections." ); //$NON-NLS-1$

      for ( int i = 0; i < maxIdleConnection; ++i ) {
        pool.addObject();
      }
      logger.debug( "Pool now has " + pool.getNumActive() + " active/" + pool.getNumIdle() + " idle connections." ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      /*
       * All of this is wrapped in a DataSource, which client code should already know how to handle (since it's the
       * same class of object they'd fetch via the container's JNDI tree
       */
      poolingDataSource.setPool( pool );

      // store the pool, so we can get to it later
      cacheManager.getDataSourceCache().put( databaseConnection.getName(), poolingDataSource );
      return ( poolingDataSource );
    } catch ( Exception e ) {
      throw new DatasourceServiceException( e );
    }
  }

  static Driver getDriver( IDatabaseDialect dialect, String driverClass, String url ) {
    if ( dialect instanceof IDriverLocator ) {
      return ( (IDriverLocator) dialect ).getDriver( url );
    } else {
      return ObjectUtilities.loadAndInstantiate( driverClass, PooledDatasourceHelper.class, Driver.class );
    }
  }

  private static String getSystemSetting( final String key ) {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    return config.getConfigProperty( "org.pentaho.reporting.engine.classic.core." + key );
  }
}
