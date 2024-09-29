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


package org.pentaho.reporting.engine.classic.core.util;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * A report drawable receives context-information from the report processor that may allow the implementation to return
 * better results. Implement this interface to get some extra hints for your drawing task.
 *
 * @author Thomas Morgner
 */
public interface ReportDrawable {
  public void draw( Graphics2D graphics2D, Rectangle2D bounds );

  /**
   * Provides the current report configuration of the current report process to the drawable. The report configuration
   * can be used to configure the drawing process through the report.
   *
   * @param config
   *          the report configuration.
   */
  public void setConfiguration( Configuration config );

  /**
   * Provides the computed stylesheet of the report element that contained this drawable. The stylesheet is immutable.
   *
   * @param style
   *          the stylesheet.
   */
  public void setStyleSheet( StyleSheet style );

  /**
   * Defines the resource-bundle factory that can be used to localize the drawing process.
   *
   * @param bundleFactory
   *          the resource-bundle factory.
   */
  public void setResourceBundleFactory( final ResourceBundleFactory bundleFactory );

  /**
   * Returns an optional image-map for the entry.
   *
   * @param bounds
   *          the bounds for which the image map is computed.
   * @return the computed image-map or null if there is no image-map available.
   */
  public ImageMap getImageMap( final Rectangle2D bounds );
}
