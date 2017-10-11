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
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPosition;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.DateTickUnit;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.Hour;
import org.jfree.data.time.Minute;
import org.jfree.data.time.Month;
import org.jfree.data.time.Second;
import org.jfree.data.time.Year;
import org.jfree.text.TextBlockAnchor;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.TextAnchor;
import org.pentaho.plugin.jfreereport.reportcharts.backport.FastNumberTickUnit;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.LegacyUpdateHandler;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.awt.Font;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;


/**
 * This class allows you to embed categorical charts into JFreeReport XML definitions.
 *
 * @author mbatchel
 * @noinspection UnusedDeclaration
 */
public abstract class CategoricalChartExpression extends AbstractChartExpression implements LegacyUpdateHandler {
  private static final long serialVersionUID = -402500824047401239L;

  private static final double DEFAULT_SCALE_FACTOR = 1.0;

  private String valueAxisLabel;
  private String categoryAxisLabel;
  private boolean horizontal;
  private boolean showGridlines;
  private Double labelRotation;
  private Float maxCategoryLabelWidthRatio;

  private Font categoryTitleFont;
  private Font categoryTickFont;
  private String categoricalLabelFormat;
  private String categoricalLabelDecimalFormat;
  private String categoricalLabelDateFormat;
  private Double categoricalItemLabelRotation;
  private boolean humanReadableLogarithmicFormat;
  private boolean logarithmicAxis;
  private String categoricalAxisMessageFormat;

  private Font rangeTitleFont;
  private Font rangeTickFont;
  private double rangeMinimum;
  private double rangeMaximum;
  private boolean rangeIncludesZero;
  private boolean rangeStickyZero;
  private NumberFormat rangeTickFormat;
  private String rangeTickFormatString;
  private Class rangeTimePeriod;
  private double rangePeriodCount;
  private boolean autoRange;
  private double scaleFactor;

  private Double lowerMargin;
  private Double upperMargin;
  private Double categoryMargin;

  protected CategoricalChartExpression() {
    categoricalAxisMessageFormat = "{0}";
    categoricalLabelFormat = "{2}";
    rangeMaximum = 1;
    rangeMinimum = 0;
    showGridlines = true;
    rangePeriodCount = 0;
    autoRange = true;
    scaleFactor = DEFAULT_SCALE_FACTOR;
  }

  public Font getCategoryTitleFont() {
    return categoryTitleFont;
  }

  public void setCategoryTitleFont( final Font categoryTitleFont ) {
    this.categoryTitleFont = categoryTitleFont;
  }

  public Font getCategoryTickFont() {
    return categoryTickFont;
  }

  public void setCategoryTickFont( final Font categoryTickFont ) {
    this.categoryTickFont = categoryTickFont;
  }

  public String getRangeTickFormatString() {
    return rangeTickFormatString;
  }

  public void setRangeTickFormatString( final String rangeTickFormatString ) {
    this.rangeTickFormatString = rangeTickFormatString;
  }

  public String getCategoricalAxisMessageFormat() {
    return categoricalAxisMessageFormat;
  }

