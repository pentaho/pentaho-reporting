package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class CanvasWidowOrphanContext implements WidowOrphanContext
{
  private StackedObjectPool<CanvasWidowOrphanContext> pool;
  private WidowOrphanContext parent;

  public CanvasWidowOrphanContext()
  {
  }

  public void init(final StackedObjectPool<CanvasWidowOrphanContext> pool, final WidowOrphanContext parent)
  {
    this.pool = pool;
    this.parent = parent;
  }

  public void startChild(final RenderBox box)
  {

  }

  public void endChild(final RenderBox box)
  {

  }

  public WidowOrphanContext commit(final RenderBox box)
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
