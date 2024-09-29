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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;

import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.WaitingImageObserver;

public class ReportDrawableImage implements ReportDrawable {
  private Image image;

  public ReportDrawableImage( final Image aImage ) {
    if ( aImage == null ) {
      throw new NullPointerException();
    }
    this.image = aImage;
  }

  public void draw( final Graphics2D graphics2D, final Rectangle2D bounds ) {
    final WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();

    graphics2D.drawImage( image, 0, 0, image.getWidth( null ), image.getHeight( null ), null );
  }

  public boolean isKeepAspectRatio() {
    return true;
  }

  public Dimension getPreferredSize() {
    final WaitingImageObserver obs = new WaitingImageObserver( image );
    obs.waitImageLoaded();

    return new Dimension( image.getWidth( null ), image.getHeight( null ) );
  }

  public ImageMap getImageMap( final Rectangle2D bounds ) {
    return null;
  }

  public void setConfiguration( final Configuration config ) {
  }

  public void setResourceBundleFactory( final ResourceBundleFactory bundleFactory ) {
  }

  public void setStyleSheet( final StyleSheet style ) {
  }

}
