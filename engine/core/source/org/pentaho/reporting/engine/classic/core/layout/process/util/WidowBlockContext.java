package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.util.RingBuffer;

public class WidowBlockContext implements WidowContext
{
  private static final Log logger = LogFactory.getLog(WidowBlockContext.class);
  private StackedObjectPool<WidowBlockContext> pool;
  private WidowContext parent;
  private RenderBox contextBox;
  private int widows;
  private int widowCount;
  private RingBuffer<RenderNode> widowSize;
  private boolean debug;
  private long widowOverride;
  private RenderNode currentNode;
  private boolean markWidowBoxes;

  public WidowBlockContext()
  {
  }

  public void init(final StackedObjectPool<WidowBlockContext> pool,
                   final WidowContext parent,
                   final RenderBox contextBox,
                   final int widows,
                   final int orphans)
  {
    this.pool = pool;
    this.parent = parent;
    this.contextBox = contextBox;
    this.widows = widows;
    this.markWidowBoxes = contextBox.isOpen() || contextBox.getContentRefCount() > 0;
    this.widowCount = 0;
    this.widowOverride = contextBox.getCachedY2();

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
      if (widowCount < widows && widows > 0)
      {
        widowSize.add(box);
        if (debug)
        {
          final long y2 = box.getCachedY2() - box.getCachedHeight();
          logger.debug("Widow size added (DIRECT): " + y2 + " -> " + box);
        }
        widowCount += 1;
        if (markWidowBoxes)
        {
          box.setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.LEAF);
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
    if (widowCount < widows && widows > 0)
    {
      widowSize.add(box);
      if (debug)
      {
        final long y2 = box.getCachedY2() - box.getCachedHeight();
        logger.debug("Widow size added (DIRECT): " + y2 + " -> " + box);
      }
      if (markWidowBoxes)
      {
        box.getParent().setRestrictFinishedClearOut(RenderBox.RestrictFinishClearOut.RESTRICTED);
      }
      widowCount += 1;
    }


    currentNode = null;
    if (parent != null)
    {
      parent.registerFinishedNode(box);
    }
  }

  private long getWidowValue()
  {
    if (widows == 0)
    {
      return widowOverride;
    }
    final RenderNode box = widowSize.getLastValue();
    if (box == null)
    {
      return widowOverride;
    }
    final long y2 = box.getCachedY2() - box.getCachedHeight();
    return Math.min(widowOverride, y2);
  }

  public WidowContext commit(final RenderBox box)
  {
    box.setWidowConstraintSize(box.getCachedY2() - getWidowValue());

    if (box.isInvalidWidowOrphanNode() == false)
    {
      final boolean incomplete = box.isOpen() || box.getContentRefCount() > 0;
      if (incomplete)
      {
        if (widows > 0 && widowCount == 0)
        {
          // the box is open, has a widow-constraint and has not seen a single widow box yet.
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
    }

    if (markWidowBoxes && widowSize != null)
    {
      for (int i = 0; i < widowSize.size(); i += 1)
      {
        final RenderNode renderNode = widowSize.get(i);
        if (renderNode == null)
        {
          continue;
        }

        if (renderNode instanceof RenderBox)
        {
          final RenderBox rbox = (RenderBox) renderNode;
          rbox.setWidowBox(true);
        }
      }
    }


    if (debug)
    {
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
    if (contextBox.getCachedY2() >= getWidowValue())
    {
      widowOverride = Math.min(widowOverride, contextBox.getCachedY2() - contextBox.getWidowConstraintSize());
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
