package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.util.RingBuffer;

public class OrphanBlockContext implements OrphanContext
{
  private static final Log logger = LogFactory.getLog(OrphanBlockContext.class);
  private StackedObjectPool<OrphanBlockContext> pool;
  private OrphanContext parent;
  private RenderBox contextBox;
  private int orphans;
  private int orphanCount;
  private RingBuffer<Long> orphanSize;
  private boolean debug;
  private long orphanOverride;

  private RenderNode currentNode;
  private boolean markWidowBoxes;

  public OrphanBlockContext()
  {
  }

  public void init(final StackedObjectPool<OrphanBlockContext> pool,
                   final OrphanContext parent,
                   final RenderBox contextBox,
                   final int orphans)
  {
    this.pool = pool;
    this.parent = parent;
    this.contextBox = contextBox;
    this.orphans = orphans;
    this.orphanOverride = contextBox.getCachedY();
    this.markWidowBoxes = contextBox.isOpen() || contextBox.getContentRefCount() > 0;
    this.orphanCount = 0;

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
        box.setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.LEAF);
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
      box.getParent().setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.RESTRICTED);
      orphanCount += 1;
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

  public OrphanContext commit(final RenderBox box)
  {
    box.setOrphanConstraintSize(Math.max(0, getOrphanValue() - box.getCachedY()));

    final boolean incomplete = box.isOpen() || box.getContentRefCount() > 0;
    if (incomplete)
    {
      if (orphanCount < orphans || box.getStaticBoxLayoutProperties().isAvoidPagebreakInside())
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

    if (debug)
    {
      logger.debug("Final Orphan Size: " + box.getOrphanConstraintSize());
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
