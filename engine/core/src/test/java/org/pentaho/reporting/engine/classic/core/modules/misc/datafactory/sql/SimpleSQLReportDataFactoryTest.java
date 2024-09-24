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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

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
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.hamcrest.Matchers.emptyArray;
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
}
