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
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Year;
import org.jfree.data.xy.TableXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;
import org.pentaho.plugin.jfreereport.reportcharts.backport.ExtCategoryTableXYDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.ExtTimeTableXYDataset;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FastNumberTickUnit;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.LegacyUpdateHandler;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

/**
 * This class allows you to embed xy charts into JFreeReport XML definitions.
 *
 * @author gmoran
 */
public abstract class XYChartExpression extends AbstractChartExpression implements LegacyUpdateHandler {
  private String titlePositionText;
  private Paint borderPaint;
  private boolean horizontal;
  //  private Paint plotBackgroundPaint;
  private boolean stacked;

  private boolean domainVerticalTickLabels;
  private boolean domainIncludesZero;
  private boolean domainStickyZero;
  private NumberFormat domainTickFormat;
  private String domainTickFormatString;
  private String domainTitle;
  private Font domainTitleFont;
  private Font domainTickFont;
  private double domainMinimum;
  private double domainMaximum;
  private boolean domainAxisAutoRange;

  // used if the chart is using a DateAxis as Domain-Axis
  private Class domainTimePeriod;
  private double domainPeriodCount;

  private String rangeTitle;
  private Font rangeTitleFont;
  private Font rangeTickFont;
  private double rangeMinimum;
  private double rangeMaximum;
  private boolean rangeIncludesZero;
  private boolean rangeStickyZero;
  private boolean rangeAxisAutoRange;

  private NumberFormat rangeTickFormat;
  private String rangeTickFormatString;
  private boolean humanReadableLogarithmicFormat;
  private boolean logarithmicAxis;

  private Class rangeTimePeriod;
  private double rangePeriodCount;

  // private Font legendFont = null;

  protected XYChartExpression() {
    domainPeriodCount = 0;
    titlePositionText = "top";
    borderPaint = Color.BLACK;
    horizontal = false;
    stacked = false;
    domainVerticalTickLabels = false;
    domainIncludesZero = false;
    domainStickyZero = false;
    rangeIncludesZero = true;
    rangeStickyZero = true;
    domainTitle = null;
    domainTitleFont = TextTitle.DEFAULT_FONT;
    domainTickFont = null;
    domainTickFormat = null;
    domainMinimum = 0;
    domainMaximum = 1;
    domainAxisAutoRange = true;
    rangeTitle = null;
    rangeTitleFont = TextTitle.DEFAULT_FONT;
    rangeMinimum = 0;
    rangeMaximum = 1;
    rangeAxisAutoRange = true;
  }

  public boolean isDomainAxisAutoRange() {
    return domainAxisAutoRange;
  }

  public void setDomainAxisAutoRange( final boolean domainAxisAutoRange ) {
    this.domainAxisAutoRange = domainAxisAutoRange;
  }

  public boolean isRangeAxisAutoRange() {
    return rangeAxisAutoRange;
  }

  public void setRangeAxisAutoRange( final boolean rangeAxisAutoRange ) {
    this.rangeAxisAutoRange = rangeAxisAutoRange;
  }

  public Class getDomainTimePeriod() {
    return domainTimePeriod;
  }

  public void setDomainTimePeriod( final Class domainTimePeriod ) {
    this.domainTimePeriod = domainTimePeriod;
  }

  public Class getRangeTimePeriod() {
    return rangeTimePeriod;
  }

  public void setRangeTimePeriod( final Class rangeTimePeriod ) {
    this.rangeTimePeriod = rangeTimePeriod;
  }

  public boolean isHumanReadableLogarithmicFormat() {
    return humanReadableLogarithmicFormat;
  }

  public void setHumanReadableLogarithmicFormat( final boolean humanReadableLogarithmicFormat ) {
    this.humanReadableLogarithmicFormat = humanReadableLogarithmicFormat;
  }

  public boolean isLogarithmicAxis() {
    return logarithmicAxis;
  }

  public void setLogarithmicAxis( final boolean logarithmicAxis ) {
    this.logarithmicAxis = logarithmicAxis;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final XYChartExpression chartExpression = (XYChartExpression) super.getInstance();
    if ( chartExpression.domainTickFormat != null ) {
      chartExpression.domainTickFormat = (NumberFormat) chartExpression.domainTickFormat.clone();
    }
    if ( chartExpression.rangeTickFormat != null ) {
      chartExpression.rangeTickFormat = (NumberFormat) chartExpression.rangeTickFormat.clone();
    }
    return chartExpression;
  }

