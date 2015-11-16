package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.rows.TableRowModel;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.util.StackedObjectPool;

public class TableRowHeightApplyStep extends IterateStructuralProcessStep {
  private static class BoxContext {
    private BoxContextPool pool;
    private BoxContext parent;
    private long boxContextStart;
    private long boxCursor;

    private BoxContext() {
    }

    public void reuse( final BoxContextPool pool, final BoxContext parent, final RenderBox box ) {
      this.pool = pool;
      this.parent = parent;
      this.boxContextStart = box.getCachedY();
      this.boxCursor = boxContextStart;
    }

    public BoxContext pop() {
      if ( parent != null ) {
        parent.boxCursor = boxCursor;
      }
      pool.free( this );
      return parent;
    }

    public long getBoxCursor() {
      return boxCursor;
    }

    public void addBoxCursor( final long height ) {
      this.boxCursor += height;
    }
  }

  private class BoxContextPool extends StackedObjectPool<BoxContext> {
    private BoxContextPool() {
    }

    protected BoxContext create() {
      return new BoxContext();
    }

    public BoxContext get( final BoxContext parent, final RenderBox box ) {
      BoxContext boxContext = super.get();
      boxContext.reuse( this, parent, box );
      return boxContext;
    }
  }

  private BoxContextPool pool;
  private BoxContext context;
  private TableRowModel rowModel;

  public TableRowHeightApplyStep() {
    pool = new BoxContextPool();
  }

  public long start( TableSectionRenderBox section ) {
    try {
      context = pool.get( null, section );
      rowModel = section.getRowModel();

      processBoxChilds( section );

      long usedTableBodyHeight = context.getBoxCursor() - context.boxContextStart;
      section.setCachedHeight( usedTableBodyHeight );

      return usedTableBodyHeight;
    } finally {
      context = context.pop();
      rowModel = null;
    }
  }

  protected void processOtherNode( final RenderNode node ) {
    node.setCachedY( context.getBoxCursor() );
    context.addBoxCursor( node.getCachedHeight() );
  }

  private void shiftBox( final RenderBox box ) {
    final long oldPosition = box.getCachedY();
    final long position = context.getBoxCursor();
    final long shift = position - oldPosition;
    if ( shift < 0 ) {
      throw new IllegalStateException( String.format( "Shift-back is not allowed: shift=%d: old=%d -> new=%d (%s)",
          shift, oldPosition, position, box ) );
    }

    CacheBoxShifter.shiftBox( box, shift );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    shiftBox( box );

    context = pool.get( context, box );
    return false;
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    final long validatedRowHeight = rowModel.getValidatedRowSize( box.getRowIndex() );
    box.setCachedHeight( validatedRowHeight );
    context.addBoxCursor( validatedRowHeight );
    context = context.pop();
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    shiftBox( box );

    context = pool.get( context, box );
    return true;
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    context = context.pop();
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    shiftBox( box );

    context = pool.get( context, box );
    return true;
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    context = context.pop();
  }

  protected boolean startAutoBox( final RenderBox box ) {
    shiftBox( box );

    context = pool.get( context, box );
    return true;
  }

  protected void finishAutoBox( final RenderBox box ) {
    RenderNode firstChild = box.getFirstChild();
    if ( firstChild != null ) {
      RenderNode lastChild = box.getLastChild();
      long height = lastChild.getCachedY2() - firstChild.getCachedY();
      box.setCachedHeight( height );
    }
    context = context.pop();
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return false;
  }

  protected void finishOtherBox( final RenderBox box ) {
    RenderNode firstChild = box.getFirstChild();
    if ( firstChild != null ) {
      RenderNode lastChild = box.getLastChild();
      long height = lastChild.getCachedY2() - firstChild.getCachedY();
      box.setCachedHeight( height );
    }
  }
}
