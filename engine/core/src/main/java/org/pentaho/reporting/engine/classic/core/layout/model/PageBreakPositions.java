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

public interface PageBreakPositions {
  long computeFixedPositionInFlow( long shiftedBoxPosition, long fixedPositionResolved );

  boolean isCrossingPagebreakWithFixedPosition( long shiftedBoxPosition, long height, long fixedPositionResolved );

  /**
   * Locates the page-start for a given page-end position.
   *
   * @param pageEndPosition
   *          the current page-end for which to find the page-start.
   * @return the start position for the page ending on <code>pageEndPosition</code>.
   * @throws IllegalStateException
   *           if the given pageEndPosition does not correspond to a major break.
   */
  long findPageStartPositionForPageEndPosition( final long pageEndPosition );

  /**
   * Locates the page-end for a given page-start position.
   *
   * @param pageStartPosition
   *          the current page-start for which to find the page-end.
   * @return the end position for the page starting on <code>pageStartPosition</code>.
   * @throws IllegalStateException
   *           if the given pageStartPosition does not correspond to a major break.
   */
  long findPageEndForPageStartPosition( final long pageStartPosition );

  /**
   * Checks, whether the given box will cross a pagebreak. The box's y-position is shifted by the given amount before
   * testing the result. A box will cross a pagebreak if its shifted y position and its shifted y2 position (y + height)
   * are located on different pages. A box with a height of zero cannot cross a pagebreak by definition.
   *
   * @param boxY
   *          the box Y, unshifted.
   * @param boxHeight
   *          the box height.
   * @param pagebreakShift
   *          the current shift that should be applied for the test
   * @return true, if the box crosses a pagebreak, false otherwise.
   */
  public boolean isCrossingPagebreak( final long boxY, final long boxHeight, final long pagebreakShift );

  /**
   * Finds the closest break-position that is larger or equal to the given position. This returns the next pagebreak in
   * the flow after the given position. If the position given is larger than the largest posible page-break, then this
   * returns the last pagebreak instead.
   *
   * @param position
   *          the position from where to search the next pagebreak.
   * @return the position.
   */
  long findNextBreakPosition( long position );

  /**
   * Finds the closest master break-position that is larger or equal to the given position. A master pagebreak is the
   * boundary of a logical page, which in itself can consist of several physical pages.
   * <p/>
   * This returns the next master pagebreak in the flow after the given position. If the position given is larger than
   * the largest posible page-break, then this returns the last pagebreak instead.
   *
   * @param position
   *          the position from where to search the next pagebreak.
   * @return the position.
   */
  long findNextMajorBreakPosition( long position );

  long findPreviousBreakPosition( final long position );

  public boolean isPageStart( final long position );

}