  /**
   * @return Returns the stacked.
   */
  public boolean isStacked() {
    return stacked;
  }

  /**
   * @param stacked The stacked to set.
   */
  public void setStacked( final boolean stacked ) {
    this.stacked = stacked;
  }

  /**
   * @return Returns the verticalTickLabels.
   */
  public boolean isDomainVerticalTickLabels() {
    return domainVerticalTickLabels;
  }

  /**
   * @param domainVerticalTickLabels The domainVerticalLabels to set.
   */
  public void setDomainVerticalTickLabels( final boolean domainVerticalTickLabels ) {
    this.domainVerticalTickLabels = domainVerticalTickLabels;
  }

  /**
   * @return Returns the domainIncludeZero.
   */
  public boolean isDomainIncludesZero() {
    return domainIncludesZero;
  }

  /**
   * @param domainIncludesZero The domainIncludesZero to set.
   */
  public void setDomainIncludesZero( final boolean domainIncludesZero ) {
    this.domainIncludesZero = domainIncludesZero;
  }

  /**
   * @return Returns the domainStickyZero.
   */
  public boolean isDomainStickyZero() {
    return domainStickyZero;
  }

  /**
   * @param domainStickyZero The domainStickyZero to set.
   */
  public void setDomainStickyZero( final boolean domainStickyZero ) {
    this.domainStickyZero = domainStickyZero;
  }

  /**
   * @return Returns the rangeIncludeZero.
   */
  public boolean isRangeIncludesZero() {
    return rangeIncludesZero;
  }

  /**
   * @param rangeIncludesZero The domainIncludesZero to set.
   */
  public void setRangeIncludesZero( final boolean rangeIncludesZero ) {
    this.rangeIncludesZero = rangeIncludesZero;
  }

  /**
   * @return Returns the rangeStickyZero.
   */
  public boolean isRangeStickyZero() {
    return rangeStickyZero;
  }

  /**
   * @param rangeStickyZero The rangeStickyZero to set.
   */
  public void setRangeStickyZero( final boolean rangeStickyZero ) {
    this.rangeStickyZero = rangeStickyZero;
  }

  public void setPlotBackgroundColor( final Color plotBackgroundPaint ) {
    if ( plotBackgroundPaint != null ) {
      super.setPlotBackgroundColor( plotBackgroundPaint );
    }
  }

  public Color getPlotBackgroundColor() {
    return super.getPlotBackgroundColor();
  }

  /**
   * @param plotBackgroundPaint
   * @deprecated this property is declared but not used anywhere
   */
  public void setPlotBackgroundPaint( final Paint plotBackgroundPaint ) {
    this.setPlotBackgroundColor( (Color) plotBackgroundPaint );
  }

  /**
   * @deprecated this property is declared but not used anywhere
   */
  public Paint getPlotBackgroundPaint() {
    return this.getPlotBackgroundColor();
  }

  public boolean isHorizontal() {
    return horizontal;
  }

  public void setHorizontal( final boolean value ) {
    horizontal = value;
  }

  /**
   * @return Returns the borderVisible.
   * @deprecated
   */
  public boolean isBorderVisible() {
    return isShowBorder();
  }

  /**
   * @param borderVisible The borderVisible to set.
   * @deprecated
   */
  public void setBorderVisible( final boolean borderVisible ) {
    setShowBorder( borderVisible );
  }

  /**
   * @return Returns the borderPaint.
   * @deprecated Is not used anywhere ...
   */
  public Paint getBorderPaint() {
    return borderPaint;
  }

  /**
   * @param borderPaint The borderPaint to set.
   * @deprecated is not used anywhere
   */
  public void setBorderPaint( final Paint borderPaint ) {
    this.borderPaint = borderPaint;
  }

  public String getTitlePositionText() {
    return titlePositionText;
  }

  public void setTitlePositionText( final String titlePositionText ) {
    this.titlePositionText = titlePositionText;
  }

  /**
   * @return Returns the titlePosition.
   * @deprecated Dont use that.
   */
  public RectangleEdge getTitlePosition() {
    return translateEdge( titlePositionText );
  }

  /**
   * @param titlePosition The titlePosition to set.
   * @deprecated Dont use that.
   */
  public void setTitlePosition( final RectangleEdge titlePosition ) {
    if ( RectangleEdge.TOP.equals( titlePosition ) ) {
      this.titlePositionText = "top";
    } else if ( RectangleEdge.LEFT.equals( titlePosition ) ) {
      this.titlePositionText = "left";
    } else if ( RectangleEdge.BOTTOM.equals( titlePosition ) ) {
      this.titlePositionText = "bottom";
    } else if ( RectangleEdge.RIGHT.equals( titlePosition ) ) {
      this.titlePositionText = "right";
    } else {
      this.titlePositionText = "left";
    }
  }


