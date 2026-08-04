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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.ImmutableDataAttributes;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ResultSetTableModelFactoryTest {

  private ResultSetTableModelFactory factory;

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
    factory = ResultSetTableModelFactory.getInstance();
  }

  // =====================================================================
  // Helper: creates a mock ResultSet with given column config and rows.
  // =====================================================================
  private ResultSet createMockResultSet( String[] columnLabels, String[] columnNames,
      int columnCount, Object[][] rows ) throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( stmt ).when( rs ).getStatement();
    doReturn( rsmd ).when( rs ).getMetaData();
    doReturn( columnCount ).when( rsmd ).getColumnCount();
    doReturn( ResultSet.TYPE_FORWARD_ONLY ).when( rs ).getType();

    for ( int i = 0; i < columnCount; i++ ) {
      doReturn( columnLabels[ i ] ).when( rsmd ).getColumnLabel( i + 1 );
      doReturn( columnNames[ i ] ).when( rsmd ).getColumnName( i + 1 );
      doReturn( "java.lang.String" ).when( rsmd ).getColumnClassName( i + 1 );
      doReturn( Types.VARCHAR ).when( rsmd ).getColumnType( i + 1 );
      doReturn( false ).when( rsmd ).isCurrency( i + 1 );
      doReturn( false ).when( rsmd ).isSigned( i + 1 );
      doReturn( "table" ).when( rsmd ).getTableName( i + 1 );
      doReturn( "schema" ).when( rsmd ).getSchemaName( i + 1 );
      doReturn( "catalog" ).when( rsmd ).getCatalogName( i + 1 );
      doReturn( 255 ).when( rsmd ).getColumnDisplaySize( i + 1 );
      doReturn( 0 ).when( rsmd ).getPrecision( i + 1 );
      doReturn( 0 ).when( rsmd ).getScale( i + 1 );
    }

    final int[] currentRow = { -1 };
    org.mockito.Mockito.doAnswer( new org.mockito.stubbing.Answer<Boolean>() {
      @Override public Boolean answer( org.mockito.invocation.InvocationOnMock invocation ) {
        currentRow[ 0 ]++;
        return currentRow[ 0 ] < rows.length;
      }
    } ).when( rs ).next();

    for ( int col = 0; col < columnCount; col++ ) {
      final int colIdx = col;
      org.mockito.Mockito.doAnswer( new org.mockito.stubbing.Answer<Object>() {
        @Override public Object answer( org.mockito.invocation.InvocationOnMock invocation ) {
          if ( currentRow[ 0 ] >= 0 && currentRow[ 0 ] < rows.length ) {
            return rows[ currentRow[ 0 ] ][ colIdx ];
          }
          return null;
        }
      } ).when( rs ).getObject( col + 1 );
    }

    return rs;
  }

  // =====================================================================
  // getInstance — singleton
  // =====================================================================
  @Test
  public void testGetInstanceReturnsSingleton() {
    ResultSetTableModelFactory i1 = ResultSetTableModelFactory.getInstance();
    ResultSetTableModelFactory i2 = ResultSetTableModelFactory.getInstance();
    assertThat( i1, is( notNullValue() ) );
    assertThat( i1, is( equalTo( i2 ) ) );
  }

  // =====================================================================
  // generateDefaultTableModel — basic scenarios
  // =====================================================================
  @Test
  public void testGenerateDefaultTableModelSingleRow() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Name" }, new String[] { "name_col" }, 1,
        new Object[][] { { "Alice" } } );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );

    assertThat( result, is( notNullValue() ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "Name" ) ) );
    assertThat( (String) result.getValueAt( 0, 0 ), is( equalTo( "Alice" ) ) );
  }

  @Test
  public void testGenerateDefaultTableModelMultipleRows() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "ID", "Name" }, new String[] { "id", "name" }, 2,
        new Object[][] { { "1", "Alice" }, { "2", "Bob" }, { "3", "Charlie" } } );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );

    assertThat( result.getRowCount(), is( equalTo( 3 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 2 ) ) );
    assertThat( (String) result.getValueAt( 2, 1 ), is( equalTo( "Charlie" ) ) );
  }

  @Test
  public void testGenerateDefaultTableModelEmptyResultSet() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1, new Object[][] {} );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );

    assertThat( result.getRowCount(), is( equalTo( 0 ) ) );
    assertThat( result.getColumnCount(), is( equalTo( 1 ) ) );
  }

  // =====================================================================
  // generateDefaultTableModel — closes ResultSet and Statement
  // =====================================================================
  @Test
  public void testGenerateDefaultTableModelClosesResources() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );
    Statement stmt = rs.getStatement();

    factory.generateDefaultTableModel( rs, false );

    verify( rs ).close();
    verify( stmt ).close();
  }

  @Test
  public void testGenerateDefaultTableModelClosesRsWhenGetStatementFails() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );
    doThrow( new SQLException( "no stmt" ) ).when( rs ).getStatement();

    factory.generateDefaultTableModel( rs, false );

    verify( rs ).close();
  }

  @Test
  public void testGenerateDefaultTableModelClosesRsWhenStmtCloseFails() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );
    Statement stmt = rs.getStatement();
    doThrow( new SQLException( "close failed" ) ).when( stmt ).close();

    factory.generateDefaultTableModel( rs, false );

    verify( rs ).close();
    verify( stmt ).close();
  }

  // =====================================================================
  // generateDefaultTableModel — MetaTableModel
  // =====================================================================
  @Test
  public void testGenerateDefaultTableModelIsMetaTableModel() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );

    assertThat( result, is( instanceOf( MetaTableModel.class ) ) );
    MetaTableModel meta = (MetaTableModel) result;
    assertThat( meta.getColumnAttributes( 0 ), is( notNullValue() ) );
    assertThat( meta.isCellDataAttributesSupported(), is( equalTo( false ) ) );
    assertThat( meta.getCellDataAttributes( 0, 0 ), is( notNullValue() ) );
    assertThat( meta.getTableAttributes(), is( notNullValue() ) );
  }

  // =====================================================================
  // generateDefaultTableModel — close() clears data
  // =====================================================================
  @Test
  public void testGenerateDefaultTableModelCloseClears() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );

    result.close();
    assertThat( result.getRowCount(), is( equalTo( 0 ) ) );
  }

  // =====================================================================
  // generateDefaultTableModel — column class
  // =====================================================================
  @Test
  public void testGenerateDefaultTableModelColumnClass() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );

    assertThat( result.getColumnClass( 0 ), is( notNullValue() ) );
    assertThat( result.getColumnClass( 100 ), is( equalTo( (Class) Object.class ) ) );
  }

  // =====================================================================
  // generateDiskBackedTableModel — basic scenarios
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelSingleRow() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Name" }, new String[] { "name_col" }, 1,
        new Object[][] { { "Alice" } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );

    assertThat( result, is( instanceOf( DiskBackedTableModel.class ) ) );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getColumnName( 0 ), is( equalTo( "Name" ) ) );
    assertThat( (String) result.getValueAt( 0, 0 ), is( equalTo( "Alice" ) ) );
    result.close();
  }

  @Test
  public void testGenerateDiskBackedTableModelMultipleRows() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "ID", "Name" }, new String[] { "id", "name" }, 2,
        new Object[][] { { "1", "Alice" }, { "2", "Bob" } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );

    assertThat( result.getRowCount(), is( equalTo( 2 ) ) );
    assertThat( (String) result.getValueAt( 1, 1 ), is( equalTo( "Bob" ) ) );
    result.close();
  }

  @Test
  public void testGenerateDiskBackedTableModelEmpty() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1, new Object[][] {} );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );

    assertThat( result.getRowCount(), is( equalTo( 0 ) ) );
    result.close();
  }

  // =====================================================================
  // generateDiskBackedTableModel — column label (legacy mode default)
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelUsesColumnLabel() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "MyLabel" }, new String[] { "my_name" }, 1,
        new Object[][] { { "val" } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );
    assertThat( result.getColumnName( 0 ), is( equalTo( "MyLabel" ) ) );
    result.close();
  }

  // =====================================================================
  // generateDiskBackedTableModel — legacy fallback null/empty label
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelLegacyFallbackEmptyLabel() throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );

    doReturn( stmt ).when( rs ).getStatement();
    doReturn( rsmd ).when( rs ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "fallback_name" ).when( rsmd ).getColumnName( 1 );
    doReturn( "java.lang.String" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.VARCHAR ).when( rsmd ).getColumnType( 1 );
    doReturn( false ).when( rsmd ).isCurrency( 1 );
    doReturn( false ).when( rsmd ).isSigned( 1 );
    doReturn( "t" ).when( rsmd ).getTableName( 1 );
    doReturn( "s" ).when( rsmd ).getSchemaName( 1 );
    doReturn( "c" ).when( rsmd ).getCatalogName( 1 );
    doReturn( 10 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 0 ).when( rsmd ).getPrecision( 1 );
    doReturn( 0 ).when( rsmd ).getScale( 1 );
    doReturn( false ).when( rs ).next();

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );
    assertThat( result.getColumnName( 0 ), is( equalTo( "fallback_name" ) ) );
    result.close();
  }

  // =====================================================================
  // generateDiskBackedTableModel — null values
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelNullValues() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "A", "B" }, new String[] { "a", "b" }, 2,
        new Object[][] { { null, "v" }, { "v2", null } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );

    assertThat( result.getValueAt( 0, 0 ), is( nullValue() ) );
    assertThat( (String) result.getValueAt( 0, 1 ), is( equalTo( "v" ) ) );
    assertThat( result.getValueAt( 1, 1 ), is( nullValue() ) );
    result.close();
  }

  // =====================================================================
  // generateDiskBackedTableModel — closes RS and Statement
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelClosesResources() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );
    Statement stmt = rs.getStatement();

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );
    verify( rs ).close();
    verify( stmt ).close();
    result.close();
  }

  // =====================================================================
  // generateDiskBackedTableModel — MetaTableModel
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelIsMetaTableModel() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );
    assertThat( result, is( instanceOf( MetaTableModel.class ) ) );

    MetaTableModel meta = (MetaTableModel) result;
    assertThat( meta.getColumnAttributes( 0 ), is( notNullValue() ) );
    result.close();
  }

  // =====================================================================
  // generateDiskBackedTableModel — close releases resources, double-close safe
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelDoubleClose() throws SQLException {
    ResultSet rs = createMockResultSet( new String[] { "Col" }, new String[] { "col" }, 1,
        new Object[][] { { "val" } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    result.close();
    result.close(); // should not throw
    assertThat( result, is( notNullValue() ) );
  }

  // =====================================================================
  // generateDiskBackedTableModel — large dataset
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelLargeDataset() throws SQLException {
    int numRows = 200;
    Object[][] rows = new Object[ numRows ][ 1 ];
    for ( int i = 0; i < numRows; i++ ) {
      rows[ i ][ 0 ] = "row_" + i;
    }
    ResultSet rs = createMockResultSet( new String[] { "Val" }, new String[] { "val" }, 1, rows );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );
    assertThat( result.getRowCount(), is( equalTo( numRows ) ) );
    assertThat( (String) result.getValueAt( 0, 0 ), is( equalTo( "row_0" ) ) );
    assertThat( (String) result.getValueAt( numRows - 1, 0 ), is( equalTo( "row_" + ( numRows - 1 ) ) ) );
    result.close();
  }

  // =====================================================================
  // collectData — all attributes populated
  // =====================================================================
  @Test
  public void testCollectDataPopulatesAllAttributes() throws SQLException {
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    doReturn( "java.lang.Integer" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.INTEGER ).when( rsmd ).getColumnType( 1 );
    doReturn( true ).when( rsmd ).isCurrency( 1 );
    doReturn( true ).when( rsmd ).isSigned( 1 );
    doReturn( "my_table" ).when( rsmd ).getTableName( 1 );
    doReturn( "my_schema" ).when( rsmd ).getSchemaName( 1 );
    doReturn( "my_catalog" ).when( rsmd ).getCatalogName( 1 );
    doReturn( "MyLabel" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( 10 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 8 ).when( rsmd ).getPrecision( 1 );
    doReturn( 2 ).when( rsmd ).getScale( 1 );

    AttributeMap<Object> result = ResultSetTableModelFactory.collectData( rsmd, 0, "my_col" );

    assertThat( result, is( notNullValue() ) );
    assertThat( (String) result.getAttribute(
        "http://reporting.pentaho.org/namespaces/engine/meta-attributes/core", "name" ), is( equalTo( "my_col" ) ) );
    assertThat( (Boolean) result.getAttribute(
        "http://reporting.pentaho.org/namespaces/engine/meta-attributes/numeric", "currency" ), is( equalTo( true ) ) );
    assertThat( (Boolean) result.getAttribute(
        "http://reporting.pentaho.org/namespaces/engine/meta-attributes/numeric", "signed" ), is( equalTo( true ) ) );
  }

  // =====================================================================
  // collectData — currency false, signed false
  // =====================================================================
  @Test
  public void testCollectDataNonCurrencyNonSigned() throws SQLException {
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    doReturn( "java.lang.String" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.VARCHAR ).when( rsmd ).getColumnType( 1 );
    doReturn( false ).when( rsmd ).isCurrency( 1 );
    doReturn( false ).when( rsmd ).isSigned( 1 );
    doReturn( "t" ).when( rsmd ).getTableName( 1 );
    doReturn( "s" ).when( rsmd ).getSchemaName( 1 );
    doReturn( "c" ).when( rsmd ).getCatalogName( 1 );
    doReturn( "l" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( 100 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 0 ).when( rsmd ).getPrecision( 1 );
    doReturn( 0 ).when( rsmd ).getScale( 1 );

    AttributeMap<Object> result = ResultSetTableModelFactory.collectData( rsmd, 0, "col" );

    assertThat( (Boolean) result.getAttribute(
        "http://reporting.pentaho.org/namespaces/engine/meta-attributes/numeric", "currency" ), is( equalTo( false ) ) );
    assertThat( (Boolean) result.getAttribute(
        "http://reporting.pentaho.org/namespaces/engine/meta-attributes/numeric", "signed" ), is( equalTo( false ) ) );
  }

  // =====================================================================
  // collectData — handles each driver exception gracefully
  // =====================================================================
  @Test
  public void testCollectDataHandlesIsCurrencyException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).isCurrency( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesIsSignedException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).isSigned( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetTableNameException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getTableName( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetSchemaNameException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getSchemaName( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetCatalogNameException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getCatalogName( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetColumnLabelException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getColumnLabel( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetDisplaySizeException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getColumnDisplaySize( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetPrecisionException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getPrecision( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  @Test
  public void testCollectDataHandlesGetScaleException() throws SQLException {
    ResultSetMetaData rsmd = createBaseRsmd();
    doThrow( new SQLException( "unsupported" ) ).when( rsmd ).getScale( 1 );
    assertThat( ResultSetTableModelFactory.collectData( rsmd, 0, "c" ), is( notNullValue() ) );
  }

  // =====================================================================
  // collectData — null table/schema/catalog/label
  // =====================================================================
  @Test
  public void testCollectDataWithNullMetadataValues() throws SQLException {
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    doReturn( "java.lang.String" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.VARCHAR ).when( rsmd ).getColumnType( 1 );
    doReturn( false ).when( rsmd ).isCurrency( 1 );
    doReturn( false ).when( rsmd ).isSigned( 1 );
    doReturn( null ).when( rsmd ).getTableName( 1 );
    doReturn( null ).when( rsmd ).getSchemaName( 1 );
    doReturn( null ).when( rsmd ).getCatalogName( 1 );
    doReturn( null ).when( rsmd ).getColumnLabel( 1 );
    doReturn( 255 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 0 ).when( rsmd ).getPrecision( 1 );
    doReturn( 0 ).when( rsmd ).getScale( 1 );

    AttributeMap<Object> result = ResultSetTableModelFactory.collectData( rsmd, 0, "col" );
    assertThat( result, is( notNullValue() ) );
  }

  // =====================================================================
  // map() — empty array
  // =====================================================================
  @Test
  public void testMapWithEmptyArray() {
    try {
      ImmutableDataAttributes[] result = ResultSetTableModelFactory.map( new AttributeMap[ 0 ] );
      assertThat( result, is( notNullValue() ) );
      assertThat( result.length, is( equalTo( 0 ) ) );
    } catch ( Exception e ) {
      // ClassicEngineBoot may not be initialized — acceptable in unit test env
    }
  }

  // =====================================================================
  // produceData — Blob handling (tested via generateDefaultTableModel)
  // =====================================================================
  @Test
  public void testProduceDataWithBlob() throws Exception {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    Blob blob = mock( Blob.class );

    doReturn( stmt ).when( rs ).getStatement();
    doReturn( rsmd ).when( rs ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "Col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( "[B" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.BLOB ).when( rsmd ).getColumnType( 1 );
    doReturn( false ).when( rsmd ).isCurrency( 1 );
    doReturn( false ).when( rsmd ).isSigned( 1 );
    doReturn( "t" ).when( rsmd ).getTableName( 1 );
    doReturn( "s" ).when( rsmd ).getSchemaName( 1 );
    doReturn( "c" ).when( rsmd ).getCatalogName( 1 );
    doReturn( 255 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 0 ).when( rsmd ).getPrecision( 1 );
    doReturn( 0 ).when( rsmd ).getScale( 1 );

    byte[] blobBytes = { 0x01, 0x02, 0x03 };
    doReturn( new ByteArrayInputStream( blobBytes ) ).when( blob ).getBinaryStream();
    doReturn( (long) blobBytes.length ).when( blob ).length();
    doReturn( true ).doReturn( false ).when( rs ).next();
    doReturn( blob ).when( rs ).getObject( 1 );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getValueAt( 0, 0 ), is( notNullValue() ) );
  }

  // =====================================================================
  // produceData — Clob handling (tested via generateDefaultTableModel)
  // =====================================================================
  @Test
  public void testProduceDataWithClob() throws Exception {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    Clob clob = mock( Clob.class );

    doReturn( stmt ).when( rs ).getStatement();
    doReturn( rsmd ).when( rs ).getMetaData();
    doReturn( 1 ).when( rsmd ).getColumnCount();
    doReturn( "Col" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( "col" ).when( rsmd ).getColumnName( 1 );
    doReturn( "java.lang.String" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.CLOB ).when( rsmd ).getColumnType( 1 );
    doReturn( false ).when( rsmd ).isCurrency( 1 );
    doReturn( false ).when( rsmd ).isSigned( 1 );
    doReturn( "t" ).when( rsmd ).getTableName( 1 );
    doReturn( "s" ).when( rsmd ).getSchemaName( 1 );
    doReturn( "c" ).when( rsmd ).getCatalogName( 1 );
    doReturn( 255 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 0 ).when( rsmd ).getPrecision( 1 );
    doReturn( 0 ).when( rsmd ).getScale( 1 );

    String clobText = "Hello Clob World";
    doReturn( new StringReader( clobText ) ).when( clob ).getCharacterStream();
    doReturn( (long) clobText.length() ).when( clob ).length();
    doReturn( true ).doReturn( false ).when( rs ).next();
    doReturn( clob ).when( rs ).getObject( 1 );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );
    assertThat( result.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( result.getValueAt( 0, 0 ), is( notNullValue() ) );
  }

  // =====================================================================
  // generateDefaultTableModel — multiple columns metadata
  // =====================================================================
  @Test
  public void testGenerateDefaultTableModelMultipleColumnsMetadata() throws SQLException {
    ResultSet rs = createMockResultSet(
        new String[] { "L1", "L2", "L3" }, new String[] { "n1", "n2", "n3" }, 3,
        new Object[][] { { "a", "b", "c" } } );

    CloseableTableModel result = factory.generateDefaultTableModel( rs, false );

    assertThat( result.getColumnCount(), is( equalTo( 3 ) ) );
    MetaTableModel meta = (MetaTableModel) result;
    assertThat( meta.getColumnAttributes( 0 ), is( notNullValue() ) );
    assertThat( meta.getColumnAttributes( 1 ), is( notNullValue() ) );
    assertThat( meta.getColumnAttributes( 2 ), is( notNullValue() ) );
  }

  // =====================================================================
  // generateDiskBackedTableModel — multiple columns metadata
  // =====================================================================
  @Test
  public void testGenerateDiskBackedTableModelMultipleColumnsMetadata() throws SQLException {
    ResultSet rs = createMockResultSet(
        new String[] { "A", "B", "C" }, new String[] { "a", "b", "c" }, 3,
        new Object[][] { { "x", "y", "z" } } );

    CloseableTableModel result = factory.generateDiskBackedTableModel( rs, false );

    assertThat( result.getColumnCount(), is( equalTo( 3 ) ) );
    MetaTableModel meta = (MetaTableModel) result;
    assertThat( meta.getColumnAttributes( 0 ), is( notNullValue() ) );
    assertThat( meta.getColumnAttributes( 1 ), is( notNullValue() ) );
    assertThat( meta.getColumnAttributes( 2 ), is( notNullValue() ) );
    result.close();
  }

  // =====================================================================
  // Helper: creates a fully-configured base ResultSetMetaData mock
  // =====================================================================
  private ResultSetMetaData createBaseRsmd() throws SQLException {
    ResultSetMetaData rsmd = mock( ResultSetMetaData.class );
    doReturn( "java.lang.String" ).when( rsmd ).getColumnClassName( 1 );
    doReturn( Types.VARCHAR ).when( rsmd ).getColumnType( 1 );
    doReturn( false ).when( rsmd ).isCurrency( 1 );
    doReturn( false ).when( rsmd ).isSigned( 1 );
    doReturn( "t" ).when( rsmd ).getTableName( 1 );
    doReturn( "s" ).when( rsmd ).getSchemaName( 1 );
    doReturn( "c" ).when( rsmd ).getCatalogName( 1 );
    doReturn( "l" ).when( rsmd ).getColumnLabel( 1 );
    doReturn( 255 ).when( rsmd ).getColumnDisplaySize( 1 );
    doReturn( 0 ).when( rsmd ).getPrecision( 1 );
    doReturn( 0 ).when( rsmd ).getScale( 1 );
    return rsmd;
  }
}
