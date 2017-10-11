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

package org.pentaho.reporting.engine.classic.core.layout.model;

import java.util.Arrays;

public class PageBreakPositionList implements PageBreakPositions {
  private static class PageBreakPositionBackend {
    public long[] pageHeaderSizes;
    public long[] masterBreaks;
    public long[] breakPositions;

    private PageBreakPositionBackend() {
      pageHeaderSizes = new long[100];
      masterBreaks = new long[100];
      breakPositions = new long[100];
    }

    public void copyFrom( final PageBreakPositionBackend parentList ) {
      final long[] parentBreaks = parentList.breakPositions;
      if ( parentBreaks.length > this.breakPositions.length ) {
        this.breakPositions = new long[parentBreaks.length];
      }
      System.arraycopy( parentBreaks, 0, breakPositions, 0, parentBreaks.length );

      if ( parentList.masterBreaks.length > this.masterBreaks.length ) {
        this.masterBreaks = new long[parentList.masterBreaks.length];
      }
      System.arraycopy( parentList.masterBreaks, 0, masterBreaks, 0, parentList.masterBreaks.length );

      if ( parentList.pageHeaderSizes.length > this.pageHeaderSizes.length ) {
        this.pageHeaderSizes = new long[parentList.pageHeaderSizes.length];
      }
      System.arraycopy( parentList.pageHeaderSizes, 0, pageHeaderSizes, 0, parentList.pageHeaderSizes.length );
    }

