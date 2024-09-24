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
public final class RightAlignmentProcessor extends AbstractAlignmentProcessor {

  public RightAlignmentProcessor() {
  }

  /**
   * Handle the next input chunk.
   *
   * @param start
   *          the start index
   * @param count
   *          the number of elements in the sequence
   * @return the index of the last element that will fit on the current line.
   */
  protected int handleElement( final int start, final int count ) {
    final RenderNode[] nodes = getNodes();
    final InlineSequenceElement[] sequenceElements = getSequenceElements();
    final long[] elementDimensions = getElementDimensions();
    final long[] elementPositions = getElementPositions();

    // if we reached that method, then this means, that the elements may fit
    // into the available space. (Assuming that there is no inner pagebreak;
    // a thing we do not handle yet)
    final int endIndex = start + count;
    long usedWidth = 0;
    int contentIndex = start;
    InlineSequenceElement contentElement = null;
    for ( int i = 0; i < endIndex; i++ ) {
      final InlineSequenceElement element = sequenceElements[i];
      final RenderNode node = nodes[i];
      usedWidth += element.getMaximumWidth( node );
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
      // We have to right align the content up to the element denoted with 'start'.

      // Ignore the retval, we know that it fits (or at least that it is correct some how ..)
      rightAlign( start, sequenceElements, nodes, elementPositions, elementDimensions );

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

    // This implementation does not handle inline-block elements correctly.
    // but for the classic engine, this is less important, as we do not allow them anyway.

    if ( rightAlign( endIndex, sequenceElements, nodes, elementPositions, elementDimensions ) ) {
      return endIndex;
    }
    return start;
  }

  private boolean rightAlign( final int endIndex, final InlineSequenceElement[] sequenceElements,
      final RenderNode[] nodes, final long[] elementPositions, final long[] elementDimensions ) {
    // iterate backwards ..
    // The left-edge. This one is fixed; crossing this edge will be punished ..
    final long startOfLine = getStartOfLine();
    // the current segment.
    int segment = getPagebreakCount() - 1;
    long endPosition = getEndOfLine();
    long segmentStart = getStartOfSegment( segment );

    for ( int i = endIndex - 1; i >= 0; i-- ) {
      final InlineSequenceElement element = sequenceElements[i];
      final long elementWidth = element.getMaximumWidth( nodes[i] );
      long elementStart = endPosition - elementWidth;
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
      elementDimensions[i] = elementWidth;
      endPosition = elementStart;
    }

    return true;
  }

  private long getStartOfSegment( final int segment ) {
    if ( segment == 0 ) {
      return getStartOfLine();
    }

    return getPageBreak( segment - 1 );
  }

}
