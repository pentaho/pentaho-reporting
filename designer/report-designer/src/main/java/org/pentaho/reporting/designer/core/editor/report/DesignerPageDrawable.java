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

package org.pentaho.reporting.designer.core.editor.report;

import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.WatermarkAreaBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.LogicalPageDrawable;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;

public class DesignerPageDrawable extends LogicalPageDrawable {

  private enum SectionSubType {
    NORMALFLOW, WATERMARK, HEADER, FOOTER
  }

  private DesignerCollectSelectedNodesStep collectSelectedNodesStep;
  private StrictBounds rootElementBounds;
  private HashSet<InstanceID> rootElementIds;
  private boolean insideRenderedElement;
  private SectionSubType subType;
  private HashSet<InstanceID> renderableParents;

  public DesignerPageDrawable( final LogicalPageBox rootBox,
                               final OutputProcessorMetaData metaData,
                               final ResourceManager resourceManager,
                               final Section section ) {
    init( rootBox, metaData, resourceManager );
    setDrawPageBackground( false );

    rootElementIds = new HashSet<InstanceID>();
    rootElementIds.add( section.getObjectID() );

    subType = determineSubType( section );

    if ( section instanceof RootLevelBand ) {
      final RootLevelBand rlb = (RootLevelBand) section;
      final SubReport[] subReports = rlb.getSubReports();
      for ( int i = 0; i < subReports.length; i++ ) {
        final SubReport subReport = subReports[ i ];
        rootElementIds.add( subReport.getObjectID() );
      }
    }

    renderableParents = new HashSet<InstanceID>();
    Section parent = section.getParentSection();
    while ( parent != null ) {
      renderableParents.add( parent.getObjectID() );
      parent = parent.getParentSection();
    }

    final SelectLayoutNodes layoutNodes = new SelectLayoutNodes();
    this.rootElementBounds = layoutNodes.select( rootElementIds, rootBox, section );

    collectSelectedNodesStep = new DesignerCollectSelectedNodesStep();
    collectSelectedNodesStep.setRootNodes( rootElementIds );
  }

  private SectionSubType determineSubType( final Section s ) {
    if ( s.getReportDefinition() != s.getMasterReport() ) {
      return SectionSubType.NORMALFLOW;
    }
    if ( s instanceof Watermark ) {
      return SectionSubType.WATERMARK;
    }
    if ( s instanceof PageFooter ) {
      return SectionSubType.FOOTER;
    }
    if ( s instanceof PageHeader ) {
      return SectionSubType.HEADER;
    }
    return SectionSubType.NORMALFLOW;
  }

  public StrictBounds getRootElementBounds() {
    return rootElementBounds;
  }

  /**
   * Draws the object.
   *
   * @param g2   the graphics device.
   * @param area the area inside which the object should be drawn. This is the clipping area for the page.
   */
  public void draw( final Graphics2D g2, final Rectangle2D area ) {
    setOutlineMode( WorkspaceSettings.getInstance().isAlwaysDrawElementFrames() );
    super.draw( g2, area );
  }

  protected void processRootBand( final StrictBounds pageBounds ) {
    if ( subType == SectionSubType.WATERMARK ) {
      final WatermarkAreaBox box = getRootBox().getWatermarkArea();
      setDrawArea( new StrictBounds( box.getX(), box.getY(), box.getWidth(), box.getHeight() ) );
      getGraphics().clip( createClipRect( getDrawArea() ) );
      startProcessing( box );
    } else if ( subType == SectionSubType.HEADER ) {
      final BlockRenderBox box = getRootBox().getHeaderArea();
      setDrawArea( new StrictBounds( box.getX(), box.getY(), box.getWidth(), box.getHeight() ) );
      getGraphics().clip( createClipRect( getDrawArea() ) );
      startProcessing( box );
    } else if ( subType == SectionSubType.FOOTER ) {
      final BlockRenderBox box = getRootBox().getFooterArea();
      setDrawArea( new StrictBounds( box.getX(), box.getY(), box.getWidth(), box.getHeight() ) );
      getGraphics().clip( createClipRect( getDrawArea() ) );
      startProcessing( box );
    } else {
      final RenderBox box = getRootBox();
      setDrawArea( new StrictBounds( box.getX(), box.getY(), box.getWidth(), box.getHeight() ) );
      getGraphics().clip( createClipRect( getDrawArea() ) );
      processBoxChilds( getRootBox() );
    }
  }

  private boolean isValidDrawTarget( RenderNode node ) {
    if ( renderableParents.contains( node.getInstanceId() ) ) {
      return true;
    }

    while ( node != null ) {
      if ( rootElementIds.contains( node.getInstanceId() ) ) {
        return true;
      }
      node = node.getParent();
    }
    return false;
  }

