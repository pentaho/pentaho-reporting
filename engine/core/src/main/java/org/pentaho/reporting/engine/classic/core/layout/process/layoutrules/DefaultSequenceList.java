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

package org.pentaho.reporting.engine.classic.core.layout.process.layoutrules;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

import java.util.ArrayList;

/**
 * Creation-Date: 17.07.2007, 11:23:20
 *
 * @author Thomas Morgner
 */
public class DefaultSequenceList implements SequenceList {
  private ArrayList<RenderNode> nodeList;
  private ArrayList<InlineSequenceElement> sequenceElementList;

  public DefaultSequenceList() {
    this( 50 );
  }

  public DefaultSequenceList( final int initialSize ) {
    this.nodeList = new ArrayList<RenderNode>( initialSize );
    this.sequenceElementList = new ArrayList<InlineSequenceElement>( initialSize );
  }

  public RenderNode getNode( final int index ) {
    return nodeList.get( index );
  }

  public InlineSequenceElement getSequenceElement( final int index ) {
    return sequenceElementList.get( index );
  }

  public long getMinimumLength( final int index ) {
    return getSequenceElement( index ).getMaximumWidth( nodeList.get( index ) );
  }

  public void clear() {
    this.nodeList.clear();
    this.sequenceElementList.clear();
  }

  public void add( final InlineSequenceElement element, final RenderNode node ) {
    if ( element == null || node == null ) {
      throw new NullPointerException();
    }

    this.sequenceElementList.add( element );
    this.nodeList.add( node );
  }

  public int size() {
    return nodeList.size();
  }

  public InlineSequenceElement[] getSequenceElements( final InlineSequenceElement[] target ) {
    return sequenceElementList.toArray( target );
  }

  public RenderNode[] getNodes( final RenderNode[] target ) {
    return nodeList.toArray( target );
  }
}
