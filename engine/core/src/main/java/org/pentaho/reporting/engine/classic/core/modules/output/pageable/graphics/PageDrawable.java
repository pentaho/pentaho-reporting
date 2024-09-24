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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * The PageDrawable can be used to render a single page into an arbitary Graphics2D context. PageDrawables are created
 * by the PrintReportProcessor.
 *
 * @author Thomas Morgner
 */
public interface PageDrawable {

  /**
   * Describes the physical output characteristics like page size, margins, and imaginable area.
   *
   * @return a pageformat describing the current page including size and margins.
   */
  public PageFormat getPageFormat();

  /**
   * Returns the preferred size of the output. This is part of the implicit 'drawable' contract.
   *
   * @return the preferred size.
   */
  public Dimension getPreferredSize();

  /**
   * Renders the content of the drawable.
   *
   * @param graphics
   *          the graphics context to which to render to.
   * @param bounds
   *          the bounds within which the content should be rendered.
   */
  public void draw( final Graphics2D graphics, final Rectangle2D bounds );

  /**
   * Returns all layouted render-nodes that occupy the given point and which have the attribute specified by name and
   * namespace set. If name and namespace are null, all elements are returned. All units are given in the same
   * coordinate system used for rendering via the "draw" method.
   *
   * @param x
   *          the x coordinate of the point.
   * @param y
   *          the y coordinate of the point.
   * @param namespace
   *          the attribute's namespace.
   * @param name
   *          the attribute's name.
   * @return the nodes for the given point, or an empty array, but never null.
   */
  public RenderNode[] getNodesAt( final double x, final double y, final String namespace, final String name );

  /**
   * Returns all layouted render-nodes that occupy the given area and which have the attribute specified by name and
   * namespace set. If name and namespace are null, all elements are returned. All units are given in the same
   * coordinate system used for rendering via the "draw" method.
   *
   * @param x
   *          the x coordinate of the origin of the area.
   * @param y
   *          the y coordinate of the origin of the area.
   * @param width
   *          the height of the area.
   * @param height
   *          the width of the area.
   * @param namespace
   *          the attribute's namespace.
   * @param name
   *          the attribute's name.
   * @return the nodes for the given point, or an empty array, but never null.
   */
  public RenderNode[] getNodesAt( final double x, final double y, final double width, final double height,
      final String namespace, final String name );

}
