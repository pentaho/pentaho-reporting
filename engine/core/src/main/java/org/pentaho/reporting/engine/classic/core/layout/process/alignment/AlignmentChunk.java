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

package org.pentaho.reporting.engine.classic.core.layout.process.alignment;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;

public class AlignmentChunk {
  private int start;
  private int length;
  private long width;
  private SequenceList sequenceList;

  public AlignmentChunk( final SequenceList sequenceList, final int start, final int length, final long width ) {
    this.sequenceList = sequenceList;
    this.start = start;
    this.length = length;
    this.width = width;
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return start + length;
  }

  public int getLength() {
    return length;
  }

  public long getWidth() {
    return width;
  }

  public RenderNode getNode( final int i ) {
    if ( i < start || i >= getEnd() ) {
      throw new IllegalStateException();
    }
    return sequenceList.getNode( i );
  }

  public InlineSequenceElement getSequenceElement( final int i ) {
    if ( i < start || i >= getEnd() ) {
      throw new IllegalStateException();
    }
    return sequenceList.getSequenceElement( i );
  }
}
