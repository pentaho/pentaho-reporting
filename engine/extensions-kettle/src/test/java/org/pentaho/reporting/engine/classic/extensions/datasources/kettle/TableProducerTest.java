/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/
package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Date;

import javax.swing.table.TableModel;

import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

public class TableProducerTest {

  @Test
  public void rowWrittenEvent_boolean() throws KettleStepException, ReportDataFactoryException, KettleValueException {
    Boolean[] row = new Boolean[] { true };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    when( rowMetaInterface.size() ).thenReturn( 1 );

    ValueMetaInterface valueMeta1 = mock( ValueMetaInterface.class );
    when( valueMeta1.getName() ).thenReturn( "COLUMN_1" );
    when( valueMeta1.getType() ).thenReturn( ValueMetaInterface.TYPE_BOOLEAN );
    when( rowMetaInterface.getValueMeta( eq( 0 ) ) ).thenReturn( valueMeta1 );
    when( rowMetaInterface.getBoolean( eq( row ), eq( 0 ) ) ).thenReturn( row[0] );

    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.rowWrittenEvent( rowMetaInterface, row );

    TypedTableModel expectedModel = new TypedTableModel( new String[] { "COLUMN_1" }, new Class[] { Boolean.class } );
    expectedModel.addRow( true );

    assertEquals( expectedModel, tableProducer.getTableModel() );
  }

  @Test
  public void rowWrittenEvent_bignumber() throws KettleStepException, ReportDataFactoryException, KettleValueException {
    BigDecimal[] row = new BigDecimal[] { new BigDecimal( 1 ) };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    when( rowMetaInterface.size() ).thenReturn( 1 );

    ValueMetaInterface valueMeta1 = mock( ValueMetaInterface.class );
    when( valueMeta1.getName() ).thenReturn( "COLUMN_1" );
    when( valueMeta1.getType() ).thenReturn( ValueMetaInterface.TYPE_BIGNUMBER );
    when( rowMetaInterface.getValueMeta( eq( 0 ) ) ).thenReturn( valueMeta1 );
    when( rowMetaInterface.getBigNumber( eq( row ), eq( 0 ) ) ).thenReturn( row[0] );

    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.rowWrittenEvent( rowMetaInterface, row );

    TypedTableModel expectedModel = new TypedTableModel( new String[] { "COLUMN_1" }, new Class[] { BigDecimal.class } );
    expectedModel.addRow( new BigDecimal( 1 ) );

    assertEquals( expectedModel, tableProducer.getTableModel() );
  }

  @Test
  public void rowWrittenEvent_date() throws KettleStepException, ReportDataFactoryException, KettleValueException {
    Date[] row = new Date[] { new Date( 1 ) };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    when( rowMetaInterface.size() ).thenReturn( 1 );

    ValueMetaInterface valueMeta1 = mock( ValueMetaInterface.class );
    when( valueMeta1.getName() ).thenReturn( "COLUMN_1" );
    when( valueMeta1.getType() ).thenReturn( ValueMetaInterface.TYPE_DATE );
    when( rowMetaInterface.getValueMeta( eq( 0 ) ) ).thenReturn( valueMeta1 );
    when( rowMetaInterface.getDate( eq( row ), eq( 0 ) ) ).thenReturn( row[0] );

    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.rowWrittenEvent( rowMetaInterface, row );

    TypedTableModel expectedModel = new TypedTableModel( new String[] { "COLUMN_1" }, new Class[] { BigDecimal.class } );
    expectedModel.addRow( new Date( 1 ) );

    assertEquals( expectedModel, tableProducer.getTableModel() );
  }

