/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2019 Object Refinery Ltd, Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AbstractDataFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryQueryTimeoutException;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.table.TableModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;


/**
 * @noinspection AssignmentToCollectionOrArrayFieldFromParameter
 */
public class SimpleSQLReportDataFactory extends AbstractDataFactory {
  private transient Connection connection;
  private ConnectionProvider connectionProvider;
  private static final Log logger = LogFactory.getLog( SimpleSQLReportDataFactory.class );

  private boolean columnNameMapping;
  private static final String COLUMN_NAME_MAPPING_KEY =
      "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ColumnNameMapping"; //$NON-NLS-1$
  private static final String[] EMPTY_NAMES = new String[0];
  private transient Statement currentRunningStatement;

  private String userField;
  private String passwordField;

  public static Configuration globalConfig;

  public SimpleSQLReportDataFactory() {
    globalConfig = ClassicEngineBoot.getInstance().getGlobalConfig();

    if ( globalConfig != null ) {
      this.columnNameMapping = "Name".equalsIgnoreCase( globalConfig.getConfigProperty( //$NON-NLS-1$
              SimpleSQLReportDataFactory.COLUMN_NAME_MAPPING_KEY, "Name" ) ); //$NON-NLS-1$
    }
  }

  public SimpleSQLReportDataFactory( final Connection connection ) {
    this( new StaticConnectionProvider( connection ) );
  }

