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

package org.pentaho.reporting.engine.classic.core.layout.table;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.process.ComputeStaticPropertiesProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ParagraphLineBreakStep;
import org.pentaho.reporting.engine.classic.core.layout.process.TableValidationStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ValidateModelStep;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

public class TableValidateIT extends TestCase {
  public TableValidateIT() {
  }

  public TableValidateIT( final String name ) {
    super( name );
  }

  private Band createTable() {
    final Band tableCellA1 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell A1" ) );
    final Band tableCellA2 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell A2" ) );
    final Band tableCellB1 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell B1" ) );
    final Band tableCellB2 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell B2" ) );
    final Band tableCellC1 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell C1" ) );
    final Band tableCellC2 = TableTestUtil.createCell( TableTestUtil.createDataItem( "Cell C2" ) );

    final Band tableRowA = TableTestUtil.createRow( tableCellA1, tableCellA2 );
    final Band tableRowB = TableTestUtil.createRow( tableCellB1, tableCellB2 );
    final Band tableRowC = TableTestUtil.createRow( tableCellC1, tableCellC2 );

    final Band tableBody = new Band();
    tableBody.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE_BODY );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    tableBody.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 150f );
    tableBody.addElement( tableRowA );
    tableBody.addElement( tableRowB );
    tableBody.addElement( tableRowC );

    final Band table = new Band();
    table.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_TABLE );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, -100f );
    table.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, 200f );
    table.addElement( tableBody );
    return table;
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testValidate() throws ReportProcessingException, ContentProcessingException {
    final MasterReport report = new MasterReport();
    final DefaultProcessingContext processingContext = new DefaultProcessingContext( report );

    final LogicalPageBox pageBox = DebugReportRunner.layoutSingleBand( report, createTable() );
    assertTrue( new ValidateModelStep().isLayoutable( pageBox ) );
    new TableValidationStep().validate( pageBox );
    new ParagraphLineBreakStep().compute( pageBox );

    final ComputeStaticPropertiesProcessStep computeStaticPropertiesProcessStep =
        new ComputeStaticPropertiesProcessStep();
    computeStaticPropertiesProcessStep.initialize( processingContext.getOutputProcessorMetaData(), processingContext );
    computeStaticPropertiesProcessStep.compute( pageBox );

    // ModelPrinter.print(pageBox);
  }

}