  /**
   * @return Returns the domainTitle.
   */
  public String getDomainTitle() {
    return domainTitle;
  }

  /**
   * @param domainTitle The domainTitle to set.
   */
  public void setDomainTitle( final String domainTitle ) {
    this.domainTitle = domainTitle;
  }

  /**
   * @return Returns the rangeTitle.
   */
  public String getRangeTitle() {
    return rangeTitle;
  }

  /**
   * @param rangeTitle The rangeTitle to set.
   */
  public void setRangeTitle( final String rangeTitle ) {
    this.rangeTitle = rangeTitle;
  }

  /**
   * @return Returns the domainTitleFont.
   */
  public Font getDomainTitleFont() {
    return domainTitleFont;
  }

  /**
   * @param domainTitleFont The domainTitleFont to set.
   */
  public void setDomainTitleFont( final Font domainTitleFont ) {
    this.domainTitleFont = domainTitleFont;
  }

  /**
   * Return the java.awt.Font to be used to display the range axis tick labels
   *
   * @return Font The Font for the range axis tick labels
   */
  public Font getDomainTickFont() {
    return domainTickFont;
  }

  /**
   * @param domainTickFont The domainTickFont to set.
   */
  public void setDomainTickFont( final Font domainTickFont ) {
    this.domainTickFont = domainTickFont;
  }

  /**
   * @return Returns the rangeTickFormat.
   */
  public NumberFormat getDomainTickFormat() {
    return domainTickFormat;
  }

  /**
   * @param domainTickFormat The range tick number format to set.
   */
  public void setDomainTickFormat( final NumberFormat domainTickFormat ) {
    this.domainTickFormat = domainTickFormat;
  }

  /**
   * @return Returns the rangeTitleFont.
   */
  public Font getRangeTitleFont() {
    return rangeTitleFont;
  }

  /**
   * @param rangeTitleFont The rangeTitleFont to set.
   */
  public void setRangeTitleFont( final Font rangeTitleFont ) {
    this.rangeTitleFont = rangeTitleFont;
  }

  /**
   * @return Returns the rangeTickFormat.
   */
  public NumberFormat getRangeTickFormat() {
    return rangeTickFormat;
  }

  /**
   * @param rangeTickFormat The range tick number format to set.
   */
  public void setRangeTickFormat( final NumberFormat rangeTickFormat ) {
    this.rangeTickFormat = rangeTickFormat;
  }

  public String getDomainTickFormatString() {
    return domainTickFormatString;
  }

  public void setDomainTickFormatString( final String domainTickFormatString ) {
    this.domainTickFormatString = domainTickFormatString;
  }

  public String getRangeTickFormatString() {
    return rangeTickFormatString;
  }

  public void setRangeTickFormatString( final String rangeTickFormatString ) {
    this.rangeTickFormatString = rangeTickFormatString;
  }

  /**
   * Return the java.awt.Font to be used to display the range axis tick labels
   *
   * @return Font The Font for the range axis tick labels
   */
  public Font getRangeTickFont() {
    return rangeTickFont;
  }

  /**
   * @param rangeTickFont The rangeTitleFont to set.
   */
  public void setRangeTickFont( final Font rangeTickFont ) {
    this.rangeTickFont = rangeTickFont;
  }

  /**
   * Return the range axis' minimum value
   *
   * @return double Range axis' minimum value
   */
  public double getRangeMinimum() {
    return rangeMinimum;
  }

  /**
   * @param rangeMinimum Set the minimum value of the range axis.
   */
  public void setRangeMinimum( final double rangeMinimum ) {
    this.rangeMinimum = rangeMinimum;
  }

  /**
   * Return the range axis' maximum value
   *
   * @return double Range axis' maximum value
   */
  public double getRangeMaximum() {
    return rangeMaximum;
  }

  /**
   * @param rangeMaximum Set the maximum value of the range axis.
   */
  public void setRangeMaximum( final double rangeMaximum ) {
    this.rangeMaximum = rangeMaximum;
  }

  /**
   * Return the domain axis' minimum value
   *
   * @return double domain axis' minimum value
   */
  public double getDomainMinimum() {
    return domainMinimum;
  }

