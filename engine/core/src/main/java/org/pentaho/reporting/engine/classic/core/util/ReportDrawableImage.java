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
