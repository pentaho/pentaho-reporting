package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;

public class PaginationShiftStatePool
{
  protected boolean isBlock(final int nodeType)
  {
    if ((nodeType & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      return true;
    }

    if ((nodeType & LayoutNodeTypes.TYPE_BOX_TABLE) == LayoutNodeTypes.TYPE_BOX_TABLE)
    {
      return true;
    }

    if ((nodeType & LayoutNodeTypes.TYPE_BOX_TABLE_SECTION) == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION)
    {
      return true;
    }
    if ((nodeType & LayoutNodeTypes.TYPE_BOX_TABLE_CELL) == LayoutNodeTypes.TYPE_BOX_TABLE_CELL)
    {
      return true;
    }
    return false;
  }

  public PaginationShiftState create(RenderBox box, PaginationShiftState parent)
  {
    final int nodeType = box.getLayoutNodeType();
    if (isBlock(nodeType))
    {
      return new BlockLevelPaginationShiftState(parent);
    }
    return new RowLevelPaginationShiftState(parent);
  }
}
