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
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;

/**
 * Creation-Date: 25.04.2007, 13:42:05
 *
 * @author Thomas Morgner
 */
public interface ParagraphLinebreaker {
  public boolean isWritable();

  public FullLinebreaker startComplexLayout();

  public void startBlockBox( final RenderBox child );

  public void finishBlockBox( final RenderBox box );

  public ParagraphLinebreaker startParagraphBox( final ParagraphRenderBox box );

  public void finishParagraphBox( final ParagraphRenderBox box );

  // public Object getSuspendItem();
  public boolean isSuspended();

  public void finish();

  public void startInlineBox( final InlineRenderBox box );

  public void finishInlineBox( final InlineRenderBox box );

  public boolean isBreakRequested();

  public void addNode( final RenderNode node );

  public void setBreakRequested( final boolean breakRequested );
}
