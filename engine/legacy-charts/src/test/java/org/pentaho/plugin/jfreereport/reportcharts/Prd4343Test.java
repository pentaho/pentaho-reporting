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
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import java.net.URL;

public class Prd4343Test extends TestCase {
  public Prd4343Test() {
  }

  public Prd4343Test( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }


  public void testValidateXYLineChartItemLabels() throws ResourceException, Exception {
    final URL url = getClass().getResource( "Prd-4343.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    final RenderNode renderNode = MatchFactory.findElementByName( logicalPageBox, "xy-chart" );
    assertTrue( renderNode instanceof RenderableReplacedContentBox );

    final RenderableReplacedContentBox xyChart = (RenderableReplacedContentBox) renderNode;
    RenderableReplacedContent xyChartContent = xyChart.getContent();
    assertTrue( xyChartContent.getRawObject() instanceof DrawableWrapper );

    final DrawableWrapper drawable = (DrawableWrapper) xyChartContent.getRawObject();
    assertTrue( drawable.getBackend() instanceof JFreeChartReportDrawable );
    final JFreeChartReportDrawable jFreeChartReportDrawable = (JFreeChartReportDrawable) drawable.getBackend();
    final JFreeChart xyLineChart = jFreeChartReportDrawable.getChart();
    final XYPlot xyLinePlot = xyLineChart.getXYPlot();

    assertTrue( xyLinePlot.getRenderer() instanceof XYLineAndShapeRenderer );
    final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyLinePlot.getRenderer();
    assertNotNull( renderer );
    assertTrue( renderer.getBaseLinesVisible() );
    assertNotNull( renderer.getBaseItemLabelGenerator() );
    assertTrue( renderer.getBaseItemLabelGenerator() instanceof StandardXYItemLabelGenerator );
  }
}