  /**
   * @param domainMinimum Set the minimum value of the domain axis.
   */
  public void setDomainMinimum( final double domainMinimum ) {
    this.domainMinimum = domainMinimum;
  }

  /**
   * Return the domain axis' maximum value
   *
   * @return double domain axis' maximum value
   */
  public double getDomainMaximum() {
    return domainMaximum;
  }

  /**
   * @param domainMaximum Set the maximum value of the domain axis.
   */
  public void setDomainMaximum( final double domainMaximum ) {
    this.domainMaximum = domainMaximum;
  }

  protected JFreeChart computeChart( final Dataset dataset ) {
    if ( dataset instanceof XYDataset == false ) {
      return computeXYChart( null );
    }

    final XYDataset xyDataset = (XYDataset) dataset;
    return computeXYChart( xyDataset );
  }

  protected void configureLogarithmicAxis( final XYPlot plot ) {
    if ( isLogarithmicAxis() ) {
      final LogarithmicAxis logarithmicAxis;
      if ( isHumanReadableLogarithmicFormat() ) {
        plot.getRenderer().setBaseItemLabelGenerator( new LogXYItemLabelGenerator() );
        logarithmicAxis = new ScalingLogarithmicAxis( getRangeTitle() );
        logarithmicAxis.setStrictValuesFlag( false );
      } else {
        logarithmicAxis = new LogarithmicAxis( getRangeTitle() );
        logarithmicAxis.setStrictValuesFlag( false );
      }

      plot.setRangeAxis( logarithmicAxis );
    }
  }

