package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public interface WidowOrphanContext
{
  public void startChild(RenderBox box);

//    public void startIndirectChild(RenderBox box);

//    public void endIndirectChild(RenderBox box, long orphan, long widow);

  public void endChild(RenderBox box);

  /**
   * Orphan value is the y2/bottom-boundary for the element after which the box becomes breakable.
   *
   * @return
   */
  public long getOrphanValue();

  /**
   * Widow is the y/top boundary for the element after which the box becomes unbreakable.
   *
   * @return
   */
  public long getWidowValue();

  public WidowOrphanContext commit(RenderBox box);

  public void subContextCommitted(RenderBox contextBox);

  public void clearForPooledReuse();
}
