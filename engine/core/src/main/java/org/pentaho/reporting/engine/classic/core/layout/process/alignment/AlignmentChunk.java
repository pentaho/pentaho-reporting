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
