/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.util.EmptyPerformanceLoggingStopWatch;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;

/**
 * Iterates over the tree of nodes and classifies nodes by their Display-Model. The Display-Model of nodes is either
 * 'Block' or 'Inline'. All steps dealing with element placement commonly use this strategy.
 *
 * @author Thomas Morgner
 */
public abstract class IterateVisualProcessStep {
  private PerformanceLoggingStopWatch summaryWatch;
  private PerformanceLoggingStopWatch eventWatch;

  protected IterateVisualProcessStep() {
    summaryWatch = EmptyPerformanceLoggingStopWatch.INSTANCE;
    eventWatch = EmptyPerformanceLoggingStopWatch.INSTANCE;
  }

  public void initializePerformanceMonitoring( final PerformanceMonitorContext monitorContext ) {
    summaryWatch.stop();
    eventWatch.stop();

    summaryWatch =
        monitorContext.createStopWatch( PerformanceTags.getSummaryTag( PerformanceTags.REPORT_LAYOUT_PROCESS_SUFFIX,
            getClass().getSimpleName() ) );
    eventWatch =
        monitorContext.createStopWatch( PerformanceTags.getDetailTag( PerformanceTags.REPORT_LAYOUT_PROCESS_SUFFIX,
            getClass().getSimpleName() ) );
  }

  public void close() {
    summaryWatch.close();
    eventWatch.close();
  }

  protected PerformanceLoggingStopWatch getSummaryWatch() {
    return summaryWatch;
  }

  protected PerformanceLoggingStopWatch getEventWatch() {
    return eventWatch;
  }

  protected final void startProcessing( final RenderNode node ) {
    final RenderBox parent = node.getParent();
    if ( parent == null ) {
      processBlockLevelChild( node );
      return;
    }

    final int parentType = parent.getLayoutNodeType();
    if ( ( parentType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      processBlockLevelChild( node );
    } else if ( ( parentType & LayoutNodeTypes.MASK_BOX_CANVAS ) == LayoutNodeTypes.MASK_BOX_CANVAS ) {
      processCanvasLevelChild( node );
    } else if ( ( parentType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
      processInlineLevelChild( node );
    } else if ( ( parentType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      processRowLevelChild( node );
    } else if ( parentType == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      processTableChild( node );
    } else if ( parentType == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      processTableSectionChild( node );
    } else if ( parentType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
      processTableRowChild( node );
    } else if ( parentType == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
      processTableCellChild( node );
    } else if ( parentType == LayoutNodeTypes.TYPE_BOX_TABLE_COL ) {
      processTableColChild( node );
    } else if ( parentType == LayoutNodeTypes.TYPE_BOX_TABLE_COL_GROUP ) {
      processTableColGroupChild( node );
    } else {
      processOtherLevelChild( node );
    }
  }

  protected final void processTableChild( final RenderNode node ) {
    final int type = node.getNodeType();
    // We do not expect or handle paragraphs on this level.
    if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startTableLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishTableLevelBox( box );
    } else {
      processTableLevelNode( node );
    }
  }

  protected void processTableLevelNode( final RenderNode node ) {
  }

  protected void finishTableLevelBox( final RenderBox box ) {
  }

  protected boolean startTableLevelBox( final RenderBox box ) {
    return true;
  }

  protected final void processTableRowChild( final RenderNode node ) {
    final int type = node.getNodeType();
    // We do not expect or handle paragraphs on this level.
    if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startTableRowLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishTableRowLevelBox( box );
    } else {
      processTableRowLevelNode( node );
    }
  }

  protected void processTableRowLevelNode( final RenderNode node ) {
  }

  protected void finishTableRowLevelBox( final RenderBox box ) {
  }

  protected boolean startTableRowLevelBox( final RenderBox box ) {
    return true;
  }

  protected final void processTableSectionChild( final RenderNode node ) {
    final int type = node.getNodeType();
    // We do not expect or handle paragraphs on this level.
    if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startTableSectionLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishTableSectionLevelBox( box );
    } else {
      processTableSectionLevelNode( node );
    }
  }

  protected void processTableSectionLevelNode( final RenderNode node ) {
  }

  protected void finishTableSectionLevelBox( final RenderBox box ) {
  }

  protected boolean startTableSectionLevelBox( final RenderBox box ) {
    return true;
  }

