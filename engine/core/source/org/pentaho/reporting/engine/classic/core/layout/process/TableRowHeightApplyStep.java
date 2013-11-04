package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;

public class TableRowHeightApplyStep extends IterateStructuralProcessStep
{
  private static class BoxContext
  {
    public long boxContextStart;
    public long boxCursor;
    public final BoxContext parent;

    public BoxContext(final BoxContext parent,
                      final RenderBox box)
    {
      this.parent = parent;
      this.boxContextStart = box.getCachedY();
      this.boxCursor = boxContextStart;
    }

    public BoxContext pop()
    {
      parent.boxCursor = boxCursor;
      return parent;
    }

    public long getBoxCursor()
    {
      return boxCursor;
    }

    public void addBoxCursor(final long height)
    {
      this.boxCursor += height;
    }
  }

  private BoxContext context;
  private TableRowModel rowModel;

  public TableRowHeightApplyStep()
  {
  }

  public long start (TableSectionRenderBox section)
  {
    try
    {
      context = null;
      rowModel = section.getRowModel();

      context = new BoxContext(context, section);
      processBoxChilds(section);

      long usedTableBodyHeight = context.getBoxCursor() - context.boxContextStart;
      section.setCachedHeight(usedTableBodyHeight);

      return usedTableBodyHeight;
    }
    finally
    {
      context = null;
      rowModel = null;
    }
  }

  protected void processOtherNode(final RenderNode node)
  {
    node.setCachedY(context.getBoxCursor());
    context.addBoxCursor(node.getCachedHeight());
  }

  private void shiftBox (final RenderBox box)
  {
    final long oldPosition = box.getCachedY();
    final long position = context.getBoxCursor();
    final long shift = position - oldPosition;
    if (shift < 0)
    {
      throw new IllegalStateException(String.format
          ("Shift-back is not allowed: shift=%d: old=%d -> new=%d (%s)", shift, oldPosition, position, box));
    }

    CacheBoxShifter.shiftBox(box, shift);
  }

  protected boolean startTableRowBox(final TableRowRenderBox box)
  {
    shiftBox(box);

    context = new BoxContext(context, box);
    return false;
  }

  protected void finishTableRowBox(final TableRowRenderBox box)
  {
    final long validatedRowHeight = rowModel.getValidatedRowSize(box.getRowIndex());
    box.setCachedHeight(validatedRowHeight);
    context.addBoxCursor(validatedRowHeight);
    context = context.pop();
  }

  protected boolean startTableSectionBox(final TableSectionRenderBox box)
  {
    shiftBox(box);

    context = new BoxContext(context, box);
    return true;
  }

  protected void finishTableSectionBox(final TableSectionRenderBox box)
  {
    context = context.pop();
  }

  protected boolean startAutoBox(final RenderBox box)
  {
    shiftBox(box);

    context = new BoxContext(context, box);
    return true;
  }

  protected void finishAutoBox(final RenderBox box)
  {
    context = context.pop();
  }
}
