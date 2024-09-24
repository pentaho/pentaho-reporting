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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.WatermarkAreaBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ReplacedContentUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * This processes the second step of the vertical-layouting.
 * <p/>
 * At this point, the static height of all elements is known (that is the height of all elements that do not use
 * percentages in either the y or height properties).
 * <p/>
 * That height is then used as base-value to resolve all relative heights and y positions and the layouting is redone.
 *
 * @author Thomas Morgner
 */
public final class CanvasMajorAxisLayoutStep extends AbstractMajorAxisLayoutStep {
  private static final Log logger = LogFactory.getLog( CanvasMajorAxisLayoutStep.class );

  // Set the maximum height to an incredibly high value. This is now 2^43 micropoints or more than
  // 3000 kilometers. Please call me directly at any time if you need more space for printing.
  private static final long MAX_AUTO = StrictGeomUtility.MAX_AUTO;
  private boolean paranoidChecks = true;
  private RevalidateAllAxisLayoutStep revalidateAllAxisLayoutStep;
  private PageGrid pageGrid;
  private boolean complexText;

  public CanvasMajorAxisLayoutStep() {
    super( true );
    revalidateAllAxisLayoutStep = new RevalidateAllAxisLayoutStep();
    paranoidChecks =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.layout.process.ParanoidChecks" ) );
  }

  public void compute( final LogicalPageBox pageBox ) {
    try {
      this.pageGrid = pageBox.getPageGrid();
      super.compute( pageBox );
    } finally {
      this.pageGrid = null;
    }
  }

  public void initialize( final OutputProcessorMetaData metaData ) {
    revalidateAllAxisLayoutStep.initialize( metaData );
    complexText = metaData.isFeatureSupported( OutputProcessorFeature.COMPLEX_TEXT );
  }

  private long resolveParentHeight( final RenderNode node ) {
    final RenderBox parent = node.getParent();
    if ( parent == null ) {
      if ( node.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
        final LogicalPageBox box = node.getLogicalPage();
        if ( box != null ) {
          // a page-box has no margins, borders or paddings.
          return box.getPageHeight();
        }
      }
      return 0;
    }
    return Math.max( 0, parent.getCachedHeight() - parent.getVerticalInsets() );
  }

  protected boolean startBlockLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    final int strictNodeType = box.getNodeType();
    performStartTable( box );

    final long oldPosition = box.getCachedY();
    final long newYPosition = computeVerticalBlockPosition( box );
    CacheBoxShifter.shiftBox( box, Math.max( 0, newYPosition - oldPosition ) );