  protected final void processTableCellChild( final RenderNode node ) {
    final int type = node.getNodeType();
    if ( type == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if ( startTableCellLevelBox( box ) ) {
        processParagraphChilds( box );
      }
      finishTableCellLevelBox( box );
    } else if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startTableCellLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishTableCellLevelBox( box );
    } else {
      processTableCellLevelNode( node );
    }
  }

  protected void processTableCellLevelNode( final RenderNode node ) {
  }

  protected void finishTableCellLevelBox( final RenderBox box ) {
  }

  protected boolean startTableCellLevelBox( final RenderBox box ) {
    return true;
  }

  protected final void processTableColChild( final RenderNode node ) {
    final int type = node.getNodeType();
    if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startTableColLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishTableColLevelBox( box );
    } else {
      processTableColLevelNode( node );
    }
  }

  protected void processTableColLevelNode( final RenderNode node ) {
  }

  protected void finishTableColLevelBox( final RenderBox box ) {
  }

  protected boolean startTableColLevelBox( final RenderBox box ) {
    return true;
  }

  protected final void processTableColGroupChild( final RenderNode node ) {
    final int type = node.getNodeType();
    if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startTableColGroupLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishTableColGroupLevelBox( box );
    } else {
      processTableColGroupLevelNode( node );
    }
  }

  protected void processTableColGroupLevelNode( final RenderNode node ) {
  }

  protected void finishTableColGroupLevelBox( final RenderBox box ) {
  }

  protected boolean startTableColGroupLevelBox( final RenderBox box ) {
    return true;
  }

  protected void processOtherLevelChild( final RenderNode node ) {
    // we do not even handle that one. Other level elements are
    // always non-visual!
  }

  protected void processInlineLevelNode( final RenderNode node ) {
  }

  protected boolean startInlineLevelBox( final RenderBox box ) {
    return true;
  }

  protected void finishInlineLevelBox( final RenderBox box ) {
  }

  protected final void processInlineLevelChild( final RenderNode node ) {
    final int type = node.getNodeType();
    if ( type == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if ( startInlineLevelBox( box ) ) {
        processParagraphChilds( box );
      }
      finishInlineLevelBox( box );
    } else if ( ( type & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startInlineLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishInlineLevelBox( box );
    } else {
      processInlineLevelNode( node );
    }
  }

  protected void processCanvasLevelNode( final RenderNode node ) {
  }

  protected boolean startCanvasLevelBox( final RenderBox box ) {
    return true;
  }

  protected void finishCanvasLevelBox( final RenderBox box ) {
  }

  protected final void processCanvasLevelChild( final RenderNode node ) {
    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if ( startCanvasLevelBox( box ) ) {
        processParagraphChilds( box );
      }
      finishCanvasLevelBox( box );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startCanvasLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishCanvasLevelBox( box );
    } else {
      processCanvasLevelNode( node );
    }
  }

  protected void processBlockLevelNode( final RenderNode node ) {
  }

  protected boolean startBlockLevelBox( final RenderBox box ) {
    return true;
  }

  protected void finishBlockLevelBox( final RenderBox box ) {
  }

  protected final void processBlockLevelChild( final RenderNode node ) {
    final int nodeType = node.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
      final LogicalPageBox box = (LogicalPageBox) node;
      if ( startBlockLevelBox( box ) ) {
        startProcessing( box.getWatermarkArea() );
        startProcessing( box.getHeaderArea() );
        processBoxChilds( box );
        startProcessing( box.getRepeatFooterArea() );
        startProcessing( box.getFooterArea() );
      }
      finishBlockLevelBox( box );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if ( startBlockLevelBox( box ) ) {
        processParagraphChilds( box );
      }
      finishBlockLevelBox( box );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startBlockLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishBlockLevelBox( box );
    } else {
      processBlockLevelNode( node );
    }
  }

  protected abstract void processParagraphChilds( final ParagraphRenderBox box );

  protected void processBoxChilds( final RenderBox box ) {
    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      startProcessing( node );
      node = node.getNext();
    }
  }

  protected final void processRowLevelChild( final RenderNode node ) {
    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox box = (ParagraphRenderBox) node;
      if ( startRowLevelBox( box ) ) {
        processParagraphChilds( box );
      }
      finishRowLevelBox( box );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      final RenderBox box = (RenderBox) node;
      if ( startRowLevelBox( box ) ) {
        processBoxChilds( box );
      }
      finishRowLevelBox( box );
    } else {
      processRowLevelNode( node );
    }
  }

  protected void processRowLevelNode( final RenderNode node ) {
  }

  protected boolean startRowLevelBox( final RenderBox box ) {
    return true;
  }

  protected void finishRowLevelBox( final RenderBox box ) {
  }

}