  public SimpleSQLReportDataFactory( final ConnectionProvider connectionProvider ) {
    this();
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }
    this.connectionProvider = connectionProvider;
  }

  public String getUserField() {
    return userField;
  }

  public void setUserField( final String userField ) {
    this.userField = userField;
  }

  public String getPasswordField() {
    return passwordField;
  }

  public void setPasswordField( final String passwordField ) {
    this.passwordField = passwordField;
  }

  protected synchronized Connection getConnection( final DataRow dataRow ) throws SQLException {
    if ( connection == null ) {
      final String user;
      if ( userField == null ) {
        user = null;
      } else {
        final Object userRaw = dataRow.get( userField );
        if ( userRaw instanceof String ) {
          user = String.valueOf( userRaw );
        } else {
          user = null;
        }
      }

      final String password;
      if ( passwordField == null ) {
        password = null;
      } else {
        final Object passwordField = dataRow.get( this.passwordField );
        if ( passwordField instanceof String ) {
          password = String.valueOf( passwordField );
        } else {
          password = null;
        }
      }

      connection = connectionProvider.createConnection( user, password );
    }
    if ( connection == null ) {
      throw new SQLException( "Unable to get a connection from the Connection-Provider." );
    }
    return connection;
  }

  public int getBestResultSetType( final DataRow dataRow ) throws SQLException {
    if ( globalConfig != null && "simple".equalsIgnoreCase( globalConfig.getConfigProperty( //$NON-NLS-1$
            ResultSetTableModelFactory.RESULTSET_FACTORY_MODE ) ) ) { //$NON-NLS-1$
      return ResultSet.TYPE_FORWARD_ONLY;
    }

    final Connection connection = getConnection( dataRow );
    final boolean supportsScrollInsensitive =
        connection.getMetaData().supportsResultSetType( ResultSet.TYPE_SCROLL_INSENSITIVE );
    final boolean supportsScrollSensitive =
        connection.getMetaData().supportsResultSetType( ResultSet.TYPE_SCROLL_SENSITIVE );

    if ( supportsScrollInsensitive ) {
      return ResultSet.TYPE_SCROLL_INSENSITIVE;
    }
    if ( supportsScrollSensitive ) {
      return ResultSet.TYPE_SCROLL_SENSITIVE;
    }
    return ResultSet.TYPE_FORWARD_ONLY;
  }

  /**
   * Queries a datasource. The string 'query' defines the name of the query. The Parameterset given here may contain
   * more data than actually needed.
   * <p/>
   * The dataset may change between two calls, do not assume anything!
   *
   * @param query
   * @param parameters
   * @return
   */
  public synchronized TableModel queryData( final String query, final DataRow parameters )
    throws ReportDataFactoryException {
    try {
      final ParametrizationProviderFactory factory = createParametrizationProviderFactory();

      final Connection connection = getConnection( parameters );
      final ParametrizationProvider parametrizationProvider = factory.create( connection );
      final String translatedQuery =
          parametrizationProvider.rewriteQueryForParametrization( connection, query, parameters );
      final String[] preparedParameterNames = parametrizationProvider.getPreparedParameterNames();
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Translated-Query: " + translatedQuery );
        logger.debug( "Detected parameter:" + Arrays.asList( preparedParameterNames ) );
      }

      return parametrizeAndQuery( parameters, translatedQuery, preparedParameterNames );
      // it catch exception only for java 1.6 and jdbc 4
    } catch ( SQLTimeoutException e ) {
      throw new ReportDataFactoryQueryTimeoutException();
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Failed to execute query.", e ); //$NON-NLS-1$
    } finally {
      currentRunningStatement = null;
    }
  }

  private ParametrizationProviderFactory createParametrizationProviderFactory() throws ReportDataFactoryException {
    final ParametrizationProviderFactory factory;

    String parametrizationProviderClassname = null;
    if ( globalConfig != null ) {
      parametrizationProviderClassname = globalConfig.getConfigProperty(
              "org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.ParametrizationProviderFactory" );
    }

    if ( parametrizationProviderClassname == null ) {
      factory = new DefaultParametrizationProviderFactory();
    } else {
      factory =
          ObjectUtilities.loadAndInstantiate( parametrizationProviderClassname, SimpleSQLReportDataFactory.class,
              ParametrizationProviderFactory.class );
      if ( factory == null ) {
        throw new ReportDataFactoryException( "The specified parametrization factory is not valid: "
            + parametrizationProviderClassname );
      }
    }
    return factory;
  }

  public String[] getReferencedFields( final String query, final DataRow parameters ) throws ReportDataFactoryException {

    final boolean isNewConnection = connection == null;
    try {
      final ParametrizationProviderFactory factory = createParametrizationProviderFactory();
      final Connection connection = getConnection( parameters );
      final ParametrizationProvider parametrizationProvider = factory.create( connection );
      final String computedQuery = computedQuery( query, parameters );
      parametrizationProvider.rewriteQueryForParametrization( connection, computedQuery, parameters );
      final LinkedHashSet<String> list = new LinkedHashSet<String>();
      list.addAll( Arrays.asList( parametrizationProvider.getPreparedParameterNames() ) );
      if ( userField != null ) {
        list.add( userField );
      }
      if ( passwordField != null ) {
        list.add( passwordField );
      }
      list.add( DataFactory.QUERY_LIMIT );
      return list.toArray( new String[ list.size() ] );
    } catch ( ReportDataFactoryException e ) {
      logger.warn( "Unable to perform cache preparation", e );
      throw e;
    } catch ( SQLException e ) {
      logger.warn( "Unable to perform cache preparation", e );
      throw new ReportDataFactoryException( "Unable to perform cache preparation", e );
    } finally {
      if ( isNewConnection ) {
        close();
      }
    }
  }

  protected String translateQuery( final String query ) {
    return query;
  }

  protected String computedQuery( final String queryName, final DataRow parameters ) throws ReportDataFactoryException {
    return queryName;
  }

  public static boolean isExpandArrayParameterNeeded( final String query ) {
    return isCallableStatement( query ) == false && isCallableStatementQuery( query ) == false;
  }

  protected TableModel parametrizeAndQuery( final DataRow parameters, final String translatedQuery,
      final String[] preparedParameterNames ) throws SQLException {
    final boolean callableStatementQuery = isCallableStatementQuery( translatedQuery );
    final boolean callableStatementUsed = callableStatementQuery || isCallableStatement( translatedQuery );
    final Statement statement;
    if ( preparedParameterNames.length == 0 ) {
      statement =
          getConnection( parameters ).createStatement( getBestResultSetType( parameters ), ResultSet.CONCUR_READ_ONLY );
    } else {
      if ( callableStatementUsed ) {
        final CallableStatement pstmt =
          getConnection( parameters ).prepareCall( translatedQuery, getBestResultSetType( parameters ),
              ResultSet.CONCUR_READ_ONLY );
        if ( isCallableStatementQuery( translatedQuery ) ) {
          pstmt.registerOutParameter( 1, Types.OTHER );
          parametrize( parameters, preparedParameterNames, pstmt, false, 1 );
        } else {
          parametrize( parameters, preparedParameterNames, pstmt, false, 0 );
        }
        statement = pstmt;
      } else {
        final PreparedStatement pstmt =
          getConnection( parameters ).prepareStatement( translatedQuery, getBestResultSetType( parameters ),
              ResultSet.CONCUR_READ_ONLY );
        parametrize( parameters, preparedParameterNames, pstmt, isExpandArrays(), 0 );
        statement = pstmt;
      }
    }

    final Object queryLimit = parameters.get( DataFactory.QUERY_LIMIT );
    try {
      if ( queryLimit instanceof Number ) {
        final Number i = (Number) queryLimit;
        final int max = i.intValue();
        if ( max > 0 ) {
          statement.setMaxRows( max );
        }
      }
    } catch ( SQLException sqle ) {
    // this fails for MySQL as their driver is buggy. We will not add workarounds here, as
    // all drivers are buggy and this is a race we cannot win. Put pressure on the driver
    // manufacturer instead.
      logger.warn( "Driver indicated error: Failed to set query-limit: " + queryLimit, sqle );
    }
    final Object queryTimeout = parameters.get( DataFactory.QUERY_TIMEOUT );
    try {
      if ( queryTimeout instanceof Number ) {
        final Number i = (Number) queryTimeout;
        final int seconds = i.intValue();
        if ( seconds > 0 ) {
          statement.setQueryTimeout( seconds );
        }
      }
    } catch ( SQLException sqle ) {
      logger.warn( "Driver indicated error: Failed to set query-timeout: " + queryTimeout, sqle );
    }

    // Track the currently running statement - just in case someone needs to cancel it
    final ResultSet res;
    try {
      currentRunningStatement = statement;
      res = performQuery( statement, translatedQuery, preparedParameterNames );
    } finally {
      currentRunningStatement = null;
    }

    // equalsIgnore, as this is what the ResultSetTableModelFactory uses.
    boolean simpleMode = true;
    if ( globalConfig != null ) {
      simpleMode = "simple".equalsIgnoreCase( globalConfig.getConfigProperty( //$NON-NLS-1$
              ResultSetTableModelFactory.RESULTSET_FACTORY_MODE ) );
    }

    if ( simpleMode ) {
      return ResultSetTableModelFactory.getInstance().generateDefaultTableModel( res, columnNameMapping );
    }
    return ResultSetTableModelFactory.getInstance().createTableModel( res, columnNameMapping, true );
  }

  public ResultSet performQuery( Statement statement, final String translatedQuery, final String[] preparedParameterNames )
    throws SQLException {
    final ResultSet res;
    if ( preparedParameterNames.length == 0 ) {
      res = statement.executeQuery( translatedQuery );
    } else {
      final PreparedStatement pstmt = (PreparedStatement) statement;
      res = pstmt.executeQuery();
    }
    return res;
  }

  private void parametrize( final DataRow parameters, final String[] params, final PreparedStatement pstmt,
      final boolean expandArrays, final int parameterOffset ) throws SQLException {
    pstmt.clearParameters();
    int paramIndex = parameterOffset;
    ParameterMetaData parameterMetaData = null;
    try {
      parameterMetaData = pstmt.getParameterMetaData();
    } catch ( Exception e ) {
      logger.debug( "Parameter metadata fetching threw an exception:" + e.getMessage() );
    }
    for ( int i = 0; i < params.length; i++ ) {
      final String param = params[i];
      final Object pvalue = parameters.get( param );
      String typeClass = null;
      if ( parameterMetaData != null ) {
        try {
          typeClass = parameterMetaData.getParameterClassName( paramIndex + 1 );
        } catch ( Exception e ) {
          logger.debug( "Parameter metadata fetching threw an exception:" + e.getMessage() );
        }
      }
      if ( pvalue == null ) {
        // this should work, but some driver are known to die here.
        // they should be fed with setNull(..) instead; something
        // we cant do as JDK1.2's JDBC does not define it.
        pstmt.setObject( paramIndex + 1, null );
        logger.debug( "Parametrize: " + ( paramIndex + 1 ) + " set to <null>" );
        paramIndex++;
      } else if ( expandArrays && pvalue instanceof Object[] ) {
        final Object[] values = (Object[]) pvalue;
        if ( values.length > 0 ) {
          for ( int j = 0; j < values.length; j++ ) {
            final Object ivalue = values[j];
            if ( ivalue instanceof java.sql.Date || ivalue instanceof java.sql.Time || ivalue instanceof Timestamp ) {
              pstmt.setObject( paramIndex + 1, ivalue );
            } else if ( ivalue instanceof Date ) {
              // for now we're going to convert java.util.Date to java.sql.Timestamp
              // this seems to be a better fit for most jdbc drivers/databases
              // if problems come from this, we can create workaround them as discovered
              final Date d = (Date) ivalue;
              pstmt.setObject( paramIndex + 1, new Timestamp( d.getTime() ) );
            } else if ( typeClass != null && typeClass.equals( "java.lang.String" ) ) {
              pstmt.setObject( paramIndex + 1, String.valueOf( ivalue ) );
            } else {
              pstmt.setObject( paramIndex + 1, ivalue );
            }
            logger.debug( "Parametrize: Array: " + ( paramIndex + 1 ) + ": " + ivalue );
            paramIndex++;
          }
        } else {
          pstmt.setObject( paramIndex + 1, null );
          logger.debug( "Parametrize: Array: " + ( paramIndex + 1 ) + " set to <null> for empty array" );
          paramIndex++;
        }
      } else {
        if ( pvalue instanceof java.sql.Date || pvalue instanceof java.sql.Time || pvalue instanceof Timestamp ) {
          pstmt.setObject( paramIndex + 1, pvalue );
        } else if ( pvalue instanceof Date ) {
          // see comment above about java.util.Date/java.sql.Timestamp conversion
          final Date d = (Date) pvalue;
          pstmt.setObject( paramIndex + 1, new Timestamp( d.getTime() ) );
        } else if ( typeClass != null && typeClass.equals( "java.lang.String" ) ) {
          pstmt.setObject( paramIndex + 1, String.valueOf( pvalue ) );
        } else {
          pstmt.setObject( paramIndex + 1, pvalue );
        }
        logger.debug( "Parametrize: " + ( paramIndex + 1 ) + ": " + pvalue );
        paramIndex++;
      }
    }
  }

  protected boolean isExpandArrays() {
    return true;
  }

  public void cancelRunningQuery() {
    if ( currentRunningStatement == null ) {
      return;
    }
    try {
      logger.debug( "Cancelling the running query..." );
      currentRunningStatement.cancel();
    } catch ( SQLException e ) {
      // Apparently this is not supported for this driver.
      logger.warn( "Could not cancel running query [maybe the driver does not support that operation] : "
          + e.getMessage() );
    } finally {
      logger.debug( "Returning from attempt to cancel current running statement" );
    }
  }

  private static boolean isCallableStatement( final String query ) {
    int state = 0;
    final char[] chars = query.toCharArray();
    final int length = query.length();
    for ( int i = 0; i < length; i++ ) {
      final char c = chars[i];
      if ( Character.isWhitespace( c ) ) {
        if ( state == 5 ) {
          return true;
        }
      } else if ( '{' == c && state == 0 ) {
        state = 1;
      } else if ( ( 'c' == c || 'C' == c ) && state == 1 ) {
        state = 2;
      } else if ( ( 'a' == c || 'A' == c ) && state == 2 ) {
        state = 3;
      } else if ( ( 'l' == c || 'L' == c ) && state == 3 ) {
        state = 4;
      } else if ( ( 'l' == c || 'L' == c ) && state == 4 ) {
        state = 5;
      } else {
        if ( state == 5 ) {
          return true;
        }
        return false;
      }
    }
    return false;
  }

  private static boolean isCallableStatementQuery( final String query ) {
    int state = 0;
    final char[] chars = query.toCharArray();
    final int length = query.length();
    for ( int i = 0; i < length; i++ ) {
      final char c = chars[i];
      if ( Character.isWhitespace( c ) ) {
        if ( state == 7 ) {
          return true;
        }
      } else if ( '{' == c && state == 0 ) {
        state = 1;
      } else if ( '?' == c && state == 1 ) {
        state = 2;
      } else if ( '=' == c && state == 2 ) {
        state = 3;
      } else if ( ( 'c' == c || 'C' == c ) && state == 3 ) {
        state = 4;
      } else if ( ( 'a' == c || 'A' == c ) && state == 4 ) {
        state = 5;
      } else if ( ( 'l' == c || 'L' == c ) && state == 5 ) {
        state = 6;
      } else if ( ( 'l' == c || 'L' == c ) && state == 6 ) {
        state = 7;
      } else {
        if ( state == 7 ) {
          return true;
        }
        return false;
      }
    }
    return false;
  }

  public synchronized void close() {
    if ( connection == null ) {
      return;
    }

    try {
      connection.close();
    } catch ( SQLException e ) {
      // we tried our very best ..
    }

    connection = null;
  }

  public SimpleSQLReportDataFactory clone() {
    final SimpleSQLReportDataFactory dataFactory = (SimpleSQLReportDataFactory) super.clone();
    dataFactory.connection = null;
    return dataFactory;
  }

  public void setConnectionProvider( final ConnectionProvider connectionProvider ) {
    if ( connectionProvider == null ) {
      throw new NullPointerException();
    }

    if ( connection != null ) {
      throw new IllegalStateException();
    }
    this.connectionProvider = connectionProvider;
  }

  public ConnectionProvider getConnectionProvider() {
    return connectionProvider;
  }

  public boolean isQueryExecutable( final String query, final DataRow parameters ) {
    return true;
  }

  public String[] getQueryNames() {
    return EMPTY_NAMES;
  }

  public ArrayList<Object> getQueryHash( final String queryName, final DataRow parameter ) {
    final Object connection = getConnectionProvider().getConnectionHash();
    final ArrayList<Object> list = new ArrayList<Object>();
    list.add( getClass().getName() );
    list.add( translateQuery( queryName ) );
    list.add( connection );
    return list;
  }
}
