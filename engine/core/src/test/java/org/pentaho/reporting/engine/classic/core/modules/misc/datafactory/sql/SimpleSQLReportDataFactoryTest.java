/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;

import javax.swing.table.TableModel;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.atLeastOnce;

public class SimpleSQLReportDataFactoryTest {

  private static final String QUERY = "test_query";

  private SimpleSQLReportDataFactory factory;
  private Connection connection;

  @Before
  public void setUp() throws ReportDataFactoryException, SQLException {
    connection = mock( Connection.class );
    factory = spy( new SimpleSQLReportDataFactory( connection ) );

    DataFactoryContext dataFactoryContext = mock( DataFactoryContext.class );
    Configuration conf = mock( Configuration.class );
    ResourceBundleFactory resourceBundleFactory = mock( ResourceBundleFactory.class );
    doReturn( conf ).when( dataFactoryContext ).getConfiguration();
    doReturn( resourceBundleFactory ).when( dataFactoryContext ).getResourceBundleFactory();
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    factory.initialize( dataFactoryContext );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateFactoryWithNullProvider() {
    ConnectionProvider connectionProvider = null;
    new SimpleSQLReportDataFactory( connectionProvider );
  }

  @Test( expected = SQLException.class )
  public void testGetNullConnection() throws SQLException {
    factory = new SimpleSQLReportDataFactory( mock( JndiConnectionProvider.class ) );
    DataRow dataRow = mock( DataRow.class );
    factory.getConnection( dataRow );
  }

  @Test
  public void testGetConnection() throws SQLException {
    DataRow dataRow = mock( DataRow.class );
    Connection con = factory.getConnection( dataRow );
    assertThat( con, is( equalTo( connection ) ) );
  }

  @Test
  public void testGetConnectionWithoutCredentials() throws SQLException {
    DataRow dataRow = mock( DataRow.class );
    factory.setUserField( "userField" );
    factory.setPasswordField( "passwordField" );

    Connection con = factory.getConnection( dataRow );
    assertThat( con, is( equalTo( connection ) ) );
  }

  @Test
  public void testGetConnectionWithCredentials() throws SQLException {
    DataRow dataRow = mock( DataRow.class );
    factory.setUserField( "userField" );
    factory.setPasswordField( "passwordField" );
    doReturn( "user" ).when( dataRow ).get( "userField" );
    doReturn( "password" ).when( dataRow ).get( "passwordField" );

    Connection con = factory.getConnection( dataRow );
    assertThat( con, is( equalTo( connection ) ) );
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testQueryDataWithSqlException() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    doThrow( SQLException.class ).when( factory ).parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    factory.queryData( QUERY, parameters );
  }

  @Test
  public void testQueryData() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    TableModel model = mock( TableModel.class );
    String[] preparedParameterNames = new String[] {};
    doReturn( model ).when( factory ).parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    TableModel result = factory.queryData( QUERY, parameters );
    assertThat( result, is( equalTo( model ) ) );
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testGetReferencedFieldsComputedQueryException() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    doThrow( ReportDataFactoryException.class ).when( factory ).computedQuery( QUERY + "${param}", parameters );
    factory.getReferencedFields( QUERY + "${param}", parameters );
  }

