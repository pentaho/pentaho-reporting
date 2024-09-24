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

package org.pentaho.reporting.engine.classic.core.layout.process.alignment;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SplittableRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineBoxSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;

/**
 * Right alignment strategy. Not working yet, as this is unimplemented right now.
 *
 * @author Thomas Morgner
 */
public final class CenterAlignmentProcessor extends AbstractAlignmentProcessor {
  public CenterAlignmentProcessor() {
  }

  protected int handleElement( final int start, final int count ) {
    final InlineSequenceElement[] sequenceElements = getSequenceElements();
    final RenderNode[] nodes = getNodes();
    final long[] elementDimensions = getElementDimensions();
    final long[] elementPositions = getElementPositions();

    // if we reached that method, then this means, that the elements may fit
    // into the available space. (Assuming that there is no inner pagebreak;
    // a thing we do not handle yet)
    final int endIndex = start + count;
    long usedWidth = 0;
    long usedWidthToStart = 0;
    int contentIndex = start;
    InlineSequenceElement contentElement = null;
    for ( int i = 0; i < endIndex; i++ ) {
      final InlineSequenceElement element = sequenceElements[i];
      final RenderNode node = nodes[i];
      usedWidth += element.getMaximumWidth( node );
      if ( i < start ) {
        usedWidthToStart += element.getMaximumWidth( node );
      }
      if ( isBorderMarker( element ) ) {
        continue;
      }
      contentElement = element;
      contentIndex = i;
    }

    final long nextPosition = ( getStartOfLine() + usedWidth );
    final long lastPageBreak = getPageBreak( getPagebreakCount() - 1 );
    if ( nextPosition > lastPageBreak ) {
      // The contents we processed so far will not fit on the current line. That's dangerous.
      // We have to center align the content up to the start position.
      performCenterAlignment( start, usedWidthToStart, sequenceElements, nodes, elementDimensions, elementPositions );

      // we cross a pagebreak. Stop working on it - we bail out here.

      if ( nodes[contentIndex] instanceof SplittableRenderNode ) {
        // the element may be splittable. Test, and if so, give a hint to the
        // outside world ..
        setSkipIndex( endIndex );
        setBreakableIndex( contentIndex );
        setBreakableMaxAllowedWidth( nextPosition - lastPageBreak );
        return ( start );
      }

      // This is the first element and it still does not fit. How evil.
      if ( start == 0 ) {
        if ( contentElement instanceof InlineBoxSequenceElement ) {
          final RenderNode node = nodes[contentIndex];
          if ( ( node.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
            // OK, limit the size of the box to the maximum line width and
            // revalidate it.
            final long contentPosition = elementPositions[contentIndex];
            final RenderBox box = (RenderBox) node;
            final long maxWidth = ( getEndOfLine() - contentPosition );
            computeInlineBlock( box, contentPosition, maxWidth );

            elementDimensions[endIndex - 1] = node.getCachedWidth();
          }
        }
        setSkipIndex( endIndex );
      }
      return ( start );
    }

    // if we reached that method, then this means, that the elements may fit
    // into the available space. (Assuming that there is no inner pagebreak;
    // a thing we do not handle yet)

    if ( performCenterAlignment( endIndex, usedWidth, sequenceElements, nodes, elementDimensions, elementPositions ) ) {
      return endIndex;
    }
    return start;
  }

  private boolean performCenterAlignment( final int endIndex, final long usedWidth,
      final InlineSequenceElement[] sequenceElements, final RenderNode[] nodes, final long[] elementDimensions,
      final long[] elementPositions ) {
    final long startOfLine = getStartOfLine();
    final long totalWidth = getEndOfLine() - startOfLine;
    final long emptySpace = Math.max( 0, ( totalWidth - usedWidth ) );
    long position = startOfLine + emptySpace / 2;
    // first, make a very simple distribution of the text over all the space, and ignore the pagebreaks
    for ( int i = 0; i < endIndex; i++ ) {
      final RenderNode node = nodes[i];
      final long elementWidth = sequenceElements[i].getMaximumWidth( node );
      elementDimensions[i] = elementWidth;
      elementPositions[i] = position;

      position += elementWidth;
    }

    // If this does not span over multiple pages, we are finished now.
    // in case the centered text is larger than the available space, we fall back to left-alignment later
    if ( getPagebreakCount() == 1 ) {
      return true;
    }

    // Now search the element at the center-point.
    // Find the center-point of the element and the center point (and center element) of the elements.
    final long centerPoint = startOfLine + totalWidth / 2;
    final int centerPageSegment = findStartOfPageSegmentForPosition( centerPoint );
    final int centerPageSegmentNext = Math.min( getPagebreakCount() - 1, centerPageSegment + 1 );
    final long centerPageSegmentStart = getPageBreak( centerPageSegment );

    final int leftShiftEndIndex;
    final int rightShiftStartIndex;
    if ( centerPageSegmentStart == centerPoint ) {
      // case 1: The center point sits directly on a pagebreak. This means, we shift the element touching the center
      // point to the left; and everything else is shifted to the right.
      final int centerElement = findElementLeftOfPosition( centerPoint, endIndex );
      final long centerElementPosition = elementPositions[centerElement];
      final long centerElementEnd = centerElementPosition + elementDimensions[centerElement];
      if ( ( centerPoint - centerElementPosition ) > ( centerElementEnd - centerPoint ) ) {
        leftShiftEndIndex = centerElement + 1;
        rightShiftStartIndex = centerElement + 1;
      } else {
        leftShiftEndIndex = centerElement;
        rightShiftStartIndex = centerElement;
      }
    } else {
      // the end-of-line is always included in the page-break-pos array.
      final int endOfLineSegment = getPagebreakCount() - 1;
      final int startOfLineSegment = 0;
      if ( centerPageSegment > startOfLineSegment ) {
        final int leftElement = findElementLeftOfPosition( centerPageSegmentStart, endIndex );
        final long elementPosition = elementPositions[leftElement];
        final long elementEnd = elementPosition + elementDimensions[leftElement];
        if ( elementEnd < centerPageSegmentStart ) {
          // if the element found fully fits on the left-hand area, include it in the shift to the left
          leftShiftEndIndex = leftElement + 1;
        } else {
          // otherwise shift it to the right
          leftShiftEndIndex = leftElement;
        }
      } else {
        leftShiftEndIndex = 0;
      }
      if ( centerPageSegment < endOfLineSegment ) {
        // we also have some elements that need to be shifted to the right.
        final long centerPageSegmentEnd = getPageBreak( centerPageSegmentNext );
        final int rightElement = findElementLeftOfPosition( centerPageSegmentEnd, endIndex );
        final long elementPosition = elementPositions[rightElement];
        final long elementEnd = elementPosition + elementDimensions[rightElement];
        if ( elementEnd < centerPageSegmentEnd ) {
          // if the element found fully fits on the left-hand area, include it in the shift to the left
          rightShiftStartIndex = rightElement + 1;
        } else {
          // otherwise shift it to the right
          rightShiftStartIndex = rightElement;
        }
      } else {
        rightShiftStartIndex = endIndex;
      }
    }

    // The distance between start of the element and the center point is greater
    // than the distance between the center point and the end of the element, then shift the center element
    // to the left. Also shift it to the left, if the element is the only element that should be centered.
    final long[] savedElementPos = (long[]) elementPositions.clone();

    // The center-element will be shifted to the right.
    if ( performShiftLeft( leftShiftEndIndex, centerPageSegment, savedElementPos )
        && performShiftRight( rightShiftStartIndex, endIndex, centerPageSegmentNext, savedElementPos ) ) {
      System.arraycopy( savedElementPos, 0, elementPositions, 0, savedElementPos.length );
      return true;
    }
    return false;
  }

  private boolean performShiftRight( final int firstElementIndex, final int lastElementIndex, int segment,
      final long[] elementPositions ) {
    if ( firstElementIndex >= lastElementIndex ) {
      // nothing to do here ..
      return true;
    }

    final long[] elementDimensions = getElementDimensions();
    final long endOfLine = getEndOfLine();

    // We dont need the start of the center-segment, we need the end of it.
    // int segment = findStartOfPageSegmentForPosition(centerPoint) + 1;
    final int pagebreakCount = getPagebreakCount();
    // prevent crash.
    if ( segment >= pagebreakCount ) {
      // Indicate that the element will not fit. More correct: the findStart.. method returned the
      // last segment of the page. There is no space to shift anything to the right ..
      return false;
    }
    long segmentEnd = getPageBreak( segment );
    long segmentStart = getStartOfSegment( segment );

    for ( int i = firstElementIndex; i < lastElementIndex; i++ ) {
      final long elementWidth = elementDimensions[i];
      long elementEnd = segmentStart + elementWidth;
      if ( elementEnd > endOfLine ) {
        // this element will not fit ..
        return false;
      }

      // make a while a if so that we shift the element only once. This results in a slightly better laoyout
      if ( ( ( segment + 1 ) < pagebreakCount ) && ( elementEnd > segmentEnd ) ) {
        // as long as there are more segments where we could shift the element to and as long as the
        // element does not fit into the current segment
        // try the next segment ..
        segment += 1;
        segmentStart = segmentEnd;
        segmentEnd = getPageBreak( segment );
        elementEnd = segmentStart + elementWidth;
      }

      if ( elementEnd > endOfLine ) {
        // the element will not fit into any of the remaining segments. So skip it.
        return false;
      }

      elementPositions[i] = segmentStart;
      segmentStart = elementEnd;
    }

    return true;
  }

  private boolean performShiftLeft( final int lastElementIndex, int segment, final long[] elementPositions ) {
    if ( lastElementIndex == 0 ) {
      // there is nothing to shift here ..
      return true;
    }

    // This code only fires if we distribute an element over more than a single page segment.
    // The text that is left of the center segment will be shifted to the left and right-aligned there.
    // The "centerPoint specifies the center of the element and therefore defines which segment is
    // considered the center-segment.

    // we will work on a clone, so that the undo is easier ..
    final long[] elementDimensions = getElementDimensions();

    final int elementIdx = lastElementIndex - 1;

    // iterate backwards; start from the center element and right align all previous elements ..
    final long startOfLine = getStartOfLine();
    // the current segment.

    //

    // int segment = findStartOfPageSegmentForPosition(centerPoint);
    long segmentEnd = getPageBreak( segment );
    long segmentStart = getStartOfSegment( segment );

    for ( int i = elementIdx; i >= 0; i-- ) {
      final long elementWidth = elementDimensions[i];
      long elementStart = segmentEnd - elementWidth;
      if ( elementStart < startOfLine ) {
        // this element will not fit. Skip it.
        return false;
      }

      while ( segment > 0 && elementStart < segmentStart ) {
        // the element will not fit into the current segment. Move it to the next segment.
        elementStart = segmentStart - elementWidth;
        segment -= 1;
        segmentStart = getStartOfSegment( segment );
      }

      if ( elementStart < segmentStart ) {
        // the element will not fit into any of the remaining segments. So skip it.
        return false;
      }

      elementPositions[i] = elementStart;
      segmentEnd = elementStart;
    }

    // Commit the changes ..
    return true;
  }

  private long getStartOfSegment( final int segment ) {
    if ( segment <= 0 ) {
      return getStartOfLine();
    }

    return getPageBreak( segment - 1 );
  }

  /**
   * Returns the index of the previous pagebreak (the page-boundary that is left to the given position) for the
   * specified position. This specifies the page-segment in which the position sits.
   *
   * @param position
   *          the position in micro-points.
   * @return the number of the page segment.
   */
  private int findStartOfPageSegmentForPosition( final long position ) {
    final long[] breaks = getPageBreaks();
    final int elementSize = getPagebreakCount();
    final int i = CenterAlignmentProcessor.binarySearch( breaks, position, elementSize );
    if ( i > -1 ) {
      return i;
    }
    if ( i == -1 ) {
      return 0;
    }

    return Math.min( -( i + 2 ), elementSize - 1 );
  }

  /**
   * Finds the element that is closest to the given position without being larger than the position. This method returns
   * endIndex - 1 if all elements are smaller than the given position.
   *
   * @param position
   *          the position for which an element should be found.
   * @param endIndex
   *          the maximum index on which to search in the element-positions list.
   * @return the index of the element closes to the given position.
   */
  private int findElementLeftOfPosition( final long position, final int endIndex ) {
    final long[] elementPositions = getElementPositions();
    final int i = CenterAlignmentProcessor.binarySearch( elementPositions, position, endIndex );
    if ( i > -1 ) {
      return i;
    }
    if ( i == -1 ) {
      return 0;
    }

    // if greater than last break, return the last break ..
    return Math.min( -( i + 2 ), endIndex - 1 );
  }

  private static int binarySearch( final long[] array, final long key, final int end ) {
    int low = 0;
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

}
