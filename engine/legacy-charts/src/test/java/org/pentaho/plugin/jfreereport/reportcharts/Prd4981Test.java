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

package org.pentaho.plugin.jfreereport.reportcharts;

import junit.framework.TestCase;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.junit.Assert;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

import java.net.URL;

public class Prd4981Test extends TestCase {
  public Prd4981Test() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }


  public void testChartProcessing50_Hidden() throws Exception {
    final URL url = getClass().getResource( "Prd-4981.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );

    final RenderNode[] elementsByElementType =
      MatchFactory.findElementsByNodeType( logicalPageBox, LayoutNodeTypes.TYPE_BOX_CONTENT );

    Assert.assertTrue( elementsByElementType.length > 0 );

    for ( final RenderNode renderNode : elementsByElementType ) {
      final RenderableReplacedContentBox c = (RenderableReplacedContentBox) renderNode;
      final DrawableWrapper rawObject = (DrawableWrapper) c.getContent().getRawObject();
      final JFreeChartReportDrawable backend = (JFreeChartReportDrawable) rawObject.getBackend();
      final JFreeChart chart = backend.getChart();
      final CategoryPlot p = (CategoryPlot) chart.getPlot();
      final CategoryDataset dataset = p.getDataset();
      Assert.assertNotNull( dataset );
      DebugLog.log( rawObject );
    }
  }
}
