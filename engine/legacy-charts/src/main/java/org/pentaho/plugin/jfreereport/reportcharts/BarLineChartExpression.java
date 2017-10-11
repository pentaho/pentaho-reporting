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

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberAxis3D;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.LineRenderer3D;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
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

public class BarLineChartExpression extends BarChartExpression implements MultiPlotChartExpression {
  private static final long serialVersionUID = 7082583397390897215L;

  private String linesDataSource;
  private String secondValueAxisLabel; //$NON-NLS-1$
  private ArrayList<String> lineSeriesColor;

  private String linesLabelFont; //$NON-NLS-1$

  @Deprecated
  private String linesTickLabelFont; //$NON-NLS-1$
  private String lineTicksLabelFormat; //$NON-NLS-1$

  private String lineStyle; //$NON-NLS-1$
  private float lineWidth;

  private boolean markersVisible;
  private boolean sharedRangeAxis;

  private double linePeriodCount;
  private Class lineTimePeriod;
  private Font lineTitleFont;
  private Font lineTickFont;
  private double lineRangeMinimum;
  private double lineRangeMaximum;
  private boolean lineAxisAutoRange;
  private boolean lineAxisIncludesZero;
  private boolean lineAxisStickyZero;

  //constructor
  public BarLineChartExpression() {
    lineSeriesColor = new ArrayList<String>();
    secondValueAxisLabel = "";
    linesLabelFont = "SansSerif--8";
    linesTickLabelFont = "SansSerif--8";
    lineWidth = 1.0f;
    markersVisible = false;
    linePeriodCount = 0;

    lineRangeMinimum = 0;
    lineRangeMaximum = 1;
    lineAxisAutoRange = true;
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

  public double getLineRangeMinimum() {
    return lineRangeMinimum;
  }

  public void setLineRangeMinimum( final double lineRangeMinimum ) {
    this.lineRangeMinimum = lineRangeMinimum;
  }

  public double getLineRangeMaximum() {
    return lineRangeMaximum;
  }

  public void setLineRangeMaximum( final double lineRangeMaximum ) {
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
    final BarLineChartExpression chartExpression = (BarLineChartExpression) super.getInstance();
    chartExpression.lineSeriesColor = (ArrayList<String>) lineSeriesColor.clone();
    return chartExpression;
  }

  public String getLinesDataSource() {
    return linesDataSource;
  }

  public void setLinesDataSource( final String linesDataSource ) {
    this.linesDataSource = linesDataSource;
  }

  public String getSecondaryDataSet() {
    return getLinesDataSource();
  }

  public void setSecondaryDataSet( final String dataset ) {
    setLinesDataSource( dataset );
  }

  public String getLinesLabelFont() {
    return linesLabelFont;
  }

  public void setLinesLabelFont( final String linesLabelFont ) {
    this.linesLabelFont = linesLabelFont;
  }

  @Deprecated
  public String getLinesTickLabelFont() {
    return linesTickLabelFont;
  }

  @Deprecated
  public void setLinesTickLabelFont( final String linesTickLabelFont ) {
    this.linesTickLabelFont = linesTickLabelFont;
  }

  public String getSecondValueAxisLabel() {
    return secondValueAxisLabel;
  }

  public void setSecondValueAxisLabel( final String secondValueAxisLabel ) {
    this.secondValueAxisLabel = secondValueAxisLabel;
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

  /**
   * @return returns the style set for the lines
   */
  public String getLineStyle() {
    return lineStyle;
  }

  /**
   * @param value set the style for all line series
   */
  public void setLineStyle( final String value ) {
    lineStyle = value;
  }

  /**
   * @return the width of all line series Valid values are float numbers zero or greater
   */
  public float getLineWidth() {
    return lineWidth;
  }

  /**
   * @param value set the width of all line series Valid values are float numbers zero or greater
   */
  public void setLineWidth( final float value ) {
    lineWidth = value;
  }

  /**
   * @return boolean whether the markers (data points) for all series are displayed
   */
  public boolean isMarkersVisible() {
    return markersVisible;
  }

  /**
   * @param markersVisible set whether the markers (data points) for all series should be displayed
   */
  public void setMarkersVisible( final boolean markersVisible ) {
    this.markersVisible = markersVisible;
  }

  /**
   * @deprecated
   */
  public String getBarsTickLabelFont() {
    return convertFontToString( getRangeTickFont() );
  }

  /**
   * @deprecated
   */
  public void setBarsTickLabelFont( final String barsTickLabelFont ) {
    setRangeTickFont( Font.decode( barsTickLabelFont ) );
  }

  /**
   * @deprecated
   */
  public String getCategoryTickLabelFont() {
    return getLabelFont();
  }

  /**
   * @deprecated
   */
  public void setCategoryTickLabelFont( final String categoryTickLabelFont ) {
    this.setLabelFont( categoryTickLabelFont );
  }

  /**
   * @return
   * @deprecated duplicate property.
   */
  public String getBarTicksLabelFormat() {
    return getRangeTickFormatString();
  }

  /**
   * @param lineTicksLabelDateFormat
   * @deprecated duplicate property.
   */
  public void setBarTicksLabelFormat( final String lineTicksLabelDateFormat ) {
    setRangeTickFormatString( lineTicksLabelDateFormat );
  }

  /**
   * @deprecated
   */
  public String getBarsLabelFont() {
    return convertFontToString( getRangeTitleFont() );
  }

  /**
   * @deprecated
   */
  public void setBarsLabelFont( final String barsLabelFont ) {
    setRangeTitleFont( Font.decode( barsLabelFont ) );
  }

  public boolean isLineAxisAutoRange() {
    return lineAxisAutoRange;
  }

  public void setLineAxisAutoRange( final boolean lineAxisAutoRange ) {
    this.lineAxisAutoRange = lineAxisAutoRange;
  }

  public JFreeChart computeCategoryChart( final CategoryDataset barsDataset ) {
    final JFreeChart chart = super.computeCategoryChart( barsDataset );
    final CategoryDataset linesDataset = createLinesDataset();

    //Create the renderer with the barchart, use a different bar renderer depending
    //if 3D chart or not
    final CategoryPlot plot = chart.getCategoryPlot();
    final CategoryItemRenderer lineRenderer;
    if ( isThreeD() ) {
      lineRenderer = new LineRenderer3D();
    } else {
      lineRenderer = new LineAndShapeRenderer();
    }

    //add lines dataset and axis to plot
    if ( linesDataset != null ) {

      //Create Axis Objects
      final ValueAxis linesAxis;
      if ( isSharedRangeAxis() ) {
        linesAxis = plot.getRangeAxis();
      } else if ( isThreeD() ) {
        linesAxis = new NumberAxis3D( getSecondValueAxisLabel() );
      } else {
        linesAxis = new NumberAxis( getSecondValueAxisLabel() );
      }

      plot.setRenderer( 1, lineRenderer );
      plot.setDataset( 1, linesDataset );
      plot.setRangeAxis( 1, linesAxis );

      //map lines to second axis
      plot.mapDatasetToRangeAxis( 1, 1 );

      //set location of second axis
      plot.setRangeAxisLocation( 1, AxisLocation.BOTTOM_OR_RIGHT );
    }

    //set rendering order
    plot.setDatasetRenderingOrder( DatasetRenderingOrder.FORWARD );

    return chart;
  }

  private CategoryDataset createLinesDataset() {
    final Object maybeCollector = getDataRow().get( getLinesDataSource() );
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


    final CategoryDataset linesDataset;
    if ( dataset instanceof CategoryDataset ) {
      linesDataset = (CategoryDataset) dataset;
    } else {
      linesDataset = null;
    }
    return linesDataset;
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final CategoryPlot plot = chart.getCategoryPlot();

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
          if ( getRangeMinimum() != 0 ) {
            linesAxis.setLowerBound( getLineRangeMinimum() );
          }
          if ( getRangeMaximum() != 1 ) {
            linesAxis.setUpperBound( getLineRangeMaximum() );
          }
          if ( getLineRangeMinimum() == 0 && getLineRangeMaximum() == 1 ) {
            linesAxis.setLowerBound( 0 );
            linesAxis.setUpperBound( 1 );
            linesAxis.setAutoRange( true );
          }
        } else {
          linesAxis.setLowerBound( getLineRangeMinimum() );
          linesAxis.setUpperBound( getLineRangeMaximum() );
          linesAxis.setAutoRange( isLineAxisAutoRange() );
        }
      }
    }

