/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
