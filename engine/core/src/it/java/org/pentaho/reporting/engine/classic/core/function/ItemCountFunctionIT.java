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
public class ItemCountFunctionIT extends TestCase {

  public static final String ROW_DIMENSION_A = "Row-Dimension-A";
  public static final String ROW_DIMENSION_B = "Row-Dimension-B";
  public static final String COLUMN_DIMENSION_A = "Column-Dimension-A";
  public static final String COLUMN_DIMENSION_B = "Column-Dimension-B";
  public static final String VALUE = "Value";

  public ItemCountFunctionIT() {
  }

  public ItemCountFunctionIT( final String name ) {
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
    model.addColumn( VALUE, String.class );
    model.addColumn( "validate-cell-sum", String.class );
    model.addColumn( "validate-detail-sum", String.class );
    model.addColumn( "validate-row-b-sum", String.class );
    model.addColumn( "validate-row-a-sum", String.class );
    model.addColumn( "validate-column-a-sum", String.class );
    model.addColumn( "validate-column-b-sum", String.class );

    model.addRow( "R1", "r1", "C1", "c1", 1, 1, 1, 1, 1, 1, 1 );
    model.addRow( "R1", "r1", "C1", "c2", 2, 1, 1, 2, 2, 2, 1 );
    model.addRow( "R1", "r1", "C2", "c1", 3, 1, 1, 1, 1, 1, 1 );
    model.addRow( "R1", "r1", "C2", "c2", 4, 1, 1, 2, 2, 2, 1 );
    model.addRow( "R1", "r2", "C1", "c1", 5, 1, 1, 1, 3, 1, 2 ); // *
    model.addRow( "R1", "r2", "C1", "c2", 6, 1, 1, 2, 4, 2, 2 );
    model.addRow( "R1", "r2", "C2", "c1", 7, 1, 1, 1, 3, 1, 2 ); //
    model.addRow( "R1", "r2", "C2", "c2", 8, 1, 1, 2, 4, 2, 2 );
    model.addRow( "R1", "r2", "C2", "c2", 8, 2, 2, 3, 5, 3, 3 );
    model.addRow( "R1", "r2", "C2", "c2", 8, 3, 3, 4, 6, 4, 4 ); //
    model.addRow( "R2", "r1", "C1", "c1", 10, 1, 1, 1, 1, 1, 1 );
    model.addRow( "R2", "r1", "C1", "c2", 11, 1, 1, 2, 2, 2, 1 );
    model.addRow( "R2", "r1", "C2", "c1", 12, 1, 1, 1, 1, 1, 1 );
    model.addRow( "R2", "r1", "C2", "c2", 13, 1, 1, 2, 2, 2, 1 );
    model.addRow( "R2", "r2", "C1", "c1", 14, 1, 1, 1, 3, 1, 2 );
    model.addRow( "R2", "r2", "C1", "c2", 15, 1, 1, 2, 4, 2, 2 );
    model.addRow( "R2", "r2", "C2", "c1", 16, 1, 1, 1, 3, 1, 2 );
    model.addRow( "R2", "r2", "C2", "c2", 17, 1, 1, 2, 4, 2, 2 );
    return model;
  }

  private AggregationFunction create( final String name, final String filter, final String group ) {
    final ItemCountFunction detailsSum = new ItemCountFunction();
    detailsSum.setName( name );
    detailsSum.setCrosstabFilterGroup( filter );
    detailsSum.setGroup( group );
    return detailsSum;
  }

  public void testReport() throws Exception {
    final TableModel tableModel = createTableModel();
    final MasterReport report = createReport( tableModel );

    report.addExpression( create( "cell-sum", null, COLUMN_DIMENSION_B ) );
    report.addExpression( new ValidateFunctionResultExpression( "#cell-sum", true, null ) );

    report.addExpression( create( "detail-sum", COLUMN_DIMENSION_B, ROW_DIMENSION_B ) );
    report.addExpression( new ValidateFunctionResultExpression( "#detail-sum", true, COLUMN_DIMENSION_B ) );

    report.addExpression( create( "row-b-sum", COLUMN_DIMENSION_A, ROW_DIMENSION_B ) );
    report.addExpression( new ValidateFunctionResultExpression( "#row-b-sum", true, COLUMN_DIMENSION_A ) );

    report.addExpression( create( "row-a-sum", COLUMN_DIMENSION_A, ROW_DIMENSION_A ) );
    report.addExpression( new ValidateFunctionResultExpression( "#row-a-sum", true, COLUMN_DIMENSION_A ) );

    report.addExpression( create( "column-a-sum", null, COLUMN_DIMENSION_A ) );
    report.addExpression( new ValidateFunctionResultExpression( "#column-a-sum", true, null ) );

    report.addExpression( create( "column-b-sum", COLUMN_DIMENSION_B, ROW_DIMENSION_A ) );
    report.addExpression( new ValidateFunctionResultExpression( "#column-b-sum", true, COLUMN_DIMENSION_B ) );

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
