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

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;

public class TableLayoutProducer extends IterateSimpleStructureProcessStep {
  private SheetLayout layout;
  private long pageOffset;
  private boolean headerProcessed;
  private long contentOffset;
  private long effectiveHeaderSize;
  private long pageEndPosition;

  private boolean unalignedPagebands;
  private boolean processWatermark;

  public TableLayoutProducer( final OutputProcessorMetaData metaData ) {
    initialize( metaData );
    this.layout = new SheetLayout( metaData );
  }

  private void initialize( final OutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    this.processWatermark = metaData.isFeatureSupported( OutputProcessorFeature.WATERMARK_SECTION );
    this.unalignedPagebands = metaData.isFeatureSupported( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
  }

  public TableLayoutProducer( final OutputProcessorMetaData metaData, final SheetLayout sheetLayout ) {
    initialize( metaData );
    this.layout = sheetLayout;
  }

  public boolean isProcessWatermark() {
    return processWatermark;
  }

  public void setProcessWatermark( final boolean processWatermark ) {
    this.processWatermark = processWatermark;
  }

  public SheetLayout getLayout() {
    return layout;
  }

  public void update( final LogicalPageBox logicalPage, final boolean iterativeUpdate ) {
    if ( unalignedPagebands == false ) {
      // The page-header and footer area are aligned/shifted within the logical pagebox so that all areas
      // share a common coordinate system. This also implies, that the whole logical page is aligned content.
      pageOffset = 0;
      effectiveHeaderSize = 0;
      pageEndPosition = logicalPage.getPageEnd();
      // Log.debug ("Content Processing " + pageOffset + " -> " + pageEnd);
      if ( startBox( logicalPage ) ) {
        if ( headerProcessed == false ) {
          if ( processWatermark ) {
            startProcessing( logicalPage.getWatermarkArea() );
          }
          final BlockRenderBox headerArea = logicalPage.getHeaderArea();
          startProcessing( headerArea );
          headerProcessed = true;
        }

        processBoxChilds( logicalPage );
        if ( iterativeUpdate == false ) {
          final BlockRenderBox repeatFooterBox = logicalPage.getRepeatFooterArea();
          startProcessing( repeatFooterBox );

          final BlockRenderBox pageFooterBox = logicalPage.getFooterArea();
          startProcessing( pageFooterBox );
        }
      }
      finishBox( logicalPage );
    } else {
      // The page-header and footer area are not aligned/shifted within the logical pagebox.
      // All areas have their own coordinate system starting at (0,0). We apply a manual shift here
      // so that we dont have to modify the nodes (which invalidates the cache, and therefore is ugly)
      effectiveHeaderSize = 0;
      pageOffset = logicalPage.getPageOffset();
      pageEndPosition = ( logicalPage.getPageEnd() );
      if ( startBox( logicalPage ) ) {
        if ( headerProcessed == false ) {
          pageOffset = 0;
          contentOffset = 0;
          effectiveHeaderSize = 0;

          if ( processWatermark ) {
            final BlockRenderBox watermarkArea = logicalPage.getWatermarkArea();
            pageEndPosition = watermarkArea.getHeight();
            startProcessing( watermarkArea );
          }

          final BlockRenderBox headerArea = logicalPage.getHeaderArea();
          pageEndPosition = headerArea.getHeight();
          startProcessing( headerArea );
          contentOffset = headerArea.getHeight();
          headerProcessed = true;
        }

        pageOffset = logicalPage.getPageOffset();
        pageEndPosition = logicalPage.getPageEnd();
        effectiveHeaderSize = contentOffset;
        processBoxChilds( logicalPage );

        if ( iterativeUpdate == false ) {
          pageOffset = 0;
          final BlockRenderBox repeatFooterArea = logicalPage.getRepeatFooterArea();
          final long repeatFooterOffset = contentOffset + ( logicalPage.getPageEnd() - logicalPage.getPageOffset() );
          final long repeatFooterPageEnd = repeatFooterOffset + repeatFooterArea.getHeight();
          effectiveHeaderSize = repeatFooterOffset;
          pageEndPosition = repeatFooterPageEnd;
          startProcessing( repeatFooterArea );

          final BlockRenderBox footerArea = logicalPage.getFooterArea();
          final long footerPageEnd = repeatFooterPageEnd + footerArea.getHeight();
          effectiveHeaderSize = repeatFooterPageEnd;
          pageEndPosition = footerPageEnd;
          startProcessing( footerArea );
        }
      }
      finishBox( logicalPage );
    }

    // try to remove as many nodes as you can ..
    logicalPage.setProcessedTableOffset( logicalPage.getPageEnd() );
  }

  protected boolean startBox( final RenderBox box ) {
    if ( box.isVisible() == false ) {
      return false;
    }

    if ( box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      processRenderableContent( (RenderableReplacedContentBox) box );
      return false;
    }

    return startBoxInternal( box );
  }

  private boolean startBoxInternal( final RenderBox box ) {

    final long height = box.getHeight();
    //
    // DebugLog.log ("Processing Box " + pageOffset + " " + effectiveHeaderSize + " " + box.getY() + " " + height);
    // DebugLog.log ("Processing Box " + box);

    if ( height > 0 ) {
      if ( ( box.getY() + height ) <= pageOffset ) {
        return false;
      }
      if ( box.getY() >= pageEndPosition ) {
        return false;
      }
    } else {
      // zero height boxes are always a bit tricky ..
      if ( ( box.getY() + height ) < pageOffset ) {
        return false;
      }
      if ( box.getY() > pageEndPosition ) {
        return false;
      }
    }

    if ( box.isOpen() == false && box.isFinishedTable() == false && box.isCommited() ) {
      if ( layout.add( box, pageOffset, effectiveHeaderSize, pageEndPosition ) ) {
        return false;
      }
      box.setFinishedTable( true );
      return true;
    }

    return true;
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    if ( box.isOpen() == false && box.isFinishedTable() == false && box.isCommited() ) {
      startBoxInternal( box );
      layout.addRenderableContent( box, pageOffset, effectiveHeaderSize, pageEndPosition );
    }
  }

  protected void processBoxChilds( final RenderBox box ) {
    if ( box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      // not needed. Keep this method empty so that the paragraph childs are *not* processed.
      return;
    }
    super.processBoxChilds( box );
  }

  public void pageCompleted() {
    layout.pageCompleted();
    headerProcessed = false;
  }

  /**
   * A designtime support method to compute a sheet layout for the given section. A new sheetlayout is created on each
   * call.
   *
   * @param box
   *          the section that should be processed.
   * @return the computed sheet layout.
   */
  public void computeDesigntimeConflicts( final RenderBox box ) {
    clear();
    pageEndPosition = box.getHeight();

    startProcessing( box );
  }

  public void clear() {
    this.layout.clear();

    effectiveHeaderSize = 0;
    pageOffset = 0;
    pageEndPosition = 0;
    contentOffset = 0;
    headerProcessed = false;
  }

}