    private void ensureSize( final int breakSize, final int masterSize ) {
      if ( breakSize >= breakPositions.length ) {
        final int newSize = breakSize + Math.min( Math.max( breakSize / 2, 5 ), 512 );
        final long[] newBreakPositions = new long[newSize];
        System.arraycopy( breakPositions, 0, newBreakPositions, 0, breakPositions.length );
        this.breakPositions = newBreakPositions;
      }

      if ( masterSize >= masterBreaks.length ) {
        final int newSize = masterSize + Math.min( Math.max( masterSize / 2, 5 ), 512 );
        final long[] newBreakPositions = new long[newSize];
        System.arraycopy( masterBreaks, 0, newBreakPositions, 0, masterBreaks.length );
        this.masterBreaks = newBreakPositions;
      }

      if ( masterSize >= pageHeaderSizes.length ) {
        final int newSize = masterSize + Math.min( Math.max( masterSize / 2, 5 ), 512 );
        final long[] newBreakPositions = new long[newSize];
        System.arraycopy( pageHeaderSizes, 0, newBreakPositions, 0, pageHeaderSizes.length );
        this.pageHeaderSizes = newBreakPositions;
      }
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final PageBreakPositionBackend that = (PageBreakPositionBackend) o;

      if ( !Arrays.equals( breakPositions, that.breakPositions ) ) {
        return false;
      }
      if ( !Arrays.equals( masterBreaks, that.masterBreaks ) ) {
        return false;
      }
      if ( !Arrays.equals( pageHeaderSizes, that.pageHeaderSizes ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = Arrays.hashCode( pageHeaderSizes );
      result = 31 * result + Arrays.hashCode( masterBreaks );
      result = 31 * result + Arrays.hashCode( breakPositions );
      return result;
    }
  }

  private int breakSize;
  private int masterSize;
  private int nextFoundIdx;
  private int prevFoundIdx;
  private int prevFoundMasterIdx;
  private int lastCommonBreak;
  private int lastMasterBreak;

  private PageBreakPositionBackend backend;
  private long scaleFactorMasters;
  private long scaleFactorMinors;
  private boolean enableQuickLookup;

  public PageBreakPositionList() {
    enableQuickLookup = true;

    // There is always a break at position ZERO. This break is always a master-break.
    backend = new PageBreakPositionBackend();
    breakSize = 1;
    masterSize = 1;
    scaleFactorMinors = 0;
    scaleFactorMasters = 0;
  }

  public PageBreakPositionList( final PageBreakPositionList parentList ) {
    this.enableQuickLookup = true;
    this.backend = parentList.backend;
    this.breakSize = parentList.breakSize;
    this.masterSize = parentList.masterSize;

    this.prevFoundMasterIdx = parentList.prevFoundMasterIdx;
    this.prevFoundIdx = parentList.prevFoundIdx;
    this.nextFoundIdx = parentList.nextFoundIdx;
    this.lastCommonBreak = parentList.lastCommonBreak;
    this.lastMasterBreak = parentList.lastMasterBreak;

    scaleFactorMinors = parentList.scaleFactorMinors;
    scaleFactorMasters = parentList.scaleFactorMasters;
  }

  public void copyFrom( final PageBreakPositionList parentList ) {
    this.backend.copyFrom( parentList.backend );
    this.breakSize = parentList.breakSize;
    this.masterSize = parentList.masterSize;

    this.prevFoundMasterIdx = parentList.prevFoundMasterIdx;
    this.prevFoundIdx = parentList.prevFoundIdx;
    this.nextFoundIdx = parentList.nextFoundIdx;
    this.lastCommonBreak = parentList.lastCommonBreak;
    this.lastMasterBreak = parentList.lastMasterBreak;

    scaleFactorMinors = parentList.scaleFactorMinors;
    scaleFactorMasters = parentList.scaleFactorMasters;
  }

  public void addMinorBreak( final long position ) {
    // If this results in a IOBEx, then we made something wrong and deserve the exception.
    final long lastPosition = backend.breakPositions[this.breakSize - 1];
    if ( position < lastPosition ) {
      // This usually happens if someone tries to pass a page with negative margins. We do not accept that.
      throw new IllegalArgumentException( "Invalid position error: Unsorted Entry or negative page area." );
    }

    backend.ensureSize( breakSize, masterSize );

    if ( position > lastPosition ) {
      backend.breakPositions[breakSize] = position;
      breakSize += 1;
      scaleFactorMinors = position / breakSize;
    }
  }

  public void addMajorBreak( final long position, final long pageHeaderSize ) {
    // If this results in a IOBEx, then we made something wrong and deserve the exception.
    final long lastPosition = backend.breakPositions[this.breakSize - 1];
    if ( position < lastPosition ) {
      // This usually happens if someone tries to pass a page with negative margins. We do not accept that.
      throw new IllegalArgumentException( "Invalid position error: Unsorted Entry or negative page area." );
    }

    backend.ensureSize( breakSize, masterSize );

    if ( position > lastPosition ) {
      backend.breakPositions[breakSize] = position;
      breakSize += 1;

      scaleFactorMinors = position / breakSize;
    }

    final long lastMaster = backend.masterBreaks[this.masterSize - 1];
    if ( position < lastMaster ) {
      throw new IllegalStateException( "Adding new values to the break-position list must be happen sorted." );
    }

    if ( position > lastMaster ) {
      backend.masterBreaks[masterSize] = position;
      backend.pageHeaderSizes[masterSize] = pageHeaderSize;
      masterSize += 1;

      scaleFactorMasters = position / masterSize;
    }
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
    final int breakIndex = findBreak( position );
    if ( breakIndex < 0 ) {
      return backend.breakPositions[0];
    }
    if ( breakIndex >= breakSize ) {
      return backend.breakPositions[breakSize - 1];
    }
    return backend.breakPositions[breakIndex];
  }

  public long findPreviousBreakPosition( final long position ) {
    final int breakIndex = findPreviousMajorBreak( position );
    if ( breakIndex < 0 ) {
      return backend.breakPositions[0];
    }
    if ( breakIndex >= breakSize ) {
      return backend.breakPositions[breakSize - 1];
    }
    return backend.breakPositions[breakIndex];
  }

  /**
   * Returns the page-segment, where a box would be located that ends at the given position. If the position is located
   * before or on the page start, then this method returns -1 to indicate that this box would not be displayed at all.
   * If the given position is direcly located on a page-boundary, the segment number of the previous page is returned.
   *
   * @param pos
   *          the starting position of the box.
   * @return -1 or a positive integer denoting the page segment where the box would be displayed.
   */
  private int findNextBreak( final long pos ) {
    int start = 0;
    int end = breakSize;

    if ( nextFoundIdx > 0 ) {
      final long foundPos = backend.breakPositions[nextFoundIdx];
      final long prevPos = backend.breakPositions[nextFoundIdx - 1];
      if ( foundPos >= pos && prevPos < pos ) {
        return nextFoundIdx - 1;
      }
    }

    if ( enableQuickLookup && breakSize > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = breakSize - 1;
      final long lastVal = backend.breakPositions[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactorMinors );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = backend.breakPositions[minTgtIdx];
        final long maxKey = backend.breakPositions[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }
      }
    }

    final int i = PageBreakPositionList.binarySearch( backend.breakPositions, pos, start, end );
    if ( i > -1 ) {
      nextFoundIdx = ( i - 1 );
      return nextFoundIdx;
    }
    if ( i == -1 ) {
      nextFoundIdx = -1;
      return -1;
    }

    final int insertPos = Math.min( -( i + 2 ), breakSize - 1 );
    // if greater than last break, return the last break ..
    nextFoundIdx = insertPos;
    return insertPos;

  }

