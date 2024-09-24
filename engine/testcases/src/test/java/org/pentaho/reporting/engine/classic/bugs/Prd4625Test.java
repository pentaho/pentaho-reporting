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

package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.plugin.jfreereport.reportcharts.PieChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.PieDataSetCollector;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.extensions.legacy.charts.LegacyChartType;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4625Test extends TestCase {
  public Prd4625Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBugExists() throws Exception {
    final URL source = getClass().getResource( "Prd-4625.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( source, MasterReport.class ).getResource();

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    // Wanna see how the model looks like? Uncomment the following line ..
    // ModelPrinter.INSTANCE.print(logicalPageBox);

    final RenderNode[] elementsByNodeType =
      MatchFactory.findElementsByElementType( logicalPageBox, new LegacyChartType() );
    assertEquals( 2, elementsByNodeType.length );
  }

  public void testChartCollectorActive() throws Exception {
    final URL source = getClass().getResource( "Prd-4625.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( source, MasterReport.class ).getResource();

    final ItemBand itemBand = report.getItemBand();
    final SubReport subReport = (SubReport) itemBand.getElement( 0 );
    subReport.addExpression( new ValidateChartConfigurationFunction() );
    // the chart is in the report header. We now validate that there is a chart-dataset collector added to the
    // report.

    DebugReportRunner.execGraphics2D( report );
  }

  public static class ValidateChartConfigurationFunction extends AbstractFunction {
    /**
     * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
     * is added to the report's function collection.
     */
    public ValidateChartConfigurationFunction() {
    }

    /**
     * Receives notification that report generation initializes the current run. <P> The event carries a
     * ReportState.Started state.  Use this to initialize the report.
     *
     * @param event The event.
     */
    public void reportInitialized( final ReportEvent event ) {
      // identifies the report we are working with. Useful for debugging!
      ReportStateKey subReportStateKey = event.getState().getProcessKey();
      ReportStateKey parentReportStateKey = event.getState().getParentSubReportState().getProcessKey();
      //      System.out.println ("SubReport: " + subReportStateKey);
      //      System.out.println ("Parent: " + parentReportStateKey);

      final ReportHeader reportHeader = event.getState().getReport().getReportHeader();
      final Element element = reportHeader.getElement( 0 ); // this should be the chart
      assertEquals( "legacy-chart", element.getElementTypeName() );
      final Expression attributeExpression =
        element.getAttributeExpression( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
      assertNotNull( attributeExpression );
      assertTrue( attributeExpression instanceof PieChartExpression );
      final PieChartExpression pe = (PieChartExpression) attributeExpression;
      final String dataSource = pe.getDataSource();

      final Expression[] expressions =
        event.getState().getFlowController().getMasterRow().getExpressionDataRow().getExpressions();
      assertEquals( expressions.length, 2 );
      // 2 expressions: One is the validate function, the other is the chart-dataset collector.
      // as the chart-dataset collector is added late (during report processing), our validate function
      // will always occupy spot 0.
      assertEquals( expressions[ 1 ].getName(), dataSource );
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

  public void testChartPreProcessor() throws Exception {
    final URL source = getClass().getResource( "Prd-4625.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final MasterReport report = (MasterReport) mgr.createDirectly( source, MasterReport.class ).getResource();

    final ItemBand itemBand = report.getItemBand();
    final SubReport subReport = (SubReport) itemBand.getElement( 0 );
    subReport.addPreProcessor( new ValidateChartPreProcessor() );

    DebugReportRunner.execGraphics2D( report );
  }

  public static class ValidateChartPreProcessor extends AbstractReportPreProcessor {
    public SubReport performPreProcessing( final SubReport definition,
                                           final DefaultFlowController flowController )
      throws ReportProcessingException {
      final ExpressionCollection expressions = definition.getExpressions();
      for ( int i = 0; i < expressions.size(); i += 1 ) {
        if ( expressions.getExpression( i ) instanceof PieDataSetCollector ) {
          fail( "Found dataset collector, but we should not have one here" );
        }
      }
      return definition;
    }
  }
}

