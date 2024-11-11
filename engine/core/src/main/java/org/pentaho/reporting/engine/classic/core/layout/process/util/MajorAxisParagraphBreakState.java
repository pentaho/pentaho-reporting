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


package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.BoxAlignContext;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * Creation-Date: 12.07.2007, 14:16:07
 *
 * @author Thomas Morgner
 */
public final class MajorAxisParagraphBreakState {
  private BoxAlignContext currentLine;

  private Object suspendItem;
  private FastStack<BoxAlignContext> contexts;
  private ParagraphRenderBox paragraph;

  /**
   */
  public MajorAxisParagraphBreakState() {
    this.contexts = new FastStack<BoxAlignContext>( 50 );
  }

  public void init( final ParagraphRenderBox paragraph ) {
    if ( paragraph == null ) {
      throw new NullPointerException();
    }
    this.paragraph = paragraph;
    this.contexts.clear();
    this.suspendItem = null;
  }

  public void deinit() {
    this.paragraph = null;
    this.suspendItem = null;
    this.contexts.clear();
  }

  public boolean isActive() {
    return paragraph != null;
  }

  public ParagraphRenderBox getParagraph() {
    return paragraph;
  }

  public Object getSuspendItem() {
    return suspendItem;
  }

  public void setSuspendItem( final Object suspendItem ) {
    this.suspendItem = suspendItem;
  }

  public boolean isSuspended() {
    return suspendItem != null;
  }

  public BoxAlignContext getCurrentLine() {
    return currentLine;
  }

  public void openContext( final RenderBox box ) {
    final BoxAlignContext context = new BoxAlignContext( box );
    if ( currentLine != null ) {
      currentLine.addChild( context );
    }
    contexts.push( context );
    currentLine = context;
  }

  public BoxAlignContext closeContext() {
    final BoxAlignContext context = contexts.pop();
    context.validate();
    if ( contexts.isEmpty() ) {
      currentLine = null;
    } else {
      currentLine = contexts.peek();
    }
    return context;
  }
}
