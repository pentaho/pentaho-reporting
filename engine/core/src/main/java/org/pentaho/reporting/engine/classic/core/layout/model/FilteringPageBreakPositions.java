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

package org.pentaho.reporting.engine.classic.core.layout.model;

public class FilteringPageBreakPositions implements PageBreakPositions {
  private PageBreakPositions backend;
  private long pageStart;

  public FilteringPageBreakPositions( final PageBreakPositions backend, final long pageStart ) {
    this.backend = backend;
    this.pageStart = pageStart;
  }

  /**
   * Finds the closest break-position that is larger or equal to the given position. This returns the next pagebreak in
   * the flow after the given position. If the position given is larger than the largest posible page-break, then this
   * returns the last pagebreak instead.
   *
   * @param position
   *          the position from where to search the next pagebreak.
   * @return the position.
   */
  public long findNextBreakPosition( final long position ) {
    if ( position <= pageStart ) {
      return pageStart;
    }

    return backend.findNextBreakPosition( position );
  }

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
  public long findNextMajorBreakPosition( final long position ) {
    if ( position <= pageStart ) {
      return pageStart;
    }
    return backend.findNextMajorBreakPosition( position );
  }

  public long findPreviousBreakPosition( final long position ) {
    return Math.max( pageStart, backend.findPreviousBreakPosition( position ) );
  }

  public long findPageStartPositionForPageEndPosition( final long pageOffset ) {
    if ( pageOffset == 0 ) {
      return 0;
    }
    if ( pageOffset < pageStart ) {
      throw new IllegalStateException();
    }
    if ( pageOffset == pageStart ) {
      return 0;
    }
    return backend.findPageStartPositionForPageEndPosition( pageOffset );
  }

  public long findPageEndForPageStartPosition( final long pageOffset ) {
    if ( pageOffset <= pageStart ) {
      return -1;
    }
    return backend.findPageEndForPageStartPosition( pageOffset );
  }

  public boolean isCrossingPagebreak( final long boxY, final long boxHeight, final long pagebreakShift ) {
    final long shiftedYPos = boxY + pagebreakShift;
    if ( shiftedYPos <= pageStart ) {
      if ( shiftedYPos + boxHeight >= pageStart ) {
        return true;
      }
      return false;
    }

    return backend.isCrossingPagebreak( boxY, boxHeight, pagebreakShift );
  }

  public boolean isCrossingPagebreakWithFixedPosition( final long shiftedBoxPosition, final long height,
      final long fixedPositionResolved ) {
    if ( shiftedBoxPosition <= pageStart ) {
      // by definition: We cannot support fixed-position handling within the complex pagebreak schema of tables.
      return false;
    }
    return backend.isCrossingPagebreakWithFixedPosition( shiftedBoxPosition, height, fixedPositionResolved );
  }

  public long computeFixedPositionInFlow( final long shiftedBoxPosition, final long fixedPositionResolved ) {
    if ( shiftedBoxPosition <= pageStart ) {
      // by definition: We cannot support fixed-position handling within the complex pagebreak schema of tables.
      return shiftedBoxPosition;
    }
    return Math.max( pageStart, backend.computeFixedPositionInFlow( shiftedBoxPosition, fixedPositionResolved ) );
  }

  public boolean isPageStart( final long position ) {
    if ( position < pageStart ) {
      return false;
    }
    if ( position == pageStart ) {
      return true;
    }

    return backend.isPageStart( position );
  }
}