  public void setCategoricalAxisMessageFormat( final String categoricalAxisMessageFormat ) {
    this.categoricalAxisMessageFormat = categoricalAxisMessageFormat;
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

  public boolean isLogarithmicAxis() {
    return logarithmicAxis;
  }

  public void setLogarithmicAxis( final boolean logarithmicAxis ) {
    this.logarithmicAxis = logarithmicAxis;
  }

  public boolean isHumanReadableLogarithmicFormat() {
    return humanReadableLogarithmicFormat;
  }

  public void setHumanReadableLogarithmicFormat( final boolean humanReadableLogarithmicFormat ) {
    this.humanReadableLogarithmicFormat = humanReadableLogarithmicFormat;
  }

  public Double getLowerMargin() {
    return lowerMargin;
  }

  public void setLowerMargin( final Double lowerMargin ) {
    this.lowerMargin = lowerMargin;
  }

  public Double getUpperMargin() {
    return upperMargin;
  }

  public void setUpperMargin( final Double upperMargin ) {
    this.upperMargin = upperMargin;
  }

  public Double getCategoryMargin() {
    return categoryMargin;
  }

  public void setCategoryMargin( final Double categoryMargin ) {
    this.categoryMargin = categoryMargin;
  }

  public Double getLabelRotationDeg() {
    if ( labelRotation == null ) {
      return null;
    } else {
      return new Double( StrictMath.toDegrees( labelRotation.doubleValue() ) );
    }
  }

  public void setLabelRotationDeg( final Double value ) {
    if ( value == null ) {
      labelRotation = null;
    } else {
      labelRotation = new Double( StrictMath.toRadians( value.doubleValue() ) );
    }
  }

  public Double getLabelRotation() {
    return labelRotation;
  }

  public void setLabelRotation( final Double value ) {
    labelRotation = value;
  }

  public Double getCategoricalItemLabelRotationDeg() {
    if ( categoricalItemLabelRotation == null ) {
      return null;
    } else {
      return new Double( StrictMath.toDegrees( categoricalItemLabelRotation.doubleValue() ) );
    }
  }

  public void setCategoricalItemLabelRotationDeg( final Double value ) {
    if ( value == null ) {
      categoricalItemLabelRotation = null;
    } else {
      categoricalItemLabelRotation = new Double( StrictMath.toRadians( value.doubleValue() ) );
    }
  }

  public Double getCategoricalItemLabelRotation() {
    return this.categoricalItemLabelRotation;
  }

  public void setCategoricalItemLabelRotation( final Double value ) {
    this.categoricalItemLabelRotation = value;
  }

  public void setMaxCategoryLabelWidthRatio( final Float value ) {
    maxCategoryLabelWidthRatio = value;
  }

  public Float getMaxCategoryLabelWidthRatio() {
    return maxCategoryLabelWidthRatio;
  }

  public boolean isShowGridlines() {
    return showGridlines;
  }

  public void setShowGridlines( final boolean value ) {
    showGridlines = value;
  }

  public boolean isHorizontal() {
    return horizontal;
  }

  public void setHorizontal( final boolean value ) {
    horizontal = value;
  }

  public String getValueAxisLabel() {
    return valueAxisLabel;
  }

  public void setValueAxisLabel( final String valueAxisLabel ) {
    this.valueAxisLabel = valueAxisLabel;
  }

  public String getCategoryAxisLabel() {
    return categoryAxisLabel;
  }

  public void setCategoryAxisLabel( final String categoryAxisLabel ) {
    this.categoryAxisLabel = categoryAxisLabel;
  }

  public void setCategoricalLabelFormat( final String value ) {
    this.categoricalLabelFormat = value;
  }

  public String getCategoricalLabelFormat() {
    return this.categoricalLabelFormat;
  }

  public void setCategoricalLabelDecimalFormat( final String value ) {
    this.categoricalLabelDecimalFormat = value;
  }

  public String getCategoricalLabelDecimalFormat() {
    return this.categoricalLabelDecimalFormat;
  }

  public void setCategoricalLabelDateFormat( final String value ) {
    this.categoricalLabelDateFormat = value;
  }

  public String getCategoricalLabelDateFormat() {
    return this.categoricalLabelDateFormat;
  }

  public boolean isAutoRange() {
    return autoRange;
  }

  public void setAutoRange( final boolean autoRange ) {
    this.autoRange = autoRange;
  }

  public double getScaleFactor() {
    return scaleFactor;
  }

  public void setScaleFactor( final double scaleFactor ) {
    this.scaleFactor = scaleFactor;
  }

  protected JFreeChart computeChart( final Dataset dataset ) {
    if ( dataset instanceof CategoryDataset == false ) {
      return computeCategoryChart( null );
    }

    final CategoryDataset categoryDataset = (CategoryDataset) dataset;
    return computeCategoryChart( categoryDataset );

  }

  protected JFreeChart computeCategoryChart( final CategoryDataset dataset ) {
    return getChart( dataset );
  }

  /**
   * @param categoryDataset the dataset.
   * @return the generated chart. This implementation returns null.
   * @deprecated should not be public and should not be a getter. In fact. it will be removed in PRD-4.0
   */
  public JFreeChart getChart( final CategoryDataset categoryDataset ) {
    return null;
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

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final CategoryPlot cpl = chart.getCategoryPlot();
    final CategoryItemRenderer renderer = cpl.getRenderer();
    if ( StringUtils.isEmpty( getTooltipFormula() ) == false ) {
      renderer.setBaseToolTipGenerator( new FormulaCategoryTooltipGenerator( getRuntime(), getTooltipFormula() ) );
    }
    if ( StringUtils.isEmpty( getUrlFormula() ) == false ) {
      renderer.setBaseItemURLGenerator( new FormulaCategoryURLGenerator( getRuntime(), getUrlFormula() ) );
    }
    if ( this.categoricalLabelFormat != null ) {
      final StandardCategoryItemLabelGenerator scilg;
      if ( categoricalLabelDecimalFormat != null ) {
        final DecimalFormat numFormat = new DecimalFormat( categoricalLabelDecimalFormat,
            new DecimalFormatSymbols( getRuntime().getResourceBundleFactory().getLocale() ) );
        numFormat.setRoundingMode( RoundingMode.HALF_UP );
        scilg = new StandardCategoryItemLabelGenerator( categoricalLabelFormat, numFormat );
      } else if ( categoricalLabelDateFormat != null ) {
        scilg = new StandardCategoryItemLabelGenerator( categoricalLabelFormat,
            new SimpleDateFormat( categoricalLabelDateFormat, getRuntime().getResourceBundleFactory().getLocale() ) );
      } else {
        final DecimalFormat formatter = new DecimalFormat();
        formatter.setDecimalFormatSymbols(
            new DecimalFormatSymbols( getRuntime().getResourceBundleFactory().getLocale() ) );
        scilg = new StandardCategoryItemLabelGenerator( categoricalLabelFormat, formatter );
      }
      renderer.setBaseItemLabelGenerator( scilg );
    }
    renderer.setBaseItemLabelsVisible( Boolean.TRUE.equals( getItemsLabelVisible() ) );
    if ( getItemLabelFont() != null ) {
      renderer.setBaseItemLabelFont( getItemLabelFont() );
    }

    if ( categoricalItemLabelRotation != null ) {
      final ItemLabelPosition orgPosItemLabelPos = renderer.getBasePositiveItemLabelPosition();
      if ( orgPosItemLabelPos == null ) {
        final ItemLabelPosition pos2 = new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER,
            TextAnchor.CENTER, categoricalItemLabelRotation.doubleValue() );
        renderer.setBasePositiveItemLabelPosition( pos2 );
      } else {
        final ItemLabelPosition pos2 = new ItemLabelPosition(
            orgPosItemLabelPos.getItemLabelAnchor(), orgPosItemLabelPos.getTextAnchor(),
            orgPosItemLabelPos.getRotationAnchor(), categoricalItemLabelRotation.doubleValue() );
        renderer.setBasePositiveItemLabelPosition( pos2 );
      }

      final ItemLabelPosition orgNegItemLabelPos = renderer.getBaseNegativeItemLabelPosition();
      if ( orgNegItemLabelPos == null ) {
        final ItemLabelPosition pos2 = new ItemLabelPosition(
            ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER,
            TextAnchor.CENTER, categoricalItemLabelRotation.doubleValue() );
        renderer.setBaseNegativeItemLabelPosition( pos2 );
      } else {
        final ItemLabelPosition neg2 = new ItemLabelPosition(
            orgNegItemLabelPos.getItemLabelAnchor(), orgNegItemLabelPos.getTextAnchor(),
            orgNegItemLabelPos.getRotationAnchor(), categoricalItemLabelRotation.doubleValue() );
        renderer.setBaseNegativeItemLabelPosition( neg2 );
      }
    }

    final Font labelFont = Font.decode( getLabelFont() );


    final CategoryAxis categoryAxis = cpl.getDomainAxis();
    categoryAxis.setLabelFont( labelFont );
    categoryAxis.setTickLabelFont( labelFont );
    if ( getCategoryTitleFont() != null ) {
      categoryAxis.setLabelFont( getCategoryTitleFont() );
    }
    if ( getCategoryTickFont() != null ) {
      categoryAxis.setTickLabelFont( getCategoryTickFont() );
    }

    if ( maxCategoryLabelWidthRatio != null ) {
      categoryAxis.setMaximumCategoryLabelWidthRatio( maxCategoryLabelWidthRatio.floatValue() );
    }
    cpl.setDomainGridlinesVisible( showGridlines );
    if ( labelRotation != null ) {
      double angle = labelRotation.doubleValue();
      CategoryLabelPosition top = createUpRotationCategoryLabelPosition( PlaneDirection.TOP, angle );
      CategoryLabelPosition bottom = createUpRotationCategoryLabelPosition( PlaneDirection.BOTTOM, angle );
      CategoryLabelPosition left = createUpRotationCategoryLabelPosition( PlaneDirection.LEFT, angle );
      CategoryLabelPosition right = createUpRotationCategoryLabelPosition( PlaneDirection.RIGHT, angle );
      CategoryLabelPositions rotationLabelPositions = new CategoryLabelPositions( top, bottom, left, right );
      categoryAxis.setCategoryLabelPositions( rotationLabelPositions );
    }

    final String[] colors = getSeriesColor();
    for ( int i = 0; i < colors.length; i++ ) {
      renderer.setSeriesPaint( i, parseColorFromString( colors[i] ) );
    }

    if ( lowerMargin != null ) {
      categoryAxis.setLowerMargin( lowerMargin.doubleValue() );
    }
    if ( upperMargin != null ) {
      categoryAxis.setUpperMargin( upperMargin.doubleValue() );
    }
    if ( categoryMargin != null ) {
      categoryAxis.setCategoryMargin( categoryMargin.doubleValue() );
    }


    configureRangeAxis( cpl, labelFont );
  }

