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

package org.pentaho.reporting.engine.classic.core.modules.output.table.csv;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.repository.ContentItem;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CSVPrinterTest {

  @Test
  public void testNullCellInLastColumnWithoutForceQuotingPreservesLegacySeparator() throws Exception {
    final StringWriter output = new StringWriter();
    final CSVPrinter printer = createPrinterWithInjectedState( output, false );

    final TableContentProducer contentProducer = mock( TableContentProducer.class );
    when( contentProducer.getSheetLayout() ).thenReturn( mock( SheetLayout.class ) );
    when( contentProducer.getColumnCount() ).thenReturn( 1 );
    when( contentProducer.getFinishedRows() ).thenReturn( 0 );
    when( contentProducer.getFilledRows() ).thenReturn( 1 );
    when( contentProducer.getContent( 0, (short) 0 ) ).thenReturn( (RenderBox) null );

    printer.print( null, contentProducer, mock( OutputProcessorMetaData.class ), true );

    assertEquals( "," + System.lineSeparator(), output.toString() );
  }

  @Test
  public void testNullCellInLastColumnWithForceQuotingWritesQuotedEmptyCell() throws Exception {
    final StringWriter output = new StringWriter();
    final CSVPrinter printer = createPrinterWithInjectedState( output, true );

    final TableContentProducer contentProducer = mock( TableContentProducer.class );
    when( contentProducer.getSheetLayout() ).thenReturn( mock( SheetLayout.class ) );
    when( contentProducer.getColumnCount() ).thenReturn( 1 );
    when( contentProducer.getFinishedRows() ).thenReturn( 0 );
    when( contentProducer.getFilledRows() ).thenReturn( 1 );
    when( contentProducer.getContent( 0, (short) 0 ) ).thenReturn( (RenderBox) null );

    printer.print( null, contentProducer, mock( OutputProcessorMetaData.class ), true );

    assertEquals( "\"\"" + System.lineSeparator(), output.toString() );
  }

  @Test
  public void testSpannedCellWithoutForceQuotingPreservesLegacySeparator() throws Exception {
    final StringWriter output = new StringWriter();
    final CSVPrinter printer = createPrinterWithInjectedState( output, false );

    final TableContentProducer contentProducer = mock( TableContentProducer.class );
    final SheetLayout sheetLayout = mock( SheetLayout.class );
    final RenderBox content = mock( RenderBox.class );

    when( contentProducer.getSheetLayout() ).thenReturn( sheetLayout );
    when( contentProducer.getColumnCount() ).thenReturn( 1 );
    when( contentProducer.getFinishedRows() ).thenReturn( 0 );
    when( contentProducer.getFilledRows() ).thenReturn( 1 );
    when( contentProducer.getContent( 0, (short) 0 ) ).thenReturn( content );

    when( content.isCommited() ).thenReturn( true );
    when( contentProducer.getContentOffset( 0, (short) 0 ) ).thenReturn( 0L );
    when( content.getX() ).thenReturn( 1L );
    when( sheetLayout.getXPosition( 0 ) ).thenReturn( 0L );
    when( content.getY() ).thenReturn( 0L );
    when( sheetLayout.getYPosition( 0 ) ).thenReturn( 0L );

    printer.print( null, contentProducer, mock( OutputProcessorMetaData.class ), true );

    assertEquals( "," + System.lineSeparator(), output.toString() );
  }

  private CSVPrinter createPrinterWithInjectedState( final StringWriter output,
                                                      final boolean forceQuoting ) throws Exception {
    final CSVPrinter printer = new CSVPrinter();
    setField( printer, "writer", new PrintWriter( output ) );
    setField( printer, "quoter", new CSVQuoter( ',', '"', forceQuoting ) );
    setField( printer, "textExtractor", mock( DefaultTextExtractor.class ) );
    setField( printer, "documentContentItem", mock( ContentItem.class ) );
    return printer;
  }

  private void setField( final Object target,
                         final String fieldName,
                         final Object value ) throws Exception {
    final Field field = CSVPrinter.class.getDeclaredField( fieldName );
    field.setAccessible( true );
    field.set( target, value );
  }
}
