package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class OrphanCanvasContext implements OrphanContext
{
  private StackedObjectPool<OrphanCanvasContext> pool;
  private OrphanContext parent;

  public OrphanCanvasContext()
  {
  }

  public void init(final StackedObjectPool<OrphanCanvasContext> pool, final OrphanContext parent)
  {
    this.pool = pool;
    this.parent = parent;
  }

  public void startChild(final RenderBox box)
  {

  }

  public void registerFinishedNode(final FinishedRenderNode node)
  {

  }

  public void endChild(final RenderBox box)
  {

  }

  public OrphanContext commit(final RenderBox box)
  {
    return parent;
  }

  public void subContextCommitted(final RenderBox contextBox)
  {

  }

  public long getOrphanValue()
  {
    return 0;
  }

  public long getWidowValue()
  {
    return 0;
  }

  public void clearForPooledReuse()
  {
    parent = null;
    pool.free(this);
  }
}
