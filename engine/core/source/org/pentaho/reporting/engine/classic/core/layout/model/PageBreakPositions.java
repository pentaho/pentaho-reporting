package org.pentaho.reporting.engine.classic.core.layout.model;

public interface PageBreakPositions
{
  long computeFixedPositionInFlow(long shiftedBoxPosition, long fixedPositionResolved);

  boolean isCrossingPagebreakWithFixedPosition(long shiftedBoxPosition, long height, long fixedPositionResolved);

  /**
   * Locates the page-start for a given page-end position.
   *
   * @param pageEndPosition the current page-end for which to find the page-start.
   * @return the start position for the page ending on <code>pageEndPosition</code>.
   * @throws IllegalStateException if the given pageEndPosition does not correspond to a major break.
   */
  long findPageStartPositionForPageEndPosition(final long pageEndPosition);

  /**
   * Checks, whether the given box will cross a pagebreak. The box's y-position is shifted by the given amount before
   * testing the result. A box will cross a pagebreak if its shifted y position and its shifted y2 position (y + height)
   * are located on different pages. A box with a height of zero cannot cross a pagebreak by definition.
   *
   * @param box the box, unshifted.
   * @param shift the current shift that should be applied for the test
   * @return true, if the box crosses a pagebreak, false otherwise.
   */
  boolean isCrossingPagebreak(RenderBox box, long shift);

  /**
   * Finds the closest break-position that is larger or equal to the given position.
   * This returns the next pagebreak in the flow after the given position. If the position given is larger than the
   * largest posible page-break, then this returns the last pagebreak instead.
   *
   * @param position the position from where to search the next pagebreak.
   * @return the position.
   */
  long findNextBreakPosition(long position);

  /**
   * Finds the closest master break-position that is larger or equal to the given position. A master pagebreak is the
   * boundary of a logical page, which in itself can consist of several physical pages.
   *
   * This returns the next master pagebreak in the flow after the given position. If the position given is larger than the
   * largest posible page-break, then this returns the last pagebreak instead.
   *
   * @param position the position from where to search the next pagebreak.
   * @return the position.
   */
  long findNextMajorBreakPosition(long position);
}
