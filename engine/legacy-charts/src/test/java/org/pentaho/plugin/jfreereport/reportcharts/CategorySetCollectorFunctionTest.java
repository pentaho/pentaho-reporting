/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import junit.framework.TestCase;
import org.jfree.data.category.DefaultCategoryDataset;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;

import javax.swing.table.DefaultTableModel;

public class CategorySetCollectorFunctionTest extends TestCase {
  private static class ValidateFunction extends AbstractFunction {
    /**
     * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
     * is added to the report's function collection.
     */
    private ValidateFunction() {
      setName( "Blah" );
    }

    /**
     * Receives notification that report generation has completed, the report footer was printed, no more output is
     * done. This is a helper event to shut down the output service.
     *
     * @param event The event.
     */
    public void reportDone( final ReportEvent event ) {
      final CategorySetCollectorFunction o = (CategorySetCollectorFunction) getDataRow().get( "Collector" );
      final DefaultCategoryDataset value = (DefaultCategoryDataset) o.getDatasourceValue();
      assertEquals( "RowCount == 1", 1, value.getRowCount() );
      assertEquals( "ColumnCount == 4", 4, value.getColumnCount() );

      assertEquals( "ColKey1", "A - 1", value.getColumnKey( 0 ) );
      assertEquals( "ColKey2", "B - 1", value.getColumnKey( 1 ) );
      assertEquals( "ColKey3", "C - 1", value.getColumnKey( 2 ) );
      assertEquals( "ColKey4", "D - 1", value.getColumnKey( 3 ) );
    }

    /**
     * Return the current expression value.
     * <p/>
     * The value depends (obviously) on the expression implementation.
     *
     * @return the value of the function.
     */
    public Object getValue() {
      return null;
    }
  }

  public CategorySetCollectorFunctionTest() {
  }

  public CategorySetCollectorFunctionTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testDuplicateLabelsBug() {
    final DefaultTableModel model = new DefaultTableModel( new String[] { "Category", "Series", "Value" }, 4 );
    model.setValueAt( "A - 1", 0, 0 );
    model.setValueAt( "A", 0, 1 );
    model.setValueAt( new Double( 100 ), 0, 2 );
    model.setValueAt( "B - 1", 1, 0 );
    model.setValueAt( "B", 1, 1 );
    model.setValueAt( new Double( 200 ), 1, 2 );
    model.setValueAt( "C - 1", 2, 0 );
    model.setValueAt( "C", 2, 1 );
    model.setValueAt( new Double( 300 ), 2, 2 );
    model.setValueAt( "D - 1", 3, 0 );
    model.setValueAt( "D", 3, 1 );
    model.setValueAt( new Double( 400 ), 3, 2 );

    final CategorySetCollectorFunction fn = new CategorySetCollectorFunction();
    fn.setCategoryColumn( "Category" );
    fn.setSeriesName( 0, "Series" );
    fn.setValueColumn( 0, "Value" );
    fn.setSummaryOnly( false );
    fn.setGeneratedReport( false );
    fn.setSeriesColumn( false );
    fn.setName( "Collector" );

    final MasterReport report = new MasterReport();
    report.setDataFactory( new TableDataFactory( report.getQuery(), model ) );
    report.addExpression( fn );
    report.addExpression( new ValidateFunction() );

    DebugReportRunner.execGraphics2D( report );
  }
}
