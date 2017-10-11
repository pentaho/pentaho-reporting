/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.columns.TableColumnModel;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.CenterAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.JustifyAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.LeftAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.RightAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.TextAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisTableContext;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public abstract class AbstractMinorAxisLayoutStep extends IterateVisualProcessStep {
  public static final long OVERFLOW_DUMMY_WIDTH = StrictGeomUtility.toInternalValue( 20000 );

  private OutputProcessorMetaData metaData;
  private boolean strictLegacyMode;

  private PageGrid pageGrid;
  private TextAlignmentProcessor centerProcessor;
  private TextAlignmentProcessor rightProcessor;
  private TextAlignmentProcessor leftProcessor;
  private TextAlignmentProcessor justifyProcessor;
  private MinorAxisTableContext tableContext;

  protected AbstractMinorAxisLayoutStep() {
  }

  public void initialize( final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    this.strictLegacyMode = metaData.isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY );
  }

  protected OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected boolean isStrictLegacyMode() {
    return strictLegacyMode;
  }

  protected PageGrid getPageGrid() {
    return pageGrid;
  }

  protected boolean checkCacheValid( final RenderNode node ) {
    final RenderNode.CacheState cacheState = node.getCacheState();
    if ( cacheState == RenderNode.CacheState.CLEAN ) {
      return true;
    }
    return false;
  }

  public void compute( final LogicalPageBox root ) {
    getEventWatch().start();
    getSummaryWatch().start();
    try {
      pageGrid = root.getPageGrid();
      startProcessing( root );
    } finally {
      pageGrid = null;
      getEventWatch().stop();
      getSummaryWatch().stop( true );
    }
  }

  protected abstract MinorAxisNodeContext getNodeContext();

  /**
   * Reuse the processors ..
   *
   * @param alignment
   * @return
   */
  protected TextAlignmentProcessor create( final ElementAlignment alignment ) {
    if ( ElementAlignment.CENTER.equals( alignment ) ) {
      if ( centerProcessor == null ) {
        centerProcessor = new CenterAlignmentProcessor();
      }
      return centerProcessor;
    } else if ( ElementAlignment.RIGHT.equals( alignment ) ) {
      if ( rightProcessor == null ) {
        rightProcessor = new RightAlignmentProcessor();
      }
      return rightProcessor;
    } else if ( ElementAlignment.JUSTIFY.equals( alignment ) ) {
      if ( justifyProcessor == null ) {
        justifyProcessor = new JustifyAlignmentProcessor();
      }
      return justifyProcessor;
    }

    if ( leftProcessor == null ) {
      leftProcessor = new LeftAlignmentProcessor();
    }
    return leftProcessor;
  }

  protected void startTableContext( final RenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      if ( isStrictLegacyMode() ) {
        throw new InvalidReportStateException( "A report with a legacy mode of pre-4.0 cannot handle table layouts. "
            + "Migrate your report to at least version 4.0." );
      }
      tableContext = new MinorAxisTableContext( (TableRenderBox) box, tableContext );
    }
  }

  protected boolean finishTableContext( final RenderBox box ) {
    if ( box.getNodeType() != LayoutNodeTypes.TYPE_BOX_TABLE ) {
      return false;
    }

    final TableColumnModel columnModel = tableContext.getTable().getColumnModel();
    if ( tableContext.isStructureValidated() == false ) {
      columnModel.validateSizes( (TableRenderBox) box );
      tableContext.setStructureValidated( true );
    }
    box.setCachedWidth( columnModel.getCachedSize() );
    tableContext = tableContext.pop();
    return true;
  }

  public MinorAxisTableContext getTableContext() {
    return tableContext;
  }

  protected long computeCellWidth( final TableCellRenderBox tableCellRenderBox ) {
    final MinorAxisTableContext tableContext = getTableContext();
    final int columnIndex = tableCellRenderBox.getColumnIndex();
    final TableColumnModel columnModel = tableContext.getColumnModel();

    final int colSpan = tableCellRenderBox.getColSpan();
    if ( colSpan <= 0 ) {
      throw new InvalidReportStateException( "A cell cannot have a col-span of zero or less" );
    }

    long cellSizeFromModel = 0;
    for ( int i = 0; i < colSpan; i++ ) {
      cellSizeFromModel += columnModel.getEffectiveColumnSize( columnIndex + i );
      cellSizeFromModel += columnModel.getBorderSpacing();
    }
    cellSizeFromModel -= columnModel.getBorderSpacing();
    return cellSizeFromModel;
  }

  protected boolean startTableColLevelBox( final RenderBox box ) {
    return false;
  }
}
