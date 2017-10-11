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

package org.pentaho.reporting.engine.classic.core.modules.gui.base.internal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

import javax.swing.UIManager;

import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;

/**
 * Creation-Date: 17.11.2006, 20:31:36
 *
 * @author Thomas Morgner
 */
public class PageBackgroundDrawable {
  private int defaultWidth;
  private int defaultHeight;
  private PageDrawable backend;
  private boolean borderPainted;
  private float shadowSize;
  private double zoom;

  public PageBackgroundDrawable() {
    this.shadowSize = 6;
    this.borderPainted = false;
    this.zoom = 1;
  }

  public PageDrawable getBackend() {
    return backend;
  }

  public void setBackend( final PageDrawable backend ) {
    this.backend = backend;
  }

  public boolean isBorderPainted() {
    return borderPainted;
  }

  public void setBorderPainted( final boolean borderPainted ) {
    this.borderPainted = borderPainted;
  }

  public double getZoom() {
    return zoom;
  }

  public void setZoom( final double zoom ) {
    this.zoom = zoom;
  }

  public int getDefaultWidth() {
    return defaultWidth;
  }

  public void setDefaultWidth( final int defaultWidth ) {
    this.defaultWidth = defaultWidth;
  }

  public int getDefaultHeight() {
    return defaultHeight;
  }

  public void setDefaultHeight( final int defaultHeight ) {
    this.defaultHeight = defaultHeight;
  }

  public Dimension getPreferredSize() {
    if ( backend == null ) {
      return new Dimension( (int) ( ( defaultWidth + shadowSize ) * zoom ),
          (int) ( ( defaultHeight + shadowSize ) * zoom ) );
    }
    final Dimension preferredSize = backend.getPreferredSize();

    return new Dimension( (int) ( ( preferredSize.width + shadowSize ) * zoom ),
        (int) ( ( preferredSize.height + shadowSize ) * zoom ) );
  }

  public boolean isPreserveAspectRatio() {
    return true;
  }

  public float getShadowSize() {
    return shadowSize;
  }

  public void setShadowSize( final float shadowSize ) {
    this.shadowSize = shadowSize;
  }

  /**
   * Draws the object.
   *
   * @param graphics
   *          the graphics device.
   * @param area
   *          the area inside which the object should be drawn.
   */
  public strictfp void draw( final Graphics2D graphics, final Rectangle2D area ) {
    if ( backend == null ) {
      return;
    }

    final PageFormat pageFormat = backend.getPageFormat();
    final float outerW = (float) pageFormat.getWidth();
    final float outerH = (float) pageFormat.getHeight();

    final float innerX = (float) pageFormat.getImageableX();
    final float innerY = (float) pageFormat.getImageableY();
    final float innerW = (float) pageFormat.getImageableWidth();
    final float innerH = (float) pageFormat.getImageableHeight();

    final Graphics2D g2 = (Graphics2D) graphics.create();
    // double paperBorder = paperBorderPixel * zoomFactor;

    /** Prepare background **/
    g2.transform( AffineTransform.getScaleInstance( getZoom(), getZoom() ) );
    g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON );

    /** Prepare background **/
    final Rectangle2D pageArea = new Rectangle2D.Float( 0, 0, outerW, outerH );
    /**
     * The border around the printable area is painted when the corresponding property is set to true.
     */
    final Rectangle2D printingArea = new Rectangle2D.Float( innerX, innerY, innerW, innerH );

    /** Paint Page Shadow */
    final Rectangle2D southborder = new Rectangle2D.Float( getShadowSize(), outerH, outerW, getShadowSize() );
    final Rectangle2D eastborder = new Rectangle2D.Float( outerW, getShadowSize(), getShadowSize(), outerH );

    g2.setPaint( UIManager.getColor( "controlShadow" ) ); //$NON-NLS-1$

    g2.fill( southborder );
    g2.fill( eastborder );

    if ( isBorderPainted() ) {
      g2.setPaint( Color.gray );
      g2.draw( printingArea );
    }

    g2.setPaint( Color.white );
    g2.fill( pageArea );

    final Graphics2D g22 = (Graphics2D) g2.create();
    backend.draw( g22, new Rectangle2D.Double( 0, 0, pageFormat.getWidth(), pageFormat.getHeight() ) );
    g22.dispose();

    final Rectangle2D transPageArea = new Rectangle2D.Float( 0, 0, outerW, outerH );
    g2.setPaint( Color.black );
    g2.draw( transPageArea );

    g2.dispose();
  }
}
