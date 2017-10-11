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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.MasterReportType;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * The logical page is the root-structure of the generated content. This object is a slotted container.
 *
 * @author Thomas Morgner
 */
public final class LogicalPageBox extends BlockRenderBox {
  // All breaks along the major-axis.
  private PageBreakPositionList allVerticalBreaks;
  private long pageOffset;
  private long pageEnd;
  private long processedTableOffset;

  private WatermarkAreaBox watermarkArea;
  private PageAreaBox headerArea;
  private PageAreaBox footerArea;
  private PageAreaBox repeatFooterArea;
  private DefaultPageGrid pageGrid;
  private InstanceID contentAreaId;
  private String pageName;

  private WatermarkAreaBox savedWatermarkArea;
  private PageAreaBox savedHeaderArea;
  private PageAreaBox savedFooterArea;
  private PageAreaBox savedRepeatFooterArea;

  public LogicalPageBox( final ReportDefinition report, final StyleSheet style, final BoxDefinition box ) {
    super( style, report.getObjectID(), box, AutoLayoutBoxType.INSTANCE, report.getAttributes(), null );
    this.headerArea = new PageAreaBox();
    this.headerArea.setName( "Logical-Page-Header-Area" );
    this.headerArea.setLogicalPage( this );

    this.repeatFooterArea = new PageAreaBox();
    this.repeatFooterArea.setName( "Logical-Repeat-Footer-Area" );
    this.repeatFooterArea.setLogicalPage( this );

    this.footerArea = new PageAreaBox();
    this.footerArea.setName( "Logical-Page-Footer-Area" );
    this.footerArea.setLogicalPage( this );

    this.watermarkArea = new WatermarkAreaBox();
    this.watermarkArea.setName( "Logical-Page-Watermark-Area" );
    this.watermarkArea.setLogicalPage( this );

    final BlockRenderBox contentArea =
        new BlockRenderBox( SimpleStyleSheet.EMPTY_STYLE, new InstanceID(), BoxDefinition.EMPTY,
            new MasterReportType(), report.getAttributes(), null );
    contentArea.setName( "Logical-Page-Content-Area" );
    addChild( contentArea );
    contentAreaId = contentArea.getInstanceId();
    this.pageGrid = new DefaultPageGrid( report.getPageDefinition() );

    this.allVerticalBreaks = new PageBreakPositionList();
  }

  public BlockRenderBox getContentArea() {
    // this should be very inexpensive, as there is only one child, which already is the box in question.
    final BlockRenderBox blockRenderBox = (BlockRenderBox) findNodeById( contentAreaId );
    if ( blockRenderBox == null ) {
      throw new IllegalStateException( "Cloning or deriving must have failed: No content area." );
    }
    return blockRenderBox;
  }

  public BlockRenderBox getHeaderArea() {
    return headerArea;
  }

  public BlockRenderBox getRepeatFooterArea() {
    return repeatFooterArea;
  }

  public BlockRenderBox getFooterArea() {
    return footerArea;
  }

  public WatermarkAreaBox getWatermarkArea() {
    return watermarkArea;
  }

  public LogicalPageBox getLogicalPage() {
    return this;
  }

  public long getPageWidth() {
    return pageGrid.getMaximumPageWidth();
  }

  public PageGrid getPageGrid() {
    return pageGrid;
  }

  public long getPageOffset() {
    return pageOffset;
  }

  public void setPageOffset( final long pageOffset ) {
    this.pageOffset = pageOffset;
  }

  public long getPageEnd() {
    return pageEnd;
  }

  public void setPageEnd( final long pageEnd ) {
    this.pageEnd = pageEnd;
  }

  public long[] getPhysicalBreaks( final int axis ) {
    if ( axis == RenderNode.HORIZONTAL_AXIS ) {
      return pageGrid.getHorizontalBreaks();
    }
    return pageGrid.getVerticalBreaks();
  }

