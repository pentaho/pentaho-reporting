/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public interface WidowContext {
  public void startChild( RenderBox box );

  // public void startIndirectChild(RenderBox box);

  // public void endIndirectChild(RenderBox box, long orphan, long widow);

  public void endChild( RenderBox box );

  public void registerFinishedNode( FinishedRenderNode node );

  public WidowContext commit( RenderBox box );

  public void subContextCommitted( RenderBox contextBox );

  public void clearForPooledReuse();

  public void registerBreakMark( RenderBox box );
}
