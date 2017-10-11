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

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.LogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class ShapeDrawable implements ReportDrawable {
  private StyleSheet layoutContext;
  private Shape shape;
  private boolean preserveAspectRatio;

  public ShapeDrawable( final Shape shape, final boolean isPreserveAspectRatio ) {
    if ( shape == null ) {
      throw new NullPointerException();
    }
    preserveAspectRatio = isPreserveAspectRatio;
    this.shape = shape;
  }

  public Shape getShape() {
    return shape;
  }

  public void draw( final Graphics2D g2, final Rectangle2D bounds ) {
    final boolean shouldDraw = layoutContext.getBooleanStyleProperty( ElementStyleKeys.DRAW_SHAPE );
    final boolean shouldFill = layoutContext.getBooleanStyleProperty( ElementStyleKeys.FILL_SHAPE );
    if ( shouldFill == false && shouldDraw == false ) {
      return;
    }
    final boolean scale = layoutContext.getBooleanStyleProperty( ElementStyleKeys.SCALE );
    final boolean keepAspectRatio = layoutContext.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
    final double x = bounds.getX();
    final double y = bounds.getY();
    final double width = bounds.getWidth();
    final double height = bounds.getHeight();

    final Shape scaledShape = ShapeTransform.transformShape( shape, scale, keepAspectRatio, width, height );
    final Graphics2D clone = (Graphics2D) g2.create();
    final double extraPadding;
    if ( layoutContext != null ) {
      final Object o = layoutContext.getStyleProperty( ElementStyleKeys.STROKE );
      if ( o instanceof BasicStroke ) {
        final BasicStroke stroke = (BasicStroke) o;
        extraPadding = stroke.getLineWidth() / 2.0;
      } else {
        extraPadding = 0.5;
      }
    } else {
      extraPadding = 0.5;
    }

    final Rectangle2D.Double drawAreaBounds =
        new Rectangle2D.Double( x - extraPadding, y - extraPadding, width + 2 * extraPadding, height + 2 * extraPadding );

    clone.clip( drawAreaBounds );
    clone.translate( x, y );
    if ( shouldFill ) {
      configureFillColor( layoutContext, clone );
      clone.fill( scaledShape );
    }
    if ( shouldDraw ) {
      configureGraphics( layoutContext, clone );
      clone.draw( scaledShape );
    }
    clone.dispose();
  }

  protected void configureGraphics( final StyleSheet layoutContext, final Graphics2D g2 ) {
    if ( layoutContext == null ) {
      return;
    }
    final Color cssColor = (Color) layoutContext.getStyleProperty( ElementStyleKeys.PAINT );
    g2.setColor( cssColor );

    final Stroke styleProperty = (Stroke) layoutContext.getStyleProperty( ElementStyleKeys.STROKE );
    if ( styleProperty != null ) {
      g2.setStroke( styleProperty );
    } else {
      // Apply a default one ..
      g2.setStroke( LogicalPageDrawable.DEFAULT_STROKE );
    }

  }

  private void configureFillColor( final StyleSheet layoutContext, final Graphics2D graphics2D ) {
    if ( layoutContext == null ) {
      return;
    }
    final Color cssColor = (Color) layoutContext.getStyleProperty( ElementStyleKeys.FILL_COLOR );
    if ( cssColor != null ) {
      graphics2D.setColor( cssColor );
    } else {
      final Color paint = (Color) layoutContext.getStyleProperty( ElementStyleKeys.PAINT );
      if ( paint != null ) {
        graphics2D.setPaint( paint );
      }
    }

  }

  /**
   * Provides the current report configuration of the current report process to the drawable. The report configuration
   * can be used to configure the drawing process through the report.
   *
   * @param config
   *          the report configuration.
   */
  public void setConfiguration( final Configuration config ) {

  }

  /**
   * Provides the computed stylesheet of the report element that contained this drawable. The stylesheet is immutable.
   *
   * @param style
   *          the stylesheet.
   */
  public void setStyleSheet( final StyleSheet style ) {
    this.layoutContext = style;
  }

  /**
   * Defines the resource-bundle factory that can be used to localize the drawing process.
   *
   * @param bundleFactory
   *          the resource-bundle factory.
   */
  public void setResourceBundleFactory( final ResourceBundleFactory bundleFactory ) {

  }

  /**
   * Returns an optional image-map for the entry.
   *
   * @param bounds
   *          the bounds for which the image map is computed.
   * @return the computed image-map or null if there is no image-map available.
   */
  public ImageMap getImageMap( final Rectangle2D bounds ) {
    return null;
  }

  public boolean isKeepAspectRatio() {
    return preserveAspectRatio;
  }

  public Dimension getPreferredSize() {
    final Rectangle bounds = shape.getBounds();
    return new Dimension( (int) bounds.getMaxX(), (int) bounds.getMaxY() );
  }
}
