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
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.plot.SpiderWebPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.util.TableOrder;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.List;


/**
 * The RadarChartExpression returns a radar chart showing the data from a given CategoryDataset.
 *
 * @author Roman Wild, Rom@n-Wild.com
 */

public class RadarChartExpression extends AbstractChartExpression {
  private static class GridCategoryItem implements Comparable {
    private String text;

    private GridCategoryItem( final String text ) {
      if ( text == null ) {
        throw new NullPointerException();
      }
      this.text = text;
    }

    public int compareTo( final Object o ) {
      final GridCategoryItem gci = (GridCategoryItem) o;
      return this.text.compareTo( gci.text );
    }

    /**
     * Returns a string representation of the object. In general, the <code>toString</code> method returns a string that
     * "textually represents" this object. The result should be a concise but informative representation that is easy
     * for a person to read. It is recommended that all subclasses override this method.
     * <p/>
     * The <code>toString</code> method for class <code>Object</code> returns a string consisting of the name of the
     * class of which the object is an instance, the at-sign character `<code>@</code>', and the unsigned hexadecimal
     * representation of the hash code of the object. In other words, this method returns a string equal to the value
     * of: <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    public String toString() {
      return text;
    }
  }

  private static class ExtendedSpiderWebPlot extends SpiderWebPlot {
    /**
     * Creates a new spider web plot with the given dataset, with each row representing a series.
     *
     * @param dataset the dataset (<code>null</code> permitted).
     */
    public ExtendedSpiderWebPlot( final CategoryDataset dataset ) {
      super( dataset );
    }

    /**
     * Returns a collection of legend items for the spider web chart.
     *
     * @return The legend items (never <code>null</code>).
     */
    public LegendItemCollection getLegendItems() {
      final LegendItemCollection result = new LegendItemCollection();
      if ( getDataset() == null ) {
        return result;
      }
      List keys = null;
      final CategoryDataset dataset = getDataset();
      final TableOrder dataExtractOrder = getDataExtractOrder();
      if ( dataExtractOrder == TableOrder.BY_ROW ) {
        keys = dataset.getRowKeys();
      } else if ( dataExtractOrder == TableOrder.BY_COLUMN ) {
        keys = dataset.getColumnKeys();
      }
      if ( keys == null ) {
        return result;
      }

      int series = 0;
      final Iterator iterator = keys.iterator();
      final Shape shape = getLegendItemShape();
      while ( iterator.hasNext() ) {
        final Comparable key = (Comparable) iterator.next();
        if ( key instanceof GridCategoryItem ) {
          continue;
        }
        final String label = key.toString();
        final Paint paint = getSeriesPaint( series );
        final Paint outlinePaint = getSeriesOutlinePaint( series );
        final Stroke stroke = getSeriesOutlineStroke( series );
        final LegendItem item = new LegendItem( label, label,
          null, null, shape, paint, stroke, outlinePaint );
        item.setDataset( getDataset() );
        item.setSeriesKey( key );
        item.setSeriesIndex( series );
        result.add( item );
        series++;
      }
      return result;
    }
  }

  private static final long serialVersionUID = 7082583397390897215L;

  private float gridintervall;
  private boolean drawgrid;
  private boolean radarwebfilled;
  private double headsize;
  private float thicknessprimaryseries;

  public RadarChartExpression() {
    drawgrid = true;
    headsize = 0.001;
    thicknessprimaryseries = 2.0f;
    gridintervall = -25;
  }

  protected JFreeChart computeChart( final Dataset dataset ) {

    //Initializing a default CategoryDataset
    DefaultCategoryDataset defaultDataset = new DefaultCategoryDataset();

    if ( dataset instanceof DefaultCategoryDataset ) {
      defaultDataset = (DefaultCategoryDataset) dataset;
    }

    //Retrieving the size of the dataset for parsing

    //Parse the dataset in order to find the biggest value
    if ( drawgrid == true ) {
      initializeGrid( defaultDataset );
    }

    //Instantiate a spiderwebplot
    final ExtendedSpiderWebPlot plot = new ExtendedSpiderWebPlot( defaultDataset );
    for ( int i = 0; i < this.getSeriesColor().length; i++ ) {
      Color seriesColor;
      String colorDef = this.getSeriesColor( i );
      if ( colorDef == null ) {
        seriesColor = Color.RED;
      } else {
        seriesColor = ColorHelper.lookupColor( colorDef );
        if ( seriesColor == null ) {
          seriesColor = Color.decode( colorDef );
        }
      }
      plot.setSeriesPaint( i, seriesColor );
    }
    //Instantiate a JFreeChart using the plot from above
    return new JFreeChart( computeTitle(), JFreeChart.DEFAULT_TITLE_FONT, plot, isShowLegend() );
  }

