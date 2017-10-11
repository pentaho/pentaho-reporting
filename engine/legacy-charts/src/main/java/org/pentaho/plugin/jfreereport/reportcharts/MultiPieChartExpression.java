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

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.MultiplePiePlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.TableOrder;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MultiPieChartExpression extends AbstractChartExpression {
  private static final long serialVersionUID = -7796999107015376070L;
  private boolean multipieByRow;
  private String multipieLabelFormat;
  private Color shadowPaint;
  private Double shadowXOffset;
  private Double shadowYOffset;
  private Font pieTitleFont;
  private String pieNoDataMessage;

  public MultiPieChartExpression() {
    multipieLabelFormat = "{2}";
    multipieByRow = true;
  }

  public String getPieNoDataMessage() {
    return pieNoDataMessage;
  }

  public void setPieNoDataMessage( final String pieNoDataMessage ) {
    this.pieNoDataMessage = pieNoDataMessage;
  }

  public Font getPieTitleFont() {
    return pieTitleFont;
  }

  public void setPieTitleFont( final Font pieTitleFont ) {
    this.pieTitleFont = pieTitleFont;
  }

  public String getMultipieLabelFormat() {
    return multipieLabelFormat;
  }

  public void setMultipieLabelFormat( final String value ) {
    multipieLabelFormat = value;
  }

  public boolean isMultipieByRow() {
    return multipieByRow;
  }

  public void setMultipieByRow( final boolean value ) {
    multipieByRow = value;
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

  protected JFreeChart computeChart( final Dataset dataset ) {
    final CategoryDataset categoryDataset;
    if ( dataset instanceof CategoryDataset == false ) {
      categoryDataset = null;
    } else {
      categoryDataset = (CategoryDataset) dataset;
    }

    final TableOrder order;
    if ( isMultipieByRow() ) {
      order = TableOrder.BY_ROW;
    } else {
      order = TableOrder.BY_COLUMN;
    }

    if ( isThreeD() ) {
      return ChartFactory
        .createMultiplePieChart3D( computeTitle(), categoryDataset, order, isShowLegend(), false, false );
    } else {
      return ChartFactory
        .createMultiplePieChart( computeTitle(), categoryDataset, order, isShowLegend(), false, false );
    }
  }

  protected void configureSubChart( final JFreeChart chart ) {
    final TextTitle chartTitle = chart.getTitle();
    if ( chartTitle != null ) {
      if ( getPieTitleFont() != null ) {
        chartTitle.setFont( getPieTitleFont() );
      } else {
        final Font titleFont = Font.decode( getTitleFont() );
        chartTitle.setFont( titleFont );
      }
    }

    if ( isAntiAlias() == false ) {
      chart.setAntiAlias( false );
    }

    final LegendTitle chLegend = chart.getLegend();
    if ( chLegend != null ) {
      final RectangleEdge loc = translateEdge( getLegendLocation().toLowerCase() );
      if ( loc != null ) {
        chLegend.setPosition( loc );
      }
      if ( getLegendFont() != null ) {
        chLegend.setItemFont( Font.decode( getLegendFont() ) );
      }
      if ( !isDrawLegendBorder() ) {
        chLegend.setBorder( BlockBorder.NONE );
      }
      if ( getLegendBackgroundColor() != null ) {
        chLegend.setBackgroundPaint( getLegendBackgroundColor() );
      }
      if ( getLegendTextColor() != null ) {
        chLegend.setItemPaint( getLegendTextColor() );
      }
    }

    final Plot plot = chart.getPlot();
    plot.setNoDataMessageFont( Font.decode( getLabelFont() ) );

    final String pieNoData = getPieNoDataMessage();
    if ( pieNoData != null ) {
      plot.setNoDataMessage( pieNoData );
    } else {
      final String message = getNoDataMessage();
      if ( message != null ) {
        plot.setNoDataMessage( message );
      }
    }
  }

  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    final Plot plot = chart.getPlot();
    final MultiplePiePlot mpp = (MultiplePiePlot) plot;
    final JFreeChart pc = mpp.getPieChart();
    configureSubChart( pc );

    final PiePlot pp = (PiePlot) pc.getPlot();
    if ( StringUtils.isEmpty( getTooltipFormula() ) == false ) {
      pp.setToolTipGenerator( new FormulaPieTooltipGenerator( getRuntime(), getTooltipFormula() ) );
    }
    if ( StringUtils.isEmpty( getUrlFormula() ) == false ) {
      pp.setURLGenerator( new FormulaPieURLGenerator( getRuntime(), getUrlFormula() ) );
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

    final CategoryDataset c = mpp.getDataset();
    if ( c != null ) {
      final String[] colors = getSeriesColor();
      final int keysSize = c.getColumnKeys().size();
      for ( int i = 0; i < colors.length; i++ ) {
        if ( keysSize > i ) {
          pp.setSectionPaint( c.getColumnKey( i ), parseColorFromString( colors[ i ] ) );
        }
      }
    }

    if ( StringUtils.isEmpty( getLabelFont() ) == false ) {
      pp.setLabelFont( Font.decode( getLabelFont() ) );
    }

    if ( Boolean.FALSE.equals( getItemsLabelVisible() ) ) {
      pp.setLabelGenerator( null );
    } else {
      final ExpressionRuntime runtime = getRuntime();
      final Locale locale = runtime.getResourceBundleFactory().getLocale();

      final FastDecimalFormat fastPercent = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, locale );
      final FastDecimalFormat fastInteger = new FastDecimalFormat( FastDecimalFormat.TYPE_INTEGER, locale );

      final DecimalFormat numFormat = new DecimalFormat( fastInteger.getPattern(), new DecimalFormatSymbols( locale ) );
      numFormat.setRoundingMode( RoundingMode.HALF_UP );

      final DecimalFormat percentFormat =
        new DecimalFormat( fastPercent.getPattern(), new DecimalFormatSymbols( locale ) );
      percentFormat.setRoundingMode( RoundingMode.HALF_UP );

      final StandardPieSectionLabelGenerator labelGen =
        new StandardPieSectionLabelGenerator( multipieLabelFormat, numFormat, percentFormat );
      pp.setLabelGenerator( labelGen );
    }
  }
}
