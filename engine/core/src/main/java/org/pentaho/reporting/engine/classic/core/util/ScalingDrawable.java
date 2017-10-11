/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * A drawable implementation that applies scaling to the wrapped up drawable object.
 *
 * @author Thomas Morgner
 */
public class ScalingDrawable extends DrawableWrapper implements ReportDrawable {
  /**
   * The horizontal scale factor.
   */
  private float scaleX;
  /**
   * The vertical scale factor.
   */
  private float scaleY;

  /**
   * The resource-bundle factory used if the drawable is a {@link ReportDrawable}.
   */
  private ResourceBundleFactory resourceBundleFactory;
  /**
   * The report configuration used if the drawable is a {@link ReportDrawable}.
   */
  private Configuration configuration;
  /**
   * The stylesheet of the element containing the drawable that is used if the drawable is a {@link ReportDrawable}.
   */
  private StyleSheet styleSheet;

  /**
   * Default constructor. Initializes the scaling to 1.
   *
   * @param drawable
   *          the drawable object
   */
  public ScalingDrawable( final Object drawable ) {
    super( drawable );
    scaleX = 1;
    scaleY = 1;
  }

  /**
   * Returns the vertical scale factor.
   *
   * @return the scale factor.
   */
  public float getScaleY() {
    return scaleY;
  }

  /**
   * Defines the vertical scale factor.
   *
   * @param scaleY
   *          the scale factor.
   */
  public void setScaleY( final float scaleY ) {
    this.scaleY = scaleY;
  }

  /**
   * Returns the horizontal scale factor.
   *
   * @return the scale factor.
   */
  public float getScaleX() {
    return scaleX;
  }

  /**
   * Defines the horizontal scale factor.
   *
   * @param scaleX
   *          the scale factor.
   */
  public void setScaleX( final float scaleX ) {
    this.scaleX = scaleX;
  }

  /**
   * Draws the object.
   *
   * @param g2
   *          the graphics device.
   * @param area
   *          the area inside which the object should be drawn.
   */
  public void draw( final Graphics2D g2, final Rectangle2D area ) {
    final Object drawable = getBackend();
    if ( drawable == null ) {
      return;
    }

    if ( drawable instanceof ReportDrawable ) {
      final ReportDrawable reportDrawable = (ReportDrawable) drawable;
      reportDrawable.setConfiguration( getConfiguration() );
      reportDrawable.setResourceBundleFactory( getResourceBundleFactory() );
      reportDrawable.setStyleSheet( getStyleSheet() );
    }

    final Graphics2D derived = (Graphics2D) g2.create();
    derived.scale( scaleX, scaleY );
    final Rectangle2D scaledArea = (Rectangle2D) area.clone();
    scaledArea.setRect( scaledArea.getX() * scaleX, scaledArea.getY() * scaleY, scaledArea.getWidth() * scaleX,
        scaledArea.getHeight() * scaleY );
    super.draw( derived, scaledArea );
    derived.dispose();
  }

  public ImageMap getImageMap( final Rectangle2D bounds ) {
    final Object drawable = getBackend();
    if ( drawable == null ) {
      return null;
    }

    if ( drawable instanceof ReportDrawable ) {
      final ReportDrawable reportDrawable = (ReportDrawable) drawable;
      final Rectangle2D scaledArea = (Rectangle2D) bounds.clone();
      scaledArea.setRect( scaledArea.getX() * scaleX, scaledArea.getY() * scaleY, scaledArea.getWidth() * scaleX,
          scaledArea.getHeight() * scaleY );
      reportDrawable.getImageMap( scaledArea );
    }
    return null;
  }

  /**
   * Returns the stylesheet of the element containing this drawable.
   *
   * @return the element's stylesheet.
   */
  public StyleSheet getStyleSheet() {
    return styleSheet;
  }

  /**
   * Defines the stylesheet of the element containing this drawable.
   *
   * @param styleSheet
   *          the element's stylesheet.
   */
  public void setStyleSheet( final StyleSheet styleSheet ) {
    if ( styleSheet == null ) {
      this.styleSheet = null;
    } else {
      this.styleSheet = new SimpleStyleSheet( styleSheet );
    }
  }

  /**
   * Returns the resource-bundle factory used if the drawable is a {@link ReportDrawable}.
   *
   * @return the resource-bundle factory.
   */
  public ResourceBundleFactory getResourceBundleFactory() {
    return resourceBundleFactory;
  }

  /**
   * Defines the resource-bundle factory used if the drawable is a {@link ReportDrawable}.
   *
   * @param resourceBundleFactory
   *          the resource-bundle factory.
   */
  public void setResourceBundleFactory( final ResourceBundleFactory resourceBundleFactory ) {
    this.resourceBundleFactory = resourceBundleFactory;
  }

  /**
   * Returns the report configuration used if the drawable is a {@link ReportDrawable}.
   *
   * @return the report's configuration.
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Defines the report configuration used if the drawable is a {@link ReportDrawable}.
   *
   * @param configuration
   *          the report's configuration.
   */
  public void setConfiguration( final Configuration configuration ) {
    this.configuration = configuration;
  }
}