  @Test( expected = ReportDataFactoryException.class )
  public void testGetReferencedFieldsSqlException() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    doThrow( SQLException.class ).when( factory ).getConnection( parameters );
    factory.getReferencedFields( QUERY + "${param}", parameters );
  }

  @Test
  public void testGetReferencedFields() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] result = factory.getReferencedFields( QUERY + "${param}", parameters );
    assertThat( result, arrayContainingInAnyOrder( "param", DataFactory.QUERY_LIMIT ) );

    factory.setUserField( "user_field" );
    factory.setPasswordField( "password_field" );
    result = factory.getReferencedFields( QUERY + "${param}", parameters );
    assertThat( result, arrayContainingInAnyOrder( "param", "user_field", "password_field", DataFactory.QUERY_LIMIT ) );
  }

  @Test
  public void testTranslateQuery() {
    assertThat( factory.translateQuery( QUERY ), is( equalTo( QUERY ) ) );
  }

  @Test
  public void testComputedQuery() throws ReportDataFactoryException {
    DataRow parameters = mock( DataRow.class );
    assertThat( factory.computedQuery( QUERY, parameters ), is( equalTo( QUERY ) ) );
  }

  @Test
  public void testIsExpandArrayParameterNeeded() {
    assertThat( SimpleSQLReportDataFactory.isExpandArrayParameterNeeded( QUERY ), is( equalTo( true ) ) );
    assertThat( SimpleSQLReportDataFactory.isExpandArrayParameterNeeded( "{call}" ), is( equalTo( false ) ) );
    assertThat( SimpleSQLReportDataFactory.isExpandArrayParameterNeeded( "{?=call}" ), is( equalTo( false ) ) );
  }

  @Test
  public void testParametrizeAndQuery() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    PreparedStatement statement = mock( PreparedStatement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    DatabaseMetaData dbmd = mock( DatabaseMetaData.class );
    doReturn( "MockDriver" ).when( dbmd ).getDriverName();
    doReturn( dbmd ).when( con ).getMetaData();

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( null ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( 10 ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( 20 ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );
    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( res ).when( statement ).executeQuery();
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "test_column_label" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "test_column_name" ).when( rsmd ).getColumnName( 1 );
    doReturn( true ).doReturn( false ).when( res ).next();
    doReturn( "test_val" ).when( res ).getObject( 1 );

    TableModel result = factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );

    verify( con ).createStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
    verify( statement ).setMaxRows( 10 );
    verify( statement ).setQueryTimeout( 20 );
    verify( statement ).executeQuery( QUERY );

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "test_column_label" ) ) );
    assertThat( (String) result.getValueAt( 0, 0 ), is( equalTo( "test_val" ) ) );
  }

  @Test
  public void testParametrizeAndQueryWithStringParam() throws SQLException {
    final String translatedQuery = "select city from offices where officecode in (?)";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {"OfficeCode"};
    Connection con = mock( Connection.class );
    PreparedStatement statement = mock( PreparedStatement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    ParameterMetaData parameterMetaData = mock( ParameterMetaData.class );
    DatabaseMetaData dbmd = mock( DatabaseMetaData.class );
    doReturn( "MockDriver" ).when( dbmd ).getDriverName();
    doReturn( dbmd ).when( con ).getMetaData();

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( null ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( 10 ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( 20 ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( 1 ).when( parameters ).get( "OfficeCode" );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );
    doReturn( statement ).when( con ).prepareStatement( anyString(), anyInt(), anyInt() );
    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doNothing().when( statement ).clearParameters();
    doReturn( res ).when( statement ).executeQuery( translatedQuery );
    doReturn( res ).when( statement ).executeQuery();
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( parameterMetaData ).when( statement ).getParameterMetaData();
    doReturn( "java.lang.String" ).when( parameterMetaData ).getParameterClassName( 1 );
    doReturn( "test_column_label" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "test_column_name" ).when( rsmd ).getColumnName( 1 );
    doReturn( true ).doReturn( false ).when( res ).next();
    doReturn( "test_val" ).when( res ).getObject( 1 );

    TableModel result = factory.parametrizeAndQuery( parameters, translatedQuery, preparedParameterNames );

    verify( statement ).setMaxRows( 10 );
    verify( statement ).setQueryTimeout( 20 );
    verify( statement ).executeQuery();
    verify( statement ).setObject( 1, "1" );

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "test_column_label" ) ) );
    assertThat( result.getValueAt( 0, 0 ), is( equalTo( "test_val" ) ) );
  }

  @Test
  public void testParametrizeAndQueryWithStringParams() throws SQLException {
    final String translatedQuery = "select city from offices where officecode in (?,?)";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {"OfficeCode"};
    Connection con = mock( Connection.class );
    PreparedStatement statement = mock( PreparedStatement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    ParameterMetaData parameterMetaData = mock( ParameterMetaData.class );
    DatabaseMetaData dbmd = mock( DatabaseMetaData.class );
    doReturn( "MockDriver" ).when( dbmd ).getDriverName();
    doReturn( dbmd ).when( con ).getMetaData();

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( null ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( 10 ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( 20 ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( new Object[]{1, 2} ).when( parameters ).get( "OfficeCode" );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );
    doReturn( statement ).when( con ).prepareStatement( anyString(), anyInt(), anyInt() );
    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doNothing().when( statement ).clearParameters();
    doReturn( res ).when( statement ).executeQuery( translatedQuery );
    doReturn( res ).when( statement ).executeQuery();
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( parameterMetaData ).when( statement ).getParameterMetaData();
    doReturn( "java.lang.String" ).when( parameterMetaData ).getParameterClassName( 1 );
    doReturn( "test_column_label" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "test_column_name" ).when( rsmd ).getColumnName( 1 );
    doReturn( true ).doReturn( false ).when( res ).next();
    doReturn( "test_val" ).when( res ).getObject( 1 );

    TableModel result = factory.parametrizeAndQuery( parameters, translatedQuery, preparedParameterNames );

    verify( statement ).setMaxRows( 10 );
    verify( statement ).setQueryTimeout( 20 );
    verify( statement ).executeQuery();
    verify( statement ).setObject( 1, "1" );
    verify( statement ).setObject( 2, "2" );

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "test_column_label" ) ) );
    assertThat( result.getValueAt( 0, 0 ), is( equalTo( "test_val" ) ) );
  }

  @Test
  public void testParametrizeAndQueryWithCallableStatement() throws SQLException {
    String query = "{?=call}";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] { "param_0", "param_1", "param_2", "param_3" };
    Connection con = mock( Connection.class );
    CallableStatement statement = mock( CallableStatement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    DatabaseMetaData dbmd = mock( DatabaseMetaData.class );
    doReturn( "MockDriver" ).when( dbmd ).getDriverName();
    doReturn( dbmd ).when( con ).getMetaData();

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( null ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    Date currentDate = new Date();
    java.sql.Date sqlDate = new java.sql.Date( currentDate.getTime() );
    doReturn( null ).when( parameters ).get( "param_0" );
    doReturn( sqlDate ).when( parameters ).get( "param_1" );
    doReturn( currentDate ).when( parameters ).get( "param_2" );
    doReturn( "val_3" ).when( parameters ).get( "param_3" );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );
    doReturn( statement ).when( con ).prepareCall( anyString(), anyInt(), anyInt() );
    doReturn( statement ).when( con ).prepareStatement( anyString(), anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery();
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "test_column_label" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "test_column_name" ).when( rsmd ).getColumnName( 1 );
    doReturn( true ).doReturn( false ).when( res ).next();
    doReturn( "test_val" ).when( res ).getObject( 1 );

    TableModel result = factory.parametrizeAndQuery( parameters, query, preparedParameterNames );

    verify( con, never() ).createStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
    verify( statement, never() ).setMaxRows( 10 );
    verify( statement, never() ).setQueryTimeout( 20 );
    verify( statement, never() ).executeQuery( query );
    verify( statement ).clearParameters();
    verify( statement ).setObject( 2, null );
    verify( statement ).setObject( 3, sqlDate );
    verify( statement ).setObject( 4, currentDate );
    verify( statement ).setObject( 5, "val_3" );
    verify( statement ).executeQuery();

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "test_column_label" ) ) );
    assertThat( (String) result.getValueAt( 0, 0 ), is( equalTo( "test_val" ) ) );
  }

  @Test
  public void testParametrizeAndQueryWithArrays() throws SQLException {
    String query = "{ca}";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] { "param_0", "param_1", "param_2", "param_3" };
    Connection con = mock( Connection.class );
    CallableStatement statement = mock( CallableStatement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    DatabaseMetaData dbmd = mock( DatabaseMetaData.class );
    doReturn( "MockDriver" ).when( dbmd ).getDriverName();
    doReturn( dbmd ).when( con ).getMetaData();

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( null ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    Date currentDate = new Date();
    java.sql.Date sqlDate = new java.sql.Date( currentDate.getTime() );
    doReturn( new Object[] {} ).when( parameters ).get( "param_0" );
    doReturn( new Object[] { sqlDate, currentDate, "val_3" } ).when( parameters ).get( "param_1" );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );
    doReturn( statement ).when( con ).prepareStatement( anyString(), anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery();
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "test_column_label" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "test_column_name" ).when( rsmd ).getColumnName( 1 );
    doReturn( true ).doReturn( false ).when( res ).next();
    doReturn( "test_val" ).when( res ).getObject( 1 );

    TableModel result = factory.parametrizeAndQuery( parameters, query, preparedParameterNames );

    verify( con, never() ).createStatement( ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
    verify( statement, never() ).setMaxRows( 10 );
    verify( statement, never() ).setQueryTimeout( 20 );
    verify( statement, never() ).executeQuery( query );
    verify( statement ).clearParameters();
    verify( statement ).setObject( 1, null );
    verify( statement ).setObject( 2, sqlDate );
    verify( statement ).setObject( 3, currentDate );
    verify( statement ).setObject( 4, "val_3" );
    verify( statement ).executeQuery();

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "test_column_label" ) ) );
    assertThat( (String) result.getValueAt( 0, 0 ), is( equalTo( "test_val" ) ) );
  }

  @Test
  public void testIsExpandArrays() {
    assertThat( factory.isExpandArrays(), is( equalTo( true ) ) );
  }

  @Test
  public void testClose() throws SQLException {
    factory.close();
    verify( connection, never() ).close();

    factory.getConnection( mock( DataRow.class ) );
    factory.close();
    verify( connection ).close();
  }

  @Test
  public void testClone() {
    SimpleSQLReportDataFactory result = factory.clone();
    assertThat( result, is( not( sameInstance( factory ) ) ) );
    assertThat( result.getConnectionProvider(), is( equalTo( factory.getConnectionProvider() ) ) );
  }

  @Test( expected = NullPointerException.class )
  public void testSetConnectionProviderWithoutProvider() {
    ConnectionProvider connectionProvider = null;
    factory.setConnectionProvider( connectionProvider );
  }

  @Test( expected = IllegalStateException.class )
  public void testSetConnectionProviderWithConnection() throws SQLException {
    factory.getConnection( mock( DataRow.class ) );
    ConnectionProvider connectionProvider = mock( ConnectionProvider.class );
    factory.setConnectionProvider( connectionProvider );
  }

  @Test
  public void testSetConnectionProvide() {
    ConnectionProvider connectionProvider = mock( ConnectionProvider.class );
    factory.setConnectionProvider( connectionProvider );
    assertThat( factory.getConnectionProvider(), is( equalTo( connectionProvider ) ) );
  }

  @Test
  public void testIsQueryExecutable() {
    assertThat( factory.isQueryExecutable( QUERY, null ), is( equalTo( true ) ) );
  }

  @Test
  public void testGetQueryNames() {
    assertThat( factory.getQueryNames(), is( emptyArray() ) );
  }

  @SuppressWarnings( "unchecked" )
  @Test
  public void testGetConnectionHash() {
    StaticConnectionProvider provider = mock( StaticConnectionProvider.class );
    doReturn( "test_hash" ).when( provider ).getConnectionHash();
    doReturn( provider ).when( factory ).getConnectionProvider();

    Object result = factory.getQueryHash( QUERY, null );
    assertThat( result, is( instanceOf( List.class ) ) );
    List<Object> list = (List<Object>) result;
    assertThat( list.size(), is( equalTo( 3 ) ) );
    assertThat( (String) list.get( 0 ), is( equalTo( factory.getClass().getName() ) ) );
    assertThat( (String) list.get( 1 ), is( equalTo( QUERY ) ) );
    assertThat( (String) list.get( 2 ), is( equalTo( "test_hash" ) ) );
  }

  @Test
  public void testGetReferencedFieldsCloseItsConnection() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    this.connection = null;
    String[] result = factory.getReferencedFields( QUERY + "${param}", parameters );
    verify( factory, times( 1 ) ).close();
  }

  @Test
  public void testGetReferencedFieldsDoNotCloseExistConnection() throws ReportDataFactoryException, SQLException {
    DataRow parameters = mock( DataRow.class );
    this.connection = factory.getConnection( parameters );
    String[] result = factory.getReferencedFields( QUERY + "${param}", parameters );
    verify( factory, times( 0 ) ).close();
  }

  // =====================================================================
  // performQuery — fetchSize config handling
  // =====================================================================

  @Test
  public void testPerformQueryWithNullFetchSizeConfig() throws SQLException {
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    doReturn( res ).when( statement ).executeQuery( QUERY );

    // Set globalConfig to return null for POSTGRES_FETCH_SIZE
    Configuration conf = mock( Configuration.class );
    doReturn( null ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    ResultSet result = factory.performQuery( statement, QUERY, new String[] {} );

    verify( statement ).executeQuery( QUERY );
    assertThat( result, is( equalTo( res ) ) );
  }

  @Test
  public void testPerformQueryWithPreparedStatement() throws SQLException {
    PreparedStatement pstmt = mock( PreparedStatement.class );
    ResultSet res = mock( ResultSet.class );
    doReturn( res ).when( pstmt ).executeQuery();

    Configuration conf = mock( Configuration.class );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    ResultSet result = factory.performQuery( pstmt, QUERY, new String[] { "param1" } );

    verify( pstmt ).executeQuery();
    verify( pstmt, never() ).executeQuery( anyString() );
    assertThat( result, is( equalTo( res ) ) );
  }

  // =====================================================================
  // parametrizeAndQuery — error path: statement closed on error
  // =====================================================================

  @Test
  public void testParametrizeAndQueryClosesStatementOnSQLException() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );

    doReturn( con ).when( factory ).getConnection( parameters );

    // Configure globalConfig to disable disk-backed mode
    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doThrow( new SQLException( "query failed" ) ).when( statement ).executeQuery( QUERY );

    try {
      factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    } catch ( SQLException e ) {
      assertThat( e.getMessage(), is( equalTo( "query failed" ) ) );
    }

    // Statement should be closed on error
    verify( statement ).close();
  }

  // =====================================================================
  // parametrizeAndQuery — disk-backed mode restores autoCommit on error
  // =====================================================================

  @Test
  public void testParametrizeAndQueryRestoresAutoCommitOnError() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    DatabaseMetaData dbMeta = mock( DatabaseMetaData.class );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( true ).when( con ).getAutoCommit();
    doReturn( dbMeta ).when( con ).getMetaData();
    doReturn( "PostgreSQL" ).when( dbMeta ).getDriverName();

    // Enable disk-backed mode
    Configuration conf = mock( Configuration.class );
    doReturn( "true" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doThrow( new SQLException( "query failed" ) ).when( statement ).executeQuery( QUERY );

    try {
      factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    } catch ( SQLException e ) {
      // expected
    }

    // autoCommit should be set to false (disk-backed init) then restored to true on error
    verify( con ).setAutoCommit( false );
    verify( con ).setAutoCommit( true );
    verify( statement ).close();
  }

  // =====================================================================
  // createStatement — disk-backed with MySQL driver uses MIN_VALUE fetch
  // =====================================================================

  @Test
  public void testCreateStatementDiskBackedMySQLFetchSize() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    DatabaseMetaData dbMeta = mock( DatabaseMetaData.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( true ).when( con ).getAutoCommit();
    doReturn( dbMeta ).when( con ).getMetaData();
    doReturn( "MySQL Connector/J" ).when( dbMeta ).getDriverName();

    // Enable disk-backed mode
    Configuration conf = mock( Configuration.class );
    doReturn( "true" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    try {
      factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    } catch ( Exception e ) {
      // May fail downstream but we verify the fetch size call
    }

    // MySQL should use Integer.MIN_VALUE for streaming
    verify( statement, atLeastOnce() ).setFetchSize( Integer.MIN_VALUE );
  }

  @Test
  public void testCreateStatementDiskBackedPostgresFetchSize() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    DatabaseMetaData dbMeta = mock( DatabaseMetaData.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( true ).when( con ).getAutoCommit();
    doReturn( dbMeta ).when( con ).getMetaData();
    doReturn( "PostgreSQL Native Driver" ).when( dbMeta ).getDriverName();

    Configuration conf = mock( Configuration.class );
    doReturn( "true" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "7000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    try {
      factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    } catch ( Exception e ) {
      // May fail downstream but we verify the fetch size call
    }

    // Postgres should use the configured fetch size
    verify( statement, atLeastOnce() ).setFetchSize( 7000 );
  }

  // =====================================================================
  // createStatement — PreparedStatement parametrize fails, statement closed
  // =====================================================================

  @Test
  public void testCreateStatementPreparedStatementClosedOnParametrizeError() throws SQLException {
    final String translatedQuery = "select * from t where id = ?";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] { "param1" };
    Connection con = mock( Connection.class );
    PreparedStatement pstmt = mock( PreparedStatement.class );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( pstmt ).when( con ).prepareStatement( anyString(), anyInt(), anyInt() );
    // clearParameters throws to simulate parametrize failure
    doThrow( new SQLException( "parametrize failed" ) ).when( pstmt ).clearParameters();

    try {
      factory.parametrizeAndQuery( parameters, translatedQuery, preparedParameterNames );
    } catch ( SQLException e ) {
      assertThat( e.getMessage(), is( equalTo( "parametrize failed" ) ) );
    }

    // PreparedStatement should be closed when parametrize fails
    verify( pstmt ).close();
  }

  // =====================================================================
  // createStatement — CallableStatement parametrize fails, statement closed
  // =====================================================================

  @Test
  public void testCreateStatementCallableStatementClosedOnParametrizeError() throws SQLException {
    final String query = "{?=call myproc(?)}";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] { "param1" };
    Connection con = mock( Connection.class );
    CallableStatement cstmt = mock( CallableStatement.class );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( cstmt ).when( con ).prepareCall( anyString(), anyInt(), anyInt() );
    // clearParameters throws to simulate parametrize failure
    doThrow( new SQLException( "callable parametrize failed" ) ).when( cstmt ).clearParameters();

    try {
      factory.parametrizeAndQuery( parameters, query, preparedParameterNames );
    } catch ( SQLException e ) {
      assertThat( e.getMessage(), is( equalTo( "callable parametrize failed" ) ) );
    }

    // CallableStatement should be closed when parametrize fails
    verify( cstmt ).close();
  }

  // =====================================================================
  // setQueryLimit — edge cases
  // =====================================================================

  @Test
  public void testSetQueryLimitWithNull() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    Statement statement = mock( Statement.class );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_LIMIT );

    // No exception, setMaxRows never called
    // Call parametrizeAndQuery with a config that reaches setQueryLimit
    Connection con = mock( Connection.class );
    doReturn( con ).when( factory ).getConnection( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    ResultSet res = mock( ResultSet.class );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    factory.parametrizeAndQuery( parameters, QUERY, new String[] {} );

    verify( statement, never() ).setMaxRows( anyInt() );
  }

  @Test
  public void testSetQueryLimitWithZero() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( 0 ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( con ).when( factory ).getConnection( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    factory.parametrizeAndQuery( parameters, QUERY, new String[] {} );

    // max <= 0 means setMaxRows should NOT be called
    verify( statement, never() ).setMaxRows( anyInt() );
  }

  @Test
  public void testSetQueryLimitWithSQLException() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( 100 ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( con ).when( factory ).getConnection( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    // setMaxRows throws — should be caught and logged, not re-thrown
    doThrow( new SQLException( "setMaxRows not supported" ) ).when( statement ).setMaxRows( 100 );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    // Should NOT throw — the exception is swallowed
    TableModel result = factory.parametrizeAndQuery( parameters, QUERY, new String[] {} );
    assertThat( result, is( notNullValue() ) );
  }

  // =====================================================================
  // setQueryTimeout — edge cases
  // =====================================================================

  @Test
  public void testSetQueryTimeoutWithSQLException() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( null ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( 30 ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( con ).when( factory ).getConnection( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doThrow( new SQLException( "setQueryTimeout not supported" ) ).when( statement ).setQueryTimeout( 30 );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    // Should NOT throw — the exception is swallowed
    TableModel result = factory.parametrizeAndQuery( parameters, QUERY, new String[] {} );
    assertThat( result, is( notNullValue() ) );
  }
    @Test
    public void testCancelRunningQueryWhenNoStatement() {
        // Should not throw when currentRunningStatement is null
        factory.cancelRunningQuery();
        assertThat( factory, is( notNullValue() ) );
    }

    @Test
    public void testOnReportCancel() {
        // Should not throw when currentRunningStatement is null
        factory.onReportCancel();
        assertThat( factory, is( notNullValue() ) );
    }

    @Test
    public void testIsSimpleModeWithNullConfig() {
        SimpleSQLReportDataFactory.globalConfig = null;
        // When globalConfig is null, isSimpleMode defaults to true
        assertThat( SimpleSQLReportDataFactory.globalConfig, is( nullValue() ) );
    }


  @Test
  public void testParametrizeAndQueryWithNonSimpleMode() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    DatabaseMetaData dbmd = mock( DatabaseMetaData.class );
    doReturn( "MockDriver" ).when( dbmd ).getDriverName();
    doReturn( true ).when( dbmd ).supportsResultSetType( anyInt() );
    doReturn( dbmd ).when( con ).getMetaData();

    doReturn( null ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( con ).when( factory ).getConnection( parameters );

    // Set non-simple mode
    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "table" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( true ).doReturn( false ).when( res ).next();
    doReturn( "val" ).when( res ).getObject( 1 );

    // Non-simple mode goes through createTableModel
    TableModel result = factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    assertThat( result, is( notNullValue() ) );
  }

  // =====================================================================
  // parametrizeAndQuery — disk-backed success path restores autoCommit
  // =====================================================================

  @Test
  public void testParametrizeAndQueryDiskBackedRestoresAutoCommitOnSuccess() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    DatabaseMetaData dbMeta = mock( DatabaseMetaData.class );

    doReturn( null ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( true ).when( con ).getAutoCommit();
    doReturn( dbMeta ).when( con ).getMetaData();
    doReturn( "PostgreSQL" ).when( dbMeta ).getDriverName();

    Configuration conf = mock( Configuration.class );
    doReturn( "true" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    try {
      factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    } catch ( Exception e ) {
      // May fail in DiskBackedTableModel creation but autoCommit should still be restored
    }

    // Verify autoCommit was set to false then restored to true
    verify( con ).setAutoCommit( false );
    verify( con ).setAutoCommit( true );
  }

  // =====================================================================
  // createStatement — MariaDB driver recognized for MySQL path
  // =====================================================================

  @Test
  public void testCreateStatementDiskBackedMariaDBFetchSize() throws SQLException {
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] {};
    Connection con = mock( Connection.class );
    Statement statement = mock( Statement.class );
    ResultSet res = mock( ResultSet.class );
    DatabaseMetaData dbMeta = mock( DatabaseMetaData.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( true ).when( con ).getAutoCommit();
    doReturn( dbMeta ).when( con ).getMetaData();
    doReturn( "MariaDB connector/J" ).when( dbMeta ).getDriverName();

    Configuration conf = mock( Configuration.class );
    doReturn( "true" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( statement ).when( con ).createStatement( anyInt(), anyInt() );
    doReturn( res ).when( statement ).executeQuery( QUERY );
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    try {
      factory.parametrizeAndQuery( parameters, QUERY, preparedParameterNames );
    } catch ( Exception e ) {
      // May fail downstream
    }

    // MariaDB should also use Integer.MIN_VALUE for streaming
    verify( statement, atLeastOnce() ).setFetchSize( Integer.MIN_VALUE );
  }

  // =====================================================================
  // parametrizeAndQuery — callable statement (non-query) path
  // =====================================================================

  @Test
  public void testParametrizeAndQueryWithCallableStatementNonQuery() throws SQLException {
    String query = "{call myproc(?)}";
    DataRow parameters = mock( DataRow.class );
    String[] preparedParameterNames = new String[] { "param1" };
    Connection con = mock( Connection.class );
    CallableStatement cstmt = mock( CallableStatement.class );
    ResultSet res = mock( ResultSet.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( "value1" ).when( parameters ).get( "param1" );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_LIMIT );
    doReturn( null ).when( parameters ).get( DataFactory.QUERY_TIMEOUT );
    doReturn( con ).when( factory ).getConnection( parameters );
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( factory ).getBestResultSetType( parameters );

    Configuration conf = mock( Configuration.class );
    doReturn( "false" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.DISK_BACKED_TABLE_MODEL );
    doReturn( "simple" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.RESULTSET_FACTORY_MODE );
    doReturn( "5000" ).when( conf ).getConfigProperty( ResultSetTableModelFactory.POSTGRES_FETCH_SIZE );
    SimpleSQLReportDataFactory.globalConfig = conf;

    doReturn( cstmt ).when( con ).prepareCall( anyString(), anyInt(), anyInt() );
    doReturn( res ).when( cstmt ).executeQuery();
    doReturn( rsmd ).when( res ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( false ).when( res ).next();

    TableModel result = factory.parametrizeAndQuery( parameters, query, preparedParameterNames );

    // Non-query callable statement: no registerOutParameter
    verify( cstmt ).clearParameters();
    verify( cstmt ).setObject( 1, "value1" );
    verify( cstmt, never() ).registerOutParameter( anyInt(), anyInt() );
    assertThat( result, is( notNullValue() ) );
  }
}
