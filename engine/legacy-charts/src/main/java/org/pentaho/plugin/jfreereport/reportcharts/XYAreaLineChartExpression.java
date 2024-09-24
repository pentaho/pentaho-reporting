/*
* Copyright 2002 - 2017 Hitachi Vantara.  All rights reserved.
* 
* This software was developed by Hitachi Vantara and is provided under the terms
* of the Mozilla Public License, Version 1.1, or any later version. You may not use
* this file except in compliance with the license. If you need a copy of the license,
* please go to http://www.mozilla.org/MPL/MPL-1.1.txt. TThe Initial Developer is Pentaho Corporation.
*
* Software distributed under the Mozilla Public License is distributed on an "AS IS"
* basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to
* the license for the specific language governing your rights and limitations.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLine3DRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Year;
import org.jfree.data.xy.XYDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.ExtTimeTableXYDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FastNumberTickUnit;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.awt.*;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class XYAreaLineChartExpression extends XYAreaChartExpression implements MultiPlotChartExpression {

  private static final long serialVersionUID = 7082583397390897215L;

  private String secondValueAxisLabel; //$NON-NLS-1$
  private ArrayList lineSeriesColor;

  private Font linesLabelFont; //$NON-NLS-1$
  private Font linesTickLabelFont; //$NON-NLS-1$
  private String lineTicksLabelFormat; //$NON-NLS-1$

  private boolean sharedRangeAxis;

  private double linePeriodCount;
  private Class lineTimePeriod;
  private Font lineTitleFont;
  private Font lineTickFont;
  private Double lineRangeMinimum;
  private Double lineRangeMaximum;
  private boolean lineAxisAutoRange;
  private String secondaryDataSet;
  private boolean lineAxisIncludesZero;
  private boolean lineAxisStickyZero;

  //constructor
  public XYAreaLineChartExpression() {
    lineSeriesColor = new ArrayList();
    secondValueAxisLabel = "";
    linePeriodCount = 0;
    lineAxisIncludesZero = true;
    lineAxisStickyZero = true;
  }

  public boolean isLineAxisIncludesZero() {
    return lineAxisIncludesZero;
  }

  public void setLineAxisIncludesZero( final boolean lineAxisIncludesZero ) {
    this.lineAxisIncludesZero = lineAxisIncludesZero;
  }

  public boolean isLineAxisStickyZero() {
    return lineAxisStickyZero;
  }

  public void setLineAxisStickyZero( final boolean lineAxisStickyZero ) {
    this.lineAxisStickyZero = lineAxisStickyZero;
  }

  public Font getLineTitleFont() {
    return lineTitleFont;
  }

  public void setLineTitleFont( final Font lineTitleFont ) {
    this.lineTitleFont = lineTitleFont;
  }

  public Font getLineTickFont() {
    return lineTickFont;
  }

  public void setLineTickFont( final Font lineTickFont ) {
    this.lineTickFont = lineTickFont;
  }

  public Double getLineRangeMinimum() {
    return lineRangeMinimum;
  }

  public void setLineRangeMinimum( final Double lineRangeMinimum ) {
    this.lineRangeMinimum = lineRangeMinimum;
  }

  public Double getLineRangeMaximum() {
    return lineRangeMaximum;
  }

  public void setLineRangeMaximum( final Double lineRangeMaximum ) {
    this.lineRangeMaximum = lineRangeMaximum;
  }

  public double getLinePeriodCount() {
    return linePeriodCount;
  }

  public void setLinePeriodCount( final double linePeriodCount ) {
    this.linePeriodCount = linePeriodCount;
  }

  public Class getLineTimePeriod() {
    return lineTimePeriod;
  }

  public void setLineTimePeriod( final Class lineTimePeriod ) {
    this.lineTimePeriod = lineTimePeriod;
  }

  public boolean isSharedRangeAxis() {
    return sharedRangeAxis;
  }

  public void setSharedRangeAxis( final boolean sharedRangeAxis ) {
    this.sharedRangeAxis = sharedRangeAxis;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final XYAreaLineChartExpression chartExpression = (XYAreaLineChartExpression) super.getInstance();
    chartExpression.lineSeriesColor = (ArrayList) lineSeriesColor.clone();
    return chartExpression;
  }

  public String getSecondaryDataSet() {
    return secondaryDataSet;
  }

  public void setSecondaryDataSet( final String dataset ) {
    secondaryDataSet = dataset;
  }

  public String getSecondValueAxisLabel() {
    return secondValueAxisLabel;
  }

  public void setSecondValueAxisLabel( final String secondValueAxisLabel ) {
    this.secondValueAxisLabel = secondValueAxisLabel;
  }

  public Font getLinesLabelFont() {
    return linesLabelFont;
  }

  public void setLinesLabelFont( final Font linesLabelFont ) {
    this.linesLabelFont = linesLabelFont;
  }

  public Font getLinesTickLabelFont() {
    return linesTickLabelFont;
  }

  public void setLinesTickLabelFont( final Font linesTickLabelFont ) {
    this.linesTickLabelFont = linesTickLabelFont;
  }

  public String getLineTicksLabelFormat() {
    return lineTicksLabelFormat;
  }

  public void setLineTicksLabelFormat( final String lineTicksLabelFormat ) {
    this.lineTicksLabelFormat = lineTicksLabelFormat;
  }

  public void setLineSeriesColor( final int index, final String field ) {
    if ( lineSeriesColor.size() == index ) {
      lineSeriesColor.add( field );
    } else {
      lineSeriesColor.set( index, field );
    }
  }

  public String getLineSeriesColor( final int index ) {
    return (String) this.lineSeriesColor.get( index );
  }

  public int getLineSeriesColorCount() {
    return this.lineSeriesColor.size();
  }

  public String[] getLineSeriesColor() {
    final Object[] toArray = this.lineSeriesColor.toArray( new String[ this.lineSeriesColor.size() ] );
    return (String[]) toArray;
  }

  public void setLineSeriesColor( final String[] fields ) {
    this.lineSeriesColor.clear();
    this.lineSeriesColor.addAll( Arrays.asList( fields ) );
  }

  private XYDataset createLinesDataset() {
    final Object maybeCollector = getDataRow().get( getSecondaryDataSet() );
    final Dataset dataset;
    if ( maybeCollector instanceof ICollectorFunction ) {
      final ICollectorFunction collector = (ICollectorFunction) maybeCollector;
      dataset = (Dataset) collector.getDatasourceValue();
    } else if ( maybeCollector instanceof CollectorFunctionResult ) {
      final CollectorFunctionResult collector = (CollectorFunctionResult) maybeCollector;
      dataset = collector.getDataSet();
    } else {
      dataset = null;
    }


    final XYDataset linesDataset;
    if ( dataset instanceof XYDataset ) {
      linesDataset = (XYDataset) dataset;
    } else {
      linesDataset = null;
    }
    return linesDataset;
  }


  protected JFreeChart computeXYChart( final XYDataset xyDataset ) {
    final JFreeChart chart;
    if ( xyDataset instanceof TimeSeriesCollection ) {

      if ( isStacked() ) {
        final ExtTimeTableXYDataset tableXYDataset = convertToTable( xyDataset );
        chart = createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), tableXYDataset,
          isShowLegend(), false, false, isStacked() );
      } else {
        chart = createTimeSeriesChart( computeTitle(), getDomainTitle(), getRangeTitle(), xyDataset,
          isShowLegend(), false, false, isStacked() );
      }
    } else {
      final PlotOrientation orientation = computePlotOrientation();
      if ( isStacked() ) {
        chart = createStackedXYAreaChart( computeTitle(), getDomainTitle(), getRangeTitle(),
          xyDataset, orientation, isShowLegend(), false, false );
      } else {
        chart = ChartFactory.createXYAreaChart( computeTitle(), getDomainTitle(), getRangeTitle(),
          xyDataset, orientation, isShowLegend(), false, false );
      }
    }

    configureLogarithmicAxis( chart.getXYPlot() );
    configureLineChart( chart.getXYPlot() );
    return chart;
  }

  protected void configureLineChart( final XYPlot plot ) {
    final XYDataset linesDataset = createLinesDataset();
    if ( linesDataset == null || linesDataset.getSeriesCount() == 0 ) {
      return;
    }

    //Create Axis Objects
    final ValueAxis linesAxis;
    if ( isSharedRangeAxis() ) {
      linesAxis = plot.getRangeAxis();
    } else if ( isThreeD() ) {
      linesAxis = new NumberAxis3D( getSecondValueAxisLabel() );
    } else {
      linesAxis = new NumberAxis( getSecondValueAxisLabel() );
    }

    final XYItemRenderer lineRenderer;
    if ( isThreeD() ) {
      lineRenderer = new XYLine3DRenderer();
    } else {
      lineRenderer = new XYLineAndShapeRenderer();
    }

    plot.setRenderer( 1, lineRenderer );
    plot.setDataset( 1, linesDataset );
    plot.setRangeAxis( 1, linesAxis );

    //map lines to second axis
    plot.mapDatasetToRangeAxis( 1, 1 );

    //set location of second axis
    plot.setRangeAxisLocation( 1, AxisLocation.BOTTOM_OR_RIGHT );
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final XYPlot plot = chart.getXYPlot();

    if ( isSharedRangeAxis() == false ) {
      final ValueAxis linesAxis = plot.getRangeAxis( 1 );
      if ( linesAxis instanceof NumberAxis ) {
        final NumberAxis numberAxis = (NumberAxis) linesAxis;
        numberAxis.setAutoRangeIncludesZero( isLineAxisIncludesZero() );
        numberAxis.setAutoRangeStickyZero( isLineAxisStickyZero() );

        if ( getLinePeriodCount() > 0 ) {
          if ( getLineTicksLabelFormat() != null ) {
            final FastDecimalFormat formatter = new FastDecimalFormat
              ( getLineTicksLabelFormat(), getResourceBundleFactory().getLocale() );
            numberAxis.setTickUnit( new FastNumberTickUnit( getLinePeriodCount(), formatter ) );
          } else {
            numberAxis.setTickUnit( new FastNumberTickUnit( getLinePeriodCount() ) );
          }
        } else {
          if ( getLineTicksLabelFormat() != null ) {
            final DecimalFormat formatter = new DecimalFormat
              ( getLineTicksLabelFormat(), new DecimalFormatSymbols( getResourceBundleFactory().getLocale() ) );
            numberAxis.setNumberFormatOverride( formatter );
          }
        }
      } else if ( linesAxis instanceof DateAxis ) {
        final DateAxis numberAxis = (DateAxis) linesAxis;

        if ( getLinePeriodCount() > 0 && getLineTimePeriod() != null ) {
          if ( getLineTicksLabelFormat() != null ) {
            final SimpleDateFormat formatter = new SimpleDateFormat
              ( getLineTicksLabelFormat(), new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
            numberAxis.setTickUnit
              ( new DateTickUnit( getDateUnitAsInt( getLineTimePeriod() ), (int) getLinePeriodCount(), formatter ) );
          } else {
            numberAxis.setTickUnit
              ( new DateTickUnit( getDateUnitAsInt( getLineTimePeriod() ), (int) getLinePeriodCount() ) );
          }
        } else if ( getRangeTickFormatString() != null ) {
          final SimpleDateFormat formatter = new SimpleDateFormat
            ( getRangeTickFormatString(), new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setDateFormatOverride( formatter );
        }
      }

      if ( linesAxis != null ) {
        final Font labelFont = Font.decode( getLabelFont() );
        linesAxis.setLabelFont( labelFont );
        linesAxis.setTickLabelFont( labelFont );

        if ( getLineTitleFont() != null ) {
          linesAxis.setLabelFont( getLineTitleFont() );
        }
        if ( getLineTickFont() != null ) {
          linesAxis.setTickLabelFont( getLineTickFont() );
        }
        final int level = getRuntime().getProcessingContext().getCompatibilityLevel();
        if ( ClassicEngineBoot.isEnforceCompatibilityFor( level, 3, 8 ) ) {
          final double lineRangeMinimumVal = lineRangeMinimum == null ? 0 : lineRangeMinimum;
          final double lineRangeMaximumVal = lineRangeMaximum == null ? 0 : lineRangeMaximum;
          if ( lineRangeMinimum != null ) {
            linesAxis.setLowerBound( getLineRangeMinimum() );
          }
          if ( lineRangeMaximum != null ) {
            linesAxis.setUpperBound( getRangeMaximum() );
          }
          if ( lineRangeMinimumVal == 0 && lineRangeMaximumVal == 1 ) {
            linesAxis.setLowerBound( 0 );
            linesAxis.setUpperBound( 1 );
            linesAxis.setAutoRange( true );
          }
        } else {
          if ( lineRangeMinimum != null ) {
            linesAxis.setLowerBound( lineRangeMinimum );
          }
          if ( lineRangeMaximum != null ) {
            linesAxis.setUpperBound( lineRangeMaximum );
          }
          linesAxis.setAutoRange( isLineAxisAutoRange() );
        }
      }
    }

    final XYLineAndShapeRenderer linesRenderer = (XYLineAndShapeRenderer) plot.getRenderer( 1 );
    if ( linesRenderer != null ) {
      //set stroke with line width
      linesRenderer.setStroke( translateLineStyle( getLineWidth(), getLineStyle() ) );
      //hide shapes on line
      linesRenderer.setShapesVisible( isMarkersVisible() );
      linesRenderer.setBaseShapesFilled( isMarkersVisible() );

      //set colors for each line
      for ( int i = 0; i < lineSeriesColor.size(); i++ ) {
        final String s = (String) lineSeriesColor.get( i );
        linesRenderer.setSeriesPaint( i, parseColorFromString( s ) );
      }
    }
  }

  public boolean isLineAxisAutoRange() {
    return lineAxisAutoRange;
  }

  public void setLineAxisAutoRange( final boolean lineAxisAutoRange ) {
    this.lineAxisAutoRange = lineAxisAutoRange;
  }

  protected int getDateUnitAsInt( final Class domainTimePeriod ) {
    if ( Second.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.SECOND;
    }
    if ( Minute.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.MINUTE;
    }
    if ( Hour.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.HOUR;
    }
    if ( Day.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.DAY;
    }
    if ( Month.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.MONTH;
    }
    if ( Year.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.YEAR;
    }
    if ( Second.class.equals( domainTimePeriod ) ) {
      return DateTickUnit.MILLISECOND;
    }
    return DateTickUnit.DAY;
  }
}
