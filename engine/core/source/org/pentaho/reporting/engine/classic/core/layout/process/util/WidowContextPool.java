package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

public class WidowContextPool
{
  private static class BlockContextPool extends StackedObjectPool<WidowBlockContext>
  {
    private BlockContextPool()
    {
    }

    protected WidowBlockContext create()
    {
      return new WidowBlockContext();
    }
  }

  private static class CanvasContextPool extends StackedObjectPool<WidowCanvasContext>
  {
    private CanvasContextPool()
    {
    }

    protected WidowCanvasContext create()
    {
      return new WidowCanvasContext();
    }
  }

  private CanvasContextPool canvasContextPool;
  private BlockContextPool blockContextPool;

  public WidowContextPool()
  {
    canvasContextPool = new CanvasContextPool();
    blockContextPool = new BlockContextPool();
  }

  public WidowContext create(final RenderBox box,
                              final WidowContext context)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
      final int widows = properties.getWidows();
      final int orphans = properties.getOrphans();
      final WidowBlockContext retval = blockContextPool.get();
      retval.init(blockContextPool, context, box, widows, orphans);
      return retval;
    }

    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW)
    {
      // todo: Make this a row-context later .. (Not needed for 3.9)
      // return new WidowCanvasContext(context);
    }

    final WidowCanvasContext retval = canvasContextPool.get();
    retval.init(canvasContextPool, context);
    return retval;
  }

  public void free(final WidowContext context)
  {
    context.clearForPooledReuse();
  }
}
