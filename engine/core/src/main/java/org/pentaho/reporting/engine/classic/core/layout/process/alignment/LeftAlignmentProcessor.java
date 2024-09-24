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
import org.pentaho.reporting.engine.classic.core.util.LongList;

/**
 * Performs the left-alignment computations.
 * <p/>
 * The inf-min-step creates the initial sequence of elements. The alignment processor now iterates over the sequence and
 * produces the layouted line.
 * <p/>
 * Elements can be split, splitting is a local operation and does not copy the children. Text splitting may produce a
 * totally different text (see: TeX hyphenation system).
 * <p/>
 * The process is iterative and continues unless all elements have been consumed.
 *
 * @author Thomas Morgner
 */
public class LeftAlignmentProcessor extends AbstractAlignmentProcessor {
  private long position;
  private int pageSegment;

  public LeftAlignmentProcessor() {
  }

  public int getPageSegment() {
    return pageSegment;
  }

  public void setPageSegment( final int pageSegment ) {
    this.pageSegment = pageSegment;
  }

  private long getPosition() {
    return position;
  }

  private void setPosition( final long position ) {
    this.position = position;
  }

  private void addPosition( final long width ) {
    this.position += width;
  }

  public RenderBox next() {
    position = getStartOfLine();
    pageSegment = 0;

    final RenderBox retval = super.next();

    position = 0;
    pageSegment = 0;

    return retval;
  }

  public void performLastLineAlignment() {
    position = getStartOfLine();
    pageSegment = 0;

    super.performLastLineAlignment();

    position = 0;
    pageSegment = 0;
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
    final InlineSequenceElement[] sequenceElements = getSequenceElements();
    final RenderNode[] nodes = getNodes();
    final long[] elementDimensions = getElementDimensions();
    final long[] elementPositions = getElementPositions();

    long width = 0;
    final int endIndex = start + count;

    // In the given range, there should be only one content element.
    InlineSequenceElement contentElement = null;
    int contentIndex = start;
    for ( int i = start; i < endIndex; i++ ) {
      final InlineSequenceElement element = sequenceElements[i];
      final RenderNode node = nodes[i];
      if ( isBorderMarker( element ) ) {
        width += element.getMaximumWidth( node );
        continue;
      }

      width += element.getMaximumWidth( node );
      contentElement = element;
      contentIndex = i;
    }

    final long nextPosition = getPosition() + width;
    final long lastPageBreak = getPageBreak( getPagebreakCount() - 1 );
    // Do we cross a page boundary?
    if ( nextPosition > lastPageBreak ) {
      // On outer break: Stop processing

      // Dont write through to the stored position; but prepare if
      // we have to fallback ..
      long position = getPosition();
      for ( int i = start; i < endIndex; i++ ) {
        final InlineSequenceElement element = sequenceElements[i];
        final RenderNode node = nodes[i];
        elementPositions[i] = position;
        final long elementWidth = element.getMaximumWidth( node );
        elementDimensions[i] = elementWidth;
        position += elementWidth;
      }

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

    final long innerPagebreak = getPageBreak( getPageSegment() );
    if ( nextPosition > innerPagebreak ) {
      // It is an inner pagebreak and the current element would not fit into the remaining space.
      // Move the element to the next page segment (but only if the start is not on
      setPosition( innerPagebreak );
      setPageSegment( getPageSegment() + 1 );
    }

    // No, it is an ordinary advance ..
    // Check, whether we hit an item-sequence element
    if ( contentElement instanceof InlineBoxSequenceElement == false ) {
      for ( int i = start; i < endIndex; i++ ) {
        final RenderNode node = nodes[i];
        final InlineSequenceElement element = sequenceElements[i];
        elementPositions[i] = getPosition();
        final long elementWidth = element.getMaximumWidth( node );
        elementDimensions[i] = elementWidth;
        addPosition( elementWidth );
      }
      return endIndex;
    }

    // Handle the ItemSequence element.

    // This is a bit more complicated. So we encountered an inline-block
    // element here. That means, the element will try to occuppy its
    // maximum-content-width.
    // Log.debug("Advance block at index " + contentIndex);
    // final long ceWidth = contentElement.getMinimumWidth();
    // final long extraSpace = contentElement.getMaximumWidth();
    // Log.debug("Advance block: Min " + ceWidth);
    // Log.debug("Advance block: Max " + extraSpace);

    final RenderNode contentNode = nodes[contentIndex];
    final long itemElementWidth = contentElement.getMaximumWidth( contentNode );

    if ( ( contentNode.getNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) contentNode;
      computeInlineBlock( box, getPosition(), itemElementWidth );
    } else {
      contentNode.setCachedX( getPosition() );
      contentNode.setCachedWidth( itemElementWidth );
    }

    final long preferredEndingPos = getPosition() + itemElementWidth;
    if ( preferredEndingPos > getEndOfLine() ) {
      // We would eat the whole space up to the end of the line and more
      // So lets move that element to the next line instead...

      // But: We could easily end in an endless loop here. So check whether
      // the element is the first in the line
      if ( start == 0 ) {
        // As it is guaranteed, that each chunk contains at least one item,
        // checking for start == 0 is safe enough ..
        return endIndex;
      }

      return start;
    }

    for ( int i = start; i < contentIndex; i++ ) {
      final InlineSequenceElement element = sequenceElements[i];
      final RenderNode node = nodes[contentIndex];
      final long elementWidth = element.getMaximumWidth( node );
      elementPositions[i] = getPosition();
      elementDimensions[i] = elementWidth;
      addPosition( elementWidth );
    }

    elementPositions[contentIndex] = getPosition();
    elementDimensions[contentIndex] = itemElementWidth;
    setPosition( preferredEndingPos );

    for ( int i = contentIndex + 1; i < endIndex; i++ ) {
      final InlineSequenceElement element = sequenceElements[i];
      final RenderNode node = nodes[contentIndex];
      final long elementWidth = element.getMaximumWidth( node );
      elementPositions[i] = getPosition();
      elementDimensions[i] = elementWidth;
      addPosition( elementWidth );
    }

    return endIndex;
  }

  public void performSkipAlignment( final int endIndex ) {
    // this is a NO-OP method, as the skip-alignment is simply a left-alignment ...
  }

  protected void updateBreaksForLastLineAlignment() {
    final long[] horizontalBreaks = getPageGrid().getHorizontalBreaks();
    final int breakCount = horizontalBreaks.length;
    final LongList pageLongList = new LongList( breakCount );
    final long endOfLine = getEndOfLine();
    final long startOfLine = getStartOfLine();
    for ( int i = 0; i < breakCount; i++ ) {
      final long pos = horizontalBreaks[i];
      if ( pos <= startOfLine ) {
        // skip ..
        continue;
      }
      if ( pos >= endOfLine ) {
        break;
      }
      pageLongList.add( pos );
    }
    // pageLongList.add(endOfLine);
    pageLongList.add( Long.MAX_VALUE );

    final long[] pagebreaks = getPageBreaks();
    updatePageBreaks( pageLongList.toArray( pagebreaks ), pageLongList.size() );
  }
}
