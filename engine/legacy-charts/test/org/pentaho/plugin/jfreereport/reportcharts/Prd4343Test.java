package org.pentaho.plugin.jfreereport.reportcharts;

import java.net.URL;

import junit.framework.TestCase;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.EventMonitorFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class Prd4343Test extends TestCase
{
  public Prd4343Test()
  {
  }

  public Prd4343Test(final String s)
  {
    super(s);
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }


  public void testValidateXYLineChartItemLabels() throws ResourceException, Exception
  {
    final URL url = getClass().getResource("Prd-4343.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage(report, 0);
    final RenderNode renderNode = MatchFactory.findElementByName(logicalPageBox, "xy-chart");
    assertTrue(renderNode instanceof RenderableReplacedContentBox);

    final RenderableReplacedContentBox xyChart = (RenderableReplacedContentBox) renderNode;
    RenderableReplacedContent xyChartContent = xyChart.getContent();
    assertTrue(xyChartContent.getRawObject() instanceof DrawableWrapper);

    final DrawableWrapper drawable = (DrawableWrapper)xyChartContent.getRawObject();
    assertTrue(drawable.getBackend() instanceof JFreeChartReportDrawable);
    final JFreeChartReportDrawable jFreeChartReportDrawable = (JFreeChartReportDrawable)drawable.getBackend();
    final JFreeChart xyLineChart = jFreeChartReportDrawable.getChart();
    final XYPlot xyLinePlot = xyLineChart.getXYPlot();

    assertTrue(xyLinePlot.getRenderer() instanceof XYLineAndShapeRenderer);
    final XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyLinePlot.getRenderer();
    assertNotNull(renderer);
    assertTrue(renderer.getBaseLinesVisible());
    assertTrue(renderer.getBaseItemLabelGenerator() instanceof StandardXYItemLabelGenerator);
  }
}