  protected void configureRangeAxis( final CategoryPlot cpl, final Font labelFont ) {
    final ValueAxis rangeAxis = cpl.getRangeAxis();
    if ( rangeAxis instanceof NumberAxis ) {
      final NumberAxis numberAxis = (NumberAxis) rangeAxis;
      numberAxis.setAutoRangeIncludesZero( isRangeIncludesZero() );
      numberAxis.setAutoRangeStickyZero( isRangeStickyZero() );

      if ( getRangePeriodCount() > 0 ) {
        if ( getRangeTickFormat() != null ) {
          numberAxis.setTickUnit( new NumberTickUnit( getRangePeriodCount(), getRangeTickFormat() ) );
        } else if ( getRangeTickFormatString() != null ) {
          final FastDecimalFormat formatter = new FastDecimalFormat(
              getRangeTickFormatString(), getResourceBundleFactory().getLocale() );
          numberAxis.setTickUnit( new FastNumberTickUnit( getRangePeriodCount(), formatter ) );
        } else {
          numberAxis.setTickUnit( new FastNumberTickUnit( getRangePeriodCount() ) );
        }
      } else {
        if ( getRangeTickFormat() != null ) {
          numberAxis.setNumberFormatOverride( getRangeTickFormat() );
        } else if ( getRangeTickFormatString() != null ) {
          final DecimalFormat formatter = new DecimalFormat(
              getRangeTickFormatString(), new DecimalFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setNumberFormatOverride( formatter );
          standardTickUnitsApplyFormat( numberAxis, formatter );
        }
      }
    } else if ( rangeAxis instanceof DateAxis ) {
      final DateAxis numberAxis = (DateAxis) rangeAxis;

      if ( getRangePeriodCount() > 0 && getRangeTimePeriod() != null ) {
        if ( getRangeTickFormatString() != null ) {
          final SimpleDateFormat formatter = new SimpleDateFormat(
              getRangeTickFormatString(), new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
          numberAxis.setTickUnit(
              new DateTickUnit( getDateUnitAsInt( getRangeTimePeriod() ), (int) getRangePeriodCount(), formatter ) );
        } else {
          numberAxis.setTickUnit(
              new DateTickUnit( getDateUnitAsInt( getRangeTimePeriod() ), (int) getRangePeriodCount() ) );
        }
      } else if ( getRangeTickFormatString() != null ) {
        final SimpleDateFormat formatter = new SimpleDateFormat(
            getRangeTickFormatString(), new DateFormatSymbols( getResourceBundleFactory().getLocale() ) );
        numberAxis.setDateFormatOverride( formatter );
      }

    }

    if ( rangeAxis != null ) {
      rangeAxis.setLabelFont( labelFont );
      rangeAxis.setTickLabelFont( labelFont );

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
          rangeAxis.setAutoRange( true );
        }
      } else {
        if ( isAutoRange() ) {
          rangeAxis.setAutoRange( isAutoRange() );
        } else {
          double factor = getScaleFactor();
          if ( factor > DEFAULT_SCALE_FACTOR ) {
            // PRD-5340 hack
            // this method is invoked after all series were populated
            // hence the axis already has the graph's max and min values;
            double lower = rangeAxis.getLowerBound();
            if ( lower < 0 ) {
              lower *= factor;
            } else if ( lower > 0 ) {
              lower /= factor;
            }

            double upper = rangeAxis.getUpperBound();
            if ( upper > 0 ) {
              upper *= factor;
            } else if ( upper < 0 ) {
              upper /= factor;
            }
            rangeAxis.setRange( lower, upper );
          } else {
            // the 'scaleFactor' property is left intact or has an incorrect value
            rangeAxis.setUpperBound( getRangeMaximum() );
            rangeAxis.setLowerBound( getRangeMinimum() );
          }
        }
      }
    }
  }

