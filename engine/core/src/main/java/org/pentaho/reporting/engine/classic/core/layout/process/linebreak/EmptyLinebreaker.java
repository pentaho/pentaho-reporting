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


package org.pentaho.reporting.engine.classic.core.layout.process.linebreak;

import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * This implementation does nothing and is used as dummy if the paragraph was unchanged.
 *
 * @author Thomas Morgner
 */
public final class EmptyLinebreaker implements ParagraphLinebreaker {
  public EmptyLinebreaker() {
  }

  public boolean isWritable() {
    return false;
  }

  public FullLinebreaker startComplexLayout() {
    throw new UnsupportedOperationException();
  }

  public void startBlockBox( final RenderBox child ) {
    throw new UnsupportedOperationException();
  }

  public void finishBlockBox( final RenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public ParagraphLinebreaker startParagraphBox( final ParagraphRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public void finishParagraphBox( final ParagraphRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public boolean isSuspended() {
    return false;
  }

  public void finish() {
  }

  public void startInlineBox( final InlineRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public void finishInlineBox( final InlineRenderBox box ) {
    throw new UnsupportedOperationException();
  }

  public boolean isBreakRequested() {
    throw new UnsupportedOperationException();
  }

  public void addNode( final RenderNode node ) {
    throw new UnsupportedOperationException();
  }

  public void setBreakRequested( final boolean breakRequested ) {
    throw new UnsupportedOperationException();
  }
}
