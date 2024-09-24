/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process.alignment;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.util.LongList;
import org.pentaho.reporting.libraries.base.util.FastStack;

import java.util.ArrayList;

public class FastAlignmentProcessor implements TextAlignmentProcessor {
  private static final long MAX_SIZE = (long) Math.pow( 2, 50 );

  private long start;
  private long end;
  private PageGrid breaks;
  private boolean overflowX;
  private long[] pagebreaks;
  private ChunkIterator iterator;

  public FastAlignmentProcessor() {
    this.pagebreaks = new long[10];
  }

  public void initialize( final OutputProcessorMetaData metaData, final SequenceList sequence, final long start,
      final long end, final PageGrid breaks, final boolean overflowX ) {
    this.start = start;
    this.end = end;
    this.breaks = breaks;
    this.overflowX = overflowX;
    if ( overflowX ) {
      this.end = MAX_SIZE;
    }

    updateBreaks();
    this.iterator = new ChunkIterator( sequence, 0 );
  }

  private void updateBreaks() {
    final long[] horizontalBreaks = breaks.getHorizontalBreaks();
    final int breakCount = horizontalBreaks.length;
    final LongList pageLongList = new LongList( breakCount );
    for ( int i = 0; i < ( breakCount - 1 ); i++ ) {
      final long pos = horizontalBreaks[i];
      if ( pos <= start ) {
        // skip ..
        continue;
      }
      if ( overflowX == false && pos >= end ) {
        break;
      }

      pageLongList.add( pos );
    }
    pageLongList.add( end );

    this.pagebreaks = pageLongList.toArray( this.pagebreaks );
  }

  public void updateLineSize( final long start, final long end ) {
    this.start = start;
    this.end = end;
  }

  public void deinitialize() {

  }

  public boolean hasNext() {
    return iterator.hasNext();
  }

  private long calculateWidth( final AlignmentChunk chunk, final boolean stripFirstSpacer ) {
    final int chunkEnd = chunk.getEnd();
    boolean first = stripFirstSpacer;
    long length = 0;
    for ( int i = chunk.getStart(); i < chunkEnd; i++ ) {
      final RenderNode node = chunk.getNode( i );
      final InlineSequenceElement sequenceElement = chunk.getSequenceElement( i );
      final InlineSequenceElement.Classification classification = sequenceElement.getType();
      if ( classification == InlineSequenceElement.Classification.CONTENT ) {
        if ( first && node.getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER ) {
          continue;
        }
        first = false;
      }
      final long minimumLength = sequenceElement.getMaximumWidth( node );
      length += minimumLength;
    }
    return length;
  }

  public RenderBox next() {
    boolean first = true;
    long posX = start;
    RenderBox rootBox = null;
    final FastStack<RenderBox> context = new FastStack<RenderBox>();
    while ( iterator.hasNext() ) {
      final AlignmentChunk chunk = iterator.next();
      final long chunkWidth = calculateWidth( chunk, first );
      if ( first || posX + chunkWidth < end ) {
        // simple, add that chunk ..
        final int chunkEnd = chunk.getEnd();
        for ( int i = chunk.getStart(); i < chunkEnd; i++ ) {
          final RenderNode node = chunk.getNode( i );
          final InlineSequenceElement sequenceElement = chunk.getSequenceElement( i );
          final InlineSequenceElement.Classification classification = sequenceElement.getType();
          final long minimumLength = sequenceElement.getMaximumWidth( node );

          if ( classification == InlineSequenceElement.Classification.START ) {
            node.setCachedX( posX );
            final RenderBox renderBox = (RenderBox) node.derive( false );
            context.push( renderBox );
            if ( rootBox == null ) {
              rootBox = renderBox;
            }
          } else if ( classification == InlineSequenceElement.Classification.END ) {
            final RenderBox b = context.pop();
            b.setCachedWidth( ( posX - b.getCachedX() ) + minimumLength );

            if ( context.isEmpty() == false ) {
              context.peek().addGeneratedChild( b );
            }
          } else {
            if ( first == false || node.getNodeType() != LayoutNodeTypes.TYPE_NODE_SPACER ) {
              final RenderNode n = node.derive( true );
              n.setCachedX( posX );
              n.setCachedWidth( minimumLength );
              context.peek().addGeneratedChild( n );
            } else {
              continue;
            }
            first = false;
          }

          posX += minimumLength;
        }

        first = false;
      } else {
        final int size = context.size();
        final ArrayList<RenderBox> paddingBoxes = new ArrayList<RenderBox>( size );
        for ( int i = 0; i < size; i++ ) {
          final RenderBox renderBox = context.get( i );
          renderBox.setCachedWidth( posX - renderBox.getCachedX() );
          final RenderBox split = renderBox.split( RenderNode.HORIZONTAL_AXIS );
          paddingBoxes.add( split );
        }

        iterator = iterator.createPadding( chunk.getStart(), paddingBoxes );
        break;
      }
    }

    return rootBox;
  }
}
