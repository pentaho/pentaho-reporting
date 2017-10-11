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

package org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.output.OutputException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.imagemap.ImageMap;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * This class is used to wrap a <i>barbecue</i> barcode element in order to offer the method
 * {@link BarcodeWrapper#draw(java.awt.Graphics2D, java.awt.geom.Rectangle2D)} which is requested by the reporting
 * engine for automatic support using <i>drawable-field</i>s.
 */
public class BarcodeWrapper implements ReportDrawable {
  private static final Log logger = LogFactory.getLog( BarcodeWrapper.class );
  static final Color ALPHA = new Color( 255, 255, 255, 0 );
  private Barcode barcode;
  private Dimension preferredSize;
  private boolean scale;
  private boolean keepAspectRatio;

  public BarcodeWrapper( final Barcode barcode ) {
    if ( barcode == null ) {
      throw new IllegalArgumentException( "Barcode to wrap must not be null" );
    }
    this.barcode = barcode;
  }

  public Barcode getBarcode() {
    return barcode;
  }

  public boolean isPreserveAspectRatio() {
    return true;
  }

  public Dimension getPreferredSize() {
    if ( preferredSize == null ) {
      preferredSize = barcode.getSize();
    }
    return preferredSize;
  }

  public void draw( final Graphics2D g2, final Rectangle2D bounds ) {
    final Graphics2D gr2 = (Graphics2D) g2.create();
    try {
      gr2.clip( bounds );
      if ( scale ) {
        final Dimension size = barcode.getPreferredSize();
        final double horzScale = bounds.getWidth() / size.getWidth();
        final double vertScale = bounds.getHeight() / size.getHeight();
        if ( keepAspectRatio ) {
          final double scale = Math.min( horzScale, vertScale );
          gr2.scale( scale, scale );
        } else {
          gr2.scale( horzScale, vertScale );
        }
      }
      barcode.draw( gr2, (int) bounds.getX(), (int) bounds.getY() );
    } catch ( OutputException e ) {
      logger.error( "Unable to draw barcode element", e );
    } finally {
      gr2.dispose();
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
    if ( style != null ) {
      final String fontName = (String) style.getStyleProperty( TextStyleKeys.FONT );
      final int fontSize = style.getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
      final boolean bold = style.getBooleanStyleProperty( TextStyleKeys.BOLD );
      final boolean italics = style.getBooleanStyleProperty( TextStyleKeys.ITALIC );
      final Color foregroundColor = (Color) style.getStyleProperty( ElementStyleKeys.PAINT );
      final Color backgroundColor = (Color) style.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
      if ( fontName != null && fontSize > 0 ) {
        int fontstyle = Font.PLAIN;
        if ( bold ) {
          fontstyle |= Font.BOLD;
        }
        if ( italics ) {
          fontstyle |= Font.ITALIC;
        }

        barcode.setFont( new Font( fontName, fontstyle, fontSize ) );
      }
      if ( foregroundColor != null ) {
        barcode.setForeground( foregroundColor );
      }
      if ( backgroundColor != null ) {
        barcode.setBackground( backgroundColor );
        barcode.setOpaque( backgroundColor.getAlpha() == 255 );
      } else {
        barcode.setBackground( ALPHA );
        barcode.setOpaque( false );
      }

      scale = style.getBooleanStyleProperty( ElementStyleKeys.SCALE );
      keepAspectRatio = style.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
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
}
