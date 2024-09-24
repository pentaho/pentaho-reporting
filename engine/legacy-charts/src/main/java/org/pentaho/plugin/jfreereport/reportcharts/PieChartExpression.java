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
* Copyright (c) 2002-2020 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.plugin.jfreereport.reportcharts;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.awt.Color;
import java.awt.Font;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class PieChartExpression extends AbstractChartExpression {
  private static final int EXPLODE_VALUE = 0;
  private static final int EXPLODE_MIN = 1;
  private static final int EXPLODE_MAX = 2;

  private static final long serialVersionUID = 5755617219149952355L;

  private boolean rotationClockwise;
  private String explodeSegment;
  private Double explodePct;
  private boolean ignoreNulls;
  private boolean ignoreZeros;
  private String pieLabelFormat;
  private boolean circular;
  private String pieLegendLabelFormat;

  private Color shadowPaint;
  private Double shadowXOffset;
  private Double shadowYOffset;

  public PieChartExpression() {
    rotationClockwise = true;
    pieLegendLabelFormat = StandardPieSectionLabelGenerator.DEFAULT_SECTION_LABEL_FORMAT;
    pieLabelFormat = "{2}";
    ignoreZeros = true;
    ignoreNulls = true;
    circular = true;
    setItemsLabelVisible( Boolean.TRUE );
  }

  public Color getShadowPaint() {
    return shadowPaint;
  }

  public void setShadowPaint( final Color shadowPaint ) {
    this.shadowPaint = shadowPaint;
  }

  public Double getShadowXOffset() {
    return shadowXOffset;
  }

  public void setShadowXOffset( final Double shadowXOffset ) {
    this.shadowXOffset = shadowXOffset;
  }

  public Double getShadowYOffset() {
    return shadowYOffset;
  }

  public void setShadowYOffset( final Double shadowYOffset ) {
    this.shadowYOffset = shadowYOffset;
  }

  public boolean isRotationClockwise() {
    return rotationClockwise;
  }

  public void setRotationClockwise( final boolean value ) {
    rotationClockwise = value;
  }

  public boolean isIgnoreNulls() {
    return ignoreNulls;
  }

  public void setIgnoreNulls( final boolean value ) {
    ignoreNulls = value;
  }

  public boolean isIgnoreZeros() {
    return ignoreZeros;
  }

  public void setIgnoreZeros( final boolean value ) {
    ignoreZeros = value;
  }

  public String getExplodeSegment() {
    return explodeSegment;
  }

  public void setExplodeSegment( final String value ) {
    explodeSegment = value;
  }

  public Double getExplodePct() {
    return explodePct;
  }

  public void setExplodePct( final Double value ) {
    explodePct = value;
  }

  public String getPieLabelFormat() {
    return pieLabelFormat;
  }

  public void setPieLabelFormat( final String value ) {
    pieLabelFormat = value;
  }

  public String getPieLegendLabelFormat() {
    return pieLegendLabelFormat;
  }

  public void setPieLegendLabelFormat( final String value ) {
    pieLegendLabelFormat = value;
  }

  public boolean getIsCircular() {
    return circular;
  }

  public void setCircular( final boolean value ) {
    circular = value;
  }

  protected JFreeChart computeChart( final Dataset dataset ) {

    PieDataset pieDataset = null;
    if ( dataset instanceof PieDataset ) {
      pieDataset = (PieDataset) dataset;
    }

    if ( isThreeD() ) {
      return ChartFactory.createPieChart3D( computeTitle(), pieDataset, isShowLegend(), false, false );
    } else {
      return ChartFactory.createPieChart( computeTitle(), pieDataset, isShowLegend(), false, false );
    }
  }


  protected void configureExplode( final PiePlot pp ) {
    final PieDataset pieDS = pp.getDataset();

    final int explodeType = computeExplodeType();
    if ( explodeType == EXPLODE_VALUE ) {
      try {
        final int actualSegment = Integer.parseInt( explodeSegment );
        if ( actualSegment >= 0 ) {
          pp.setExplodePercent( pieDS.getKey( actualSegment ), explodePct.doubleValue() );
        }
      } catch ( Exception ignored ) {
      }
      return;
    }

    // Calculate min and max...
    if ( pieDS != null ) {
      final int itemCount = pieDS.getItemCount();
      Number maxNum = new Double( Integer.MIN_VALUE );
      Number minNum = new Double( Integer.MAX_VALUE );
      int maxSegment = -1;
      int minSegment = -1;
      for ( int i = 0; i < itemCount; i++ ) {
        final Number nbr = pieDS.getValue( i );
        if ( nbr.doubleValue() > maxNum.doubleValue() ) {
          maxNum = nbr;
          maxSegment = i;
        }
        if ( nbr.doubleValue() < minNum.doubleValue() ) {
          minNum = nbr;
          minSegment = i;
        }
      }

      if ( explodeType == EXPLODE_MIN ) { //$NON-NLS-1$
        if ( minSegment >= 0 ) {
          pp.setExplodePercent( pieDS.getKey( minSegment ), explodePct.doubleValue() );
        }
      } else {
        if ( maxSegment >= 0 ) {
          pp.setExplodePercent( pieDS.getKey( maxSegment ), explodePct.doubleValue() );
        }
      }
    }

  }

  private int computeExplodeType() {
    if ( "minValue".equals( explodeSegment ) ) {
      return EXPLODE_MIN;
    } else if ( "maxValue".equals( explodeSegment ) ) {
      return EXPLODE_MAX;
    }
    return EXPLODE_VALUE;
  }


  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final Plot plot = chart.getPlot();
    final PiePlot pp = (PiePlot) plot;
    final PieDataset pieDS = pp.getDataset();
    pp.setDirection( rotationClockwise ? Rotation.CLOCKWISE : Rotation.ANTICLOCKWISE );
    if ( ( explodeSegment != null ) && ( explodePct != null ) ) {
      configureExplode( pp );
    }
    if ( StringUtils.isEmpty( getTooltipFormula() ) == false ) {
      pp.setToolTipGenerator( new FormulaPieTooltipGenerator( getRuntime(), getTooltipFormula() ) );
    }
    if ( StringUtils.isEmpty( getUrlFormula() ) == false ) {
      pp.setURLGenerator( new FormulaPieURLGenerator( getRuntime(), getUrlFormula() ) );
    }

    pp.setIgnoreNullValues( ignoreNulls );
    pp.setIgnoreZeroValues( ignoreZeros );
    if ( Boolean.FALSE.equals( getItemsLabelVisible() ) ) {
      pp.setLabelGenerator( null );
    } else {
      final ExpressionRuntime runtime = getRuntime();
      final Locale locale = runtime.getResourceBundleFactory().getLocale();

      final FastDecimalFormat fastPercent = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, locale, true );
      final FastDecimalFormat fastInteger = new FastDecimalFormat( FastDecimalFormat.TYPE_INTEGER, locale, true );

      final DecimalFormat numFormat = new DecimalFormat( fastInteger.getPattern(), new DecimalFormatSymbols( locale ) );
      numFormat.setRoundingMode( RoundingMode.HALF_UP );

      final DecimalFormat percentFormat =
        new DecimalFormat( fastPercent.getPattern(), new DecimalFormatSymbols( locale ) );
      percentFormat.setRoundingMode( RoundingMode.HALF_UP );

      final StandardPieSectionLabelGenerator labelGen = new StandardPieSectionLabelGenerator( pieLabelFormat,
        numFormat, percentFormat );
      pp.setLabelGenerator( labelGen );

      final StandardPieSectionLabelGenerator legendGen = new StandardPieSectionLabelGenerator( pieLegendLabelFormat,
        numFormat, percentFormat );
      pp.setLegendLabelGenerator( legendGen );
    }

    if ( StringUtils.isEmpty( getLabelFont() ) == false ) {
      pp.setLabelFont( Font.decode( getLabelFont() ) );
    }

    if ( pieDS != null ) {
      final String[] colors = getSeriesColor();
      for ( int i = 0; i < colors.length; i++ ) {
        if ( i < pieDS.getItemCount() ) {
          pp.setSectionPaint( pieDS.getKey( i ), parseColorFromString( colors[ i ] ) );
        } else {
          break;
        }
      }
    }

    if ( shadowPaint != null ) {
      pp.setShadowPaint( shadowPaint );
    }
    if ( shadowXOffset != null ) {
      pp.setShadowXOffset( shadowXOffset.doubleValue() );
    }
    if ( shadowYOffset != null ) {
      pp.setShadowYOffset( shadowYOffset.doubleValue() );
    }
    pp.setCircular( circular );

    if ( isShowBorder() == false || isChartSectionOutline() == false ) {
      chart.setBorderVisible( false );
      chart.getPlot().setOutlineVisible( false );
    }

  }

}
