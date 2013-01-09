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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.swt.base.internal;

/**
 * =========================================================
 * Pentaho-Reporting-Classic : a free Java reporting library
 * =========================================================
 *
 * Project Info:  http://reporting.pentaho.org/
 *
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * [Java is a trademark or registered trademark of Sun Microsystems, Inc.
 * in the United States and other countries.]
 *
 * ------------
 * PageBackgroundDrawable.java
 * ------------
 * (C) Copyright 2001-2007, by Object Refinery Ltd, Pentaho Corporation and Contributors.
 */

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
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */

public class PageBackgroundDrawable
{
  private int defaultWidth;
  private int defaultHeight;
  private PageDrawable backend;
  private boolean borderPainted;
  private float shadowSize;
  private double zoom;

  public PageBackgroundDrawable()
  {
    this.shadowSize = 6;
    this.borderPainted = false;
    this.zoom = 1;
  }

  public PageDrawable getBackend()
  {
    return backend;
  }

  public void setBackend(final PageDrawable backend)
  {
    this.backend = backend;
  }

  public boolean isBorderPainted()
  {
    return borderPainted;
  }

  public void setBorderPainted(final boolean borderPainted)
  {
    this.borderPainted = borderPainted;
  }

  public double getZoom()
  {
    return zoom;
  }

  public void setZoom(final double zoom)
  {
    this.zoom = zoom;
  }

  public int getDefaultWidth()
  {
    return defaultWidth;
  }

  public void setDefaultWidth(final int defaultWidth)
  {
    this.defaultWidth = defaultWidth;
  }

  public int getDefaultHeight()
  {
    return defaultHeight;
  }

  public void setDefaultHeight(final int defaultHeight)
  {
    this.defaultHeight = defaultHeight;
  }

  public Dimension getPreferredSize()
  {
    if (backend == null)
    {
      return new Dimension((int) ((defaultWidth + shadowSize) * zoom),
          (int) ((defaultHeight + shadowSize) * zoom));
    }
    final Dimension preferredSize = backend.getPreferredSize();

    return new Dimension
        ((int) ((preferredSize.width + shadowSize) * zoom),
            (int) ((preferredSize.height + shadowSize) * zoom));
  }

  public boolean isPreserveAspectRatio()
  {
    return true;
  }

  public float getShadowSize()
  {
    return shadowSize;
  }

  public void setShadowSize(final float shadowSize)
  {
    this.shadowSize = shadowSize;
  }

  /**
   * Draws the object.
   *
   * @param g2   the graphics device.
   * @param area the area inside which the object should be drawn.
   */
  public void draw(final Graphics2D g2, final Rectangle2D area)
  {
    if (backend == null)
    {
      return;
    }

    final PageFormat pageFormat = backend.getPageFormat();
    final float outerW = (float) pageFormat.getWidth();
    final float outerH = (float) pageFormat.getHeight();
    final float innerX = (float) pageFormat.getImageableX();
    final float innerY = (float) pageFormat.getImageableY();
    final float innerW = (float) pageFormat.getImageableWidth();
    final float innerH = (float) pageFormat.getImageableHeight();
    //System.err.println("imageable x:"+innerX+" y:"+innerY+" W:"+innerW+" H:"+innerH);

    //double paperBorder = paperBorderPixel * zoomFactor;

    /** Prepare background **/
    g2.transform(AffineTransform.getScaleInstance(getZoom(), getZoom()));
    g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);

    /** Prepare background **/
    final Rectangle2D pageArea = new Rectangle2D.Float(0, 0, outerW, outerH);

    g2.setPaint(Color.white);
    g2.fill(pageArea);

    
    final Graphics2D g22 = (Graphics2D) g2.create();
    backend.draw(g22, new Rectangle2D.Double(0, 0, pageFormat.getImageableWidth(), pageFormat.getImageableHeight()));
    g22.dispose();
    
    /**
     * The border around the printable area is painted when the corresponding property is
     * set to true.
     */
    final Rectangle2D printingArea = new Rectangle2D.Float(innerX, innerY, innerW, innerH);

    /** Paint Page Shadow */
    final Rectangle2D southborder = new Rectangle2D.Float
        (getShadowSize(), outerH,
            outerW, getShadowSize());

    g2.setPaint(UIManager.getColor("controlShadow")); //$NON-NLS-1$
    g2.fill(southborder);
     
    final Rectangle2D eastborder = new Rectangle2D.Float
        (outerW, getShadowSize(), getShadowSize(), outerH);

    g2.fill(eastborder);
        
    final Rectangle2D transPageArea = new Rectangle2D.Float(0, 0, outerW, outerH);

    g2.setPaint(Color.black);
    g2.draw(transPageArea);
    if (isBorderPainted())
    {
      g2.setPaint(Color.gray);
      g2.draw(printingArea);
    }
  }
}

