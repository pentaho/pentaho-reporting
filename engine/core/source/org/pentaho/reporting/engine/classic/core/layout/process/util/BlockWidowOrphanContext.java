package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.RingBuffer;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class BlockWidowOrphanContext implements WidowOrphanContext
{
  private StackedObjectPool<BlockWidowOrphanContext> pool;
  private WidowOrphanContext parent;
  private RenderBox contextBox;
  private int widows;
  private int orphans;
  private int count;
  private RingBuffer<Long> orphanSize;
  private RingBuffer<Long> widowSize;
  private boolean debug;
  private long orphanOverride;
  private long widowOverride;

  private RenderBox currentNode;

  public BlockWidowOrphanContext()
  {
  }

  public void init(final StackedObjectPool<BlockWidowOrphanContext> pool,
                   final WidowOrphanContext parent,
                   final RenderBox contextBox,
                   final int widows,
                   final int orphans)
  {
    this.pool = pool;
    this.parent = parent;
    this.contextBox = contextBox;
    this.widows = widows;
    this.orphans = orphans;
    this.widowOverride = contextBox.getY() + contextBox.getHeight();

    if (widows > 0)
    {
      if (this.widowSize == null)
      {
        this.widowSize = new RingBuffer<Long>(widows);
      }
      else
      {
        this.widowSize.resize(widows);
      }
    }
    if (orphans > 0)
    {
      if (this.orphanSize == null)
      {
        this.orphanSize = new RingBuffer<Long>(orphans);
      }
      else
      {
        this.orphanSize.resize(orphans);
      }
    }

    if (contextBox.getElementType() instanceof RelationalGroupType)
    {
    }
    debug = true;
  }


  public void startChild(final RenderBox box)
  {
    currentNode = box;

    if (parent != null)
    {
      parent.startChild(box);
    }
  }

  public void endChild(final RenderBox box)
  {
    if (currentNode != null)
    {
      if (count < orphans && orphans > 0)
      {
        final long y2 = box.getY() + box.getHeight();
        orphanSize.add(y2);
        if (debug)
        {
          DebugLog.log("Orphan size added (DIRECT): " + y2 + " -> " + box);
        }
        count += 1;
      }

      if (widows > 0)
      {
        widowSize.add(box.getY());
        if (debug)
        {
          DebugLog.log("Widow size added (DIRECT): " + box.getY() + " -> " + box);
        }
      }

      currentNode = null;
    }

    if (parent != null)
    {
      parent.endChild(box);
    }
  }

  public long getOrphanValue()
  {
    if (orphans == 0)
    {
      return orphanOverride;
    }
    final Long lastValue = orphanSize.getLastValue();
    if (lastValue == null)
    {
      return orphanOverride;
    }
    return Math.max(orphanOverride, lastValue.longValue());
  }

  public long getWidowValue()
  {
    if (widows == 0)
    {
      return widowOverride;
    }
    final Long firstValue = widowSize.getFirstValue();
    if (firstValue == null)
    {
      return widowOverride;
    }
    return Math.min(widowOverride, firstValue.longValue());
  }

  public WidowOrphanContext commit(final RenderBox box)
  {
    box.setOrphanConstraintSize(Math.max(0, getOrphanValue() - box.getY()));
    box.setWidowConstraintSize((box.getY() + box.getHeight()) - getWidowValue());

    if (debug)
    {
      DebugLog.log("Final Orphan Size: " + box.getOrphanConstraintSize());
      DebugLog.log("Final Widow Size: " + box.getWidowConstraintSize());
    }
    if (parent != null)
    {
      parent.subContextCommitted(box);
    }

    return parent;
  }

  public void subContextCommitted(final RenderBox contextBox)
  {
    // if there is overlap between the child context and the current lock-out area, process it.
    if (contextBox.getY() <= getOrphanValue())
    {
      orphanOverride = Math.max(orphanOverride, contextBox.getY() + contextBox.getOrphanConstraintSize());
    }

    final long widowLimit = getWidowValue();
    final long contextY2 = contextBox.getY() + contextBox.getHeight();
    if (contextY2 >= widowLimit)
    {
      final long absConstraint = contextY2 - contextBox.getWidowConstraintSize();
      widowOverride = Math.min(widowOverride, absConstraint);
    }

    if (parent != null)
    {
      parent.subContextCommitted(contextBox);
    }
  }

  public void clearForPooledReuse()
  {
    parent = null;
    contextBox = null;
    pool.free(this);
  }
}
