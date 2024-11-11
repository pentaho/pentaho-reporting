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

import org.pentaho.plugin.jfreereport.reportcharts.AreaChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BarLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.BubbleChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ExtendedXYLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.LineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.MultiPieChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.PieChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.RadarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.RingChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ScatterPlotChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.ThermometerChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.WaterfallChartExpressions;
import org.pentaho.plugin.jfreereport.reportcharts.XYAreaChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.XYAreaLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.XYBarChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.XYLineChartExpression;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.CategorySetDataCollector;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.PieDataSetCollector;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.ValueDataSetCollector;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.XYSeriesCollector;
import org.pentaho.plugin.jfreereport.reportcharts.collectors.XYZSeriesCollector;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public enum ChartType {
  BAR( ChartDataSource.CATEGORY, BarChartExpression.class, CategorySetDataCollector.class ),
  LINE( ChartDataSource.CATEGORY, LineChartExpression.class, CategorySetDataCollector.class ),
  AREA( ChartDataSource.CATEGORY, AreaChartExpression.class, CategorySetDataCollector.class ),
  PIE( ChartDataSource.PIE, PieChartExpression.class, PieDataSetCollector.class ),
  MULTI_PIE( ChartDataSource.CATEGORY, MultiPieChartExpression.class, CategorySetDataCollector.class ),
  BAR_LINE( ChartDataSource.CATEGORY, ChartDataSource.CATEGORY, "linesDataSource",
    BarLineChartExpression.class, CategorySetDataCollector.class, CategorySetDataCollector.class ),
  RING( ChartDataSource.PIE, RingChartExpression.class, PieDataSetCollector.class ),
  BUBBLE( ChartDataSource.XYZ, BubbleChartExpression.class, XYZSeriesCollector.class ),
  SCATTER_PLOT( ChartDataSource.XY, ScatterPlotChartExpression.class, XYSeriesCollector.class ),
  XY_BAR( ChartDataSource.XY, XYBarChartExpression.class, XYSeriesCollector.class ),
  XY_LINE( ChartDataSource.XY, XYLineChartExpression.class, XYSeriesCollector.class ),
  XY_AREA( ChartDataSource.XY, XYAreaChartExpression.class, XYSeriesCollector.class ),
  EXTENDED_XY_LINE( ChartDataSource.XY, ExtendedXYLineChartExpression.class, XYSeriesCollector.class ),
  WATERFALL( ChartDataSource.CATEGORY, WaterfallChartExpressions.class, CategorySetDataCollector.class ),
  RADAR( ChartDataSource.CATEGORY, RadarChartExpression.class, CategorySetDataCollector.class ),
  XY_AREA_LINE( ChartDataSource.XY, ChartDataSource.XY, "secondaryDataSet",
    XYAreaLineChartExpression.class, XYSeriesCollector.class, XYSeriesCollector.class ),
  THERMOMETER( ChartDataSource.VALUE, ThermometerChartExpression.class, ValueDataSetCollector.class );

  private ChartDataSource datasource;
  private ChartDataSource secondaryDataSource;
  private Class expressionType;
  private String secondaryDataSourceProperty;
  private Class preferredPrimaryDataSourceImplementation;
  private Class preferredSecondaryDataSourceImplementation;

  private ChartType( final ChartDataSource datasource, final Class expressionType,
                     final Class preferredPrimaryDataSource ) {
    this( datasource, null, null, expressionType, preferredPrimaryDataSource, null );
  }

  private ChartType( final ChartDataSource datasource,
                     final ChartDataSource secondaryDataSource,
                     final String secondaryDataSourceProperty,
                     final Class expressionType,
                     final Class preferredPrimaryDataSource,
                     final Class preferredSecondaryDataSource ) {
    if ( datasource == null ) {
      throw new NullPointerException();
    }
    this.secondaryDataSourceProperty = secondaryDataSourceProperty;
    this.datasource = datasource;
    this.secondaryDataSource = secondaryDataSource;
    this.expressionType = expressionType;
    this.preferredPrimaryDataSourceImplementation = preferredPrimaryDataSource;
    this.preferredSecondaryDataSourceImplementation = preferredSecondaryDataSource;
  }

  public ChartDataSource getDatasource() {
    return datasource;
  }

  public ChartDataSource getSecondaryDataSource() {
    return secondaryDataSource;
  }

  public String getPrimaryDataSourceProperty() {
    return "dataSource";
  }

  public Class getPreferredPrimaryDataSourceImplementation() {
    return preferredPrimaryDataSourceImplementation;
  }

  public Class getPreferredSecondaryDataSourceImplementation() {
    return preferredSecondaryDataSourceImplementation;
  }

  public String getSecondaryDataSourceProperty() {
    return secondaryDataSourceProperty;
  }

  public Class getExpressionType() {
    return expressionType;
  }

  public static ChartType getTypeByChartExpression( final Class aClass ) {
    final ChartType[] types = ChartType.values();
    for ( int i = 0; i < types.length; i++ ) {
      final ChartType type = types[ i ];
      if ( ObjectUtilities.equal( type.getExpressionType(), aClass ) ) {
        return type;
      }
    }
    return null;
  }
}
