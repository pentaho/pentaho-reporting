package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

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
public class WidowOrphanStep extends IterateStructuralProcessStep
{
  public WidowOrphanStep()
  {
  }

  public void processWidowOrphanAnnotation (final LogicalPageBox box)
  {

  }
}
