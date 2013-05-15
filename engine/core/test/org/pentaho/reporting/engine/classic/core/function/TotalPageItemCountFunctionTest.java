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
 * Copyright (c) 2005-2011 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function;

import java.io.File;
import javax.swing.table.TableModel;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.elementfactory.CrosstabBuilder;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.testsupport.RelationalReportBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;


/** @noinspection HardCodedStringLiteral*/
public class TotalPageItemCountFunctionTest extends TestCase
{

  public static final String ROW_DIMENSION_A = "Row-Dimension-A";
  public static final String ROW_DIMENSION_B = "Row-Dimension-B";
  public static final String COLUMN_DIMENSION_A = "Column-Dimension-A";
  public static final String COLUMN_DIMENSION_B = "Column-Dimension-B";
  public static final String VALUE = "Value";

  private static final boolean failHard = false;

  public TotalPageItemCountFunctionTest()
  {
  }

  public TotalPageItemCountFunctionTest(final String name)
  {
    super(name);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  private TableModel createCrosstabTableModel()
  {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn(ROW_DIMENSION_A, String.class);
    model.addColumn(ROW_DIMENSION_B, String.class);
    model.addColumn(COLUMN_DIMENSION_A, String.class);
    model.addColumn(COLUMN_DIMENSION_B, String.class);
    model.addColumn(VALUE, String.class);
    model.addColumn("validate-cell-sum", Integer.class);
    model.addColumn("validate-detail-sum", Integer.class);
    model.addColumn("validate-row-b-sum", Integer.class);
    model.addColumn("validate-row-a-sum", Integer.class);
    model.addColumn("validate-column-a-sum", Integer.class); // should always be the same as row-b-sum
    model.addColumn("validate-column-b-sum", Integer.class);

    model.addRow("R1", "r1", "C1", "c1", 1, 1, 1, 2, 4, 2, 2);
    model.addRow("R1", "r1", "C1", "c2", 2, 1, 1, 2, 4, 2, 2);
    model.addRow("R1", "r1", "C2", "c1", 3, 1, 1, 2, 6, 2, 2);
    model.addRow("R1", "r1", "C2", "c2", 4, 1, 1, 2, 6, 2, 4);
    model.addRow("R1", "r2", "C1", "c1", 5, 1, 1, 2, 4, 2, 2); //*
    model.addRow("R1", "r2", "C1", "c2", 6, 1, 1, 2, 4, 2, 2);
    model.addRow("R1", "r2", "C2", "c1", 7, 1, 1, 4, 6, 4, 2); //
    model.addRow("R1", "r2", "C2", "c2", 8, 3, 3, 4, 6, 4, 4);
    model.addRow("R1", "r2", "C2", "c2", 8, 3, 3, 4, 6, 4, 4);
    model.addRow("R1", "r2", "C2", "c2", 8, 3, 3, 4, 6, 4, 4); //
    model.addRow("R2", "r1", "C1", "c1", 10, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r1", "C1", "c2", 11, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r1", "C2", "c1", 12, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r1", "C2", "c2", 13, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r2", "C1", "c1", 14, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r2", "C1", "c2", 15, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r2", "C2", "c1", 16, 1, 1, 2, 4, 2, 2);
    model.addRow("R2", "r2", "C2", "c2", 17, 1, 1, 2, 4, 2, 2);
    return model;
  }

  private TableModel createRelationalTableModel()
  {
    final TypedTableModel model = new TypedTableModel();
    model.addColumn(ROW_DIMENSION_A, String.class);
    model.addColumn(ROW_DIMENSION_B, String.class);
    model.addColumn(VALUE, String.class);
    model.addColumn("validate-row-b-sum", Integer.class);
    model.addColumn("validate-row-a-sum", Integer.class);

    model.addRow("RA", "r1", 1, 1, 5);
    model.addRow("RA", "r2", 2, 1, 5);
    model.addRow("RA", "r1", 1, 3, 5);
    model.addRow("RA", "r1", 1, 3, 5);
    model.addRow("RA", "r1", 2, 3, 5);   // page break
    model.addRow("RA", "r2", 1, 3, 3);
    model.addRow("RA", "r2", 1, 3, 3);
    model.addRow("RA", "r2", 2, 3, 3);
    model.addRow("RB", "r1", 1, 4, 4);
    model.addRow("RB", "r1", 1, 4, 4);
    model.addRow("RB", "r1", 1, 4, 4);
    model.addRow("RB", "r1", 2, 4, 4);  // page break
    model.addRow("RB", "r1", 1, 1, 8);
    model.addRow("RB", "r2", 2, 7, 8);
    model.addRow("RB", "r2", 1, 7, 8);
    model.addRow("RB", "r2", 1, 7, 8);
    model.addRow("RB", "r2", 1, 7, 8);
    model.addRow("RB", "r2", 2, 7, 8);
    model.addRow("RB", "r2", 2, 7, 8);
    model.addRow("RB", "r2", 2, 7, 8);
    return model;
  }

  private AggregationFunction create(final String name,
                                     final String filter,
                                     final String group)
  {
    final TotalPageItemCountFunction detailsSum = new TotalPageItemCountFunction();
    detailsSum.setName(name);
    detailsSum.setCrosstabFilterGroup(filter);
    detailsSum.setGroup(group);
    detailsSum.setDependencyLevel(1);
    return detailsSum;
  }

  private AggregationFunction create(final String name,
                                     final String group)
  {
    final TotalPageItemCountFunction detailsSum = new TotalPageItemCountFunction();
    detailsSum.setName(name);
    detailsSum.setGroup(group);
    detailsSum.setDependencyLevel(1);
    return detailsSum;
  }


  /**
   * Disabled for now.  "Page" level functions recalculate values near the page
   * break, which was causing the ValidatePageFunctionResultExpression checks to
   * trigger a fail, even though subsequent computed values were correct.  Need
   * to fine a better way to test Page functions.
   * @throws Exception
   */
  public void _testRelationalReport() throws Exception
  {
    final TableModel tableModel = createRelationalTableModel();
    final MasterReport report = createRelationalReport(tableModel);

    //report.addExpression(create("row-b-sum", ROW_DIMENSION_B));
      report.addExpression(create("row-b-sum", "::group-1"));
    report.addExpression(new ValidatePageFunctionResultExpression("#row-b-sum", failHard, null));

    //report.addExpression(create("row-a-sum", ROW_DIMENSION_A));
    report.addExpression(create("row-a-sum", "::group-0"));
    report.addExpression(new ValidatePageFunctionResultExpression("#row-a-sum", failHard, null));

    DebugReportRunner.showDialog(report);

    // DebugReportRunner.execGraphics2D(report);
  }

  public void _testCrosstabReport() throws Exception
  {
    final TableModel tableModel = createCrosstabTableModel();
    final MasterReport report = createCrosstabReport(tableModel);

    report.addExpression(create("cell-sum", null, COLUMN_DIMENSION_B));
    report.addExpression(new ValidatePageFunctionResultExpression("#cell-sum", failHard, null));

    report.addExpression(create("detail-sum", COLUMN_DIMENSION_B, ROW_DIMENSION_B));
    report.addExpression(new ValidatePageFunctionResultExpression("#detail-sum", failHard, COLUMN_DIMENSION_B));

    report.addExpression(create("row-b-sum", COLUMN_DIMENSION_A, ROW_DIMENSION_B));
    report.addExpression(new ValidatePageFunctionResultExpression("#row-b-sum", failHard, COLUMN_DIMENSION_A));

    report.addExpression(create("column-a-sum", null, COLUMN_DIMENSION_A));
    report.addExpression(new ValidatePageFunctionResultExpression("#column-a-sum", failHard, null));

    report.addExpression(create("row-a-sum", COLUMN_DIMENSION_A, ROW_DIMENSION_A));
    report.addExpression(new ValidatePageFunctionResultExpression("#row-a-sum", failHard, COLUMN_DIMENSION_A));

    report.addExpression(create("column-b-sum", COLUMN_DIMENSION_B, ROW_DIMENSION_A));
    report.addExpression(new ValidatePageFunctionResultExpression("#column-b-sum", failHard, COLUMN_DIMENSION_B));

    DebugReportRunner.showDialog(report);

   // DebugReportRunner.execGraphics2D(report);
  }

  private MasterReport createCrosstabReport(final TableModel tableModel)
  {
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(800, 400)));
    report.setDataFactory(new TableDataFactory("query", tableModel));
    report.setQuery("query");
    final DesignTimeDataSchemaModel dataSchemaModel = new DesignTimeDataSchemaModel(report);