  private void initializeGrid( final DefaultCategoryDataset defaultDataset ) {

    if ( gridintervall < 0 ) {
      final double gridIntervalIncrement = -gridintervall;
      if ( ( 100.0 / gridIntervalIncrement ) > 5000 ) {
        return;
      }

      //insert the gridlines (fake data sets)
      double gridline = gridIntervalIncrement;
      final int columns = defaultDataset.getColumnCount();
      final double maxdata = computeMaxValue( defaultDataset );

      final NumberFormat format =
        NumberFormat.getPercentInstance( getRuntime().getResourceBundleFactory().getLocale() );
      while ( gridline <= 100 ) {
        final double gridScaled = maxdata * gridline / 100.0;
        final String gridLineText = format.format( gridline / 100.0 );
        final GridCategoryItem rowKey = new GridCategoryItem( gridLineText );
        for ( int i = 0; i < columns; i++ ) {
          defaultDataset.addValue( gridScaled, rowKey, defaultDataset.getColumnKey( i ) );
        }
        gridline = gridline + gridIntervalIncrement;
      }
    } else if ( gridintervall > 0 ) {
      final int columns = defaultDataset.getColumnCount();
      final double maxdata = computeMaxValue( defaultDataset );
      final double gridIntervalIncrement = gridintervall;
      if ( ( maxdata / gridIntervalIncrement ) > 5000 ) {
        return;
      }

      final NumberFormat format = NumberFormat.getNumberInstance( getRuntime().getResourceBundleFactory().getLocale() );
      double gridline = 0;
      while ( gridline < maxdata ) {
        gridline = gridline + gridIntervalIncrement;
        final String gridLineText = format.format( gridline );
        final GridCategoryItem rowKey = new GridCategoryItem( gridLineText );
        for ( int i = 0; i < columns; i++ ) {
          defaultDataset.addValue( gridline, rowKey, defaultDataset.getColumnKey( i ) );
        }
      }

    }
  }

  private double computeMaxValue( final DefaultCategoryDataset defaultDataset ) {
    final int rows = defaultDataset.getRowCount();
    final int columns = defaultDataset.getColumnCount();
    double maxdata = 0.01;

    for ( int r = 0; r < rows; r++ ) {
      for ( int cc = 0; cc < columns; cc++ ) {
        final Number value = defaultDataset.getValue( r, cc );
        if ( value == null ) {
          continue;
        }

        if ( value.doubleValue() > maxdata ) {
          maxdata = value.doubleValue();
        }
      }
    }
    return maxdata;
  }

  //Method used for changes to settings of the chart
  protected void configureChart( final JFreeChart chart ) {
    super.configureChart( chart );

    //Create the stroke for the primary (= real) data series...
    final Stroke thick = new BasicStroke( thicknessprimaryseries );

    //...and apply that stroke to the series
    final SpiderWebPlot webPlot = (SpiderWebPlot) chart.getPlot();
    webPlot.setLabelFont( Font.decode( getLabelFont() ) );

    if ( StringUtils.isEmpty( getTooltipFormula() ) == false ) {
      webPlot.setToolTipGenerator( new FormulaCategoryTooltipGenerator( getRuntime(), getTooltipFormula() ) );
    }
    if ( StringUtils.isEmpty( getUrlFormula() ) == false ) {
      webPlot.setURLGenerator( new FormulaCategoryURLGenerator( getRuntime(), getUrlFormula() ) );
    }


    final CategoryDataset categoryDataset = webPlot.getDataset();
    final int count = categoryDataset.getRowCount();

    for ( int t = 0; t < count; t++ ) {
      if ( categoryDataset.getRowKey( t ) instanceof GridCategoryItem ) {
        continue;
      }
      webPlot.setSeriesOutlineStroke( t, thick );
    }

    //Set the spiderweb filled (or not)
    webPlot.setWebFilled( radarwebfilled );
    //Set the size of the datapoints on the axis
    webPlot.setHeadPercent( headsize );

    //Set the color of the fake datasets (gridlines) to grey
    for ( int t = 0; t < count; t++ ) {
      if ( categoryDataset.getRowKey( t ) instanceof GridCategoryItem ) {
        webPlot.setSeriesPaint( t, Color.GRAY );
      }
    }

  }

  //Getters and setters

  public void setGridintervall( final float gridintervall ) {
    this.gridintervall = gridintervall;
  }

  public float getGridintervall() {
    return gridintervall;
  }

  public void setDrawgrid( final boolean drawgrid ) {
    this.drawgrid = drawgrid;
  }

  public boolean isDrawgrid() {
    return drawgrid;
  }

  public void setRadarwebfilled( final boolean radarwebfilled ) {
    this.radarwebfilled = radarwebfilled;
  }

  public boolean isRadarwebfilled() {
    return radarwebfilled;
  }

  public void setHeadsize( final double headsize ) {
    this.headsize = headsize;
  }

  public double getHeadsize() {
    return headsize;
  }

  public void setThicknessprimaryseries( final float thicknessprimaryseries ) {
    this.thicknessprimaryseries = thicknessprimaryseries;
  }

  public float getThicknessprimaryseries() {
    return thicknessprimaryseries;
  }


}
