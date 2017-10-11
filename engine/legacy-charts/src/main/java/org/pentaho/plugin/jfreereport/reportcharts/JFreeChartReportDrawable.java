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

import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.CategoryItemEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.entity.PieSectionEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.AbstractImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.CircleImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.imagemap.PolygonImageMapEntry;
import org.pentaho.reporting.engine.classic.core.imagemap.RectangleImageMapEntry;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.FloatList;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.LibXmlInfo;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class JFreeChartReportDrawable implements ReportDrawable {
  private boolean debugRendering;
  private boolean buggyDrawArea;

  private JFreeChart chart;
  private ChartRenderingInfo chartRenderingInfo;
  private Rectangle2D bounds;

  public JFreeChartReportDrawable( final JFreeChart chart, final boolean collectRenderingInfo ) {
    debugRendering = ClassicEngineBoot.getInstance().getExtendedConfig().
      getBoolProperty( "org.pentaho.plugin.jfreereport.reportcharts.DebugChartEntities" );
    buggyDrawArea = ClassicEngineBoot.getInstance().getExtendedConfig().
      getBoolProperty( "org.pentaho.plugin.jfreereport.reportcharts.DrawAreaBug" );
    this.chart = chart;
    if ( collectRenderingInfo ) {
      this.chartRenderingInfo = new ChartRenderingInfo();
    }
  }

  public void draw( final Graphics2D graphics2D, final Rectangle2D bounds ) {
    this.bounds = (Rectangle2D) bounds.clone();
    if ( chartRenderingInfo != null ) {
      this.chartRenderingInfo.clear();
    }
    final Graphics2D g2 = (Graphics2D) graphics2D.create();
    this.chart.draw( g2, bounds, chartRenderingInfo );
    g2.dispose();

    if ( chartRenderingInfo == null || debugRendering == false ) {
      return;
    }

    graphics2D.setColor( Color.RED );
    final Rectangle2D dataArea = getDataAreaOffset();
    final EntityCollection entityCollection = chartRenderingInfo.getEntityCollection();
    for ( int i = 0; i < entityCollection.getEntityCount(); i++ ) {
      final ChartEntity chartEntity = entityCollection.getEntity( i );
      if ( chartEntity instanceof XYItemEntity ||
        chartEntity instanceof CategoryItemEntity ||
        chartEntity instanceof PieSectionEntity ) {
        final Area a = new Area( chartEntity.getArea() );
        if ( buggyDrawArea ) {
          a.transform( AffineTransform.getTranslateInstance( dataArea.getX(), dataArea.getY() ) );
        }
        a.intersect( new Area( dataArea ) );
        graphics2D.draw( a );
      } else {
        graphics2D.draw( chartEntity.getArea() );
      }
    }
  }

  private Rectangle2D getDataAreaOffset() {
    return chartRenderingInfo.getPlotInfo().getDataArea();
  }

  /**
   * Provides the current report configuration of the current report process to the drawable. The report configuration
   * can be used to configure the drawing process through the report.
   *
   * @param config the report configuration.
   */
  public void setConfiguration( final Configuration config ) {

  }

  /**
   * Provides the computed stylesheet of the report element that contained this drawable. The stylesheet is immutable.
   *
   * @param style the stylesheet.
   */
  public void setStyleSheet( final StyleSheet style ) {

  }

  /**
   * Defines the resource-bundle factory that can be used to localize the drawing process.
   *
   * @param bundleFactory the resource-bundle factory.
   */
  public void setResourceBundleFactory( final ResourceBundleFactory bundleFactory ) {

  }

  public JFreeChart getChart() {
    return chart;
  }

  /**
   * Returns an optional image-map for the entry.
   *
   * @param bounds the bounds for which the image map is computed.
   * @return the computed image-map or null if there is no image-map available.
   */
  public ImageMap getImageMap( final Rectangle2D bounds ) {
    if ( chartRenderingInfo == null ) {
      return null;
    }
    final Rectangle2D dataArea = getDataAreaOffset();
    final Rectangle2D otherArea = new Rectangle2D.Double();

    if ( ( ObjectUtilities.equal( bounds, this.bounds ) ) == false ) {
      final BufferedImage image = new BufferedImage( 1, 1, BufferedImage.TYPE_4BYTE_ABGR );
      final Graphics2D graphics = image.createGraphics();
      draw( graphics, bounds );
      graphics.dispose();
    }

    final ImageMap map = new ImageMap();
    final EntityCollection entityCollection = chartRenderingInfo.getEntityCollection();
    final int count = entityCollection.getEntityCount();
    for ( int i = 0; i < count; i++ ) {
      final ChartEntity chartEntity = entityCollection.getEntity( i );
      final Shape area = chartEntity.getArea();
      final String hrefValue = chartEntity.getURLText();
      final String tooltipValue = chartEntity.getToolTipText();
      if ( StringUtils.isEmpty( tooltipValue ) == false ||
        StringUtils.isEmpty( hrefValue ) == false ) {
        final AbstractImageMapEntry entry;
        if ( chartEntity instanceof XYItemEntity ||
          chartEntity instanceof CategoryItemEntity ||
          chartEntity instanceof PieSectionEntity ) {
          entry = createMapEntry( area, dataArea );
        } else {
          entry = createMapEntry( area, otherArea );
        }
        if ( entry == null ) {
          continue;
        }
        if ( StringUtils.isEmpty( hrefValue ) == false ) {
          entry.setAttribute( LibXmlInfo.XHTML_NAMESPACE, "href", hrefValue );
        } else {
          entry.setAttribute( LibXmlInfo.XHTML_NAMESPACE, "href", "#" );
        }
        if ( StringUtils.isEmpty( tooltipValue ) == false ) {
          entry.setAttribute( LibXmlInfo.XHTML_NAMESPACE, "title", tooltipValue );
        }
        map.addMapEntry( entry );
      }
    }

    return map;
  }

  private AbstractImageMapEntry createMapEntry( final Shape area,
                                                final Rectangle2D dataArea ) {
    if ( buggyDrawArea ) {
      if ( area instanceof Ellipse2D ) {
        final Ellipse2D ellipse2D = (Ellipse2D) area;
        if ( ellipse2D.getWidth() == ellipse2D.getHeight() ) {
          return new CircleImageMapEntry( (float) ( ellipse2D.getCenterX() + dataArea.getX() ),
            (float) ( ellipse2D.getCenterY() + dataArea.getY() ), (float) ( ellipse2D.getWidth() / 2 ) );
        }
      } else if ( area instanceof Rectangle2D ) {
        final Rectangle2D rect = (Rectangle2D) area;
        return ( new RectangleImageMapEntry( (float) ( rect.getX() + dataArea.getX() ),
          (float) ( rect.getY() + dataArea.getY() ),
          (float) ( rect.getX() + rect.getWidth() ),
          (float) ( rect.getY() + rect.getHeight() ) ) );
      }
    } else {
      if ( area instanceof Ellipse2D ) {
        final Ellipse2D ellipse2D = (Ellipse2D) area;
        if ( ellipse2D.getWidth() == ellipse2D.getHeight() ) {
          return new CircleImageMapEntry( (float) ( ellipse2D.getCenterX() ),
            (float) ( ellipse2D.getCenterY() ), (float) ( ellipse2D.getWidth() / 2 ) );
        }
      } else if ( area instanceof Rectangle2D ) {
        final Rectangle2D rect = (Rectangle2D) area;
        return ( new RectangleImageMapEntry( (float) ( rect.getX() ),
          (float) ( rect.getY() ),
          (float) ( rect.getX() + rect.getWidth() ),
          (float) ( rect.getY() + rect.getHeight() ) ) );
      }
    }

    final Area a = new Area( area );
    if ( buggyDrawArea ) {
      a.transform( AffineTransform.getTranslateInstance( dataArea.getX(), dataArea.getY() ) );
    }
    if ( dataArea.isEmpty() == false ) {
      a.intersect( new Area( dataArea ) );
    }
    final PathIterator pathIterator = a.getPathIterator( null, 2 );
    final FloatList floats = new FloatList( 100 );
    final float[] coords = new float[ 6 ];
    while ( pathIterator.isDone() == false ) {
      final int retval = pathIterator.currentSegment( coords );
      if ( retval == PathIterator.SEG_MOVETO ||
        retval == PathIterator.SEG_LINETO ) {
        floats.add( coords[ 0 ] );
        floats.add( coords[ 1 ] );
      }
      pathIterator.next();
    }

    if ( floats.size() == 0 ) {
      return null;
    }
    return ( new PolygonImageMapEntry( floats.toArray() ) );
  }
}