  /**
   * Returns the page-segment, where a box would be located that starts at the given position. If the position is
   * located before the page start, then this method returns -1 to indicate that this box would not be displayed at all.
   *
   * @param pos
   *          the starting position of the box.
   * @return -1 or a positive integer denoting the page segment where the box would be displayed.
   */
  private int findPreviousBreak( final long pos ) {
    int start = 0;
    if ( prevFoundIdx >= 0 ) {
      final long prevFoundPos = backend.breakPositions[prevFoundIdx];
      if ( prevFoundPos == pos ) {
        return prevFoundIdx;
      }

      if ( prevFoundPos < pos ) {
        if ( prevFoundIdx >= ( breakSize - 1 ) ) {
          // This is behind or directly at the end of the known breaks ...
          return prevFoundIdx;
        }

        // Check, whether the next one would be smaller too
        final long nextBreak = backend.breakPositions[prevFoundIdx + 1];
        if ( nextBreak > pos ) {
          return prevFoundIdx;
        }
      }
    }

    int end = breakSize;
    if ( enableQuickLookup && breakSize > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = breakSize - 1;
      final long lastVal = backend.breakPositions[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactorMinors );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = backend.breakPositions[minTgtIdx];
        final long maxKey = backend.breakPositions[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }
      }
    }

    final int i = PageBreakPositionList.binarySearch( backend.breakPositions, pos, start, end );
    if ( i > -1 ) {
      prevFoundIdx = i;
      return prevFoundIdx;
    }

    if ( i == -1 ) {
      prevFoundIdx = -1;
      return -1;
    }

    final int insertPos = Math.min( -( i + 1 ), breakSize ) - 1;
    // if greater than last break, return the last break ..
    prevFoundIdx = insertPos;
    return insertPos;

  }

  /**
   * Returns the page-segment, where a box would be located that starts at the given position. If the position is
   * located before the page start, then this method returns -1 to indicate that this box would not be displayed at all.
   *
   * @param pos
   *          the starting position of the box.
   * @return -1 or a positive integer denoting the page segment where the box would be displayed.
   */
  private int findPreviousMajorBreak( final long pos ) {
    if ( prevFoundMasterIdx >= 0 ) {
      final long prevFoundPos = backend.masterBreaks[prevFoundMasterIdx];
      if ( prevFoundPos == pos ) {
        return prevFoundMasterIdx;
      }

      if ( prevFoundPos < pos ) {
        if ( prevFoundMasterIdx >= ( masterSize - 1 ) ) {
          // This is behind or directly at the end of the known breaks ...
          return prevFoundMasterIdx;
        }

        // Check, whether the next one would be smaller too
        final long nextBreak = backend.masterBreaks[prevFoundMasterIdx + 1];
        if ( nextBreak > pos ) {
          return prevFoundMasterIdx;
        }
      }
    }

    int start = 0;
    int end = masterSize;
    if ( enableQuickLookup && masterSize > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = masterSize - 1;
      final long lastVal = backend.masterBreaks[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactorMasters );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = backend.masterBreaks[minTgtIdx];
        final long maxKey = backend.masterBreaks[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }
      }
    }

    final int i = PageBreakPositionList.binarySearch( backend.masterBreaks, pos, start, end );
    if ( i > -1 ) {
      prevFoundMasterIdx = i;
      return prevFoundMasterIdx;
    }

    if ( i == -1 ) {
      prevFoundMasterIdx = -1;
      return -1;
    }

