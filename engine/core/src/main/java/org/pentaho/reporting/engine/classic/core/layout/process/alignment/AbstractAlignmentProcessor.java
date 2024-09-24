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

import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SplittableRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.EndSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.LongList;
import org.pentaho.reporting.libraries.base.util.FastStack;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Todo: The whole horizontal alignment is not suitable for spanned page breaks.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractAlignmentProcessor implements TextAlignmentProcessor, LastLineTextAlignmentProcessor {
  private static final InlineSequenceElement[] EMPTY_ELEMENTS = new InlineSequenceElement[0];
  private static final RenderNode[] EMPTY_NODES = new RenderNode[0];

  private long startOfLine;
  private long endOfLine;
  private long[] pagebreaks;
  private int pagebreakCount;
  private PageGrid pageGrid;

  private InlineSequenceElement[] sequenceElements = AbstractAlignmentProcessor.EMPTY_ELEMENTS;
  private RenderNode[] nodes = AbstractAlignmentProcessor.EMPTY_NODES;
  private int sequenceFill;

  /**
   * A layouter hint, that indicates a possibly breakable element
   */
  private int breakableIndex;
  /**
   * A layouter hint, that indicates where to continue on unbreakable elements.
   */
  private int skipIndex;
  /**
   * A layouter hint, that shows up the maximum element's width to fit into line's limit. This value should be accessed
   * only if {@code breakableIndex != -1}, as {@code breakableIndex} refers to the element
   */
  private long breakableMaxAllowedWidth;

  private long[] elementPositions;
  private long[] elementDimensions;
  private FastStack<RenderBox> contexts;
  private ArrayList<RenderNode> pendingElements;
  private static final long[] EMPTY = new long[0];
  private boolean lastLineAlignment;
  private LeftAlignmentProcessor leftAlignProcessor;
  private boolean overflowX;
  private LongList pageLongList;

  protected AbstractAlignmentProcessor() {
    this.contexts = new FastStack<RenderBox>( 50 );
    this.pendingElements = new ArrayList<RenderNode>();
    this.elementDimensions = AbstractAlignmentProcessor.EMPTY;
    this.elementPositions = AbstractAlignmentProcessor.EMPTY;
  }

  public boolean isLastLineAlignment() {
    return lastLineAlignment;
  }

  protected long getStartOfLine() {
    return startOfLine;
  }

  protected PageGrid getPageGrid() {
    return pageGrid;
  }

  protected InlineSequenceElement[] getSequenceElements() {
    return sequenceElements;
  }

  protected RenderNode[] getNodes() {
    return nodes;
  }

  protected long[] getElementPositions() {
    return elementPositions;
  }

  protected long[] getElementDimensions() {
    return elementDimensions;
  }

  protected long getEndOfLine() {
    return endOfLine;
  }

  public int getPagebreakCount() {
    return pagebreakCount;
  }

  protected long getPageBreak( final int pageIndex ) {
    if ( pageIndex < 0 || pageIndex >= pagebreakCount ) {
      throw new IndexOutOfBoundsException();
    }
    return pagebreaks[pageIndex];
  }

  protected long[] getPageBreaks() {
    return pagebreaks;
  }

  protected void updatePageBreaks( final long[] pagebreaks, final int pageBreakCount ) {
    this.pagebreakCount = pageBreakCount;
    this.pagebreaks = pagebreaks;
  }

  protected int getBreakableIndex() {
    return breakableIndex;
  }

  protected void setBreakableIndex( final int breakableIndex ) {
    this.breakableIndex = breakableIndex;
  }

  protected int getSkipIndex() {
    return skipIndex;
  }

  protected void setSkipIndex( final int skipIndex ) {
    this.skipIndex = skipIndex;
  }

  protected long getBreakableMaxAllowedWidth() {
    return breakableMaxAllowedWidth;
  }

  protected void setBreakableMaxAllowedWidth( long breakableMaxAllowedWidth ) {
    this.breakableMaxAllowedWidth = breakableMaxAllowedWidth;
  }

  /**
   * Processes the text and calls the layouting methods. This method returns the index of the last element that fits on
   * the current line.
   *
   * @param elements
   * @param maxPos
   * @return
   */
  protected int iterate( final InlineSequenceElement[] elements, final int maxPos ) {
    breakableIndex = -1;
    breakableMaxAllowedWidth = -1;
    skipIndex = -1;
    // The state transitions are as follows:
    // ......From....START...CONTENT...END
    // to...START....-.......X.........X
    // ...CONTENT....-.......X.........X
    // .......END....-.......-.........-
    //
    // Dash signals, that there is no break opportunity,
    // while X means, that it is possible to break the inline flow at that
    // position.

    if ( maxPos == 0 ) {
      // nothing to do ..
      return 0;
    }

    int lastElementType = elements[0].getClassification();
    int startIndex = 0;
    boolean lastNodeWasSpacer =
        ( lastElementType == InlineSequenceElement.CONTENT && nodes[0].getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER );
    for ( int i = 1; i < maxPos; i++ ) {
      final InlineSequenceElement element = elements[i];
      final int elementType = element.getClassification();
      if ( lastNodeWasSpacer == false && lastElementType != InlineSequenceElement.START
          && elementType != InlineSequenceElement.END ) {
        final int newIndex = handleElement( startIndex, i - startIndex );
        if ( newIndex <= startIndex ) {
          return startIndex;
        }

        startIndex = i;
      }

      lastNodeWasSpacer =
          ( elementType == InlineSequenceElement.CONTENT && nodes[i].getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER );
      lastElementType = elementType;
    }

    return handleElement( startIndex, maxPos - startIndex );
  }

  /**
   * Initializes the alignment process. The start and end parameters specify the line boundaries, and have been
   * precomputed.
   *
   * @param sequence
   * @param start
   * @param end
   * @param breaks
   */
  public void initialize( final OutputProcessorMetaData metaData, final SequenceList sequence, final long start,
      final long end, final PageGrid breaks, final boolean overflowX ) {
    if ( sequence == null ) {
      throw new NullPointerException();
    }
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    if ( breaks == null ) {
      throw new NullPointerException();
    }
    if ( end < start ) {
      // This is most certainly an error, treat it as such ..
      throw new IllegalArgumentException( "Start is <= end; which is stupid!: " + end + ' ' + start );
    }

    this.overflowX = overflowX;
    this.sequenceElements = sequence.getSequenceElements( this.sequenceElements );
    this.nodes = sequence.getNodes( this.nodes );
    this.sequenceFill = sequence.size();
    this.pageGrid = breaks;
    if ( elementPositions.length < sequenceFill ) {
      this.elementPositions = new long[sequenceFill];
    } else {
      Arrays.fill( this.elementPositions, 0 );
    }

    if ( elementDimensions.length < sequenceFill ) {
      this.elementDimensions = new long[sequenceFill];
    } else {
      Arrays.fill( this.elementDimensions, 0 );
    }
    updateLineSize( start, end );
  }

  public void updateLineSize( final long start, final long end ) {
    // to be computed by the pagegrid ..
    if ( startOfLine != start || endOfLine != end || pagebreaks == null ) {
      this.startOfLine = start;
      this.endOfLine = end;
      updateBreaks();
    }
  }

  public void deinitialize() {
    this.pageGrid = null;
    this.pendingElements.clear();
    this.contexts.clear();
    this.sequenceFill = 0;
  }

  private void updateBreaks() {
    final long[] horizontalBreaks = pageGrid.getHorizontalBreaks();
    final int breakCount = horizontalBreaks.length;
    if ( pageLongList == null ) {
      pageLongList = new LongList( breakCount );
    } else {
      pageLongList.clear();
    }
    for ( int i = 0; i < breakCount; i++ ) {
      final long pos = horizontalBreaks[i];
      if ( pos <= startOfLine ) {
        // skip ..
        continue;
      }
      if ( pos >= endOfLine ) {
        break;
      }
      if ( overflowX == false || ( i < ( breakCount - 1 ) ) ) {
        pageLongList.add( pos );
      }
    }
    pageLongList.add( endOfLine );

    this.pagebreaks = pageLongList.toArray( this.pagebreaks );
    this.pagebreakCount = pageLongList.size();
  }

  public boolean hasNext() {
    return sequenceFill > 0;
  }

  public RenderBox next() {
    cleanFirstSpacers();

    Arrays.fill( elementDimensions, 0 );
    Arrays.fill( elementPositions, 0 );

    int lastPosition = iterate( sequenceElements, sequenceFill );
    if ( lastPosition == 0 ) {
      if ( splitBreakableIfPossible() ) {
        return next();
      }

      if ( getSkipIndex() >= 0 ) {
        // This causes an overflow ..
        performSkipAlignment( getSkipIndex() );
        lastPosition = getSkipIndex();
      } else {
        // Skip the complete line. Oh, thats not good, really!
        lastPosition = sequenceFill;
      }
    }

    // now, build the line and update the array ..
    pendingElements.clear();
    contexts.clear();
    RenderBox firstBox = null;
    RenderBox box = null;
    for ( int i = 0; i < lastPosition; i++ ) {
      final RenderNode node = nodes[i];
      final InlineSequenceElement element = sequenceElements[i];
      if ( element instanceof EndSequenceElement ) {
        contexts.pop();
        final long boxX2 = ( elementPositions[i] + elementDimensions[i] );
        box.setCachedWidth( boxX2 - box.getCachedX() );

        if ( contexts.isEmpty() ) {
          box = null;
        } else {
          final RenderNode tmpnode = box;
          box = contexts.peek();
          box.addGeneratedChild( tmpnode );
        }
        continue;
      }

      if ( element instanceof StartSequenceElement ) {
        box = (RenderBox) node.derive( false );
        box.setCachedX( elementPositions[i] );
        contexts.push( box );
        if ( firstBox == null ) {
          firstBox = box;
        }
        continue;
      }

      if ( box == null ) {
        throw new IllegalStateException( "Invalid sequence: " + "Cannot have elements before we open the box context." );
      }

      // Content element: Perform a deep-deriveForAdvance, so that we preserve the
      // possibly existing sub-nodes.
      final RenderNode child = node.derive( true );
      child.setCachedX( elementPositions[i] );
      child.setCachedWidth( elementDimensions[i] );
      if ( box.getStaticBoxLayoutProperties().isPreserveSpace()
          && box.getStyleSheet().getBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT ) == false ) {
        // Take a shortcut as we know that we will never have any pending elements if preserve is true and
        // trim-content is false.
        box.addGeneratedChild( child );
        continue;
      }

      if ( child.isIgnorableForRendering() ) {
        pendingElements.add( child );
      } else {
        for ( int j = 0; j < pendingElements.size(); j++ ) {
          final RenderNode pendingNode = pendingElements.get( j );
          box.addGeneratedChild( pendingNode );
        }
        pendingElements.clear();
        box.addGeneratedChild( child );
      }
    }

    // Remove all spacers and other non printable content that might
    // look ugly at the beginning of a new line ..
    for ( ; lastPosition < sequenceFill; lastPosition++ ) {
      final RenderNode node = nodes[lastPosition];
      final StyleSheet styleSheet = node.getStyleSheet();
      if ( WhitespaceCollapse.PRESERVE.equals( styleSheet.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE ) )
          && styleSheet.getBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT ) == false ) {
        break;
      }

      if ( node.isIgnorableForRendering() == false ) {
        break;
      }
    }

    // If there are open contexts, then add the split-result to the new line
    // and update the width of the current line
    RenderBox previousContext = null;
    final int openContexts = contexts.size();
    for ( int i = 0; i < openContexts; i++ ) {
      final RenderBox renderBox = contexts.get( i );
      final long cachedWidth = getEndOfLine() - renderBox.getCachedX();
      renderBox.setCachedWidth( cachedWidth );

      final InlineRenderBox rightBox = (InlineRenderBox) renderBox.split( RenderNode.HORIZONTAL_AXIS );
      sequenceElements[i] = StartSequenceElement.INSTANCE;
      nodes[i] = rightBox;
      if ( previousContext != null ) {
        previousContext.addGeneratedChild( renderBox );
      }
      previousContext = renderBox;
    }

    final int length = sequenceFill - lastPosition;
    System.arraycopy( sequenceElements, lastPosition, sequenceElements, openContexts, length );
    System.arraycopy( nodes, lastPosition, nodes, openContexts, length );
    sequenceFill = openContexts + length;
    Arrays.fill( sequenceElements, sequenceFill, sequenceElements.length, null );
    Arrays.fill( nodes, sequenceFill, nodes.length, null );

    return firstBox;
  }

  private void cleanFirstSpacers() {
    InlineSequenceElement[] sequenceElements = this.sequenceElements;
    RenderNode[] nodes = this.nodes;
    int sequenceFill = this.sequenceFill;

    boolean changed = false;
    int targetIndex = 0;
    for ( int i = 0; i < this.sequenceFill; i += 1 ) {
      final InlineSequenceElement ise = this.sequenceElements[i];
      final InlineSequenceElement.Classification type = ise.getType();
      if ( type == InlineSequenceElement.Classification.CONTENT ) {
        final RenderNode node = this.nodes[i];
        if ( node instanceof SpacerRenderNode ) {
          if ( changed == false ) {
            // copy on demand ...
            sequenceElements = this.sequenceElements.clone();
            nodes = this.nodes.clone();
          }
          sequenceFill -= 1;
          changed = true;
          continue;
        }
      }

      if ( changed ) {
        // only copy if there is a change..
        sequenceElements[targetIndex] = ise;
        nodes[targetIndex] = this.nodes[i];
      }

      if ( type != InlineSequenceElement.Classification.START ) {
        if ( !changed ) {
          return;
        }

        System.arraycopy( this.sequenceElements, i, sequenceElements, targetIndex, this.sequenceFill - i );
        System.arraycopy( this.nodes, i, nodes, targetIndex, this.sequenceFill - i );
        Arrays.fill( nodes, sequenceFill, nodes.length, null );
        Arrays.fill( sequenceElements, sequenceFill, sequenceElements.length, null );

        this.sequenceElements = sequenceElements;
        this.nodes = nodes;
        this.sequenceFill = sequenceFill;
        return;
      }

      targetIndex += 1;
    }
  }

  /**
   * Handle the next input chunk.
   *
   * @param start
   *          the start index
   * @param count
   *          the number of elements in the sequence
   * @return the processing position. Linebreaks will be inserted, if the returned value is equal or less the start
   *         index.
   */
  protected abstract int handleElement( final int start, final int count );

  protected void computeInlineBlock( final RenderBox box, final long position, final long itemElementWidth ) {
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    box.setCachedX( position + blp.getMarginLeft() );
    final long width = itemElementWidth - blp.getMarginLeft() - blp.getMarginRight();
    if ( width == 0 ) {
      // ModelPrinter.printParents(box);

      throw new IllegalStateException( "A box without any width? "
          + Integer.toHexString( System.identityHashCode( box ) ) + ' ' + box.getClass() );
    }
    box.setCachedWidth( width );

    final BoxDefinition bdef = box.getBoxDefinition();
    final long leftInsets = bdef.getPaddingLeft() + blp.getBorderLeft();
    final long rightInsets = bdef.getPaddingRight() + blp.getBorderRight();
    box.setContentAreaX1( box.getCachedX() + leftInsets );
    box.setContentAreaX2( box.getCachedX() + box.getCachedWidth() - rightInsets );

    // final InfiniteMinorAxisLayoutStep layoutStep = new InfiniteMinorAxisLayoutStep(metaData);
    // layoutStep.continueComputation(getPageGrid(), box);
  }

  protected int getSequenceFill() {
    return sequenceFill;
  }

  public void performLastLineAlignment() {
    if ( pagebreakCount == 0 ) {
      throw new IllegalStateException( "Alignment processor has not been initialized correctly." );
    }

    Arrays.fill( elementDimensions, 0 );
    Arrays.fill( elementPositions, 0 );

    int lastPosition = iterate( sequenceElements, sequenceFill );
    if ( lastPosition == 0 ) {
      // This could evolve into an infinite loop. Thats evil.
      // We have two choices to prevent that:
      // (1) Try to break the element.
      // if (getBreakableIndex() >= 0)
      // {
      // // Todo: Breaking is not yet implemented ..
      // }
      if ( getSkipIndex() >= 0 ) {
        // This causes an overflow ..
        performSkipAlignment( getSkipIndex() );
        lastPosition = getSkipIndex();
      } else {
        // Skip the complete line. Oh, thats not good, really!
        lastPosition = sequenceFill;
      }
    }

    // the elements up to the 'lastPosition' are now aligned according to the alignment rules.
    // now, update the element's positions and dimensions ..

    if ( lastPosition == sequenceFill || lastLineAlignment ) {
      // First, the simple case: The line's content did fully fit into the linebox. No linebreaks were necessary.
      RenderBox firstBox = null;
      for ( int i = 0; i < lastPosition; i++ ) {
        final RenderNode node = nodes[i];
        final InlineSequenceElement element = sequenceElements[i];
        if ( element instanceof EndSequenceElement ) {
          final long boxX2 = ( elementPositions[i] + elementDimensions[i] );
          final RenderBox box = (RenderBox) node;
          box.setCachedWidth( boxX2 - box.getCachedX() );
          continue;
        }

        if ( element instanceof StartSequenceElement ) {
          final RenderBox box = (RenderBox) node;
          box.setCachedX( elementPositions[i] );
          if ( firstBox == null ) {
            firstBox = box;
          }
          continue;
        }

        // Content element: Perform a deep-deriveForAdvance, so that we preserve the
        // possibly existing sub-nodes.
        node.setCachedX( elementPositions[i] );
        node.setCachedWidth( elementDimensions[i] );
      }

      return;
    }

    // The second case is more complicated. The text did not fit fully into the text-element.

    // Left align all elements after the layouted content ..
    if ( leftAlignProcessor == null ) {
      leftAlignProcessor = new LeftAlignmentProcessor();
    }
    leftAlignProcessor.initializeForLastLineAlignment( this );
    leftAlignProcessor.performLastLineAlignment();
    leftAlignProcessor.deinitialize();
  }

  public void performSkipAlignment( final int endIndex ) {
    // Left align all elements after the layouted content ..
    if ( leftAlignProcessor == null ) {
      leftAlignProcessor = new LeftAlignmentProcessor();
    }
    leftAlignProcessor.initializeForSkipAlignment( this, endIndex );
    leftAlignProcessor.performLastLineAlignment();
    leftAlignProcessor.deinitialize();
  }

  protected void initializeForSkipAlignment( final AbstractAlignmentProcessor proc, final int endIndex ) {
    this.lastLineAlignment = true;
    this.sequenceElements = proc.sequenceElements;
    this.nodes = proc.nodes;
    this.sequenceFill = endIndex;
    this.pageGrid = proc.pageGrid;
    this.elementDimensions = proc.elementDimensions;
    this.elementPositions = proc.elementPositions;
    Arrays.fill( this.elementPositions, 0 );
    Arrays.fill( this.elementDimensions, 0 );

    this.startOfLine = proc.startOfLine;
    this.endOfLine = proc.endOfLine;
    if ( this.pagebreaks == null || this.pagebreaks.length < proc.pagebreakCount ) {
      this.pagebreaks = proc.pagebreaks.clone();
      this.pagebreakCount = proc.pagebreakCount;
    } else {
      System.arraycopy( proc.pagebreaks, 0, this.pagebreaks, 0, proc.pagebreakCount );
      this.pagebreakCount = proc.pagebreakCount;
    }
  }

  protected void initializeForLastLineAlignment( final AbstractAlignmentProcessor proc ) {
    this.lastLineAlignment = true;
    this.sequenceElements = proc.sequenceElements;
    this.nodes = proc.nodes;
    this.sequenceFill = proc.sequenceFill;
    this.pageGrid = proc.pageGrid;
    this.elementDimensions = proc.elementDimensions;
    this.elementPositions = proc.elementPositions;
    Arrays.fill( this.elementPositions, 0 );
    Arrays.fill( this.elementDimensions, 0 );

    this.startOfLine = proc.startOfLine;
    this.endOfLine = proc.endOfLine;

    updateBreaksForLastLineAlignment();
  }

  protected void updateBreaksForLastLineAlignment() {
    updateBreaks();
  }

  /**
   * Returns {@code true} if {@code element} represents border type, namely if it is either
   * {@linkplain StartSequenceElement} or {@linkplain EndSequenceElement}
   *
   * @param element
   *          element
   * @return {@code true}, if element represents border type and {@code false} otherwise
   */
  protected boolean isBorderMarker( InlineSequenceElement element ) {
    return ( element == StartSequenceElement.INSTANCE ) || ( element == EndSequenceElement.INSTANCE );
  }

  /**
   * Tries to split a breakable component if possible.
   * <p/>
   * First, checks whether each of the following conditions is true:
   * <ol>
   * <li>{@code getBreakableIndex() >= 0}</li>
   * <li>{@code nodes[breakableIndex]} is an instance of {@linkplain SplittableRenderNode}</li>
   * <li>{@code getBreakableMaxAllowedWidth() > 0}</li>
   * </ol>
   *
   * Then asks the node to split limiting the first kid's width so, that it can be put inside the bounds. The node can
   * be unable to do the separation and return {@code null}.
   *
   * If the separation was successful, then the method re-initialises the processors internal fields by invoking
   * {@linkplain #reInitializeForHandlingComponentSplit(int, RenderNode[])}
   *
   * @return {@code true} if the split was done or {@code false} otherwise
   */
  protected boolean splitBreakableIfPossible() {
    int breakableIndex = getBreakableIndex();
    if ( breakableIndex >= 0 ) {
      RenderNode breakableNode = nodes[breakableIndex];
      if ( breakableNode instanceof SplittableRenderNode ) {
        SplittableRenderNode splittableNode = (SplittableRenderNode) breakableNode;
        long widthExceeding = getBreakableMaxAllowedWidth();
        if ( widthExceeding > 0 ) {
          long widthByLayout = getElementDimensions()[breakableIndex];
          long maxAllowedWidth;
          if ( widthByLayout > 0 ) {
            maxAllowedWidth = widthByLayout - widthExceeding;
          } else {
            // was not computed, use node's self estimation
            maxAllowedWidth = splittableNode.getMinimumWidth() - widthExceeding;
          }
          if ( maxAllowedWidth > 0 ) {
            RenderNode[] pair = splittableNode.splitBy( maxAllowedWidth );
            if ( pair != null ) {
              reInitializeForHandlingComponentSplit( breakableIndex, pair );
              return true;
            }
          }
        }
      }
    }
    return false;
  }

  /**
   * A utility method for shifting {@code array}'s part that starts at {@code startIndex} and contains {@code amount}
   * elements by {@code offset}
   * <p/>
   * Note:<br/>
   * org.apache.commons.lang.ArrayUtils.shift() solves the problem, but the version used now is too old and it does not
   * contain this method
   *
   * @param array
   *          array
   * @param startIndex
   *          start index of the block to be shifted (included)
   * @param amount
   *          length of the block to be shifted
   * @param offset
   *          indexes delta
   */
  // package local visibility for testing purposes
  static void shiftArray( Object[] array, int startIndex, int amount, int offset ) {
    System.arraycopy( array, startIndex, array, startIndex + offset, amount );
  }

  /**
   * For tests only!
   */
  @Deprecated
  void setNodes( RenderNode[] nodes ) {
    this.nodes = nodes;
  }

  /**
   * For tests only!
   */
  @Deprecated
  void setElementDimensions( long[] elementDimensions ) {
    this.elementDimensions = elementDimensions;
  }

  protected void reInitializeForHandlingComponentSplit( int breakableIndex, RenderNode[] replacement ) {
    // shift by (replacement.length - 1), because split component should be replaced
    final int replacementAmountMinus1 = replacement.length - 1;
    final int newSize = sequenceFill + replacementAmountMinus1;

    if ( newSize > sequenceElements.length ) {
      // need to create larger arrays
      sequenceElements = Arrays.copyOf( sequenceElements, newSize );
      nodes = Arrays.copyOf( nodes, newSize );
      elementPositions = Arrays.copyOf( elementPositions, newSize );
      elementDimensions = Arrays.copyOf( elementDimensions, newSize );
    }

    int shiftedPartStartIndex = breakableIndex + 1;
    int shiftedPartLength = sequenceFill - shiftedPartStartIndex;

    shiftArray( sequenceElements, shiftedPartStartIndex, shiftedPartLength, replacementAmountMinus1 );
    // split elements derive their demiurge's type
    InlineSequenceElement breakableNodeType = sequenceElements[breakableIndex];
    Arrays.fill( sequenceElements, breakableIndex + 1, breakableIndex + replacement.length, breakableNodeType );

    shiftArray( nodes, shiftedPartStartIndex, shiftedPartLength, replacementAmountMinus1 );
    System.arraycopy( replacement, 0, nodes, breakableIndex, replacement.length );

    // deliberately ignore elementPositions and elementDimensions,
    // as they are reinitialised on each iterate() call

    sequenceFill = newSize;
  }
}
