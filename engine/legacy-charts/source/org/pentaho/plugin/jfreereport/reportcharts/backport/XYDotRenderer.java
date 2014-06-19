/* ===========================================================
* JFreeChart : a free chart library for the Java(tm) platform
* ===========================================================
*
* (C) Copyright 2000-2007, by Object Refinery Limited and Contributors.
*
* Project Info:  http://www.jfree.org/jfreechart/index.html
*
* This library is free software; you can redistribute it and/or modify it
* under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation; either version 2.1 of the License, or
* (at your option) any later version.
*
* This library is distributed in the hope that it will be useful, but
* WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
* or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
* License for more details.
*
* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
* USA.
*
* [Java is a trademark or registered trademark of Sun Microsystems, Inc.
* in the United States and other countries.]
*
* ------------------
* XYDotRenderer.java
* ------------------
* (C) Copyright 2002-2007, by Object Refinery Limited.
*
* Original Author:  David Gilbert (for Object Refinery Limited);
* Contributor(s):   Christian W. Zuckschwerdt;
*
* Changes (from 29-Oct-2002)
* --------------------------
* 29-Oct-2002 : Added standard header (DG);
* 25-Mar-2003 : Implemented Serializable (DG);
* 01-May-2003 : Modified drawItem() method signature (DG);
* 30-Jul-2003 : Modified entity constructor (CZ);
* 20-Aug-2003 : Implemented Cloneable and PublicCloneable (DG);
* 16-Sep-2003 : Changed ChartRenderingInfo --> PlotRenderingInfo (DG);
* 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
* 19-Jan-2005 : Now uses only primitives from dataset (DG);
* ------------- JFREECHART 1.0.x ---------------------------------------------
* 10-Jul-2006 : Added dotWidth and dotHeight attributes (DG);
* 06-Feb-2007 : Fixed bug 1086307, crosshairs with multiple axes (DG);
* 09-Nov-2007 : Added legend shape attribute, plus override for
*               getLegendItem() (DG);
* 25-Feb-2004 : Replaced CrosshairInfo with CrosshairState (DG);
* 19-Jan-2005 : Now uses only primitives from dataset (DG);
* ------------- JFREECHART 1.0.x ---------------------------------------------
* 10-Jul-2006 : Added dotWidth and dotHeight attributes (DG);
* 06-Feb-2007 : Fixed bug 1086307, crosshairs with multiple axes (DG);
* 09-Nov-2007 : Added legend shape attribute, plus override for 
*               getLegendItem() (DG);
*
*/

package org.pentaho.plugin.jfreereport.reportcharts.backport;

import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.jfree.chart.LegendItem;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.event.RendererChangeEvent;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.data.xy.XYDataset;
import org.jfree.io.SerialUtilities;
import org.jfree.ui.RectangleEdge;
import org.jfree.util.PublicCloneable;
import org.jfree.util.ShapeUtilities;

/**
 * A renderer that draws a small dot at each data point for an {@link XYPlot}.
 */
