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

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
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

/**
 * Iterates over the document tree using the display-role of the current node as selector. Usually all structural
 * processing steps use this iteration strategy.
 *
 * @author Thomas Morgner
 */
public abstract class IterateStructuralProcessStep {
  protected IterateStructuralProcessStep() {
  }

  protected final void startProcessing( final RenderNode node ) {
    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_AUTOLAYOUT ) {
      final RenderBox box = (RenderBox) node;
      if ( startAutoBox( box ) ) {
        processBoxChilds( box );
      }
      finishAutoBox( box );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      processRenderableContent( (RenderableReplacedContentBox) node );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_COL ) {
      processTableColumn( (TableColumnNode) node );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
      if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE ) {
        final TableRenderBox box = (TableRenderBox) node;
        if ( startTableBox( box ) ) {
          processBoxChilds( box );
        }
        finishTableBox( box );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_COL_GROUP ) {
        final TableColumnGroupNode box = (TableColumnGroupNode) node;
        if ( startTableColumnGroupBox( box ) ) {
          processBoxChilds( box );
        }
        finishTableColumnGroupBox( box );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
        final TableSectionRenderBox box = (TableSectionRenderBox) node;
        if ( startTableSectionBox( box ) ) {
          processBoxChilds( box );
        }
        finishTableSectionBox( box );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
        final TableRowRenderBox box = (TableRowRenderBox) node;
        if ( startTableRowBox( box ) ) {
          processBoxChilds( box );
        }
        finishTableRowBox( box );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
        final TableCellRenderBox box = (TableCellRenderBox) node;
        if ( startTableCellBox( box ) ) {
          processBoxChilds( box );
        }
        finishTableCellBox( box );
      } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
        if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
          final ParagraphRenderBox box = (ParagraphRenderBox) node;
          if ( startBlockBox( box ) ) {
            processParagraphChilds( box );
          }
          finishBlockBox( box );
        } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
          final LogicalPageBox box = (LogicalPageBox) node;
          if ( startBlockBox( box ) ) {
            startProcessing( box.getWatermarkArea() );
            startProcessing( box.getHeaderArea() );
            processBoxChilds( box );
            startProcessing( box.getRepeatFooterArea() );
            startProcessing( box.getFooterArea() );
          }
          finishBlockBox( box );
        } else {
          final BlockRenderBox box = (BlockRenderBox) node;
          if ( startBlockBox( box ) ) {
            processBoxChilds( box );
          }
          finishBlockBox( box );
        }
      } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_CANVAS ) == LayoutNodeTypes.MASK_BOX_CANVAS ) {
        final CanvasRenderBox box = (CanvasRenderBox) node;
        if ( startCanvasBox( box ) ) {
          processBoxChilds( box );
        }
        finishCanvasBox( box );
      } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
        final InlineRenderBox box = (InlineRenderBox) node;
        if ( startInlineBox( box ) ) {
          processBoxChilds( box );
        }
        finishInlineBox( box );
      } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
        final RenderBox box = (RenderBox) node;
        if ( startRowBox( box ) ) {
          processBoxChilds( box );
        }
        finishRowBox( box );
      } else {
        final RenderBox box = (RenderBox) node;
        if ( startOtherBox( box ) ) {
          processBoxChilds( box );
        }
        finishOtherBox( box );
      }
    } else {
      processOtherNode( node );
    }
  }

  protected void finishTableCellBox( final TableCellRenderBox box ) {
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    return true;
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    return true;
  }

  protected void processTableColumn( final TableColumnNode node ) {

  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    return true;
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    return true;
  }

  protected void finishTableColumnGroupBox( final TableColumnGroupNode box ) {
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    return true;
  }

  protected void finishTableBox( final TableRenderBox box ) {

  }

  protected void finishCanvasBox( final CanvasRenderBox box ) {

  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    return true;
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    processBoxChilds( box.getPool() );
  }

  protected final void processBoxChilds( final RenderBox box ) {
    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      startProcessing( node );
      node = node.getNext();
    }
  }

  protected void processOtherNode( final RenderNode node ) {
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return true;
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return true;
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return true;
  }

  protected void finishOtherBox( final RenderBox box ) {
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
  }

  protected boolean startRowBox( final RenderBox box ) {
    return true;
  }

  protected void finishRowBox( final RenderBox box ) {
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return true;
  }

  protected void finishAutoBox( final RenderBox box ) {
  }
}
