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

package org.pentaho.reporting.engine.classic.core.layout.process.linebreak;

import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * Creation-Date: 25.04.2007, 13:44:48
 *
 * @author Thomas Morgner
 */
public final class FullLinebreaker implements ParagraphLinebreaker {
  private RenderBox insertationPoint;
  private ParagraphRenderBox paragraphRenderBox;
  private Object suspendItem;
  private boolean breakRequested;

  public FullLinebreaker( final ParagraphRenderBox paragraphRenderBox ) {
    this.paragraphRenderBox = paragraphRenderBox;
    final RenderBox pool = (RenderBox) paragraphRenderBox.getPool().deriveFrozen( false );
    final RenderBox lineboxContainer = this.paragraphRenderBox.createLineboxContainer();
    lineboxContainer.clear();
    lineboxContainer.addGeneratedChild( pool );
    this.paragraphRenderBox.setLineBoxAge( 0 );
    this.insertationPoint = pool;
  }

  public boolean isWritable() {
    return true;
  }

  public void startBlockBox( final RenderBox child ) {
    final RenderBox derived = (RenderBox) child.deriveFrozen( false );
    insertationPoint.addGeneratedChild( derived );
    insertationPoint = derived;
    if ( suspendItem != null ) {
      suspendItem = derived.getInstanceId();
    }
  }

  public void finishBlockBox( final RenderBox box ) {
    insertationPoint = insertationPoint.getParent();
    if ( suspendItem == box.getInstanceId() ) {
      suspendItem = null;
    }
  }

  public ParagraphLinebreaker startParagraphBox( final ParagraphRenderBox box ) {
    startBlockBox( box );
    return new FullLinebreaker( (ParagraphRenderBox) insertationPoint );
  }

  public void finishParagraphBox( final ParagraphRenderBox box ) {
    finishBlockBox( box );
  }

  public boolean isSuspended() {
    return suspendItem != null;
  }

  public FullLinebreaker startComplexLayout() {
    return this;
  }

  public void finish() {
    paragraphRenderBox.setLineBoxAge( paragraphRenderBox.getPool().getChangeTracker() );
  }

  public void startInlineBox( final InlineRenderBox box ) {
    final RenderBox child = (RenderBox) box.deriveFrozen( false );
    insertationPoint.addGeneratedChild( child );
    insertationPoint = child;
  }

  public void finishInlineBox( final InlineRenderBox box ) {
    insertationPoint = insertationPoint.getParent();
  }

  public void addNode( final RenderNode node ) {
    insertationPoint.addGeneratedChild( node.deriveFrozen( true ) );
  }

  public void performBreak() {

    // If we come that far, it means, we have a forced linebreak and we
    // are a node in the middle of the tree ..
    final FastStack<RenderBox> contexts = new FastStack<RenderBox>();

    // perform a simple split
    // as long as the splitted element is at the end of it's box, it is not
    // needed to split the box at all. Just let it end naturally is enough for
    // them to look good.

    // As the real context (from the break-State) is currently being built,
    // we have to use the original pool to query the 'is-end-of-line' flag.
    RenderBox context = insertationPoint;
    final RenderBox lines = paragraphRenderBox.getLineboxContainer();
    while ( context != lines ) {
      // save the context ..

      if ( ( context.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_INLINE ) != LayoutNodeTypes.MASK_BOX_INLINE ) {
        throw new IllegalStateException( "Confused: I expect InlineBoxes .." );
      }

      final InlineRenderBox inline = (InlineRenderBox) context;
      contexts.push( inline.split( RenderNode.HORIZONTAL_AXIS ) );
      context = context.getParent();
    }

    // reset to a known state and add all saved contexts ..
    insertationPoint = lines;
    while ( contexts.isEmpty() == false ) {
      final RenderBox box = contexts.pop();
      insertationPoint.addGeneratedChild( box );
      insertationPoint = box;
    }

    breakRequested = false;
  }

  public boolean isBreakRequested() {
    return breakRequested;
  }

  public void setBreakRequested( final boolean breakRequested ) {
    this.breakRequested = breakRequested;
  }
}
