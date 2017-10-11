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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;

import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;

/**
 * Creation-Date: 15.11.2006, 22:14:09
 *
 * @author Thomas Morgner
 */
public class DrawablePrintable implements Printable {
  private PageDrawable drawable;

  public DrawablePrintable( final PageDrawable drawable ) {
    this.drawable = drawable;
  }

  /**
   * Prints the page at the specified index into the specified {@link java.awt.Graphics} context in the specified
   * format. A <code>PrinterJob</code> calls the <code>Printable</code> interface to request that a page be rendered
   * into the context specified by <code>graphics</code>. The format of the page to be drawn is specified by
   * <code>pageFormat</code>. The zero based index of the requested page is specified by <code>pageIndex</code>. If the
   * requested page does not exist then this method returns NO_SUCH_PAGE; otherwise PAGE_EXISTS is returned. The
   * <code>Graphics</code> class or subclass implements the {@link java.awt.print.PrinterGraphics} interface to provide
   * additional information. If the <code>Printable</code> object aborts the print job then it throws a
   * {@link java.awt.print.PrinterException}.
   *
   * @param graphics
   *          the context into which the page is drawn
   * @param pageFormat
   *          the size and orientation of the page being drawn
   * @param pageIndex
   *          the zero based index of the page to be drawn
   * @return PAGE_EXISTS if the page is rendered successfully or NO_SUCH_PAGE if <code>pageIndex</code> specifies a
   *         non-existent page.
   * @throws java.awt.print.PrinterException
   *           thrown when the print job is terminated.
   */
  public int print( final Graphics graphics, final PageFormat pageFormat, final int pageIndex ) throws PrinterException {
    if ( drawable == null ) {
      return Printable.NO_SUCH_PAGE;
    }

    final Graphics2D g2 = (Graphics2D) graphics;
    final Rectangle2D bounds =
        new Rectangle2D.Double( 0, 0, pageFormat.getImageableWidth(), pageFormat.getImageableHeight() );
    drawable.draw( g2, bounds );
    return Printable.PAGE_EXISTS;
  }
}
