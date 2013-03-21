package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.util.RingBuffer;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Computes break positions that prevent Orphan and Widow elements, according to the definitions on
 * the boxes themselves.
 * <p/>
 * An Orphan is an element pushed on its own page, with all other elements on the previous pages. This is commonly
 * found in groups where the group-footer is pushed to the next page.
 * <p/>
 * An Widow is an element left on the current page, where all other elements are pushed to the next page. This
 * is commonly found for group-headers, where the group-body is pushed to the next page.
 * <p/>
 * This step calculates the minimum required space that an element would consume if it honours the widow and
 * orphan rules.
 * <p/>
 * When computing the rules, all children are considered, as long as they do not opt-out of the processing. A box
 * that opts out, has the 'widow-orphan-opt-out' flag set to true. In the simple set of rules, only block-level
 * elements are considered to opt-in for widow and orphan processing.
 * <p/>
 * For orphans, this step computes the minimum space the element requires to be safely placed on this page. If the
 * elements occupying that space would trigger a manual page-break, the break overrides the orphan rule, and the
 * space for the orphan processing is limited to the point of the manual break.
 * <p/>
 * For widows, this step also computes the minimum space required to satisfy the constraint. Manual breaks override
 * the widow constraint. During pagination, the pagination processor has to check all parents to see whether their
 * widow constrains are still fulfilled.
 * <p/>
 * If the sum of the widow and orphan constraints is larger than the computed size of the box, the box is considered
 * unbreakable and behaves as if the "keep-together" flag has been set.
 * <p/>
 * The widow-orphan calculation ignores the 'fixed-position' setting when calculating constraints. Combining a
 * widow-orphan constraint with the fixed-position constrained yields undefined results. The widow and orphan constraint
 * is only active for paginated reports. It has no effect on flow or streaming report outputs.
 */
public class WidowOrphanStep extends IterateSimpleStructureProcessStep
{
  private static class ElementContext
  {
    public final long value;
    public final RenderNode instance;
    public boolean open;

    private ElementContext(final long value, final RenderNode instance)
    {
      this.open = true;
      this.value = value;
      this.instance = instance;
    }
  }

  private static interface WidowOrphanContext
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