    // Compute the block-position of the box. The box is positioned relative to the previous silbling or
    // relative to the parent.
    final int nodeType = box.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) box;
      box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
    } else {
      final RenderBox watermark = isWatermark( box );
      if ( watermark != null ) {
        final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) watermark;
        box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
      } else {
        long parentHeightForResolve = 0;
        final RenderBox parent = box.getParent();
        if ( parent != null && parent.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
          parentHeightForResolve = resolveParentHeight( box );
        }

        if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
          final long blockHeight = computeBlockHeightAndAlign( box, parentHeightForResolve, false );
          box.setCachedHeight( blockHeight );
        } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
          final long blockHeight = computeRowHeightAndAlign( box, parentHeightForResolve, false );
          box.setCachedHeight( blockHeight );
        } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
          final RenderableReplacedContentBox rpc = (RenderableReplacedContentBox) box;
          box.setCachedHeight( ReplacedContentUtil.computeHeight( rpc, parentHeightForResolve, box.getCachedWidth() ) );
        } else {
          final long cachedHeight = computeCanvasHeight( box, parentHeightForResolve == 0 );
          box.setCachedHeight( cachedHeight );
        }
      }
    }

    return true;
  }

  private RenderBox isWatermark( final RenderBox box ) {
    final RenderBox parent = box.getParent();
    if ( parent == null ) {
      return null;
    }
    if ( parent.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      return parent;
    }
    final RenderBox parent2 = parent.getParent();
    if ( parent2 == null ) {
      return null;
    }
    if ( parent2.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      return parent2;
    }
    return null;
  }

  protected void processBlockLevelNode( final RenderNode node ) {
    // This could be anything, text, or an image.
    node.setCachedY( computeVerticalBlockPosition( node ) );

    final int type = node.getNodeType();
    if ( type == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      final FinishedRenderNode fnode = (FinishedRenderNode) node;
      node.setCachedHeight( fnode.getLayoutedHeight() );
    } else if ( ( type & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
      throw new IllegalStateException( "A Inline-Box must be contained in a paragraph." );
    }
  }

  protected void finishBlockLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return;
    }

    final int nodeType = box.getLayoutNodeType();
    performFinishTable( box );

    if ( nodeType == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) box;
      box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
    } else {
      long parentHeightForResolve = 0;
      final RenderBox parent = box.getParent();
      if ( parent != null && parent.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
        parentHeightForResolve = resolveParentHeight( box );
      }

      final RenderBox watermark = isWatermark( box );
      if ( watermark != null ) {
        final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) watermark;
        box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
      } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
        final long blockHeight = computeBlockHeightAndAlign( box, parentHeightForResolve, true );
        box.setCachedHeight( blockHeight );
      } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
        final long blockHeight = computeRowHeightAndAlign( box, parentHeightForResolve, true );
        box.setCachedHeight( blockHeight );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
        // do nothing ..
        return;
      } else {
        box.setCachedHeight( computeCanvasHeight( box, parentHeightForResolve == 0 ) );
      }
    }
  }

  private long computeVerticalBlockPosition( final RenderNode node ) {
    return InfiniteMajorAxisLayoutStep.computeVerticalBlockPosition( node );
  }

  private long computeBlockHeightAndAlign( final RenderBox box, final long resolveSize, final boolean alignChilds ) {
    return InfiniteMajorAxisLayoutStep.computeBlockHeightAndAlign( box, box.getBoxDefinition(), resolveSize,
        alignChilds );
  }

  private long computeRowHeightAndAlign( final RenderBox box, final long resolveSize, final boolean align ) {
    if ( resolveSize < 0 ) {
      throw new IllegalArgumentException( "ResovleSize cannot be negative" );
    }

    // For the water-mark area, this computation is different. The Watermark-area uses the known height of
    // the parent (=the page size)
    if ( box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) box;
      final LogicalPageBox lpb = watermarkAreaBox.getLogicalPage();
      // set the page-height as watermark size.
      return lpb.getPageHeight();
    }

    // Check the height. Set the height.
    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final RenderLength preferredHeight = boxDefinition.getPreferredHeight();
    final RenderLength minimumHeight = boxDefinition.getMinimumHeight();
    final RenderLength maximumHeight = boxDefinition.getMaximumHeight();

    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final long insetBottom = blp.getBorderBottom() + boxDefinition.getPaddingBottom();
    final long insetTop = blp.getBorderTop() + boxDefinition.getPaddingTop();

    final long usedHeight;
    RenderNode child = box.getFirstChild();
    // initialize with the values computed in the InfMajorStep
    long maxChildY2 = box.getCachedY() + box.getCachedHeight();
    if ( child != null ) {
      while ( child != null ) {
        maxChildY2 =
            Math.max( child.getCachedY() + child.getCachedHeight() + child.getEffectiveMarginBottom(), maxChildY2 );

        child = child.getNext();
      }
      usedHeight = ( maxChildY2 - ( box.getCachedY() + insetTop ) );
    } else {
      usedHeight = 0;
    }

    final long rminH = minimumHeight.resolve( resolveSize, 0 );
    final long rmaxH = maximumHeight.resolve( resolveSize, CanvasMajorAxisLayoutStep.MAX_AUTO );

    final long computedContentHeight;
    if ( boxDefinition.isSizeSpecifiesBorderBox() ) {
      final long rprefH = preferredHeight.resolve( resolveSize, usedHeight + insetTop + insetBottom );
      final long specifiedHeight = ProcessUtility.computeLength( rminH, rmaxH, rprefH );
      computedContentHeight = specifiedHeight - insetTop - insetBottom;
    } else {
      final long rprefH = preferredHeight.resolve( resolveSize, usedHeight );
      computedContentHeight = ProcessUtility.computeLength( rminH, rmaxH, rprefH );
    }

    if ( align ) {
      child = box.getFirstChild();
      final ElementAlignment valign = box.getNodeLayoutProperties().getVerticalAlignment();
      final long boxY1 = box.getCachedY() + insetTop;
      final long boxY2 = boxY1 + computedContentHeight;
      while ( child != null ) {
        final long childY1 = child.getCachedY();
        final long childY2 = childY1 + child.getCachedHeight();
        // we have extra space to distribute. So lets shift some boxes.
        if ( ElementAlignment.BOTTOM.equals( valign ) ) {
          final long boxBottom = ( boxY2 - insetBottom );
          final long delta = boxBottom - childY2;
          CacheBoxShifter.shiftBox( child, delta );
        } else if ( ElementAlignment.MIDDLE.equals( valign ) ) {
          final long extraHeight = computedContentHeight - ( childY2 - childY1 );
          final long boxTop = boxY1 + ( extraHeight / 2 );
          final long delta = boxTop - childY1;
          CacheBoxShifter.shiftBox( child, delta );
        }
        child = child.getNext();
      }
    }

    final long retval = Math.max( 0, computedContentHeight + insetTop + insetBottom );
    if ( retval < 0 ) {
      throw new IllegalStateException( "A child cannot exceed the area of the parent." );
    }
    if ( retval == 0 && box.getCachedHeight() > 0 ) {
      throw new IllegalStateException( "A child cannot exceed the area of the parent." );
    }
    return retval;
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    revalidateAllAxisLayoutStep.processBoxChilds( box, pageGrid );
  }

  protected boolean startCanvasLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    performStartTable( box );

    final long oldPosition = box.getCachedY();
    final long newYPosition = computeVerticalCanvasPosition( box );
    CacheBoxShifter.shiftBox( box, Math.max( 0, newYPosition - oldPosition ) );

    final int nodeType = box.getLayoutNodeType();
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeBlockHeightAndAlign( box, resolveSize, false );
      box.setCachedHeight( blockHeight );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeRowHeightAndAlign( box, resolveSize, false );
      box.setCachedHeight( blockHeight );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      final long resolveSize = resolveUseableParentHeight( box );
      final RenderableReplacedContentBox rpc = (RenderableReplacedContentBox) box;
      final long computedHeight = ReplacedContentUtil.computeHeight( rpc, resolveSize, box.getCachedWidth() );
      box.setCachedHeight( computedHeight );
    } else {

      box.setCachedHeight( computeCanvasHeight( box, false ) );
    }

    return true;
  }

  protected void processCanvasLevelNode( final RenderNode node ) {
    node.setCachedY( computeVerticalCanvasPosition( node ) );

    if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      final FinishedRenderNode fnode = (FinishedRenderNode) node;
      node.setCachedHeight( fnode.getLayoutedHeight() );
    } else {
      node.setCachedHeight( 0 );
    }
  }

  /**
   * Finishes up a canvas level box. This updates/affects the height of the parent, as the canvas model defines that the
   * parent always fully encloses all of its childs.
   * <p/>
   * When no preferred height is defined, the height of an element is the maximum of its minimum-height and the absolute
   * height of all of its direct children.
   * <p/>
   * To resolve the value of percentages, the system uses the maximum of the parent's height and the maximum of all (y +
   * height) of all children.)
   *
   * @param box
   */
  protected void finishCanvasLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return;
    }

    final int type = box.getLayoutNodeType();
    if ( type == LayoutNodeTypes.TYPE_BOX_TABLE ) {
      performFinishTable( box );
      box.setCachedHeight( computeCanvasHeight( box, false ) );
      return;
    }

    if ( ( type & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeBlockHeightAndAlign( box, resolveSize, true );
      box.setCachedHeight( blockHeight );
    } else if ( ( type & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeRowHeightAndAlign( box, resolveSize, true );
      box.setCachedHeight( blockHeight );
    } else if ( type == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      // do nothing ..
      return;
    } else {
      box.setCachedHeight( computeCanvasHeight( box, false ) );
    }
  }

  private long computeVerticalCanvasPosition( final RenderNode node ) {
    final RenderBox parent = node.getParent();
    final long parentPosition;
    if ( parent == null ) {
      parentPosition = 0;
    } else {
      final StaticBoxLayoutProperties blp = parent.getStaticBoxLayoutProperties();
      final BoxDefinition bdef = parent.getBoxDefinition();
      final long insetsTop = ( blp.getBorderTop() + bdef.getPaddingTop() );
      parentPosition = parent.getCachedY() + insetsTop;
    }

    final double posY = node.getStyleSheet().getDoubleStyleProperty( ElementStyleKeys.POS_Y, 0 );
    if ( node.isSizeSpecifiesBorderBox() ) {
      return ( parentPosition + RenderLength.resolveLength( resolveParentHeight( node ), posY ) );
    } else {
      final long insetsTop;
      if ( ( node.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX ) {
        final RenderBox box = (RenderBox) node;
        final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
        final BoxDefinition bdef = box.getBoxDefinition();
        insetsTop = ( blp.getBorderTop() + bdef.getPaddingTop() );
      } else {
        insetsTop = 0;
      }
      return ( parentPosition + RenderLength.resolveLength( resolveParentHeight( node ), posY ) - insetsTop );
    }
  }

  /**
   * The usable parent height is computed as the nodes's computed y position up to the remaining parent height, not
   * counting any of the parent's borders or paddings. It is assumed that the parent's top-insets already have been used
   * to compute the node's y-position, so that we must ignore them here.
   *
   * @param node
   * @return
   */
  private long resolveUseableParentHeight( final RenderNode node ) {
    final RenderBox parent = node.getParent();
    if ( parent == null ) {
      return node.getCachedHeight();
    }
    final long height = parent.getCachedHeight();
    final BoxDefinition bdef = parent.getBoxDefinition();
    final StaticBoxLayoutProperties blp = parent.getStaticBoxLayoutProperties();
    final long insetsBottom = blp.getBorderBottom() + bdef.getPaddingBottom();
    final long parentAvailableHeight = ( parent.getCachedY() + height - insetsBottom ) - node.getCachedY();
    if ( paranoidChecks && isWatermark( parent ) == null ) {
      // the check is only valid if there is no preferred height
      // a preferred height may create overflowing childs, as it limits the height of the box to the defined value
      if ( RenderLength.AUTO.equals( bdef.getPreferredHeight() ) ) {
        // the check is only valid if there is no max height
        // a max height may create overflowing childs, as it limits the height of the box to the defined value
        final RenderLength maxHeight = bdef.getMaximumHeight();
        if ( RenderLength.AUTO.equals( maxHeight ) ) {
          final long childConsumedHeight = parentAvailableHeight - node.getCachedHeight();
          if ( childConsumedHeight < 0 ) {
            if ( parent.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_CELL
                || parent.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_ROW ) {
              // row-spanned cells consistently exceed the parent height ..
              return 0;
            }
            logger.warn( "A child cannot exceed the area of the parent: " + node.getName() + " Parent: "
                + parentAvailableHeight + " Child: " + childConsumedHeight );
          }
        }
      }
    }
    return parentAvailableHeight;
  }

  private long computeCanvasHeight( final RenderBox box, final boolean heightResolvesToZero ) {
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = box.getBoxDefinition();

    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final RenderLength minHeight = boxDefinition.getMinimumHeight();
    final RenderLength preferredHeight = boxDefinition.getPreferredHeight();
    final RenderLength maxHeight = boxDefinition.getMaximumHeight();

    final long insetsTop = ( blp.getBorderTop() + bdef.getPaddingTop() );
    final long insetsBottom = blp.getBorderBottom() + bdef.getPaddingBottom();
    final long insets = insetsTop + insetsBottom;

    final long parentHeight;
    final long usableParentHeight;
    if ( heightResolvesToZero ) {
      parentHeight = 0;
      usableParentHeight = 0;
    } else {
      parentHeight = Math.max( resolveParentHeight( box ), box.getCachedHeight() );
      usableParentHeight = resolveUseableParentHeight( box );
    }
    // find the maximum of the used height (for all childs) and the specified min-height.
    long consumedHeight =
        Math.max( box.getCachedHeight(), Math.min( minHeight.resolve( parentHeight ), usableParentHeight ) );

    // The consumed height computed above specifies the size at the border-edge.
    // However, depending on the box-sizing property, we may have to resolve them against the
    // content-edge instead.

    final long minHeightResolved = minHeight.resolve( parentHeight );
    final long maxHeightResolved = maxHeight.resolve( parentHeight, CanvasMajorAxisLayoutStep.MAX_AUTO );
    if ( box.isSizeSpecifiesBorderBox() ) {
      final long prefHeightResolved;
      if ( RenderLength.AUTO.equals( preferredHeight ) ) {
        prefHeightResolved = consumedHeight;
      } else {
        prefHeightResolved = preferredHeight.resolve( parentHeight );
      }

      final long height = ProcessUtility.computeLength( minHeightResolved, maxHeightResolved, prefHeightResolved );
      if ( heightResolvesToZero ) {
        return height;
      }
      return Math.min( height, usableParentHeight );
    } else {
      consumedHeight = Math.max( 0, consumedHeight - insets );
      final long prefHeightResolved;
      if ( RenderLength.AUTO.equals( preferredHeight ) ) {
        prefHeightResolved = consumedHeight;
      } else {
        prefHeightResolved = preferredHeight.resolve( parentHeight );
      }

      final long height = ProcessUtility.computeLength( minHeightResolved, maxHeightResolved, prefHeightResolved );
      if ( heightResolvesToZero ) {
        return height;
      }
      return Math.min( height + insets, usableParentHeight );
    }
  }

  protected boolean startRowLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    performStartTable( box );

    final long oldPosition = box.getCachedY();
    final long newYPosition = computeVerticalRowPosition( box );
    CacheBoxShifter.shiftBox( box, Math.max( 0, newYPosition - oldPosition ) );

    // Compute the block-position of the box. The box is positioned relative to the previous silbling or
    // relative to the parent.
    final int nodeType = box.getLayoutNodeType();
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeBlockHeightAndAlign( box, resolveSize, false );
      box.setCachedHeight( blockHeight );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeRowHeightAndAlign( box, resolveSize, false );
      box.setCachedHeight( blockHeight );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      final RenderableReplacedContentBox rpc = (RenderableReplacedContentBox) box;
      final long resolveSize = resolveParentHeight( box );
      box.setCachedHeight( ReplacedContentUtil.computeHeight( rpc, resolveSize, box.getCachedWidth() ) );
    } else {
      final long cachedHeight = computeCanvasHeight( box, false );
      box.setCachedHeight( cachedHeight );
    }

    return true;
  }

  protected void processRowLevelNode( final RenderNode node ) {
    // This could be anything, text, or an image.
    node.setCachedY( computeVerticalRowPosition( node ) );

    final int type = node.getNodeType();
    if ( type == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      final FinishedRenderNode fnode = (FinishedRenderNode) node;
      node.setCachedHeight( fnode.getLayoutedHeight() );
    } else if ( ( type & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
      throw new IllegalStateException( "A Inline-Box must be contained in a paragraph." );
    }
  }

  protected void finishRowLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return;
    }

    final int nodeType = box.getLayoutNodeType();
    performFinishTable( box );

    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeBlockHeightAndAlign( box, resolveSize, true );
      box.setCachedHeight( blockHeight );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      final long resolveSize = resolveParentHeight( box );
      final long blockHeight = computeRowHeightAndAlign( box, resolveSize, true );
      box.setCachedHeight( blockHeight );
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      // do nothing ..
      return;
    } else {
      box.setCachedHeight( computeCanvasHeight( box, false ) );
    }
  }

  private long computeVerticalRowPosition( final RenderNode node ) {
    if ( node.isVisible() == false ) {
      return node.getCachedY();
    }

    final RenderBox parent = node.getParent();

    if ( parent != null ) {
      // the computed position of an inline-element must be the same as the position of the parent element.
      // A inline-box always has an other inline-box as parent (the paragraph-pool-box is the only exception;
      // and this one is handled elsewhere).

      // Top and bottom margins are not applied to inline-elements.
      final StaticBoxLayoutProperties blp = parent.getStaticBoxLayoutProperties();
      final BoxDefinition bdef = parent.getBoxDefinition();
      final long insetTop = ( blp.getBorderTop() + bdef.getPaddingTop() );

      return ( insetTop + parent.getCachedY() );
    } else {
      // there's no parent .. Should not happen, shouldn't it?
      return ( 0 );
    }
  }

  protected boolean startTableCellLevelBox( final RenderBox box ) {
    return startBlockLevelBox( box );
  }

  protected void finishTableCellLevelBox( final RenderBox box ) {
    // table cells behave like block-level cells most of the time.
    finishBlockLevelBox( box );
  }

  protected boolean startTableRowLevelBox( final RenderBox box ) {
    final long oldPosition = box.getCachedY();
    final long newYPosition = computeVerticalRowPosition( box );
    CacheBoxShifter.shiftBox( box, Math.max( 0, newYPosition - oldPosition ) );

    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
      getTableRowHeightStep().startTableCell( (TableCellRenderBox) box );
    } else {
      final long blockHeight = computeTableHeightAndAlign( box, false );
      box.setCachedHeight( blockHeight );
    }

    markAllChildsDirty( box );
    return true;
  }

  protected void finishTableRowLevelBox( final RenderBox box ) {
    clearAllChildsDirtyMarker( box );
    if ( box instanceof TableCellRenderBox ) {
      final long blockHeight = computeTableHeightAndAlign( box, true );
      getTableRowHeightStep().finishTableCell( (TableCellRenderBox) box, blockHeight );
    } else {
      final long blockHeight = computeTableHeightAndAlign( box, true );
      box.setCachedHeight( blockHeight );
    }
  }

  protected boolean startTableLevelBox( final RenderBox box ) {
    final long oldPosition = box.getCachedY();
    final long newYPosition = computeVerticalBlockPosition( box );
    CacheBoxShifter.shiftBox( box, Math.max( 0, newYPosition - oldPosition ) );

    final long blockHeight = computeTableHeightAndAlign( box, false );
    box.setCachedHeight( blockHeight );

    if ( box instanceof TableSectionRenderBox ) {
      getTableRowHeightStep().startTableSection( (TableSectionRenderBox) box );
    }
    return true;
  }

  protected void processTableLevelNode( final RenderNode node ) {
    processBlockLevelNode( node );
  }

  protected void finishTableLevelBox( final RenderBox box ) {
    if ( box instanceof TableSectionRenderBox ) {
      getTableRowHeightStep().finishTableSection( (TableSectionRenderBox) box );
    } else {
      final long blockHeight = computeTableHeightAndAlign( box, true );
      box.setCachedHeight( blockHeight );
    }
  }

  protected boolean startTableSectionLevelBox( final RenderBox box ) {
    if ( box instanceof TableRowRenderBox ) {
      getTableRowHeightStep().startTableRow( (TableRowRenderBox) box );
      final long blockHeight = computeRowHeightAndAlign( box, 0, false );
      box.setCachedHeight( blockHeight );
    } else {
      // must be an auto-box, so we treat it as a block-element.

      final long oldPosition = box.getCachedY();
      final long newYPosition = computeVerticalBlockPosition( box );
      CacheBoxShifter.shiftBox( box, Math.max( 0, newYPosition - oldPosition ) );

      final long blockHeight = computeTableHeightAndAlign( box, false );
      box.setCachedHeight( blockHeight );
    }
    return true;
  }

  protected void processTableSectionLevelNode( final RenderNode node ) {
    processBlockLevelNode( node );
  }

  protected void finishTableSectionLevelBox( final RenderBox box ) {
    box.setCachedHeight( 0 );
  }

  private static long computeTableHeightAndAlign( final RenderBox box, final boolean align ) {
    return InfiniteMajorAxisLayoutStep.computeBlockHeightAndAlign( box, BoxDefinition.EMPTY, 0, align );
  }
}