    final int insertPos = Math.min( -( i + 1 ), masterSize ) - 1;
    // if greater than last break, return the last break ..
    prevFoundMasterIdx = insertPos;
    return insertPos;

  }

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
  public boolean isCrossingPagebreak( final long boxY, final long boxHeight, final long pagebreakShift ) {
    // A box does not cross the break, if both Y1 and Y2 are on the same page.
    if ( boxHeight == 0 ) {
      // A box without a height can appear on either side of the pagebreak.
      // But under no circumstances it can cross it.
      return false;
    }

    // Simple case: No fixed position at all ..
    final long shiftedStartPos = boxY + pagebreakShift;
    final int y1 = findPreviousBreak( shiftedStartPos );
    final int y2 = findNextBreak( shiftedStartPos + boxHeight );
    return y1 != y2;
  }

  public boolean isCrossingPagebreakWithFixedPosition( final long shiftedBoxPosition, final long boxHeight,
      final long fixedPositionResolved ) {
    // A box does not cross the break, if both Y1 and Y2 are on the same page.
    if ( boxHeight == 0 ) {
      // A box without a height can appear on either side of the pagebreak. But under no circumstances it may cross it.
      return false;
    }

    // Only allow positive values.
    // Make sure that we do not cover the page header area. If so, then correct the value to be
    // directly below the header-area.

    // Compute, the distance between the fixed-positioned box and the bottom edge of the page-header.
    final long shiftedSpaceOnPage = Math.max( 0, ( fixedPositionResolved - getPageHeaderHeight( shiftedBoxPosition ) ) );
    // Compute the page-start on the normal flow.
    final int pageIndex = findPreviousMajorBreak( shiftedBoxPosition );
    final long fixedPositionInFlow;
    if ( pageIndex < 0 ) {
      fixedPositionInFlow = backend.masterBreaks[0] + shiftedSpaceOnPage;
    } else {
      fixedPositionInFlow = backend.masterBreaks[pageIndex] + shiftedSpaceOnPage;
    }

    final int y1 = findPreviousBreak( fixedPositionInFlow );
    final int y2 = findNextBreak( fixedPositionInFlow + boxHeight );
    return y1 != y2;
  }

  /**
   * Computes the box's position in the normal-flow that will fullfill the 'fixed-position' constraint. The result will
   * be the position on the current page. This position might sit on already processed content, so the caller has to
   * check whether the return value of this function is less than the shifted box position. In that case, the band must
   * cause a pagebreak before it can be positioned.
   *
   * @param shiftedBoxPosition
   * @param fixedPositionResolved
   * @return the computed fixed position, which may be invalid.
   */
  public long computeFixedPositionInFlow( final long shiftedBoxPosition, final long fixedPositionResolved ) {
    // (1) Compute the local position of the fixed-pos box in the current page. For that, lookup the current pageheader
    // height and subtract that from the total page size.
    final long pageHeaderHeight = getPageHeaderHeight( shiftedBoxPosition );
    final long positionInPageContentArea = Math.max( 0, ( fixedPositionResolved - pageHeaderHeight ) );

    // (2) Compute the page-start position in the normal flow for the current box.
    final int pageIndex = findPreviousMajorBreak( shiftedBoxPosition );
    final long pageStart;
    if ( pageIndex < 0 ) {
      pageStart = backend.masterBreaks[0];
    } else {
      pageStart = backend.masterBreaks[pageIndex];
    }

    return pageStart + positionInPageContentArea;
  }

  protected long getPageHeaderHeight( final long position ) {
    final int majorBreak = findNextMajorBreak( position );
    if ( isMasterBreak( position ) ) {
      return backend.pageHeaderSizes[Math.min( majorBreak + 1, masterSize - 1 )];
    }
    return backend.pageHeaderSizes[majorBreak];
  }

  /**
   * Returns the first break position that is greater than the given position.
   *
   * @param pos
   * @return -1 or a positive integer.
   */
  private int findBreak( final long pos ) {
    int start = 0;
    if ( lastCommonBreak >= breakSize ) {
      // the last known pagebreak.
      final long lastBreakPos = backend.breakPositions[breakSize - 1];
      if ( lastBreakPos > pos ) {
        // the position behind the last break, so return it
        return breakSize;
      }
      if ( lastBreakPos == pos ) {
        lastCommonBreak = breakSize - 1;
        return lastCommonBreak;
      }
      // else the position we search is lower. Search from the beginning ..
    } else if ( lastCommonBreak == 0 ) {
      final long lastBreakPos = backend.breakPositions[lastCommonBreak];
      if ( lastBreakPos >= pos ) {
        return lastCommonBreak;
      }
    } else {
      final long lastBreakPos = backend.breakPositions[lastCommonBreak];
      if ( lastBreakPos >= pos ) {
        final long prevBreakPos = backend.breakPositions[lastCommonBreak - 1];
        if ( prevBreakPos < pos ) {
          return lastCommonBreak;
        }
      }
    }

    int end = breakSize;
    if ( enableQuickLookup && breakSize > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = breakSize - 1;
      final long lastVal = backend.breakPositions[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactorMinors );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = backend.breakPositions[minTgtIdx];
        final long maxKey = backend.breakPositions[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }
      }
    }

    final int i = PageBreakPositionList.binarySearch( backend.breakPositions, pos, start, end );
    if ( i > -1 ) {
      lastCommonBreak = i;
      return i;
    }
    if ( i == -1 ) {
      // not found ..
      lastCommonBreak = 0;
      return 0;
    }

    final int insertPos = -( i + 1 );
    lastCommonBreak = insertPos;
    return insertPos;

  }

  /**
   * Returns the page number that would contain this position. Mapping the page-number into a pagebreak position returns
   * the page boundary (y + height). This method returns -1 if the given position is *before* the first page-boundary.
   *
   * @param pos
   * @return -1 or a positive integer.
   */
  private int findNextMajorBreak( final long pos ) {
    int start = 0;
    if ( lastMasterBreak >= masterSize ) {
      // the last known pagebreak.
      final long lastBreakPos = backend.masterBreaks[breakSize - 1];
      if ( lastBreakPos > pos ) {
        // the position behind the last break, so return it
        return masterSize;
      }
      if ( lastBreakPos == pos ) {
        lastMasterBreak = masterSize - 1;
        return lastMasterBreak;
      }
      // else the position we search is lower. Search from the beginning ..
    } else if ( lastMasterBreak == 0 ) {
      final long lastBreakPos = backend.masterBreaks[lastMasterBreak];
      if ( lastBreakPos >= pos ) {
        return lastMasterBreak;
      }
    } else {
      final long lastBreakPos = backend.masterBreaks[lastMasterBreak];
      if ( lastBreakPos >= pos ) {
        final long prevBreakPos = backend.masterBreaks[lastMasterBreak - 1];
        if ( prevBreakPos < pos ) {
          return lastMasterBreak;
        }
      }
    }

    int end = masterSize;
    if ( enableQuickLookup && masterSize > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = masterSize - 1;
      final long lastVal = backend.masterBreaks[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactorMasters );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = backend.masterBreaks[minTgtIdx];
        final long maxKey = backend.masterBreaks[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }
      }
    }

    final int i = PageBreakPositionList.binarySearch( backend.masterBreaks, pos, start, end );
    if ( i > -1 ) {
      lastMasterBreak = i;
      return i;
    }
    if ( i == -1 ) {
      // not found ..
      lastMasterBreak = 0;
      return 0;
    }

    final int insertPos = -( i + 1 );
    lastMasterBreak = insertPos;
    return insertPos;
  }

  private boolean isMasterBreak( final long pos ) {
    int start = 0;
    int end = masterSize;
    if ( enableQuickLookup && masterSize > 0 ) {
      // assume a relatively uniform layout, all rows have roughly the same size ..
      // this means, we can guess-jump close to the target-position ..
      final int maxIdx = masterSize - 1;
      final long lastVal = backend.masterBreaks[maxIdx];
      if ( lastVal > 0 ) {
        final int targetIdx = (int) ( pos / scaleFactorMasters );
        final int minTgtIdx = Math.max( 0, Math.min( maxIdx, targetIdx - 7 ) );
        final int maxTgtIdx = Math.min( maxIdx, targetIdx + 7 );

        final long minKey = backend.masterBreaks[minTgtIdx];
        final long maxKey = backend.masterBreaks[maxTgtIdx];
        final boolean minLessPos = pos >= minKey;
        final boolean maxMorePos = pos <= maxKey;
        if ( minLessPos ) {
          start = minTgtIdx;
        }
        if ( maxMorePos ) {
          end = maxTgtIdx + 1;
        }
      }
    }
    return ( PageBreakPositionList.binarySearch( backend.masterBreaks, pos, start, end ) > -1 );
  }

  private static int binarySearch( final long[] array, final long key, final int start, final int end ) {
    int low = start;
    int high = end - 1;

    while ( low <= high ) {
      final int mid = ( low + high ) >>> 1;
      final long midVal = array[mid];

      if ( midVal < key ) {
        low = mid + 1;
      } else if ( midVal > key ) {
        high = mid - 1;
      } else {
        return mid; // key found
      }
    }
    return -( low + 1 ); // key not found.
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
    final int majorBreakIndex = findNextMajorBreak( position );
    if ( majorBreakIndex < 0 ) {
      return backend.masterBreaks[0];
    }
    if ( majorBreakIndex >= masterSize ) {
      return backend.masterBreaks[masterSize - 1];
    }
    return backend.masterBreaks[majorBreakIndex];
  }

  public long getLastMasterBreak() {
    return backend.masterBreaks[masterSize - 1];
  }

  public int getMasterBreakSize() {
    return masterSize;
  }

  public long getMasterBreak( final int index ) {
    return backend.masterBreaks[index];
  }

  public String toString() {
    final StringBuilder retval = new StringBuilder( 100 );
    retval.append( "PageBreakPositionList{breakSize=" );
    retval.append( breakSize );
    retval.append( ", masterSize=" );
    retval.append( masterSize );
    retval.append( ", prevFoundIdx=" );
    retval.append( prevFoundIdx );
    retval.append( ", masterBreaks={" );

    final int masterBreakCount = masterSize;
    for ( int i = 0; i < masterBreakCount; i++ ) {
      if ( i > 0 ) {
        retval.append( ", " );
      }
      final long aBreak = backend.masterBreaks[i];
      retval.append( String.valueOf( aBreak ) );
    }
    retval.append( "}, breakPositions={" );

    final int breakPosCount = breakSize;
    for ( int i = 0; i < breakPosCount; i++ ) {
      if ( i > 0 ) {
        retval.append( ", " );
      }
      final long position = backend.breakPositions[i];
      retval.append( String.valueOf( position ) );

    }
    retval.append( "}}" );
    return retval.toString();
  }

  public long findPageEndForPageStartPosition( final long pageOffset ) {
    final int masterBreakSize = getMasterBreakSize();
    if ( masterBreakSize > 0 ) {
      final long lastBreak = getMasterBreak( masterBreakSize - 1 );
      if ( pageOffset == lastBreak ) {
        return lastBreak;
      }

      for ( int i = masterBreakSize - 2; i >= 0; i -= 1 ) {
        final long masterBreak = getMasterBreak( i );
        if ( masterBreak == pageOffset ) {
          return getMasterBreak( i + 1 );
        }
        if ( masterBreak < pageOffset ) {
          break;
        }
      }
    }
    throw new IllegalStateException( "Unable to locate proper page start for given offset " + pageOffset );
  }

  public long findPageStartPositionForPageEndPosition( final long pageOffset ) {
    final int masterBreakSize = getMasterBreakSize();
    for ( int i = masterBreakSize - 1; i > 0; i -= 1 ) {
      final long masterBreak = getMasterBreak( i );
      if ( masterBreak == pageOffset ) {
        return getMasterBreak( i - 1 );
      }
      if ( masterBreak < pageOffset ) {
        throw new IllegalStateException( "Unable to locate proper page start for given offset " + pageOffset );
      }
    }
    return 0;
  }

  public boolean isPageStart( final long position ) {
    return isMasterBreak( position );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final PageBreakPositionList that = (PageBreakPositionList) o;

    if ( breakSize != that.breakSize ) {
      return false;
    }
    if ( masterSize != that.masterSize ) {
      return false;
    }
    if ( !backend.equals( that.backend ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    int result = breakSize;
    result = 31 * result + masterSize;
    result = 31 * result + backend.hashCode();
    return result;
  }
}