  protected void configureLogarithmicAxis( final CategoryPlot plot ) {
    if ( isLogarithmicAxis() ) {
      final LogarithmicAxis logarithmicAxis;
      if ( isHumanReadableLogarithmicFormat() ) {
        plot.getRenderer().setBaseItemLabelGenerator( new LogCategoryItemLabelGenerator() );
        logarithmicAxis = new ScalingLogarithmicAxis( getValueAxisLabel() );
        logarithmicAxis.setStrictValuesFlag( false );
      } else {
        logarithmicAxis = new LogarithmicAxis( getValueAxisLabel() );
        logarithmicAxis.setStrictValuesFlag( false );
      }

      plot.setRangeAxis( logarithmicAxis );
    }
  }

  public Class getRangeTimePeriod() {
    return rangeTimePeriod;
  }

  public void setRangeTimePeriod( final Class rangeTimePeriod ) {
    this.rangeTimePeriod = rangeTimePeriod;
  }

  public double getRangePeriodCount() {
    return rangePeriodCount;
  }

  public void setRangePeriodCount( final double rangePeriodCount ) {
    this.rangePeriodCount = rangePeriodCount;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance() {
    final CategoricalChartExpression expression = (CategoricalChartExpression) super.getInstance();
    if ( expression.rangeTickFormat != null ) {
      expression.rangeTickFormat = (NumberFormat) expression.rangeTickFormat.clone();
    }
    return expression;
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

  public void reconfigureForCompatibility( final int versionTag ) {
    if ( ClassicEngineBoot.isEnforceCompatibilityFor( versionTag, 3, 8 ) ) {
      setAutoRange( getRangeMinimum() == 0 && getRangeMaximum() == 1 );
    }
  }

  /**
   * Used instead of <code>org.jfree.chart.axis.CategoryLabelPosition.createUpRotationLabelPositions</code>.
   * <p>
   * It additionally takes into consideration the axis position.
   *
   * @param axisPosition
   * @param labelAngle
   * @return
   */
  protected CategoryLabelPosition createUpRotationCategoryLabelPosition( PlaneDirection axisPosition, double labelAngle ) {
    RectangleAnchor categoryAnchor = axisPosition.opposite().asRectangleAnchor();
    double labelAnchorDirectionAngle = axisPosition.opposite().asAngle() - labelAngle;
    PlaneDirection labelAnchorDirection = getTextAnchorDirectionOfAngle( labelAnchorDirectionAngle );
    TextBlockAnchor labelAnchor = labelAnchorDirection.asTextBlockAnchor();
    TextAnchor rotationAnchor = labelAnchorDirection.asTextAnchor();
    return new CategoryLabelPosition( categoryAnchor, labelAnchor, rotationAnchor, -labelAngle,
        CategoryLabelWidthType.RANGE, 0.50f );
  }

  /**
   * Chooses a proper anchor for a text label at a chart axis tick.
   * <p>
   * E.g.
   * <p>
   * Axis position is LEFT, label rotation = 0. So angle = 0.
   * <p>
   * Axis position is BOTTOM, label rotation = 90. So angle = 0.
   * <p>
   * Axis position is BOTTOM, label rotation = 0. So angle = pi/2 (90 degrees).
   *
   * @param angle can be assumed as the label-relative direction to the axis.
   * @return
   */
  protected PlaneDirection getTextAnchorDirectionOfAngle( double angle ) {
    //Divide to 32 sectors (0..31). Counterclockwise from RIGHT.
    int sectorIndex = ( (int) ( ( ( ( angle * 16 / Math.PI ) ) % 32 ) + 32 ) ) % 32;
    switch ( sectorIndex ) {
      case 5:
      case 6:
        return PlaneDirection.TOP_RIGHT;
      case 7:
      case 8:
        return PlaneDirection.TOP;
      case 9:
      case 10:
        return PlaneDirection.TOP_LEFT;
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 17:
      case 18:
      case 19:
      case 20:
        return PlaneDirection.LEFT;
      case 21:
      case 22:
        return PlaneDirection.BOTTOM_LEFT;
      case 23:
      case 24:
        return PlaneDirection.BOTTOM;
      case 25:
      case 26:
        return PlaneDirection.BOTTOM_RIGHT;
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      default:
        return PlaneDirection.RIGHT;
    }
  }

  /**
   * Local utility enum.
   * Used to calculate ahchors.
   */
  static enum PlaneDirection {
    RIGHT, TOP_RIGHT, TOP, TOP_LEFT, LEFT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT;
    private static final int COUNT = values().length;

    public static PlaneDirection byUnlimitedIndex( int unlimitedIndex ) {
      return values()[( unlimitedIndex % COUNT + COUNT ) % COUNT];
    }

    public PlaneDirection opposite() {
      return byUnlimitedIndex( this.ordinal() + COUNT / 2 );
    }

    public RectangleAnchor asRectangleAnchor() {
      switch ( this ) {
        case RIGHT:
          return RectangleAnchor.RIGHT;
        case TOP_RIGHT:
          return RectangleAnchor.TOP_RIGHT;
        case TOP:
          return RectangleAnchor.TOP;
        case TOP_LEFT:
          return RectangleAnchor.TOP_LEFT;
        case LEFT:
          return RectangleAnchor.LEFT;
        case BOTTOM_LEFT:
          return RectangleAnchor.BOTTOM_LEFT;
        case BOTTOM:
          return RectangleAnchor.BOTTOM;
        case BOTTOM_RIGHT:
          return RectangleAnchor.BOTTOM_RIGHT;
        default:
          return null;
      }
    }

    public TextBlockAnchor asTextBlockAnchor() {
      switch ( this ) {
        case RIGHT:
          return TextBlockAnchor.CENTER_RIGHT;
        case TOP_RIGHT:
          return TextBlockAnchor.TOP_RIGHT;
        case TOP:
          return TextBlockAnchor.TOP_CENTER;
        case TOP_LEFT:
          return TextBlockAnchor.TOP_LEFT;
        case LEFT:
          return TextBlockAnchor.CENTER_LEFT;
        case BOTTOM_LEFT:
          return TextBlockAnchor.BOTTOM_LEFT;
        case BOTTOM:
          return TextBlockAnchor.BOTTOM_CENTER;
        case BOTTOM_RIGHT:
          return TextBlockAnchor.BOTTOM_RIGHT;
        default:
          return null;
      }
    }

    public TextAnchor asTextAnchor() {
      switch ( this ) {
        case RIGHT:
          return TextAnchor.CENTER_RIGHT;
        case TOP_RIGHT:
          return TextAnchor.TOP_RIGHT;
        case TOP:
          return TextAnchor.TOP_CENTER;
        case TOP_LEFT:
          return TextAnchor.TOP_LEFT;
        case LEFT:
          return TextAnchor.CENTER_LEFT;
        case BOTTOM_LEFT:
          return TextAnchor.BOTTOM_LEFT;
        case BOTTOM:
          return TextAnchor.BOTTOM_CENTER;
        case BOTTOM_RIGHT:
          return TextAnchor.BOTTOM_RIGHT;
        default:
          return null;
      }
    }

    public double asAngle() {
      return this.ordinal() * 0.25 * Math.PI;
    }
  }

}