public class XYDotRenderer extends AbstractXYItemRenderer
    implements XYItemRenderer,
    Cloneable,
    PublicCloneable,
    Serializable
{

  /**
   * For serialization.
   */
  private static final long serialVersionUID = -2764344339073566425L;

  /**
   * The dot width.
   */
  private int dotWidth;

  /**
   * The dot height.
   */
  private int dotHeight;

  /**
   * The shape that is used to represent an item in the legend.
   *
   * @since 1.0.7
   */
  private transient Shape legendShape;

  /**
   * Constructs a new renderer.
   */
  public XYDotRenderer()
  {
    super();
    this.dotWidth = 1;
    this.dotHeight = 1;
    this.legendShape = new Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0);
  }

  /**
   * Returns the dot width (the default value is 1).
   *
   * @return The dot width.
   * @see #setDotWidth(int)
   * @since 1.0.2
   */
  public int getDotWidth()
  {
    return this.dotWidth;
  }

  /**
   * Sets the dot width and sends a {@link RendererChangeEvent} to all
   * registered listeners.
   *
   * @param w the new width (must be greater than zero).
   * @throws IllegalArgumentException if <code>w</code> is less than one.
   * @see #getDotWidth()
   * @since 1.0.2
   */
  public void setDotWidth(final int w)
  {
    if (w < 1)
    {
      throw new IllegalArgumentException("Requires w > 0.");
    }
    this.dotWidth = w;
    fireChangeEvent();
  }

  /**
   * Returns the dot height (the default value is 1).
   *
   * @return The dot height.
   * @see #setDotHeight(int)
   * @since 1.0.2
   */
  public int getDotHeight()
  {
    return this.dotHeight;
  }

  /**
   * Sets the dot height and sends a {@link RendererChangeEvent} to all
   * registered listeners.
   *
   * @param h the new height (must be greater than zero).
   * @throws IllegalArgumentException if <code>h</code> is less than one.
   * @see #getDotHeight()
   * @since 1.0.2
   */
  public void setDotHeight(final int h)
  {
    if (h < 1)
    {
      throw new IllegalArgumentException("Requires h > 0.");
    }
    this.dotHeight = h;
    fireChangeEvent();
  }

  /**
   * Returns the shape used to represent an item in the legend.
   *
   * @return The legend shape (never <code>null</code>).
   * @see #setLegendShape(Shape)
   * @since 1.0.7
   */
  public Shape getLegendShape()
  {
    return this.legendShape;
  }

  /**
   * Sets the shape used as a line in each legend item and sends a
   * {@link RendererChangeEvent} to all registered listeners.
   *
   * @param shape the shape (<code>null</code> not permitted).
   * @see #getLegendShape()
   * @since 1.0.7
   */
  public void setLegendShape(final Shape shape)
  {
    if (shape == null)
    {
      throw new IllegalArgumentException("Null 'shape' argument.");
    }
    this.legendShape = shape;
    fireChangeEvent();
  }

  /**
   * Draws the visual representation of a single data item.
   *
   * @param g2             the graphics device.
   * @param state          the renderer state.
   * @param dataArea       the area within which the data is being drawn.
   * @param info           collects information about the drawing.
   * @param plot           the plot (can be used to obtain standard color
   *                       information etc).
   * @param domainAxis     the domain (horizontal) axis.
   * @param rangeAxis      the range (vertical) axis.
   * @param dataset        the dataset.
   * @param series         the series index (zero-based).
   * @param item           the item index (zero-based).
   * @param crosshairState crosshair information for the plot
   *                       (<code>null</code> permitted).
   * @param pass           the pass index.
   */
  public void drawItem(final Graphics2D g2,
                       final XYItemRendererState state,
                       final Rectangle2D dataArea,
                       final PlotRenderingInfo info,
                       final XYPlot plot,
                       final ValueAxis domainAxis,
                       final ValueAxis rangeAxis,
                       final XYDataset dataset,
                       final int series,
                       final int item,
                       final CrosshairState crosshairState,
                       final int pass)
  {

    // get the data point...
    final double x = dataset.getXValue(series, item);
    final double y = dataset.getYValue(series, item);
    final double adjx = (this.dotWidth - 1) / 2.0;
    final double adjy = (this.dotHeight - 1) / 2.0;
    if (!Double.isNaN(y))
    {
      final RectangleEdge xAxisLocation = plot.getDomainAxisEdge();
      final RectangleEdge yAxisLocation = plot.getRangeAxisEdge();
      final double transX = domainAxis.valueToJava2D(x, dataArea,
          xAxisLocation) - adjx;
      final double transY = rangeAxis.valueToJava2D(y, dataArea, yAxisLocation)
          - adjy;

      g2.setPaint(getItemPaint(series, item));
      final PlotOrientation orientation = plot.getOrientation();
      final Shape s;
      if (orientation == PlotOrientation.HORIZONTAL)
      {
        //noinspection SuspiciousNameCombination
        s = new Rectangle2D.Double(transY, transX, this.dotHeight,
            this.dotWidth);
      }
      else if (orientation == PlotOrientation.VERTICAL)
      {
        s = new Rectangle2D.Double(transX, transY, this.dotWidth,
            this.dotHeight);
      }
      else
      {
        throw new IllegalStateException("PlotOrientation is neither Horizontal nor Vertical");
      }
      g2.fill(s);

      final int domainAxisIndex = plot.getDomainAxisIndex(domainAxis);
      final int rangeAxisIndex = plot.getRangeAxisIndex(rangeAxis);
      updateCrosshairValues(crosshairState, x, y, domainAxisIndex,
          rangeAxisIndex, transX, transY, orientation);

      // collect entity and tool tip information...
      if (state.getInfo() != null)
      {
        final EntityCollection entities = state.getEntityCollection();
        if (entities != null)
        {
          String tip = null;
          final XYToolTipGenerator generator
              = getToolTipGenerator(series, item);
          if (generator != null)
          {
            tip = generator.generateToolTip(dataset, series, item);
          }
          String url = null;
          if (getURLGenerator() != null)
          {
            url = getURLGenerator().generateURL(dataset, series,
                item);
          }
          final XYItemEntity entity = new XYItemEntity(s, dataset,
              series, item, tip, url);
          entities.add(entity);
        }
      }
    }

  }

  /**
   * Returns a legend item for the specified series.
   *
   * @param datasetIndex the dataset index (zero-based).
   * @param series       the series index (zero-based).
   * @return A legend item for the series (possibly <code>null</code>).
   */
  public LegendItem getLegendItem(final int datasetIndex, final int series)
  {

    // if the renderer isn't assigned to a plot, then we don't have a
    // dataset...
    final XYPlot plot = getPlot();
    if (plot == null)
    {
      return null;
    }

    final XYDataset dataset = plot.getDataset(datasetIndex);
    if (dataset == null)
    {
      return null;
    }

    LegendItem result = null;
    if (getItemVisible(series, 0))
    {
      final String label = getLegendItemLabelGenerator().generateLabel(dataset,
          series);
      String toolTipText = null;
      if (getLegendItemToolTipGenerator() != null)
      {
        toolTipText = getLegendItemToolTipGenerator().generateLabel(
            dataset, series);
      }
      String urlText = null;
      if (getLegendItemURLGenerator() != null)
      {
        urlText = getLegendItemURLGenerator().generateLabel(
            dataset, series);
      }
      final Paint fillPaint = lookupSeriesPaint(series);
      result = new LegendItem(label, label, toolTipText, urlText,
          getLegendShape(), fillPaint);
      result.setSeriesKey(dataset.getSeriesKey(series));
      result.setSeriesIndex(series);
      result.setDataset(dataset);
      result.setDatasetIndex(datasetIndex);
    }

    return result;

  }

  /**
   * Tests this renderer for equality with an arbitrary object.  This method
   * returns <code>true</code> if and only if:
   * <p/>
   * <ul>
   * <li><code>obj</code> is not <code>null</code>;</li>
   * <li><code>obj</code> is an instance of <code>XYDotRenderer</code>;</li>
   * <li>both renderers have the same attribute values.
   * </ul>
   *
   * @param obj the object (<code>null</code> permitted).
   * @return A boolean.
   */
  public boolean equals(final Object obj)
  {
    if (obj == this)
    {
      return true;
    }
    if (!(obj instanceof XYDotRenderer))
    {
      return false;
    }
    final XYDotRenderer that = (XYDotRenderer) obj;
    if (this.dotWidth != that.dotWidth)
    {
      return false;
    }
    if (this.dotHeight != that.dotHeight)
    {
      return false;
    }
    if (!ShapeUtilities.equal(this.legendShape, that.legendShape))
    {
      return false;
    }
    return super.equals(obj);
  }

  /**
   * Returns a clone of the renderer.
   *
   * @return A clone.
   * @throws CloneNotSupportedException if the renderer cannot be cloned.
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  /**
   * Provides serialization support.
   *
   * @param stream the input stream.
   * @throws IOException            if there is an I/O error.
   * @throws ClassNotFoundException if there is a classpath problem.
   */
  private void readObject(final ObjectInputStream stream)
      throws IOException, ClassNotFoundException
  {
    stream.defaultReadObject();
    this.legendShape = SerialUtilities.readShape(stream);
  }

  /**
   * Provides serialization support.
   *
   * @param stream the output stream.
   * @throws IOException if there is an I/O error.
   */
  private void writeObject(final ObjectOutputStream stream) throws IOException
  {
    stream.defaultWriteObject();
    SerialUtilities.writeShape(this.legendShape, stream);
  }

}
