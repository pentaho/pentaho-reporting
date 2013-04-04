package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public interface WidowContext
{
  public void startChild(RenderBox box);

//    public void startIndirectChild(RenderBox box);

//    public void endIndirectChild(RenderBox box, long orphan, long widow);

  public void endChild(RenderBox box);

  public void registerFinishedNode(FinishedRenderNode node);

  public WidowContext commit(RenderBox box);

  public void subContextCommitted(RenderBox contextBox);

  public void clearForPooledReuse();
}