    final LineAndShapeRenderer linesRenderer = (LineAndShapeRenderer) plot.getRenderer( 1 );
    if ( linesRenderer != null ) {
      //set stroke with line width
      linesRenderer.setStroke( translateLineStyle( lineWidth, lineStyle ) );
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

  private String convertFontToString( final Font font ) {
    if ( font == null ) {
      return null;
    }

    final String fontName = font.getFamily();
    final int fontSize = font.getSize();
    final int fontStyle = font.getStyle();
    final String fontStyleText;
    if ( ( fontStyle & ( Font.BOLD | Font.ITALIC ) ) == ( Font.BOLD | Font.ITALIC ) ) {
      fontStyleText = "BOLDITALIC";
    } else if ( ( fontStyle & Font.BOLD ) == Font.BOLD ) {
      fontStyleText = "BOLD";
    } else if ( ( fontStyle & Font.ITALIC ) == Font.ITALIC ) {
      fontStyleText = "ITALIC";
    } else {
      fontStyleText = "PLAIN";
    }
    return ( fontName + "-" + fontStyleText + "-" + fontSize );
  }

  public void reconfigureForCompatibility( final int versionTag ) {
    super.reconfigureForCompatibility( versionTag );

    if ( ClassicEngineBoot.isEnforceCompatibilityFor( versionTag, 3, 8 ) ) {
      setLineAxisAutoRange( getLineRangeMinimum() == 0 && getLineRangeMaximum() == 1 );
    }
  }
}