    public void subContextCommited(RenderBox contextBox);
  }

  private static class BlockWidowOrphanContext implements WidowOrphanContext
  {
    private WidowOrphanContext parent;
    private RenderBox contextBox;
    private int widows;
    private int orphans;
    private int count;
    private RingBuffer<ElementContext> orphanSize;
    private RingBuffer<ElementContext> widowSize;
    private boolean debug;
    private long orphanOverride;
    private long widowOverride;

    private RenderBox currentNode;

    private BlockWidowOrphanContext(final WidowOrphanContext parent,
                                    final RenderBox contextBox,
                                    final int widows, final int orphans)
    {
      this.parent = parent;
      this.contextBox = contextBox;
      this.widows = widows;
      this.orphans = orphans;
      this.widowOverride = contextBox.getY() + contextBox.getHeight();

      if (widows > 0)
      {
        this.widowSize = new RingBuffer(widows);
      }
      if (orphans > 0)
      {
        this.orphanSize = new RingBuffer(orphans);
      }

      if ("group-outside".equals(contextBox.getName()))
      {
        debug = true;
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
        if (count < orphans && orphanSize != null)
        {
          final long y2 = box.getY() + box.getHeight();
          orphanSize.add(new ElementContext(y2, box));
          if (debug)
          {
            DebugLog.log("Orphan size added (DIRECT): " + y2 + " -> " + box.getName());
          }
          count += 1;
        }

        if (widowSize != null)
        {
          widowSize.add(new ElementContext(box.getY(), box));
          if (debug)
          {
            DebugLog.log("Widow size added (DIRECT): " + box.getY() + " -> " + box.getName());
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
      if (orphanSize == null)
      {
        return orphanOverride;
      }
      final ElementContext lastValue = orphanSize.getLastValue();
      if (lastValue == null)
      {
        return orphanOverride;
      }
      return Math.max(orphanOverride, lastValue.value);
    }

    public long getWidowValue()
    {
      if (widowSize == null)
      {
        return widowOverride;
      }
      final ElementContext firstValue = widowSize.getFirstValue();
      if (firstValue == null)
      {
        return widowOverride;
      }
      return Math.min(widowOverride, firstValue.value);
    }

    public WidowOrphanContext commit(final RenderBox box)
    {
      box.setOrphanConstraintSize(Math.max(0, getOrphanValue() - box.getY()));
      box.setWidowConstraintSize((box.getY() + box.getHeight()) - getWidowValue());
/*
      if (box.getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE))
      {
        box.setOrphanConstraintSize(box.getHeight());
        box.setWidowConstraintSize(box.getHeight());
      }
*/
      if (debug)
      {
        DebugLog.log("Final Orphan Size: " + box.getOrphanConstraintSize());
        DebugLog.log("Final Widow Size: " + box.getWidowConstraintSize());
      }
      if (parent != null)
      {
        parent.subContextCommited(box);
      }

      return parent;
    }

    public void subContextCommited(final RenderBox contextBox)
    {
      // if there is overlap between the child context and the current lock-out area, process it.
      if (contextBox.getY() <= getOrphanValue())
      {
        orphanOverride = Math.max(orphanOverride, contextBox.getY() + contextBox.getOrphanConstraintSize());
      }

      if (debug)
        DebugLog.logHere();
      final long widowLimit = getWidowValue();
      final long contextY2 = contextBox.getY() + contextBox.getHeight();
      if (contextY2 >= widowLimit)
      {
        final long absConstraint = contextY2 - contextBox.getWidowConstraintSize();
        widowOverride = Math.min(widowOverride, absConstraint);
      }

      if (parent != null)
      {
        parent.subContextCommited(contextBox);
      }
    }
  }

  private static class PassThroughWidowOrphanContext implements WidowOrphanContext
  {
    private WidowOrphanContext parent;

    private PassThroughWidowOrphanContext(final WidowOrphanContext parent)
    {
      this.parent = parent;
    }

    public WidowOrphanContext getParent()
    {
      return parent;
    }

    public void startChild(final RenderBox box)
    {
      if (parent != null)
      {
        parent.startChild(box);
      }
    }

    public void endChild(final RenderBox box)
    {
      if (parent != null)
      {
        parent.endChild(box);
      }
    }

    public long getOrphanValue()
    {
      return 0;
    }

    public long getWidowValue()
    {
      return 0;
    }

    public WidowOrphanContext commit(final RenderBox box)
    {
      return parent;
    }

    public void subContextCommited(final RenderBox contextBox)
    {
      if (parent != null)
      {
        parent.subContextCommited(contextBox);
      }
    }
  }

  public static class CanvasWidowOrphanContext implements WidowOrphanContext
  {
    private WidowOrphanContext parent;

    public CanvasWidowOrphanContext(final WidowOrphanContext parent)
    {
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

    public void subContextCommited(final RenderBox contextBox)
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
  }

  private WidowOrphanContext context;

  public WidowOrphanStep()
  {
  }

  public void processWidowOrphanAnnotation(final LogicalPageBox box)
  {
    context = new PassThroughWidowOrphanContext(null);
    startProcessing(box.getContentArea());
    context = null;
  }

  private WidowOrphanContext create(final RenderBox box)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
      final int widows = properties.getWidows();
      final int orphans = properties.getOrphans();
      if (widows == 0 && orphans == 0)
      {
        return new PassThroughWidowOrphanContext(context);
      }
      return new BlockWidowOrphanContext(context, box, widows, orphans);
    }
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW)
    {
      // todo: Make this a row-context later ..
      return new CanvasWidowOrphanContext(context);
    }
    return new CanvasWidowOrphanContext(context);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  protected boolean startBox(final RenderBox box)
  {
    final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
    if (properties.isWidowOrphanOptOut() == false)
    {
      context.startChild(box);
    }

    context = create(box);
    return true;
  }

  protected void finishBox(final RenderBox box)
  {
    context = context.commit(box);
    final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
    if (properties.isWidowOrphanOptOut() == false)
    {
      context.endChild(box);
    }
  }

}