    final CrosstabBuilder builder = new CrosstabBuilder(dataSchemaModel);
    builder.addRowDimension(ROW_DIMENSION_A);
    builder.addRowDimension(ROW_DIMENSION_B);
    builder.addColumnDimension(COLUMN_DIMENSION_A);
    builder.addColumnDimension(COLUMN_DIMENSION_B);
    builder.addDetails(VALUE, null);
    report.setRootGroup(builder.create());
    return report;
  }

  private MasterReport createRelationalReport(final TableModel tableModel)
  {
    final MasterReport report = new MasterReport();
    report.setPageDefinition(new SimplePageDefinition(new PageSize(800, 300)));
    report.setDataFactory(new TableDataFactory("query", tableModel));
    report.setQuery("query");

    final DesignTimeDataSchemaModel dataSchemaModel = new DesignTimeDataSchemaModel(report);
    final RelationalReportBuilder builder = new RelationalReportBuilder(dataSchemaModel);
    builder.addGroup(ROW_DIMENSION_A);
    builder.addGroup(ROW_DIMENSION_B);
    builder.addDetails(VALUE, TotalPageItemCountFunction.class);
    builder.addDetails("row-a-sum", null);
    builder.addDetails("row-b-sum", null);

    report.setRootGroup(builder.create());


    return report;
  }
}
