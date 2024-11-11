/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;

public final class ApplyCachedValuesStep extends IterateStructuralProcessStep {
  private boolean cacheClean;

  public ApplyCachedValuesStep() {
  }

  public void compute( final RenderBox box ) {
    cacheClean = true;
    startProcessing( box );
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box );
  }

  public boolean startCanvasBox( final CanvasRenderBox box ) {
    return processBox( box );
  }

  protected void processOtherNode( final RenderNode node ) {
    processFinishNode( node );
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    processBox( box );
    box.setOverflowAreaWidth( box.getCachedWidth() );
    box.setOverflowAreaHeight( box.getCachedHeight() );
    processFinishBox( box );
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return processBox( box );
  }

  protected boolean startRowBox( final RenderBox box ) {
    return processBox( box );
  }

  private boolean processBox( final RenderBox box ) {
    if ( box.getApplyState() != RenderNode.CacheState.CLEAN ) {
      cacheClean = false;
    }

    if ( cacheClean ) {
      final RenderNode.CacheState state = box.getCacheState();
      if ( state == RenderNode.CACHE_CLEAN ) {
        return false;
      }
      if ( state == RenderNode.CACHE_DEEP_DIRTY ) {
        cacheClean = false;
      }
    }

    box.apply();
    return true;
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return processBox( box );
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return processBox( box );
  }

  protected void processTableColumn( final TableColumnNode box ) {
    processBox( box );
    processFinishBox( box );
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    return processBox( box );
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    return processBox( box );
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    return processBox( box );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    return processBox( box );
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    return processBox( box );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return processBox( box );
  }

  protected void finishAutoBox( final RenderBox box ) {
    processFinishBox( box );
  }

  public void finishCanvasBox( final CanvasRenderBox box ) {
    processFinishBox( box );
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    processFinishBox( box );
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    processFinishBox( box );
  }

  protected void finishRowBox( final RenderBox box ) {
    processFinishBox( box );
  }

  protected void finishOtherBox( final RenderBox box ) {
    processFinishBox( box );
  }

  protected void finishTableBox( final TableRenderBox box ) {
    processFinishBox( box );
  }

  protected void finishTableCellBox( final TableCellRenderBox box ) {
    processFinishBox( box );
  }

  protected void finishTableColumnGroupBox( final TableColumnGroupNode box ) {
    processFinishBox( box );
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    processFinishBox( box );
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    processFinishBox( box );
  }

  private void processFinishBox( final RenderBox box ) {
    processFinishNode( box );
    box.setStaticBoxPropertiesAge( box.getChangeTracker() );
  }

  private void processFinishNode( final RenderNode box ) {
    box.apply();
  }
}
