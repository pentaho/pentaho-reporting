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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Paper;

import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Creation-Date: 17.11.2006, 18:00:46
 *
 * @author Thomas Morgner
 */
public class PhysicalPageDrawable implements PageDrawable {
  private LogicalPageDrawable pageDrawable;
  private PageFormat pageFormat;
  private long globalX;
  private long globalY;

  /**
   * @param pageDrawable
   * @param page
   * @noinspection SuspiciousNameCombination
   */
  public PhysicalPageDrawable( final LogicalPageDrawable pageDrawable, final PhysicalPageBox page ) {
    if ( pageDrawable == null ) {
      throw new NullPointerException();
    }
    if ( page == null ) {
      throw new NullPointerException();
    }

    this.pageDrawable = pageDrawable;

    this.globalX = page.getGlobalX();
    this.globalY = page.getGlobalY();

    final Paper p = new Paper();

    final float marginLeft = (float) StrictGeomUtility.toExternalValue( page.getImageableX() );
    final float marginRight =
        (float) StrictGeomUtility.toExternalValue( page.getWidth() - page.getImageableWidth() - page.getImageableX() );
    final float marginTop = (float) StrictGeomUtility.toExternalValue( page.getImageableY() );
    final float marginBottom =
        (float) StrictGeomUtility.toExternalValue( page.getHeight() - page.getImageableHeight() - page.getImageableY() );
    switch ( page.getOrientation() ) {
      case PageFormat.PORTRAIT:
        p.setSize( StrictGeomUtility.toExternalValue( page.getWidth() ), StrictGeomUtility.toExternalValue( page
            .getHeight() ) );
        PageFormatFactory.getInstance().setBorders( p, marginTop, marginLeft, marginBottom, marginRight );
        break;
      case PageFormat.LANDSCAPE:
        // right, top, left, bottom
        p.setSize( StrictGeomUtility.toExternalValue( page.getHeight() ), StrictGeomUtility.toExternalValue( page
            .getWidth() ) );
        PageFormatFactory.getInstance().setBorders( p, marginRight, marginTop, marginLeft, marginBottom );
        break;
      case PageFormat.REVERSE_LANDSCAPE:
        p.setSize( StrictGeomUtility.toExternalValue( page.getHeight() ), StrictGeomUtility.toExternalValue( page
            .getWidth() ) );
        PageFormatFactory.getInstance().setBorders( p, marginLeft, marginBottom, marginRight, marginTop );
        break;
      default:
        // will not happen..
        throw new IllegalArgumentException( "Unexpected page-orientation encountered." );
    }

    this.pageFormat = new PageFormat();
    this.pageFormat.setPaper( p );
    this.pageFormat.setOrientation( page.getOrientation() );
  }

  public PageFormat getPageFormat() {
    return pageFormat;
  }

  public Dimension getPreferredSize() {
    return new Dimension( (int) pageFormat.getWidth(), (int) pageFormat.getHeight() );
  }

  public boolean isPreserveAspectRatio() {
    return true;
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
    g2.translate( pageFormat.getImageableX(), pageFormat.getImageableY() );

    pageDrawable.draw( g2, new Rectangle2D.Double( StrictGeomUtility.toExternalValue( globalX ), StrictGeomUtility
        .toExternalValue( globalY ), pageFormat.getImageableWidth(), pageFormat.getImageableHeight() ) );
  }

  public RenderNode[] getNodesAt( final double x, final double y, final String namespace, final String name ) {
    return pageDrawable.getNodesAt( x - pageFormat.getImageableX(), y - pageFormat.getImageableY(), namespace, name );
  }

  public RenderNode[] getNodesAt( final double x, final double y, final double width, final double height,
      final String namespace, final String name ) {
    return pageDrawable.getNodesAt( x - pageFormat.getImageableX(), y - pageFormat.getImageableY(), width, height,
        namespace, name );
  }

  public LogicalPageDrawable getPageDrawable() {
    return pageDrawable;
  }
}
