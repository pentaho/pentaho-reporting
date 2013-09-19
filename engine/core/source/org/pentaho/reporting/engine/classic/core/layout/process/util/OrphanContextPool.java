package org.pentaho.reporting.engine.classic.core.layout.process.util;

import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;

public class OrphanContextPool
{
  private static class BlockContextPool extends StackedObjectPool<OrphanBlockContext>
  {
    private BlockContextPool()
    {
    }

    protected OrphanBlockContext create()
    {
      return new OrphanBlockContext();
    }
  }

  private static class CanvasContextPool extends StackedObjectPool<OrphanCanvasContext>
  {
    private CanvasContextPool()
    {
    }

    protected OrphanCanvasContext create()
    {
      return new OrphanCanvasContext();
    }
  }

  private CanvasContextPool canvasContextPool;
  private BlockContextPool blockContextPool;

  public OrphanContextPool()
  {
    canvasContextPool = new CanvasContextPool();
    blockContextPool = new BlockContextPool();
  }

  public OrphanContext create(final RenderBox box,
                                   final OrphanContext context)
  {
    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      final StaticBoxLayoutProperties properties = box.getStaticBoxLayoutProperties();
      final int orphans = properties.getOrphans();
      final OrphanBlockContext retval = blockContextPool.get();
      retval.init(blockContextPool, context, box, orphans);
      return retval;
    }

    if ((box.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW)
    {
      // todo: Make this a row-context later .. (Not needed for 3.9)
      // return new WidowCanvasContext(context);
    }

    final OrphanCanvasContext retval = canvasContextPool.get();
    retval.init(canvasContextPool, context);
    return retval;
  }

  public void free (final OrphanContext context)
  {
    context.clearForPooledReuse();
  }
}
