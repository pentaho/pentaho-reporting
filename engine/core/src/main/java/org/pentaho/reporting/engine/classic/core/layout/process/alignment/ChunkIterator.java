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
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.DefaultSequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class ChunkIterator implements Iterator<AlignmentChunk>, Cloneable {
  private SequenceList sequenceList;
  private int startPosition;

  public ChunkIterator( final SequenceList sequenceList, final int startPosition ) {
    this.sequenceList = sequenceList;
    this.startPosition = startPosition;
  }

  public int getPosition() {
    return startPosition;
  }

  public boolean hasNext() {
    if ( startPosition < sequenceList.size() ) {
      return true;
    }

    return false;
  }

  public AlignmentChunk next() {
    if ( startPosition >= sequenceList.size() ) {
      throw new NoSuchElementException();
    }

    if ( startPosition == 0 ) {
      if ( sequenceList.size() == 1 ) {
        // special case ..
        startPosition = 0;
        return new AlignmentChunk( sequenceList, 0, 1, sequenceList.getMinimumLength( 0 ) );
      }
    }

    InlineSequenceElement.Classification lastType = sequenceList.getSequenceElement( startPosition ).getType();
    long length = sequenceList.getMinimumLength( startPosition );
    boolean lastNodeWasSpacer =
        ( lastType == InlineSequenceElement.Classification.CONTENT && sequenceList.getNode( startPosition )
            .getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER );
    for ( int i = startPosition + 1; i < sequenceList.size(); i += 1 ) {
      final InlineSequenceElement sequenceElement = sequenceList.getSequenceElement( i );
      final InlineSequenceElement.Classification classification = sequenceElement.getType();
      if ( lastNodeWasSpacer == false && lastType != InlineSequenceElement.Classification.START
          && classification != InlineSequenceElement.Classification.END ) {
        final int chunkStart = startPosition;
        startPosition = i;
        return new AlignmentChunk( sequenceList, chunkStart, i - chunkStart, length );
      }
      lastNodeWasSpacer =
          ( classification == InlineSequenceElement.Classification.CONTENT && sequenceList.getNode( i ).getNodeType() == LayoutNodeTypes.TYPE_NODE_SPACER );
      lastType = classification;
      length += sequenceList.getMinimumLength( i );
    }

    final int chunkStart = startPosition;
    startPosition = sequenceList.size();
    return new AlignmentChunk( sequenceList, chunkStart, sequenceList.size() - chunkStart, length );
  }

  public Object clone() {
    try {
      return super.clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }

  public ChunkIterator createPadding( final int start, final ArrayList<RenderBox> paddingBoxes ) {
    final DefaultSequenceList list = new DefaultSequenceList( paddingBoxes.size() + sequenceList.size() - start );
    for ( int i = 0; i < paddingBoxes.size(); i++ ) {
      final RenderBox box = paddingBoxes.get( i );
      list.add( StartSequenceElement.INSTANCE, box );
    }
    for ( int i = start; i < sequenceList.size(); i++ ) {
      list.add( sequenceList.getSequenceElement( i ), sequenceList.getNode( i ) );
    }
    return new ChunkIterator( list, 0 );
  }
}