  public long getPageHeight() {
    return pageGrid.getMaximumPageHeight();
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, silbling, child or any other relationships with other nodes.
   *
   * @return
   */
  public LogicalPageBox deriveFrozen( final boolean deepDerive ) {
    final LogicalPageBox box = (LogicalPageBox) super.deriveFrozen( deepDerive );
    box.headerArea = (PageAreaBox) headerArea.deriveFrozen( deepDerive );
    box.headerArea.setLogicalPage( box );
    box.footerArea = (PageAreaBox) footerArea.deriveFrozen( deepDerive );
    box.footerArea.setLogicalPage( box );
    box.repeatFooterArea = (PageAreaBox) repeatFooterArea.deriveFrozen( deepDerive );
    box.repeatFooterArea.setLogicalPage( box );
    box.watermarkArea = (WatermarkAreaBox) watermarkArea.deriveFrozen( deepDerive );
    box.watermarkArea.setLogicalPage( box );

    return box;
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, silbling, child or any other relationships with other nodes.
   *
   * @return
   */
  public LogicalPageBox derive( final boolean deepDerive ) {
    final LogicalPageBox box = (LogicalPageBox) super.derive( deepDerive );
    box.headerArea = (PageAreaBox) headerArea.derive( deepDerive );
    box.headerArea.setLogicalPage( box );
    box.footerArea = (PageAreaBox) footerArea.derive( deepDerive );
    box.footerArea.setLogicalPage( box );
    box.repeatFooterArea = (PageAreaBox) repeatFooterArea.derive( deepDerive );
    box.repeatFooterArea.setLogicalPage( box );
    box.watermarkArea = (WatermarkAreaBox) watermarkArea.derive( deepDerive );
    box.watermarkArea.setLogicalPage( box );

    if ( box.savedFooterArea != null ) {
      box.savedFooterArea = (PageAreaBox) savedFooterArea.derive( deepDerive );
      box.savedFooterArea.setLogicalPage( box );
    }
    if ( box.savedRepeatFooterArea != null ) {
      box.savedRepeatFooterArea = (PageAreaBox) savedRepeatFooterArea.derive( deepDerive );
      box.savedRepeatFooterArea.setLogicalPage( box );
    }
    if ( box.savedHeaderArea != null ) {
      box.savedHeaderArea = (PageAreaBox) savedHeaderArea.derive( deepDerive );
      box.savedHeaderArea.setLogicalPage( box );
    }
    if ( box.savedWatermarkArea != null ) {
      box.savedWatermarkArea = (WatermarkAreaBox) savedWatermarkArea.derive( deepDerive );
      box.savedWatermarkArea.setLogicalPage( box );
    }
    return box;
  }

  /**
   * Clones this node. Be aware that cloning can get you into deep trouble, as the relations this node has may no longer
   * be valid.
   *
   * @return
   */
  public LogicalPageBox clone() {
    try {
      final LogicalPageBox o = (LogicalPageBox) super.clone();
      o.pageGrid = (DefaultPageGrid) pageGrid.clone();
      o.allVerticalBreaks = allVerticalBreaks;
      return o;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( "Cloning *must* be supported." );
    }
  }

  public void setAllVerticalBreaks( final PageBreakPositionList allVerticalBreaks ) {
    if ( allVerticalBreaks == null ) {
      throw new NullPointerException();
    }
    // create a new list-controller but share the backend with the old list.
    this.allVerticalBreaks = new PageBreakPositionList( this.allVerticalBreaks );
    this.allVerticalBreaks.copyFrom( allVerticalBreaks );
  }

  public PageBreakPositionList getAllVerticalBreaks() {
    return allVerticalBreaks;
  }

  public long computePageEnd() {
    final long pageOffset = getPageOffset();
    final PageBreakPositionList allVerticalBreaks = getAllVerticalBreaks();
    final long lastMasterBreak = allVerticalBreaks.getLastMasterBreak();
    if ( pageOffset == lastMasterBreak ) {
      return getHeight();
    }

    return allVerticalBreaks.findNextMajorBreakPosition( pageOffset + 1 );
  }

  public String getPageName() {
    return pageName;
  }

  public void setPageName( final String pageName ) {
    this.pageName = pageName;
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_BOX_LOGICALPAGE;
  }

  public void storeSaveInformation() {
    savedFooterArea = ( (PageAreaBox) getFooterArea().derive( true ) );
    savedRepeatFooterArea = ( (PageAreaBox) getRepeatFooterArea().derive( true ) );
    savedHeaderArea = ( (PageAreaBox) getHeaderArea().derive( true ) );
    savedWatermarkArea = ( (WatermarkAreaBox) getWatermarkArea().derive( true ) );
  }

  public void rollbackSaveInformation() {
    headerArea = ( (PageAreaBox) savedHeaderArea.derive( true ) );
    footerArea = ( (PageAreaBox) savedFooterArea.derive( true ) );
    repeatFooterArea = ( (PageAreaBox) savedRepeatFooterArea.derive( true ) );
    watermarkArea = ( (WatermarkAreaBox) savedWatermarkArea.derive( true ) );
  }

  public long getProcessedTableOffset() {
    return processedTableOffset;
  }

  public void setProcessedTableOffset( final long processedTableOffset ) {
    this.processedTableOffset = processedTableOffset;
  }
}
