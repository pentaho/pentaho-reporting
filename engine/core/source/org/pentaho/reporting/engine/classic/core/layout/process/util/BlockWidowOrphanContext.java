package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.util.RingBuffer;

public class BlockWidowOrphanContext implements WidowOrphanContext
{
  private static final Log logger = LogFactory.getLog(BlockWidowOrphanContext.class);
  private StackedObjectPool<BlockWidowOrphanContext> pool;
  private WidowOrphanContext parent;
  private RenderBox contextBox;
  private int widows;
  private int widowCount;
  private int orphans;
  private int orphanCount;
  private RingBuffer<Long> orphanSize;
  private RingBuffer<RenderNode> widowSize;
  private boolean debug;
  private long orphanOverride;

  private RenderNode currentNode;
  private boolean markWidowBoxes;

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
    this.orphanOverride = contextBox.getCachedY();
    this.markWidowBoxes = contextBox.isOpen() || contextBox.getContentRefCount() > 0;
    this.orphanCount = 0;
    this.widowCount = 0;

    if (widows > 0)
    {
      if (this.widowSize == null)
      {
        this.widowSize = new RingBuffer<RenderNode>(widows);
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
      if (orphanCount < orphans && orphans > 0)
      {
        final long y2 = box.getCachedY() + box.getCachedHeight();
        orphanSize.add(y2);
        if (debug)
        {
          logger.debug("Orphan size added (DIRECT): " + y2 + " -> " + box);
        }
        orphanCount += 1;
        if (markWidowBoxes)
        {
          box.setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.LEAF);
        }
      }

      if (widows > 0)
      {
        widowCount += 1;
        widowSize.add(box);
        if (debug)
        {
          logger.debug("Widow size added (DIRECT): " + box.getCachedY() + " -> " + box);
        }
      }

      currentNode = null;
    }

    if (parent != null)
    {
      parent.endChild(box);
    }
  }

  public void registerFinishedNode(final FinishedRenderNode box)
  {
    if (orphanCount < orphans && orphans > 0)
    {
      final long y2 = box.getCachedY() + box.getCachedHeight();
      orphanSize.add(y2);
      if (debug)
      {
        logger.debug("Orphan size added (DIRECT): " + y2 + " -> " + box);
      }
      if (markWidowBoxes)
      {
        box.getParent().setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.RESTRICTED);
      }
      orphanCount += 1;
    }

    if (widows > 0)
    {
      widowCount += 1;
      widowSize.add(box);
      if (debug)
      {
        logger.debug("Widow size added (DIRECT): " + box.getCachedY() + " -> " + box);
      }
    }

    currentNode = null;
    if (parent != null)
    {
      parent.registerFinishedNode(box);
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

  private long getWidowValue()
  {
    if (widows == 0)
    {
      return contextBox.getCachedY() + contextBox.getCachedHeight();
    }
    final RenderNode firstValue = widowSize.getFirstValue();
    if (firstValue == null)
    {
      return contextBox.getCachedY() + contextBox.getCachedHeight();
    }

    return firstValue.getCachedY();
  }

  public WidowOrphanContext commit(final RenderBox box)
  {
    box.setOrphanConstraintSize(Math.max(0, getOrphanValue() - box.getCachedY()));
    box.setWidowConstraintSize((box.getCachedY() + box.getCachedHeight()) - getWidowValue());

    final boolean incomplete = box.isOpen() || box.getContentRefCount() > 0;
    if (incomplete)
    {
      if (orphanCount < orphans ||
          widows > 0 ||
          box.getStaticBoxLayoutProperties().isAvoidPagebreakInside())
      {
        // the box is either open or has an open sub-report and the orphan constraint is not fulfilled.
        // also block if there is an overlap between the orphan range and the widow range.
        box.setInvalidWidowOrphanNode(true);
      }
      else
      {
        box.setInvalidWidowOrphanNode(false);
      }
    }
    else
    {
      // the box is safe to process
      box.setInvalidWidowOrphanNode(false);
    }

    if (widows > 0 && markWidowBoxes)
    {
      for (int i = 0; i < widowSize.size(); i += 1)
      {
        final RenderNode widowBox = widowSize.get(i);
        if (widowBox != null)
        {
          widowBox.getParent().setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.RESTRICTED);
          widowBox.setWidowBox(true);
        }
        if (widowBox instanceof RenderBox)
        {
          final RenderBox widowBoxBox = (RenderBox) widowBox;
          widowBoxBox.setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.LEAF);
        }
      }
    }

    if (debug)
    {
      logger.debug("Final Orphan Size: " + box.getOrphanConstraintSize());
      logger.debug("Final Widow Size: " + box.getWidowConstraintSize());
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
    if (contextBox.getCachedY() <= getOrphanValue())
    {
      orphanOverride = Math.max(orphanOverride, contextBox.getCachedY() + contextBox.getOrphanConstraintSize());
    }
/*
    final long widowLimit = getWidowValue();
    final long contextY2 = contextBox.getCachedY() + contextBox.getCachedHeight();
    if (contextY2 >= widowLimit)
    {
      final long absConstraint = contextY2 - contextBox.getWidowConstraintSize();
      widowOverride = Math.min(widowOverride, absConstraint);
    }
*/
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
