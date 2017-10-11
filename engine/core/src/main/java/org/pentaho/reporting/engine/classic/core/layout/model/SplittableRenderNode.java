/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.model;

/**
 * This interface represents a component, that can be split horizontally. This is needed, if the component's width
 * exceeds a line's capacity.
 *
 * @author Andrey Khayrutdinov
 * @see RenderableText
 */
public interface SplittableRenderNode {

  /**
   * Splits the component into two children. The width of first cannot exceed {@code widthOfFirst}. If the separation is
   * impossible {@code null} is returned.
   * <p/>
   * Note, there is no guarantee that the first kid has width equal to {@code widthOfFirst}.
   *
   * @param widthOfFirst
   *          the maximum width of the first component
   * @return a pair of children or {@code null}
   * @throws IllegalStateException
   *           if {@code widthOfFirst >= getMinimumWidth()}
   */
  RenderNode[] splitBy( long widthOfFirst );

  /**
   * Returns the component's minimum width.
   * <p/>
   * A layout processor can be implemented so, that it does not compute its children's positions on the spot, postponing
   * this action until the end. However, if it finds out the component should be broken, it stops the routine. And this
   * is the case when this method is useful, as it helps not to change the implementation of the processor. Normally
   * each layout component knows about its bounds, hence it will not takes much effort to implement the method.
   *
   * @return the minimum width of the component
   */
  long getMinimumWidth();
}
