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

package org.pentaho.reporting.engine.classic.core.function;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;

import javax.swing.table.TableModel;

/**
 * @noinspection HardCodedStringLiteral
 */
public class RowBandingFunctionIT extends TestCase {

  public static final String ROW_DIMENSION_A = "Row-Dimension-A";
  public static final String ROW_DIMENSION_B = "Row-Dimension-B";
  public static final String COLUMN_DIMENSION_A = "Column-Dimension-A";
  public static final String COLUMN_DIMENSION_B = "Column-Dimension-B";
  public static final String VALUE = "Value";

  public RowBandingFunctionIT() {
  }

  public RowBandingFunctionIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private TableModel createTableModel() {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn( ROW_DIMENSION_A, String.class );
    model.addColumn( ROW_DIMENSION_B, String.class );
    model.addColumn( COLUMN_DIMENSION_A, String.class );
    model.addColumn( COLUMN_DIMENSION_B, String.class );
    model.addColumn( VALUE, Integer.class );
    model.addColumn( "validate-cell-sum", Boolean.class );
    model.addColumn( "validate-detail-sum", Boolean.class );

    model.addRow( "R1", "r1", "C1", "c1", 1, false, false );
    model.addRow( "R1", "r1", "C1", "c2", 2, false, false );
    model.addRow( "R1", "r1", "C2", "c1", 3, false, false );
    model.addRow( "R1", "r1", "C2", "c2", 4, false, false );
    model.addRow( "R1", "r2", "C1", "c1", 5, true, false ); // *
    model.addRow( "R1", "r2", "C1", "c2", 6, true, false );
    model.addRow( "R1", "r2", "C2", "c1", 7, true, false ); //
    model.addRow( "R1", "r2", "C2", "c2", 8, true, false );
    model.addRow( "R1", "r2", "C2", "c2", 8, true, true );
    model.addRow( "R1", "r2", "C2", "c2", 8, true, false ); //
    model.addRow( "R2", "r1", "C1", "c1", 10, false, false );
    model.addRow( "R2", "r1", "C1", "c2", 11, false, false );
    model.addRow( "R2", "r1", "C2", "c1", 12, false, false );
    model.addRow( "R2", "r1", "C2", "c2", 13, false, false );
    model.addRow( "R2", "r2", "C1", "c1", 14, true, false );
    model.addRow( "R2", "r2", "C1", "c2", 15, true, false );
    model.addRow( "R2", "r2", "C2", "c1", 16, true, false );
    model.addRow( "R2", "r2", "C2", "c2", 17, true, false );
    return model;
  }

  private Expression create( final String name, final boolean ignoreCrosstabMode ) {
    final RowBandingFunction detailsSum = new RowBandingFunction();
    detailsSum.setName( name );
    detailsSum.setIgnoreCrosstabMode( ignoreCrosstabMode );
    return detailsSum;
  }

  public void testReport() throws Exception {
    final TableModel tableModel = createTableModel();
    final MasterReport report = createReport( tableModel );

    report.addExpression( create( "cell-sum", false ) );
    report.addExpression( new ValidateFunctionResultExpression( "#cell-sum", true, null ) );

    report.addExpression( create( "detail-sum", true ) );
    report.addExpression( new ValidateFunctionResultExpression( "#detail-sum", false, null ) );

    DebugReportRunner.execGraphics2D( report );
  }

  private MasterReport createReport( final TableModel tableModel ) {
    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( "query", tableModel ) );
    report.setQuery( "query" );
    final DesignTimeDataSchemaModel dataSchemaModel = new DesignTimeDataSchemaModel( report );

    final CrosstabBuilder builder = new CrosstabBuilder( dataSchemaModel );
    builder.addRowDimension( "Row-Dimension-A" );
    builder.addRowDimension( ROW_DIMENSION_B );
    builder.addColumnDimension( COLUMN_DIMENSION_A );
    builder.addColumnDimension( COLUMN_DIMENSION_B );
    builder.addDetails( VALUE, null );
    report.setRootGroup( builder.create() );
    return report;
  }
}
