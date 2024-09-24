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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import javax.swing.table.TableModel;

import org.jfree.chart.plot.SpiderWebPlot;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.AggregateTestDataTableModel;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugExpressionRuntime;

public class RadarChartExpressionTest {
  @BeforeClass
  public static void ensureBootIsDone() {
    ClassicEngineBoot.getInstance().start();
  }
  @Test
  public void testComputeChart() {
    System.out.println( "Start" );
    RadarChartExpression radarChart = new RadarChartExpression();
    TableModel tm = new AggregateTestDataTableModel();
    Map<String, Object> data = new HashMap<String, Object>();
    CollectorFunctionResult cfr = Mockito.mock( CollectorFunctionResult.class );
    Mockito.when( cfr.getDataSet() ).thenReturn(  Mockito.mock( org.jfree.data.general.Dataset.class ) );

    data.put( "name1", cfr );
    StaticDataRow dr = new StaticDataRow( data );
    ProcessingContext pc = new DefaultProcessingContext();
    DebugExpressionRuntime der = new DebugExpressionRuntime( dr, tm, 0, pc );
    radarChart.setRuntime( der );
    radarChart.setSeriesColor( new String[]{ "maroon", "#FDDF00" } );
    radarChart.setDataSource( "name1" );
    JFreeChartReportDrawable chart = (JFreeChartReportDrawable) radarChart.getValue();
    Assert.assertTrue( "first color is maroon : ", ( ( (SpiderWebPlot) chart.getChart().getPlot() ).getSeriesPaint( 0 ).equals( ColorHelper.lookupColor( "maroon" ) ) ) );
    Assert.assertTrue( "second color is #FDDF00 : ", ( ( (SpiderWebPlot) chart.getChart().getPlot() ).getSeriesPaint( 1 ).equals( Color.decode( "#FDDF00" ) ) ) );
    System.out.println( "Finish" );
  }
}