  protected boolean startBox( final RenderBox box ) {
    if ( box.getLayoutNodeType() != LayoutNodeTypes.TYPE_BOX_LOGICALPAGE ) {
      final StrictBounds bounds = new StrictBounds( box.getX(), box.getY(), box.getWidth(), box.getHeight() );
      if ( StrictBounds.intersects( rootElementBounds, bounds ) == false ) {
        return false;
      }
    }

    return super.startBox( box );
  }

  private void finishBox( final RenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = false;
    }
  }

  public boolean startCanvasBox( final CanvasRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startCanvasBox( box );
  }

  protected void finishCanvasBox( final CanvasRenderBox box ) {
    finishBox( box );
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startBlockBox( box );
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    finishBox( box );
  }

  protected boolean startRowBox( final RenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startRowBox( box );
  }

  protected void finishRowBox( final RenderBox box ) {
    finishBox( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startInlineBox( box );
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    finishBox( box );
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startTableCellBox( box );
  }

  protected void finishTableCellBox( final TableCellRenderBox box ) {
    finishBox( box );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startTableRowBox( box );
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    finishBox( box );
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startTableSectionBox( box );
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    finishBox( box );
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startTableBox( box );
  }

  protected void finishTableBox( final TableRenderBox box ) {
    finishBox( box );
  }

  protected boolean startOtherBox( final RenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startOtherBox( box );
  }

  protected void finishOtherBox( final RenderBox box ) {
    finishBox( box );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    if ( rootElementIds.contains( box.getInstanceId() ) ) {
      insideRenderedElement = true;
    }

    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return false;
    }

    if ( isValidDrawTarget( box ) == false ) {
      return true;
    }
    return super.startAutoBox( box );
  }

  protected void finishAutoBox( final RenderBox box ) {
    finishBox( box );
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( node ) ) {
      return;
    }

    if ( isValidDrawTarget( node ) == false ) {
      return;
    }
    super.processOtherNode( node );
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    if ( insideRenderedElement && ModelUtility.isHideInLayoutGui( box ) ) {
      return;
    }
    if ( isValidDrawTarget( box ) == false ) {
      return;
    }
    super.processRenderableContent( box );
  }


  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    if ( box.getFirstChild() == null ) {
      if ( box.getPoolSize() > 0 ) {
        final Graphics2D graphics1 = getGraphics();
        graphics1.setFont( new Font( "Serif", Font.PLAIN, 10 ) ); // NON-NLS
        graphics1.drawString( "Your box is too small ...", // NON-NLS : TODO _ Change the way we handle this case
          (int) StrictGeomUtility.toExternalValue( box.getX() ),
          (int) StrictGeomUtility.toExternalValue( box.getY() ) + 10 );
      }
    } else {
      super.processParagraphChilds( box );
    }
  }

  /**
   * Retries the nodes under the given coordinate which have a given attribute set. If name and namespace are null, all
   * nodes are returned. The nodes returned are listed in their respective hierarchical order.
   *
   * @param x         the x coordinate
   * @param y         the y coordinate
   * @param namespace the namespace on which to filter on
   * @param name      the name on which to filter on
   * @return the ordered list of nodes.
   */
  public RenderNode[] getNodesAt( final double x, final double y, final String namespace, final String name ) {
    return collectSelectedNodesStep.getNodesAt
      ( getRootBox(), StrictGeomUtility.createBounds( x, y, 1, 1 ), namespace, name );
  }

  public RenderNode[] getNodesAt( final double x,
                                  final double y,
                                  final double width,
                                  final double height,
                                  final String namespace,
                                  final String name ) {

    return collectSelectedNodesStep.getNodesAt
      ( getRootBox(), StrictGeomUtility.createBounds( x, y, width, height ), namespace, name );
  }

  protected boolean isIgnoreBorderWhenDrawingOutline() {
    return true;
  }

  protected void drawOutlineBox( final Graphics2D g2, final RenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
      return;
    } else {
      g2.setPaint( Color.lightGray );
    }

    final double x = StrictGeomUtility.toExternalValue( box.getX() );
    final double y = StrictGeomUtility.toExternalValue( box.getY() );
    final double w = StrictGeomUtility.toExternalValue( box.getWidth() );
    final double h = StrictGeomUtility.toExternalValue( box.getHeight() );
    final Rectangle2D boxArea = getBoxArea();
    boxArea.setFrame( x, y, w, h );
    g2.draw( boxArea );
  }
}
