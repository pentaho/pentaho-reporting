/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.libsparklines.LineGraphDrawable;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class LineSparklinesWrapper extends DrawableWrapper implements ReportDrawable {
  private LineGraphDrawable sparkline;

  public LineSparklinesWrapper( final LineGraphDrawable sparkline ) {
    super( sparkline );
    this.sparkline = sparkline;
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
    if ( style != null ) {
      sparkline.setBackground( (Color) style.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR ) );
      sparkline.setColor( (Color) style.getStyleProperty( ElementStyleKeys.PAINT ) );
      final Color lastColor = (Color) style.getStyleProperty( SparklineStyleKeys.LAST_COLOR );
      if ( lastColor != null ) {
        sparkline.setLastColor( lastColor );
      }
    }
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

  public boolean isPreserveAspectRatio() {
    return false;
  }

  public Dimension getPreferredSize() {
    return null;
  }

  public void draw( final Graphics2D g2, final Rectangle2D bounds ) {
    sparkline.draw( g2, bounds );
  }
}
