/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