  protected JFreeChart computeXYChart( final XYDataset xyDataset ) {
    return getChart( xyDataset );
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final XYPlot plot = chart.getXYPlot();
    final XYItemRenderer renderer = plot.getRenderer();

    if ( StringUtils.isEmpty( getTooltipFormula() ) == false ) {
      renderer.setBaseToolTipGenerator( new FormulaXYZTooltipGenerator( getRuntime(), getTooltipFormula() ) );
    }
    if ( StringUtils.isEmpty( getUrlFormula() ) == false ) {
      renderer.setURLGenerator( new FormulaXYZURLGenerator( getRuntime(), getUrlFormula() ) );
    }

    renderer.setBaseItemLabelGenerator( new StandardXYItemLabelGenerator() );
    renderer.setBaseItemLabelsVisible( Boolean.TRUE.equals( getItemsLabelVisible() ) );
    if ( getItemLabelFont() != null ) {
      renderer.setBaseItemLabelFont( getItemLabelFont() );
    }

    plot.setOrientation( computePlotOrientation() );

    // May be an axis that supports dates
    final ValueAxis domainAxis = plot.getDomainAxis();
    if ( domainAxis instanceof NumberAxis ) {
      final NumberAxis numberAxis = (NumberAxis) domainAxis;
      numberAxis.setAutoRangeIncludesZero( isDomainIncludesZero() );
      numberAxis.setAutoRangeStickyZero( isDomainStickyZero() );
      if ( getDomainPeriodCount() > 0 ) {
        if ( getDomainTickFormat() != null ) {
          numberAxis.setTickUnit( new NumberTickUnit( getDomainPeriodCount(), getDomainTickFormat() ) );
        } else if ( getDomainTickFormatString() != null ) {
          final FastDecimalFormat formatter = new FastDecimalFormat( getDomainTickFormatString(),
              getResourceBundleFactory().getLocale() );
          numberAxis.setTickUnit( new FastNumberTickUnit( getDomainPeriodCount(), formatter ) );
        } else {
          numberAxis.setTickUnit( new FastNumberTickUnit( getDomainPeriodCount() ) );
        }
      } else {
        if ( getDomainTickFormat() != null ) {
          numberAxis.setNumberFormatOverride( getDomainTickFormat() );
        } else if ( getDomainTickFormatString() != null ) {
          final DecimalFormat formatter = new DecimalFormat( getDomainTickFormatString(),
              new DecimalFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setNumberFormatOverride( formatter );
        }
      }
    } else if ( domainAxis instanceof DateAxis ) {
      final DateAxis numberAxis = (DateAxis) domainAxis;

      if ( getDomainPeriodCount() > 0 && getDomainTimePeriod() != null ) {
        if ( getDomainTickFormatString() != null ) {
          final SimpleDateFormat formatter = new SimpleDateFormat( getDomainTickFormatString(),
              new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setTickUnit( new DateTickUnit( getDateUnitAsInt( getDomainTimePeriod() ),
              (int) getDomainPeriodCount(), formatter ) );
        } else {
          numberAxis.setTickUnit( new DateTickUnit( getDateUnitAsInt( getDomainTimePeriod() ),
              (int) getDomainPeriodCount() ) );
        }
      }
    }

    if ( domainAxis != null ) {
      domainAxis.setLabel( getDomainTitle() );
      if ( getDomainTitleFont() != null ) {
        domainAxis.setLabelFont( getDomainTitleFont() );
      }
      domainAxis.setVerticalTickLabels( isDomainVerticalTickLabels() );
      if ( getDomainTickFont() != null ) {
        domainAxis.setTickLabelFont( getDomainTickFont() );
      }
      final int level = getRuntime().getProcessingContext().getCompatibilityLevel();
      if ( ClassicEngineBoot.isEnforceCompatibilityFor( level, 3, 8 ) ) {
        if ( getDomainMinimum() != 0 ) {
          domainAxis.setLowerBound( getDomainMinimum() );
        }
        if ( getDomainMaximum() != 1 ) {
          domainAxis.setUpperBound( getDomainMaximum() );
        }
        if ( getDomainMinimum() == 0 && getDomainMaximum() == 0 ) {
          domainAxis.setLowerBound( 0 );
          domainAxis.setUpperBound( 1 );
          domainAxis.setAutoRange( true );
        }
      } else {
        domainAxis.setLowerBound( getDomainMinimum() );
        domainAxis.setUpperBound( getDomainMaximum() );
        domainAxis.setAutoRange( isDomainAxisAutoRange() );
      }
    }

    final ValueAxis rangeAxis = plot.getRangeAxis();
    if ( rangeAxis instanceof NumberAxis ) {
      final NumberAxis numberAxis = (NumberAxis) rangeAxis;
      numberAxis.setAutoRangeIncludesZero( isRangeIncludesZero() );
      numberAxis.setAutoRangeStickyZero( isRangeStickyZero() );

      if ( getRangePeriodCount() > 0 ) {
        if ( getRangeTickFormat() != null ) {
          numberAxis.setTickUnit( new NumberTickUnit( getRangePeriodCount(), getRangeTickFormat() ) );
        } else if ( getRangeTickFormatString() != null ) {
          final FastDecimalFormat formatter = new FastDecimalFormat( getRangeTickFormatString(),
              getResourceBundleFactory().getLocale() );
          numberAxis.setTickUnit( new FastNumberTickUnit( getRangePeriodCount(), formatter ) );
        } else {
          numberAxis.setTickUnit( new FastNumberTickUnit( getRangePeriodCount() ) );
        }
      } else {
        if ( getRangeTickFormat() != null ) {
          numberAxis.setNumberFormatOverride( getRangeTickFormat() );
        } else if ( getRangeTickFormatString() != null ) {
          final DecimalFormat formatter = new DecimalFormat( getRangeTickFormatString(),
              new DecimalFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setNumberFormatOverride( formatter );
          standardTickUnitsApplyFormat( numberAxis, formatter );
        }
      }
    } else if ( rangeAxis instanceof DateAxis ) {
      final DateAxis numberAxis = (DateAxis) rangeAxis;

      if ( getRangePeriodCount() > 0 && getRangeTimePeriod() != null ) {
        if ( getRangeTickFormatString() != null ) {
          final SimpleDateFormat formatter = new SimpleDateFormat( getRangeTickFormatString(),
              new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setTickUnit( new DateTickUnit( getDateUnitAsInt( getRangeTimePeriod() ),
              (int) getRangePeriodCount(), formatter ) );
        } else {
          numberAxis.setTickUnit( new DateTickUnit( getDateUnitAsInt( getRangeTimePeriod() ),
              (int) getRangePeriodCount() ) );
        }
      } else {
        if ( getRangeTickFormatString() != null ) {
          final SimpleDateFormat formatter = new SimpleDateFormat( getRangeTickFormatString(),
              new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setDateFormatOverride( formatter );
        }
      }
    }

    if ( rangeAxis != null ) {
      rangeAxis.setLabel( getRangeTitle() );
      if ( getRangeTitleFont() != null ) {
        rangeAxis.setLabelFont( getRangeTitleFont() );
      }
      if ( getRangeTickFont() != null ) {
        rangeAxis.setTickLabelFont( getRangeTickFont() );
      }
      final int level = getRuntime().getProcessingContext().getCompatibilityLevel();
      if ( ClassicEngineBoot.isEnforceCompatibilityFor( level, 3, 8 ) ) {
        if ( getRangeMinimum() != 0 ) {
          rangeAxis.setLowerBound( getRangeMinimum() );
        }
        if ( getRangeMaximum() != 1 ) {
          rangeAxis.setUpperBound( getRangeMaximum() );
        }
        if ( getRangeMinimum() == 0 && getRangeMaximum() == 0 ) {
          rangeAxis.setLowerBound( 0 );
          rangeAxis.setUpperBound( 1 );
          rangeAxis.setAutoRange( true );
        }
      } else {
        rangeAxis.setLowerBound( getRangeMinimum() );
        rangeAxis.setUpperBound( getRangeMaximum() );
        rangeAxis.setAutoRange( isRangeAxisAutoRange() );
      }
    }

    final String[] colors = getSeriesColor();
    for ( int i = 0; i < colors.length; i++ ) {
      renderer.setSeriesPaint( i, parseColorFromString( colors[i] ) );
    }
  }

  protected PlotOrientation computePlotOrientation() {
    final PlotOrientation orientation;
    if ( isHorizontal() ) {
      orientation = PlotOrientation.HORIZONTAL;
    } else {
      orientation = PlotOrientation.VERTICAL;
    }
    return orientation;
  }

  //--------------------------------------------------

  /**
   * @return Returns the subTitles.
   * @deprecated Subtitles are not used.
   */
  public List getSubtitles() {
    return Collections.emptyList();
  }

  /**
   * @return Returns the subTitles.
   * @deprecated Subtitles are not used.
   */
  public void addSubTitle( final String subTitle ) {
  }

  /**
   * @param xyDataset
   * @return
   * @deprecated Not public, no getter
   */
  public JFreeChart getChart( final XYDataset xyDataset ) {
    return null;
  }

  /**
   * @return
   * @deprecated Not used anywhere. You might want to use "itemLabelVisible"
   */
  public boolean isDisplayLabels() {
    return false;
  }

  public double getDomainPeriodCount() {
    return domainPeriodCount;
  }

  public void setDomainPeriodCount( final double domainPeriodCount ) {
    this.domainPeriodCount = domainPeriodCount;
  }

  public double getRangePeriodCount() {
    return rangePeriodCount;
  }

  public void setRangePeriodCount( final double rangePeriodCount ) {
    this.rangePeriodCount = rangePeriodCount;
  }

  private int getDateUnitAsInt( final Class domainTimePeriod ) {
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

  protected ExtTimeTableXYDataset convertToTable( final XYDataset xyDataset ) {
    final TimeSeriesCollection timeSeriesCollection = (TimeSeriesCollection) xyDataset;
    final ExtTimeTableXYDataset tableXYDataset = new ExtTimeTableXYDataset();
    final int count = timeSeriesCollection.getSeriesCount();
    for ( int i = 0; i < count; i++ ) {
      final Comparable key = timeSeriesCollection.getSeriesKey( i );
      final TimeSeries timeSeries = timeSeriesCollection.getSeries( i );
      final int itemCount = timeSeries.getItemCount();
      for ( int ic = 0; ic < itemCount; ic++ ) {
        final TimeSeriesDataItem seriesDataItem = timeSeries.getDataItem( ic );
        tableXYDataset.add( seriesDataItem.getPeriod(), seriesDataItem.getValue(), key, false );
      }
    }
    return tableXYDataset;
  }

  protected TableXYDataset convertToTable( final XYSeriesCollection xyDataset ) {
    final ExtCategoryTableXYDataset tableXYDataset = new ExtCategoryTableXYDataset();
    final int count = xyDataset.getSeriesCount();
    for ( int i = 0; i < count; i++ ) {
      final XYSeries timeSeries = xyDataset.getSeries( i );
      final Comparable key = timeSeries.getKey();
      final int itemCount = timeSeries.getItemCount();
      for ( int ic = 0; ic < itemCount; ic++ ) {
        final XYDataItem seriesDataItem = timeSeries.getDataItem( ic );
        tableXYDataset.add( seriesDataItem.getX(), seriesDataItem.getY(), key, false );
      }
    }
    return tableXYDataset;
  }

  public void reconfigureForCompatibility( final int versionTag ) {
    if ( ClassicEngineBoot.isEnforceCompatibilityFor( versionTag, 3, 8 ) ) {
      setRangeAxisAutoRange( getRangeMinimum() == 0 && getRangeMaximum() == 1 );
      setDomainAxisAutoRange( getDomainMinimum() == 0 && getDomainMaximum() == 1 );
    }
  }
}
