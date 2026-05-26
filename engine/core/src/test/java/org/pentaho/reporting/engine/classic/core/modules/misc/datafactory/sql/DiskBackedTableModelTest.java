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

import org.junit.After;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.misc.tablemodel.TableMetaData;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class DiskBackedTableModelTest {

  private DiskBackedTableModel model;

  @After
  public void tearDown() {
    if ( model != null ) {
      model.close();
      model = null;
    }
  }

  // =====================================================================
  // Helper: creates a mock ResultSet with given column names, types, and rows.
  // Uses an internal counter to track the current row position.
  // =====================================================================
  private ResultSet createMockResultSet( String[] columnNames, Object[][] rows )
      throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();

    // Use an int array as a mutable counter for the current row
    final int[] currentRow = { -1 };

    // Configure rs.next() — increments the row counter and returns true while rows remain
    doAnswer( new org.mockito.stubbing.Answer<Boolean>() {
      @Override public Boolean answer( org.mockito.invocation.InvocationOnMock invocation ) {
        currentRow[ 0 ]++;
        return currentRow[ 0 ] < rows.length;
      }
    } ).when( rs ).next();

    // Configure rs.getObject(col) — returns the value at the current row
    for ( int col = 0; col < columnNames.length; col++ ) {
      final int colIdx = col;
      doAnswer( new org.mockito.stubbing.Answer<Object>() {
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

  private DiskBackedTableModel createModel( String[] columnNames, Class[] columnTypes,
      Object[][] rows, TableMetaData metaData ) throws SQLException {
    ResultSet rs = createMockResultSet( columnNames, rows );
    return new DiskBackedTableModel( rs, columnNames, columnTypes, metaData );
  }

  // =====================================================================
  // Constructor and basic TableModel tests
  // =====================================================================

  @Test
  public void testConstructorWithSingleStringRow() throws SQLException {
    String[] columns = { "Name" };
    Class[] types = { String.class };
    Object[][] rows = { { "Alice" } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( model.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( model.getColumnName( 0 ), is( equalTo( "Name" ) ) );
    assertThat( model.getColumnClass( 0 ), is( equalTo( (Class) String.class ) ) );
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "Alice" ) ) );
  }

  @Test
  public void testConstructorWithMultipleRows() throws SQLException {
    String[] columns = { "ID", "Name" };
    Class[] types = { Integer.class, String.class };
    Object[][] rows = {
        { 1, "Alice" },
        { 2, "Bob" },
        { 3, "Charlie" }
    };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 3 ) ) );
    assertThat( model.getColumnCount(), is( equalTo( 2 ) ) );
    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 1 ) ) );
    assertThat( (String) model.getValueAt( 0, 1 ), is( equalTo( "Alice" ) ) );
    assertThat( model.getValueAt( 1, 0 ), is( equalTo( 2 ) ) );
    assertThat( (String) model.getValueAt( 1, 1 ), is( equalTo( "Bob" ) ) );
    assertThat( model.getValueAt( 2, 0 ), is( equalTo( 3 ) ) );
    assertThat( (String) model.getValueAt( 2, 1 ), is( equalTo( "Charlie" ) ) );
  }

  @Test
  public void testConstructorWithEmptyResultSet() throws SQLException {
    String[] columns = { "ID" };
    Class[] types = { Integer.class };
    Object[][] rows = {};

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 0 ) ) );
    assertThat( model.getColumnCount(), is( equalTo( 1 ) ) );
  }

  // =====================================================================
  // Type serialization/deserialization tests — all supported types
  // =====================================================================

  @Test
  public void testNullValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Object.class };
    Object[][] rows = { { null } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( nullValue() ) );
  }

  @Test
  public void testStringValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    Object[][] rows = { { "Hello World" } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "Hello World" ) ) );
  }

  @Test
  public void testEmptyStringValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    Object[][] rows = { { "" } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "" ) ) );
  }

  @Test
  public void testUnicodeStringValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    String unicode = "日本語テスト αβγ €£¥";
    Object[][] rows = { { unicode } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( unicode ) ) );
  }

  @Test
  public void testIntegerValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Integer.class };
    Object[][] rows = { { 42 } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 42 ) ) );
  }

  @Test
  public void testIntegerMinMaxValues() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Integer.class };
    Object[][] rows = {
        { Integer.MIN_VALUE },
        { Integer.MAX_VALUE }
    };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( Integer.MIN_VALUE ) ) );
    assertThat( model.getValueAt( 1, 0 ), is( equalTo( Integer.MAX_VALUE ) ) );
  }

  @Test
  public void testLongValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Long.class };
    Object[][] rows = { { 123456789012345L } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 123456789012345L ) ) );
  }

  @Test
  public void testDoubleValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Double.class };
    Object[][] rows = { { 3.14159265 } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 3.14159265 ) ) );
  }

  @Test
  public void testFloatValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Float.class };
    Object[][] rows = { { 2.718f } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 2.718f ) ) );
  }

  @Test
  public void testBooleanValues() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Boolean.class };
    Object[][] rows = { { true }, { false } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( true ) ) );
    assertThat( model.getValueAt( 1, 0 ), is( equalTo( false ) ) );
  }

  @Test
  public void testShortValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Short.class };
    Object[][] rows = { { (short) 123 } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( (short) 123 ) ) );
  }

  @Test
  public void testByteValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Byte.class };
    Object[][] rows = { { (byte) 42 } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( (byte) 42 ) ) );
  }

  @Test
  public void testBigDecimalValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { BigDecimal.class };
    BigDecimal bd = new BigDecimal( "12345678901234567890.12345" );
    Object[][] rows = { { bd } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( bd ) ) );
  }

  @Test
  public void testSqlTimestampValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Timestamp.class };
    Timestamp ts = new Timestamp( 1700000000000L );
    ts.setNanos( 123456789 );
    Object[][] rows = { { ts } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( Timestamp.class ) ) );
    Timestamp resultTs = (Timestamp) result;
    assertThat( resultTs.getTime(), is( equalTo( ts.getTime() ) ) );
    assertThat( resultTs.getNanos(), is( equalTo( 123456789 ) ) );
  }

  @Test
  public void testSqlDateValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { java.sql.Date.class };
    java.sql.Date date = new java.sql.Date( 1700000000000L );
    Object[][] rows = { { date } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( java.sql.Date.class ) ) );
    assertThat( ( (java.sql.Date) result ).getTime(), is( equalTo( date.getTime() ) ) );
  }

  @Test
  public void testSqlTimeValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { java.sql.Time.class };
    java.sql.Time time = new java.sql.Time( 1700000000000L );
    Object[][] rows = { { time } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( java.sql.Time.class ) ) );
    assertThat( ( (java.sql.Time) result ).getTime(), is( equalTo( time.getTime() ) ) );
  }

  @Test
  public void testUtilDateValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { java.util.Date.class };
    java.util.Date date = new java.util.Date( 1700000000000L );
    Object[][] rows = { { date } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( java.util.Date.class ) ) );
    assertThat( ( (java.util.Date) result ).getTime(), is( equalTo( date.getTime() ) ) );
  }

  @Test
  public void testByteArrayValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { byte[].class };
    byte[] bytes = { 0x01, 0x02, 0x03, (byte) 0xFF };
    Object[][] rows = { { bytes } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( byte[].class ) ) );
    assertArrayEquals( bytes, (byte[]) result );
  }

  @Test
  public void testEmptyByteArrayValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { byte[].class };
    byte[] bytes = {};
    Object[][] rows = { { bytes } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( byte[].class ) ) );
    assertArrayEquals( bytes, (byte[]) result );
  }

  @Test
  public void testFallbackToStringForUnknownType() throws SQLException {
    // An object type not explicitly handled should be serialized via toString()
    String[] columns = { "Val" };
    Class[] types = { Object.class };
    // Use a StringBuilder which is not a handled type
    Object customObj = new StringBuilder( "custom_value" );
    Object[][] rows = { { customObj } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "custom_value" ) ) );
  }

  // =====================================================================
  // Multi-column, mixed-type rows
  // =====================================================================

  @Test
  public void testMultipleColumnTypes() throws SQLException {
    String[] columns = { "ID", "Name", "Score", "Active", "Amount" };
    Class[] types = { Integer.class, String.class, Double.class, Boolean.class, BigDecimal.class };
    BigDecimal amount = new BigDecimal( "99.99" );
    Object[][] rows = {
        { 1, "Alice", 95.5, true, amount },
        { 2, null, 80.0, false, null }
    };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 2 ) ) );
    assertThat( model.getColumnCount(), is( equalTo( 5 ) ) );

    // Row 0
    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 1 ) ) );
    assertThat( (String) model.getValueAt( 0, 1 ), is( equalTo( "Alice" ) ) );
    assertThat( model.getValueAt( 0, 2 ), is( equalTo( 95.5 ) ) );
    assertThat( model.getValueAt( 0, 3 ), is( equalTo( true ) ) );
    assertThat( model.getValueAt( 0, 4 ), is( equalTo( amount ) ) );

    // Row 1 — with nulls
    assertThat( model.getValueAt( 1, 0 ), is( equalTo( 2 ) ) );
    assertThat( model.getValueAt( 1, 1 ), is( nullValue() ) );
    assertThat( model.getValueAt( 1, 2 ), is( equalTo( 80.0 ) ) );
    assertThat( model.getValueAt( 1, 3 ), is( equalTo( false ) ) );
    assertThat( model.getValueAt( 1, 4 ), is( nullValue() ) );
  }

  // =====================================================================
  // Column name / class edge cases
  // =====================================================================

  @Test
  public void testGetColumnNameOutOfBounds() throws SQLException {
    String[] columns = { "Col1" };
    Class[] types = { String.class };
    Object[][] rows = { { "val" } };

    model = createModel( columns, types, rows, null );

    // Out of bounds should fall back to AbstractTableModel's default naming (e.g. "B")
    String name = model.getColumnName( 10 );
    assertThat( name, is( notNullValue() ) );
  }

  @Test
  public void testGetColumnClassOutOfBounds() throws SQLException {
    String[] columns = { "Col1" };
    Class[] types = { String.class };
    Object[][] rows = { { "val" } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getColumnClass( 10 ), is( equalTo( (Class) Object.class ) ) );
  }

  @Test
  public void testGetValueAtColumnOutOfBounds() throws SQLException {
    String[] columns = { "Col1" };
    Class[] types = { String.class };
    Object[][] rows = { { "val" } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 10 ), is( nullValue() ) );
  }

  // =====================================================================
  // Cache behavior — verify repeated reads return correct data
  // =====================================================================

  @Test
  public void testCacheHitReturnsSameData() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    Object[][] rows = { { "cached_value" } };

    model = createModel( columns, types, rows, null );

    // First access — cache miss, reads from disk
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "cached_value" ) ) );
    // Second access — should be a cache hit
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "cached_value" ) ) );
  }

  @Test
  public void testCacheEviction() throws SQLException {
    // Create more rows than the cache size (4096) to force eviction
    int numRows = 5000;
    String[] columns = { "Val" };
    Class[] types = { Integer.class };

    // Build a mock ResultSet with 5000 rows using Answer-based stubbing
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();

    final int[] currentRow = { -1 };

    doAnswer( new org.mockito.stubbing.Answer<Boolean>() {
      @Override public Boolean answer( org.mockito.invocation.InvocationOnMock invocation ) {
        currentRow[ 0 ]++;
        return currentRow[ 0 ] < numRows;
      }
    } ).when( rs ).next();

    doAnswer( new org.mockito.stubbing.Answer<Object>() {
      @Override public Object answer( org.mockito.invocation.InvocationOnMock invocation ) {
        if ( currentRow[ 0 ] >= 0 && currentRow[ 0 ] < numRows ) {
          return currentRow[ 0 ];
        }
        return null;
      }
    } ).when( rs ).getObject( 1 );

    model = new DiskBackedTableModel( rs, columns, types, null );

    assertThat( model.getRowCount(), is( equalTo( numRows ) ) );

    // Access first row (will be evicted after accessing 4096+ different rows)
    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 0 ) ) );

    // Access rows beyond cache size to force eviction of row 0
    for ( int i = 1; i <= 4096; i++ ) {
      assertThat( model.getValueAt( i, 0 ), is( equalTo( i ) ) );
    }

    // Access row 0 again — should still return correct data (re-read from disk)
    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 0 ) ) );
  }

  // =====================================================================
  // MetaTableModel implementation tests
  // =====================================================================

  @Test
  public void testGetCellDataAttributesWithNullMetaData() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    assertThat( model.getCellDataAttributes( 0, 0 ), is( equalTo( EmptyDataAttributes.INSTANCE ) ) );
  }

  @Test
  public void testGetCellDataAttributesWithMetaData() throws SQLException {
    TableMetaData metaData = mock( TableMetaData.class );
    DataAttributes attrs = mock( DataAttributes.class );
    doReturn( attrs ).when( metaData ).getCellDataAttribute( 0, 0 );

    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, metaData );

    assertThat( model.getCellDataAttributes( 0, 0 ), is( equalTo( attrs ) ) );
  }

  @Test
  public void testIsCellDataAttributesSupportedWithNullMetaData() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    assertThat( model.isCellDataAttributesSupported(), is( equalTo( false ) ) );
  }

  @Test
  public void testIsCellDataAttributesSupportedWithMetaData() throws SQLException {
    TableMetaData metaData = mock( TableMetaData.class );
    doReturn( true ).when( metaData ).isCellDataAttributesSupported();

    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, metaData );

    assertThat( model.isCellDataAttributesSupported(), is( equalTo( true ) ) );
  }

  @Test
  public void testGetColumnAttributesWithNullMetaData() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    assertThat( model.getColumnAttributes( 0 ), is( equalTo( EmptyDataAttributes.INSTANCE ) ) );
  }

  @Test
  public void testGetColumnAttributesWithMetaData() throws SQLException {
    TableMetaData metaData = mock( TableMetaData.class );
    DataAttributes attrs = mock( DataAttributes.class );
    doReturn( attrs ).when( metaData ).getColumnAttribute( 0 );

    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, metaData );

    assertThat( model.getColumnAttributes( 0 ), is( equalTo( attrs ) ) );
  }

  @Test
  public void testGetTableAttributesWithNullMetaData() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    assertThat( model.getTableAttributes(), is( nullValue() ) );
  }

  @Test
  public void testGetTableAttributesWithMetaData() throws SQLException {
    TableMetaData metaData = mock( TableMetaData.class );
    DataAttributes attrs = mock( DataAttributes.class );
    doReturn( attrs ).when( metaData ).getTableAttribute();

    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, metaData );

    assertThat( model.getTableAttributes(), is( equalTo( attrs ) ) );
  }

  // =====================================================================
  // close() tests
  // =====================================================================

  @Test
  public void testCloseReleasesResources() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    // Verify data is accessible before close
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "val" ) ) );

    model.close();
    // Calling close again should not throw
    model.close();
    // After close, row count is still set but internal state is cleared
    assertThat( model.getRowCount(), is( equalTo( 1 ) ) );
    model = null; // prevent tearDown from closing again
  }

  // =====================================================================
  // Constructor — ResultSet/Statement close behavior
  // =====================================================================

  @Test
  public void testResultSetAndStatementAreClosedAfterConstruction() throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();
    doReturn( false ).when( rs ).next();

    model = new DiskBackedTableModel( rs, new String[] { "Col" }, new Class[] { String.class }, null );

    verify( rs ).close();
    verify( stmt ).close();
  }

  @Test
  public void testResultSetClosedEvenWhenGetStatementFails() throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    doThrow( new SQLException( "no statement" ) ).when( rs ).getStatement();
    doReturn( false ).when( rs ).next();

    model = new DiskBackedTableModel( rs, new String[] { "Col" }, new Class[] { String.class }, null );

    verify( rs ).close();
  }

  @Test
  public void testResultSetClosedEvenWhenStatementCloseFails() throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();
    doThrow( new SQLException( "close failed" ) ).when( stmt ).close();
    doReturn( false ).when( rs ).next();

    model = new DiskBackedTableModel( rs, new String[] { "Col" }, new Class[] { String.class }, null );

    verify( rs ).close();
    verify( stmt ).close();
  }

  // =====================================================================
  // Spill failure — IOException during write
  // =====================================================================

  @Test
  public void testConstructorThrowsSQLExceptionOnSpillFailure() throws SQLException {
    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();
    doReturn( true ).when( rs ).next();
    // Return a value that triggers writing, then throw on getObject to simulate failure
    doThrow( new SQLException( "read error" ) ).when( rs ).getObject( anyInt() );

    try {
      model = new DiskBackedTableModel( rs, new String[] { "Col" }, new Class[] { String.class }, null );
      fail( "Expected SQLException to be thrown" );
    } catch ( SQLException e ) {
      assertThat( e.getMessage(), is( notNullValue() ) );
    }
  }

  // =====================================================================
  // All types in a single row
  // =====================================================================

  @Test
  public void testAllSupportedTypesInSingleRow() throws SQLException {
    Timestamp ts = new Timestamp( 1700000000000L );
    ts.setNanos( 999999999 );
    java.sql.Date sqlDate = new java.sql.Date( 1700000000000L );
    java.sql.Time sqlTime = new java.sql.Time( 1700000000000L );
    java.util.Date utilDate = new java.util.Date( 1700000000000L );
    BigDecimal bd = new BigDecimal( "12345.6789" );

    String[] columns = { "c1", "c2", "c3", "c4", "c5", "c6", "c7", "c8", "c9", "c10", "c11", "c12", "c13", "c14" };
    Class[] types = { Object.class, String.class, Integer.class, Long.class, Double.class,
        Float.class, Boolean.class, Short.class, Byte.class, BigDecimal.class,
        Timestamp.class, java.sql.Date.class, java.sql.Time.class, java.util.Date.class };
    Object[][] rows = { { null, "text", 42, 100L, 3.14, 2.7f, true, (short) 5, (byte) 9,
        bd, ts, sqlDate, sqlTime, utilDate } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( nullValue() ) );
    assertThat( (String) model.getValueAt( 0, 1 ), is( equalTo( "text" ) ) );
    assertThat( model.getValueAt( 0, 2 ), is( equalTo( 42 ) ) );
    assertThat( model.getValueAt( 0, 3 ), is( equalTo( 100L ) ) );
    assertThat( model.getValueAt( 0, 4 ), is( equalTo( 3.14 ) ) );
    assertThat( model.getValueAt( 0, 5 ), is( equalTo( 2.7f ) ) );
    assertThat( model.getValueAt( 0, 6 ), is( equalTo( true ) ) );
    assertThat( model.getValueAt( 0, 7 ), is( equalTo( (short) 5 ) ) );
    assertThat( model.getValueAt( 0, 8 ), is( equalTo( (byte) 9 ) ) );
    assertThat( model.getValueAt( 0, 9 ), is( equalTo( bd ) ) );

    Timestamp resultTs = (Timestamp) model.getValueAt( 0, 10 );
    assertThat( resultTs.getTime(), is( equalTo( ts.getTime() ) ) );
    assertThat( resultTs.getNanos(), is( equalTo( 999999999 ) ) );

    assertThat( ( (java.sql.Date) model.getValueAt( 0, 11 ) ).getTime(), is( equalTo( sqlDate.getTime() ) ) );
    assertThat( ( (java.sql.Time) model.getValueAt( 0, 12 ) ).getTime(), is( equalTo( sqlTime.getTime() ) ) );
    assertThat( ( (java.util.Date) model.getValueAt( 0, 13 ) ).getTime(), is( equalTo( utilDate.getTime() ) ) );
  }

  // =====================================================================
  // Byte array serialization with large data
  // =====================================================================

  @Test
  public void testLargeByteArrayValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { byte[].class };
    byte[] largeBytes = new byte[ 100000 ];
    for ( int i = 0; i < largeBytes.length; i++ ) {
      largeBytes[ i ] = (byte) ( i % 256 );
    }
    Object[][] rows = { { largeBytes } };

    model = createModel( columns, types, rows, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( instanceOf( byte[].class ) ) );
    assertArrayEquals( largeBytes, (byte[]) result );
  }

  // =====================================================================
  // Long string value — exceeds initial encode buffer size (4096)
  // =====================================================================

  @Test
  public void testLargeStringValue() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < 5000; i++ ) {
      sb.append( "abcde" );
    }
    String longStr = sb.toString();
    Object[][] rows = { { longStr } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( longStr ) ) );
  }

  // =====================================================================
  // Row count and column count consistency
  // =====================================================================

  @Test
  public void testRowAndColumnCountConsistency() throws SQLException {
    String[] columns = { "A", "B", "C" };
    Class[] types = { String.class, Integer.class, Double.class };
    Object[][] rows = {
        { "x", 1, 1.0 },
        { "y", 2, 2.0 }
    };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 2 ) ) );
    assertThat( model.getColumnCount(), is( equalTo( 3 ) ) );
    assertThat( model.getColumnName( 0 ), is( equalTo( "A" ) ) );
    assertThat( model.getColumnName( 1 ), is( equalTo( "B" ) ) );
    assertThat( model.getColumnName( 2 ), is( equalTo( "C" ) ) );
  }

  // =====================================================================
  // getValueAt after close() — raf is null, should return empty/null
  // =====================================================================

  @Test
  public void testGetValueAtAfterCloseThrowsNPE() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    // Verify data is accessible before close
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "val" ) ) );

    model.close();

    // After close, cacheMap is null so getCachedRow will throw NPE
    try {
      model.getValueAt( 0, 0 );
      fail( "Expected NullPointerException to be thrown after close()" );
    } catch ( NullPointerException e ) {
      assertThat( e, is( notNullValue() ) );
    }
    model = null; // prevent tearDown from closing again
  }

  // =====================================================================
  // readRow with negative row index — should return empty array
  // =====================================================================

  @Test
  public void testGetValueAtWithNegativeRowReturnsNull() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    // Negative row index returns empty array from readRow, then column out of bounds
    assertThat( model.getValueAt( -1, 0 ), is( nullValue() ) );
  }

  // =====================================================================
  // readRow with row index >= rowCount — should return empty array
  // =====================================================================

  @Test
  public void testGetValueAtWithRowBeyondCountReturnsNull() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    assertThat( model.getValueAt( 999, 0 ), is( nullValue() ) );
  }

  // =====================================================================
  // Random access pattern — read rows in reverse order
  // =====================================================================

  @Test
  public void testReverseAccessPattern() throws SQLException {
    int numRows = 100;
    String[] columns = { "Val" };
    Class[] types = { Integer.class };
    Object[][] rows = new Object[ numRows ][ 1 ];
    for ( int i = 0; i < numRows; i++ ) {
      rows[ i ][ 0 ] = i;
    }

    model = createModel( columns, types, rows, null );

    // Access in reverse order
    for ( int i = numRows - 1; i >= 0; i-- ) {
      assertThat( model.getValueAt( i, 0 ), is( equalTo( i ) ) );
    }
  }

  // =====================================================================
  // Supplementary Unicode characters (surrogate pairs) in strings
  // =====================================================================

  @Test
  public void testSupplementaryUnicodeCharacters() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    // Supplementary characters: emoji 😀 (U+1F600), musical symbol 𝄞 (U+1D11E)
    String supplementary = "Hello 😀 World 𝄞";
    Object[][] rows = { { supplementary } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( supplementary ) ) );
  }

  // =====================================================================
  // Blob handling — getObject returns Blob
  // =====================================================================

  @Test
  public void testBlobValueIsReadAsBytes() throws Exception {
    byte[] blobData = { 0x01, 0x02, 0x03, 0x04, 0x05 };

    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();

    final int[] currentRow = { -1 };
    doAnswer( invocation -> {
      currentRow[ 0 ]++;
      return currentRow[ 0 ] < 1;
    } ).when( rs ).next();

    Blob blob = mock( Blob.class );
    when( blob.length() ).thenReturn( (long) blobData.length );
    when( blob.getBytes( 1L, blobData.length ) ).thenReturn( blobData );
    when( blob.getBinaryStream() ).thenReturn( new java.io.ByteArrayInputStream( blobData ) );

    doReturn( blob ).when( rs ).getObject( 1 );

    model = new DiskBackedTableModel( rs, new String[] { "BlobCol" },
        new Class[] { byte[].class }, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( notNullValue() ) );
    // The result should be a byte array read via IOUtils.readBlob
    assertThat( result, is( instanceOf( byte[].class ) ) );
  }

  // =====================================================================
  // Clob handling — getObject returns Clob
  // =====================================================================

  @Test
  public void testClobValueIsReadAsString() throws Exception {
    String clobContent = "Hello Clob Content";

    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();

    final int[] currentRow = { -1 };
    doAnswer( invocation -> {
      currentRow[ 0 ]++;
      return currentRow[ 0 ] < 1;
    } ).when( rs ).next();

    Clob clob = mock( Clob.class );
    when( clob.length() ).thenReturn( (long) clobContent.length() );
    when( clob.getCharacterStream() ).thenReturn( new java.io.StringReader( clobContent ) );

    doReturn( clob ).when( rs ).getObject( 1 );

    model = new DiskBackedTableModel( rs, new String[] { "ClobCol" },
        new Class[] { String.class }, null );

    Object result = model.getValueAt( 0, 0 );
    assertThat( result, is( notNullValue() ) );
    // Clob should be read as a String
    assertThat( result, is( instanceOf( String.class ) ) );
  }

  // =====================================================================
  // ensureOffsetCapacity — trigger dynamic growth beyond initial 65536
  // =====================================================================

  @Test
  public void testOffsetArrayGrowsBeyondInitialCapacity() throws SQLException {
    // Create more rows than initial offset array size (65536) to trigger ensureOffsetCapacity
    int numRows = 70000;
    String[] columns = { "Val" };
    Class[] types = { Integer.class };

    ResultSet rs = mock( ResultSet.class );
    Statement stmt = mock( Statement.class );
    doReturn( stmt ).when( rs ).getStatement();

    final int[] currentRow = { -1 };
    doAnswer( invocation -> {
      currentRow[ 0 ]++;
      return currentRow[ 0 ] < numRows;
    } ).when( rs ).next();

    doAnswer( invocation -> {
      return currentRow[ 0 ] >= 0 && currentRow[ 0 ] < numRows ? currentRow[ 0 ] : null;
    } ).when( rs ).getObject( 1 );

    model = new DiskBackedTableModel( rs, columns, types, null );

    assertThat( model.getRowCount(), is( equalTo( numRows ) ) );
    // Verify first, middle, and last rows are accessible
    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 0 ) ) );
    assertThat( model.getValueAt( 65535, 0 ), is( equalTo( 65535 ) ) );
    assertThat( model.getValueAt( 65536, 0 ), is( equalTo( 65536 ) ) );
    assertThat( model.getValueAt( numRows - 1, 0 ), is( equalTo( numRows - 1 ) ) );
  }

  // =====================================================================
  // close() idempotency — verify repeated close calls are safe
  // =====================================================================

  @Test
  public void testMultipleCloseCallsAreSafe() throws SQLException {
    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, null );

    model.close();
    model.close();
    model.close();
    // After close, column count and row count are still accessible
    assertThat( model.getColumnCount(), is( equalTo( 1 ) ) );
    assertThat( model.getRowCount(), is( equalTo( 1 ) ) );
    model = null;
  }

  // =====================================================================
  // Row with all null values
  // =====================================================================

  @Test
  public void testRowWithAllNullValues() throws SQLException {
    String[] columns = { "A", "B", "C" };
    Class[] types = { String.class, Integer.class, Double.class };
    Object[][] rows = { { null, null, null } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 1 ) ) );
    assertThat( model.getValueAt( 0, 0 ), is( nullValue() ) );
    assertThat( model.getValueAt( 0, 1 ), is( nullValue() ) );
    assertThat( model.getValueAt( 0, 2 ), is( nullValue() ) );
  }

  // =====================================================================
  // Multiple rows with identical data — verify each row is independent
  // =====================================================================

  @Test
  public void testMultipleRowsWithIdenticalData() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    Object[][] rows = {
        { "same" },
        { "same" },
        { "same" }
    };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 3 ) ) );
    for ( int i = 0; i < 3; i++ ) {
      assertThat( (String) model.getValueAt( i, 0 ), is( equalTo( "same" ) ) );
    }
  }

  // =====================================================================
  // isCellDataAttributesSupported returns false when metaData says false
  // =====================================================================

  @Test
  public void testIsCellDataAttributesSupportedReturnsFalseFromMetaData() throws SQLException {
    TableMetaData metaData = mock( TableMetaData.class );
    doReturn( false ).when( metaData ).isCellDataAttributesSupported();

    model = createModel( new String[] { "Col" }, new Class[] { String.class },
        new Object[][] { { "val" } }, metaData );

    assertThat( model.isCellDataAttributesSupported(), is( equalTo( false ) ) );
  }

  // =====================================================================
  // Large readBuffer growth — single row larger than 64KB read buffer
  // =====================================================================

  @Test
  public void testLargeRowExceedsReadBuffer() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    // Create a string larger than 65536 bytes to exceed the readBuffer
    StringBuilder sb = new StringBuilder();
    for ( int i = 0; i < 20000; i++ ) {
      sb.append( "ABCDE" ); // 100,000 chars = ~100KB in UTF-8
    }
    String largeStr = sb.toString();
    Object[][] rows = { { largeStr } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( largeStr ) ) );
  }

  // =====================================================================
  // Cache eviction and re-read — verify data integrity after eviction
  // =====================================================================

  @Test
  public void testCacheEvictionAndReReadIntegrity() throws SQLException {
    // Create exactly CACHE_SIZE + 1 rows to guarantee one eviction
    int numRows = 4097;
    String[] columns = { "Val" };
    Class[] types = { String.class };
    Object[][] rows = new Object[ numRows ][ 1 ];
    for ( int i = 0; i < numRows; i++ ) {
      rows[ i ][ 0 ] = "row_" + i;
    }

    model = createModel( columns, types, rows, null );

    // Access all rows sequentially to fill the cache and evict row 0
    for ( int i = 0; i < numRows; i++ ) {
      assertThat( (String) model.getValueAt( i, 0 ), is( equalTo( "row_" + i ) ) );
    }

    // Re-access evicted row 0 — should re-read from disk
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "row_0" ) ) );
  }

  // =====================================================================
  // Single column with many types across rows
  // =====================================================================

  @Test
  public void testSingleColumnMixedTypesAcrossRows() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Object.class };
    Object[][] rows = {
        { "text" },
        { 42 },
        { 3.14 },
        { true },
        { null },
        { 999999L },
        { (short) 7 },
        { (byte) 1 },
        { 2.5f }
    };

    model = createModel( columns, types, rows, null );

    assertThat( model.getRowCount(), is( equalTo( 9 ) ) );
    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( "text" ) ) );
    assertThat( model.getValueAt( 1, 0 ), is( equalTo( 42 ) ) );
    assertThat( model.getValueAt( 2, 0 ), is( equalTo( 3.14 ) ) );
    assertThat( model.getValueAt( 3, 0 ), is( equalTo( true ) ) );
    assertThat( model.getValueAt( 4, 0 ), is( nullValue() ) );
    assertThat( model.getValueAt( 5, 0 ), is( equalTo( 999999L ) ) );
    assertThat( model.getValueAt( 6, 0 ), is( equalTo( (short) 7 ) ) );
    assertThat( model.getValueAt( 7, 0 ), is( equalTo( (byte) 1 ) ) );
    assertThat( model.getValueAt( 8, 0 ), is( equalTo( 2.5f ) ) );
  }

  // =====================================================================
  // String with special characters (newlines, tabs, quotes)
  // =====================================================================

  @Test
  public void testStringWithSpecialCharacters() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { String.class };
    String special = "line1\nline2\ttab\r\n\"quoted\"";
    Object[][] rows = { { special } };

    model = createModel( columns, types, rows, null );

    assertThat( (String) model.getValueAt( 0, 0 ), is( equalTo( special ) ) );
  }

  // =====================================================================
  // BigDecimal with extreme precision
  // =====================================================================

  @Test
  public void testBigDecimalWithExtremePrecision() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { BigDecimal.class };
    BigDecimal bd = new BigDecimal( "99999999999999999999999999999.999999999999999999999" );
    Object[][] rows = { { bd } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( bd ) ) );
  }

  // =====================================================================
  // Timestamp with zero nanos
  // =====================================================================

  @Test
  public void testTimestampWithZeroNanos() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Timestamp.class };
    Timestamp ts = new Timestamp( 1700000000000L );
    ts.setNanos( 0 );
    Object[][] rows = { { ts } };

    model = createModel( columns, types, rows, null );

    Timestamp result = (Timestamp) model.getValueAt( 0, 0 );
    assertThat( result.getTime(), is( equalTo( ts.getTime() ) ) );
    assertThat( result.getNanos(), is( equalTo( 0 ) ) );
  }

  // =====================================================================
  // getColumnName and getColumnClass with exact boundary index
  // =====================================================================

  @Test
  public void testGetColumnNameAtExactBoundary() throws SQLException {
    String[] columns = { "First", "Last" };
    Class[] types = { String.class, String.class };
    Object[][] rows = { { "a", "b" } };

    model = createModel( columns, types, rows, null );

    // Last valid index
    assertThat( model.getColumnName( 1 ), is( equalTo( "Last" ) ) );
    // One past — should fall back to AbstractTableModel default
    String fallback = model.getColumnName( 2 );
    assertThat( fallback, is( notNullValue() ) );
  }

  @Test
  public void testGetColumnClassAtExactBoundary() throws SQLException {
    String[] columns = { "Col" };
    Class[] types = { Integer.class };
    Object[][] rows = { { 1 } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getColumnClass( 0 ), is( equalTo( (Class) Integer.class ) ) );
    assertThat( model.getColumnClass( 1 ), is( equalTo( (Class) Object.class ) ) );
  }

  // =====================================================================
  // Interleaved access pattern — row 0, last, 0, last ...
  // =====================================================================

  @Test
  public void testInterleavedAccessPattern() throws SQLException {
    String[] columns = { "Val" };
    Class[] types = { Integer.class };
    Object[][] rows = new Object[ 10 ][ 1 ];
    for ( int i = 0; i < 10; i++ ) {
      rows[ i ][ 0 ] = i * 10;
    }

    model = createModel( columns, types, rows, null );

    for ( int i = 0; i < 50; i++ ) {
      int rowIdx = ( i % 2 == 0 ) ? 0 : 9;
      int expected = rowIdx * 10;
      assertThat( model.getValueAt( rowIdx, 0 ), is( equalTo( expected ) ) );
    }
  }

  // =====================================================================
  // getValueAt with multiple columns — verify correct column mapping
  // =====================================================================

  @Test
  public void testMultiColumnAccessVerifiesCorrectMapping() throws SQLException {
    String[] columns = { "A", "B", "C", "D", "E" };
    Class[] types = { Integer.class, Integer.class, Integer.class, Integer.class, Integer.class };
    Object[][] rows = { { 10, 20, 30, 40, 50 } };

    model = createModel( columns, types, rows, null );

    assertThat( model.getValueAt( 0, 0 ), is( equalTo( 10 ) ) );
    assertThat( model.getValueAt( 0, 1 ), is( equalTo( 20 ) ) );
    assertThat( model.getValueAt( 0, 2 ), is( equalTo( 30 ) ) );
    assertThat( model.getValueAt( 0, 3 ), is( equalTo( 40 ) ) );
    assertThat( model.getValueAt( 0, 4 ), is( equalTo( 50 ) ) );
  }
}