  @Test
  public void rowWrittenEvent_integer() throws KettleStepException, ReportDataFactoryException, KettleValueException {
    Long[] row = new Long[] { new Long( 1 ) };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    when( rowMetaInterface.size() ).thenReturn( 1 );

    ValueMetaInterface valueMeta1 = mock( ValueMetaInterface.class );
    when( valueMeta1.getName() ).thenReturn( "COLUMN_1" );
    when( valueMeta1.getType() ).thenReturn( ValueMetaInterface.TYPE_INTEGER );
    when( rowMetaInterface.getValueMeta( eq( 0 ) ) ).thenReturn( valueMeta1 );
    when( rowMetaInterface.getInteger( eq( row ), eq( 0 ) ) ).thenReturn( row[0] );

    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.rowWrittenEvent( rowMetaInterface, row );

    TypedTableModel expectedModel = new TypedTableModel( new String[] { "COLUMN_1" }, new Class[] { Integer.class } );
    expectedModel.addRow( new Long( 1 ) );

    assertEquals( expectedModel, tableProducer.getTableModel() );
  }

  @Test
  public void rowWrittenEvent_none() throws KettleStepException, ReportDataFactoryException, KettleValueException {
    String[] row = new String[] { "NONE" };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    when( rowMetaInterface.size() ).thenReturn( 1 );

    ValueMetaInterface valueMeta1 = mock( ValueMetaInterface.class );
    when( valueMeta1.getName() ).thenReturn( "COLUMN_1" );
    when( valueMeta1.getType() ).thenReturn( ValueMetaInterface.TYPE_NONE );
    when( rowMetaInterface.getValueMeta( eq( 0 ) ) ).thenReturn( valueMeta1 );
    when( rowMetaInterface.getString( eq( row ), eq( 0 ) ) ).thenReturn( row[0] );

    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.rowWrittenEvent( rowMetaInterface, row );

    TypedTableModel expectedModel = new TypedTableModel( new String[] { "COLUMN_1" }, new Class[] { String.class } );
    expectedModel.addRow( "NONE" );

    assertEquals( expectedModel, tableProducer.getTableModel() );
  }

  @Test
  public void rowWrittenEvent_binary() throws KettleStepException, ReportDataFactoryException, KettleValueException {
    Object[] row = new Object[] { new byte[] { 1, 2, 3 } };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    when( rowMetaInterface.size() ).thenReturn( 1 );

    ValueMetaInterface valueMeta1 = mock( ValueMetaInterface.class );
    when( valueMeta1.getName() ).thenReturn( "COLUMN_1" );
    when( valueMeta1.getType() ).thenReturn( ValueMetaInterface.TYPE_BINARY );
    when( rowMetaInterface.getValueMeta( eq( 0 ) ) ).thenReturn( valueMeta1 );
    when( rowMetaInterface.getBinary( eq( row ), eq( 0 ) ) ).thenReturn( (byte[]) row[0] );

    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.rowWrittenEvent( rowMetaInterface, row );

    TypedTableModel expectedModel = new TypedTableModel( new String[] { "COLUMN_1" }, new Class[] { byte[].class } );
    expectedModel.addRow( new byte[] { 1, 2, 3 } );

    assertEqualsForByteArrayData( expectedModel, tableProducer.getTableModel() );
  }

  @Test( expected = KettleStepException.class )
  public void errorRowWrittenEvent() throws KettleValueException, KettleStepException {
    Object[] row = new Object[] { "TEST" };
    RowMetaInterface rowMetaInterface = mock( RowMetaInterface.class );
    TableProducer tableProducer = new TableProducer( rowMetaInterface, 0, true );
    tableProducer.errorRowWrittenEvent( rowMetaInterface, row );
  }

  private static void assertEquals( TypedTableModel expectedModel, TableModel actualModel ) {
    for ( int i = 0; i < expectedModel.getRowCount(); i++ ) {
      for ( int j = 0; j < expectedModel.getColumnCount(); j++ ) {
        Assert.assertEquals( expectedModel.getValueAt( i, j ), actualModel.getValueAt( i, j ) );
      }
    }
  }

  private static void assertEqualsForByteArrayData( TypedTableModel expectedModel, TableModel actualModel ) {
    for ( int i = 0; i < expectedModel.getRowCount(); i++ ) {
      for ( int j = 0; j < expectedModel.getColumnCount(); j++ ) {
        Assert.assertArrayEquals( (byte[]) expectedModel.getValueAt( i, j ), (byte[]) actualModel.getValueAt( i, j ) );
      }
    }
  }

}
