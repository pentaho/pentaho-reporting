package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.util.LongRingBuffer;

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
  private static interface WidowOrphanContext
  {
    public WidowOrphanContext getParent();

    public void add(RenderBox box);
    public void addDeep(RenderBox box);

    public long getOrphanValue();
    public long getWidowValue();
    public void finish();
  }

  private static class BlockWidowOrphanContext implements WidowOrphanContext
  {
    private WidowOrphanContext parent;
    private int widows;
    private int orphans;
    private int count;
    private LongRingBuffer orphanSize;
    private LongRingBuffer widowSize;
    private boolean firstDeep;

    private BlockWidowOrphanContext(final WidowOrphanContext parent,
                                    final int widows, final int orphans)
    {
      this.parent = parent;
      this.widows = widows;
      this.orphans = orphans;
      this.firstDeep = true;
      if (widows > 0)
      {
        this.widowSize = new LongRingBuffer(widows);
      }
      if (orphans > 0)
      {
        this.orphanSize = new LongRingBuffer(orphans);
      }
    }

    public WidowOrphanContext getParent()
    {
      return parent;
    }

    public void add(final RenderBox box)
    {
      if (count < orphans && orphanSize != null)
      {
        final long y2 = box.getY() + box.getHeight();
        orphanSize.add(y2);
      }

      if (widowSize != null)
      {
        widowSize.add(box.getY());
      }

      count += 1;

      if (parent != null)
      {
        parent.addDeep(box);
      }
    }

    public void addDeep(final RenderBox box)
    {
      final long y2 = box.getY() + box.getHeight();
      if (firstDeep)
      {
        if (widowSize != null)
        {
          widowSize.replaceLastAdded(box.getY());
        }
        if (count < orphans && orphanSize != null)
        {
          orphanSize.replaceLastAdded(y2);
        }

        firstDeep = false;
      }
      else
      {
        if (count < orphans && orphanSize != null)
        {
          orphanSize.add(y2);
        }
        if (widowSize != null)
        {
          widowSize.add(box.getY());
        }
      }

      count += 1;
    }

    public long getOrphanValue()
    {
      return orphanSize.getLastValue();
    }

    public long getWidowValue()
    {
      return widowSize.getFirstValue();
    }

    public void finish()
    {

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

    public void add(final RenderBox box)
    {
      if (parent != null)
      {
        parent.add(box);
      }
    }

    public void addDeep(final RenderBox box)
    {
      if (parent != null)
      {
        parent.addDeep(box);
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

    public void finish()
    {

    }
  }

  public static class CanvasWidowOrphanContext implements WidowOrphanContext
  {
    private WidowOrphanContext parent;

    public CanvasWidowOrphanContext(final WidowOrphanContext parent)
    {
      this.parent = parent;
    }

    public WidowOrphanContext getParent()
    {
      return parent;
    }

    public void add(final RenderBox box)
    {
      // ignore. A canvas box cannot compute a widow/orphan context in a meaningful way.
    }

    public void addDeep(final RenderBox box)
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

    public void finish()
    {
      // ignore ..
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

  private WidowOrphanContext create(final RenderBox box, final int widows, final int orphans)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      if (widows == 0 && orphans == 0)
      {
        return new PassThroughWidowOrphanContext(context);
      }
      return new BlockWidowOrphanContext(context, widows, orphans);
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
      context.add(box);
    }

    final int widows = properties.getWidows();
    final int orphans = properties.getOrphans();
    context = create(box, widows, orphans);
    return true;
  }

  protected void finishBox(final RenderBox box)
  {
    context.finish();
    box.setOrphanConstraintSize(context.getOrphanValue() - box.getY());
    box.setWidowConstraintSize((box.getY() + box.getHeight()) - context.getWidowValue());

    context = context.getParent();
  }

}
