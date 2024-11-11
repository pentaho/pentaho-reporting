/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.designer.extensions.legacycharts;

import junit.framework.TestCase;
import org.pentaho.plugin.jfreereport.reportcharts.BarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BarLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.CategorySetDataCollector;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.XYSeriesCollector;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;

public class LegacyChartEditModelTest extends TestCase {
  public LegacyChartEditModelTest() {
  }

  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testDefaults() {
    final LegacyChartEditModel model = new LegacyChartEditModel();
    assertNull( model.getChartExpression() );
    assertNull( model.getPrimaryDataSource() );
    assertNull( model.getSecondaryDataSource() );
    assertNull( model.getCurrentChartType() );
    assertNotNull( model.getChartExpressionsModel() );
    assertNotNull( model.getPrimaryDataSourcesModel() );
    assertNotNull( model.getSecondaryDataSourcesModel() );

    assertTrue( model.getChartExpressionsModel().getSize() > 0 );
    assertNull( model.getChartExpressionsModel().getSelectedItem() );
    assertNull( model.getPrimaryDataSourcesModel().getSelectedItem() );
    assertNull( model.getSecondaryDataSourcesModel().getSelectedItem() );
  }

  public void testInitWithChart() {
    final BarLineChartExpression chartExpression = new BarLineChartExpression();
    final CategorySetDataCollector primaryDataCollector = new CategorySetDataCollector();
    final XYSeriesCollector secondaryDataCollector = new XYSeriesCollector();

    final LegacyChartEditModel model = new LegacyChartEditModel();
    model.setChartExpression( chartExpression );
    model.setPrimaryDataSource( primaryDataCollector );
    model.setSecondaryDataSource( secondaryDataCollector );

    assertSame( chartExpression, model.getChartExpression() );
    assertSame( primaryDataCollector, model.getPrimaryDataSource() );
    assertSame( secondaryDataCollector, model.getSecondaryDataSource() );
    assertEquals( ChartType.BAR_LINE, model.getCurrentChartType() );
    assertNotNull( model.getChartExpressionsModel() );
    assertNotNull( model.getPrimaryDataSourcesModel() );
    assertNotNull( model.getSecondaryDataSourcesModel() );

    assertTrue( model.getChartExpressionsModel().getSize() > 0 );
    assertNotNull( model.getChartExpressionsModel().getSelectedItem() );
    assertNotNull( model.getPrimaryDataSourcesModel().getSelectedItem() );
    assertNotNull( model.getSecondaryDataSourcesModel().getSelectedItem() );
  }


  public void testChartTypeChange() {
    final BarLineChartExpression chartExpression = new BarLineChartExpression();
    final CategorySetDataCollector primaryDataCollector = new CategorySetDataCollector();
    final XYSeriesCollector secondaryDataCollector = new XYSeriesCollector();

    final LegacyChartEditModel model = new LegacyChartEditModel();
    model.setChartExpression( chartExpression );
    model.setPrimaryDataSource( primaryDataCollector );
    model.setSecondaryDataSource( secondaryDataCollector );

    assertSame( chartExpression, model.getChartExpression() );
    assertSame( primaryDataCollector, model.getPrimaryDataSource() );
    assertSame( secondaryDataCollector, model.getSecondaryDataSource() );
    assertEquals( ChartType.BAR_LINE, model.getCurrentChartType() );
    assertNotNull( model.getChartExpressionsModel() );
    assertNotNull( model.getPrimaryDataSourcesModel() );
    assertNotNull( model.getSecondaryDataSourcesModel() );

    assertTrue( model.getChartExpressionsModel().getSize() > 0 );
    assertNotNull( model.getChartExpressionsModel().getSelectedItem() );
    assertNotNull( model.getPrimaryDataSourcesModel().getSelectedItem() );
    assertNotNull( model.getSecondaryDataSourcesModel().getSelectedItem() );


    final ExpressionMetaData expressionMetaData =
      ExpressionRegistry.getInstance().getExpressionMetaData( BarChartExpression.class.getName() );
    model.getChartExpressionsModel().setSelectedItem( expressionMetaData );
    assertEquals( expressionMetaData, model.getChartExpressionsModel().getSelectedItem() );
    assertTrue( model.getChartExpression().getClass() == BarChartExpression.class );
    assertEquals( ChartType.BAR, model.getCurrentChartType() );
    assertSame( primaryDataCollector, model.getPrimaryDataSource() );

    final ExpressionMetaData dsExpressionMetaData =
      ExpressionRegistry.getInstance().getExpressionMetaData( CategorySetDataCollector.class.getName() );
    assertEquals( dsExpressionMetaData, model.getPrimaryDataSourcesModel().getSelectedItem() );
    assertNull( model.getSecondaryDataSource() );
    assertNull( model.getSecondaryDataSourcesModel().getSelectedItem() );
  }
}
