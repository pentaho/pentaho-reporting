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

package org.pentaho.reporting.libraries.resourceloader.modules.factory.svg;

import org.apache.batik.gvt.GraphicsNode;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Creation-Date: 21.12.2005, 20:25:29
 *
 * @author Thomas Morgner
 */
public class SVGDrawable {
  private GraphicsNode rootNode;
  private double width;
  private double height;

  public SVGDrawable( final GraphicsNode rootNode ) {
    if ( rootNode == null ) {
      throw new NullPointerException();
    }
    this.rootNode = rootNode;
    final Rectangle2D bounds = rootNode.getBounds();
    if ( bounds != null ) {
      this.width = bounds.getWidth();
      this.height = bounds.getHeight();
    }
  }

  /**
   * Returns the preferred size of the drawable. If the drawable is aspect ratio aware, these bounds should be used to
   * compute the preferred aspect ratio for this drawable.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize() {
    final int w = (int) width;
    final int h = (int) height;
    if ( w == 0 || h == 0 ) {
      return null;
    }

    return new Dimension( w, h );
  }

  /**
   * Returns true, if this drawable will preserve an aspect ratio during the drawing.
   *
   * @return true, if an aspect ratio is preserved, false otherwise.
   */
  public boolean isPreserveAspectRatio() {
    return true;
  }

  /**
   * Draws the object.
   *
   * @param g    the graphics device.
   * @param area the area inside which the object should be drawn.
   */
  public void draw( final Graphics2D g, final Rectangle2D area ) {
    if ( width == 0 || height == 0 ) {
      return;
    }
    final Graphics2D g2 = (Graphics2D) g.create();
    try {
      g2.translate( -area.getX(), -area.getY() );
      final double sx = area.getWidth() / width;
      final double sy = area.getHeight() / height;
      final double sm = Math.min( sx, sy );
      g2.scale( sm, sm );

      rootNode.paint( g2 );
    } finally {
      g2.dispose();
    }
  }
}
