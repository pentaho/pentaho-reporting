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

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.WatermarkAreaBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MajorAxisParagraphBreakState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ReplacedContentUtil;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.BoxAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.InlineBlockAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.NodeAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.ReplacedContentAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.TextElementAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.VerticalAlignmentProcessor;

/**
 * Computes the absolute layout. The computed height and y positions of all abolutely positioned elements will be stored
 * in the 'canvasY' and 'canvasHeight' properties of RenderNode. Percentages will be resolved to zero.
 *
 * @author Thomas Morgner
 * @noinspection PointlessArithmeticExpression, ConstantConditions
 */
public final class InfiniteMajorAxisLayoutStep extends AbstractMajorAxisLayoutStep {
  private MajorAxisParagraphBreakState breakState;
  private VerticalAlignmentProcessor processor;
  private boolean complexText;

  public InfiniteMajorAxisLayoutStep() {
    super( false );
    this.breakState = new MajorAxisParagraphBreakState();
    this.processor = new VerticalAlignmentProcessor();
  }

  public void initialize( OutputProcessorMetaData metaData ) {
    complexText = metaData.isFeatureSupported( OutputProcessorFeature.COMPLEX_TEXT );
  }

  public void compute( final LogicalPageBox pageBox ) {
    this.breakState.deinit();
    try {
      super.compute( pageBox );
    } finally {
      this.breakState.deinit();
    }
  }

  /**
   * Continues processing. The renderbox must have a valid x-layout (that is: X, content-X1, content-X2 and Width)
   *
   * @param box
   *          the box.
   */
  public void continueComputation( final RenderBox box ) {
    // This is most-likely wrong, but as we do not support inline-block elements yet, we can ignore this for now.
    if ( box.getCachedWidth() == 0 ) {
      throw new IllegalStateException( "Box must be layouted a bit .." );
    }

    this.breakState.deinit();
    try {
      super.continueComputation( box );
    } finally {
      this.breakState.deinit();
    }
  }

  protected boolean startBlockLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    performStartTable( box );
    // Compute the block-position of the box. The box is positioned relative to the previous sibling or
    // relative to the parent.
    box.setCachedY( computeVerticalBlockPosition( box ) );

    if ( breakState.isActive() ) {
      if ( complexText ) {
        return true;
      }

      // No breakstate and not being suspended? Why this?
      if ( breakState.isSuspended() == false ) {
        throw new IllegalStateException( "This cannot be." );
      }

      // this way or another - we are suspended now. So there is no need to look
      // at the children anymore ..

      // This code is only executed for inline-block elements. Inline-block elements are not part of
      // the 0.8.9 or 1.0 engine layouting.
      return false;
    }

    final int layoutNodeType = box.getLayoutNodeType();
    if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
      // We cant cache that ... the shift operations later would misbehave
      // One way around would be to at least store the layouted offsets
      // (which should be immutable as long as the line did not change its
      // contents) and to reapply them on each run. This is cheaper than
      // having to compute the whole v-align for the whole line.
      breakState.init( paragraphBox );
    } else if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      final RenderableReplacedContentBox contentBox = (RenderableReplacedContentBox) box;
      contentBox.setCachedHeight( ReplacedContentUtil.computeHeight( contentBox, 0, contentBox.getCachedWidth() ) );

    } else if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) box;
      box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
    }
    return true;
  }

  protected void processBlockLevelNode( final RenderNode node ) {
    // This could be anything, text, or an image.
    node.setCachedY( computeVerticalBlockPosition( node ) );

    if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      final FinishedRenderNode fnode = (FinishedRenderNode) node;
      node.setCachedHeight( fnode.getLayoutedHeight() );
    }
  }

  protected void finishBlockLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return;
    }

    performFinishTable( box );

    final int nodeType = box.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) box;
      box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
    } else {
      final int layoutNodeType = box.getLayoutNodeType();
      final RenderBox watermark = isWatermark( box );
      if ( watermark != null ) {
        final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) watermark;
        box.setCachedHeight( watermarkAreaBox.getLogicalPage().getPageHeight() );
      } else if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
        box.setCachedHeight( computeBlockHeightAndAlign( box ) );
      } else if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
        box.setCachedHeight( computeRowHeight( box, 0 ) );
      } else {
        box.setCachedHeight( computeCanvasHeight( box ) );
      }
    }

    if ( breakState.isActive() ) {
      final Object suspender = breakState.getSuspendItem();
      if ( box.getInstanceId() == suspender ) {
        breakState.setSuspendItem( null );
        return;
      }
      if ( suspender != null ) {
        return;
      }

      if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        breakState.deinit();
      }
    }
  }

  private RenderBox isWatermark( final RenderBox box ) {
    final RenderBox parent = box.getParent();
    if ( parent == null ) {
      return null;
    }
    if ( parent.getNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      return parent;
    }
    final RenderBox parent2 = parent.getParent();
    if ( parent2 == null ) {
      return null;
    }
    if ( parent2.getNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      return parent2;
    }
    return null;
  }

  public static long computeVerticalBlockPosition( final RenderNode node ) {
    // we have no margins yet ..
    final long marginTop = 0;

    // The y-position of a box depends on the parent.
    final RenderBox parent = node.getParent();

    // A table row is something special. Although it is a block box,
    // it layouts its children from left to right
    if ( parent != null ) {
      final RenderNode prev = node.getPrev();
      if ( prev != null ) {
        if ( prev.isVisible() ) {
          // we have a silbling. Position yourself directly below your silbling ..
          return ( marginTop + prev.getCachedY() + prev.getCachedHeight() );
        } else {
          return ( marginTop + prev.getCachedY() );
        }
      } else {
        final StaticBoxLayoutProperties blp = parent.getStaticBoxLayoutProperties();
        final BoxDefinition bdef = parent.getBoxDefinition();
        final long insetTop = ( blp.getBorderTop() + bdef.getPaddingTop() );

        return ( marginTop + insetTop + parent.getCachedY() );
      }
    } else {
      // there's no parent ..
      return ( marginTop );
    }
  }

  private long computeBlockHeightAndAlign( final RenderBox box ) {
    return computeBlockHeightAndAlign( box, box.getBoxDefinition(), 0, true );
  }

  private static long computeTableHeightAndAlign( final RenderBox box ) {
    return computeBlockHeightAndAlign( box, BoxDefinition.EMPTY, 0, true );
  }

  public static long computeBlockHeightAndAlign( final RenderBox box, final BoxDefinition boxDefinition,
      final long resolveSize, final boolean alignChilds ) {
    if ( resolveSize < 0 ) {
      throw new IllegalArgumentException( "ResovleSize cannot be negative" );
    }

    // Check the height. Set the height.
    final RenderLength preferredHeight = boxDefinition.getPreferredHeight();
    final RenderLength minimumHeight = boxDefinition.getMinimumHeight();
    final RenderLength maximumHeight = boxDefinition.getMaximumHeight();

    final long usedHeight;
    final long childY2;
    final long childY1;
    final RenderNode lastChildNode = box.getLastChild();
    if ( lastChildNode != null ) {
      childY1 = box.getFirstChild().getCachedY();
      if ( lastChildNode.isVisible() ) {
        childY2 =
            lastChildNode.getCachedY() + lastChildNode.getCachedHeight() + lastChildNode.getEffectiveMarginBottom();
      } else {
        childY2 = lastChildNode.getCachedY();
      }
      usedHeight = ( childY2 - childY1 );
    } else {
      usedHeight = 0;
      childY2 = 0;
      childY1 = 0;
    }

    // final long blockContextWidth = box.getStaticBoxLayoutProperties().getBlockContextWidth();
    final long rminH = minimumHeight.resolve( resolveSize, 0 );
    final long rmaxH = maximumHeight.resolve( resolveSize, InfiniteMajorAxisLayoutStep.MAX_AUTO );

    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final long insetBottom = blp.getBorderBottom() + boxDefinition.getPaddingBottom();
    final long insetTop = blp.getBorderTop() + boxDefinition.getPaddingTop();

    // computed height is always the height of the content-box, excluding any paddings and borders
    final long computedContentHeight;
    if ( boxDefinition.isSizeSpecifiesBorderBox() ) {
      final long rprefH = preferredHeight.resolve( resolveSize, usedHeight + insetTop + insetBottom );
      final long specifiedHeight = ProcessUtility.computeLength( rminH, rmaxH, rprefH );
      computedContentHeight = specifiedHeight - insetTop - insetBottom;
    } else {
      final long rprefH = preferredHeight.resolve( resolveSize, usedHeight );
      computedContentHeight = ProcessUtility.computeLength( rminH, rmaxH, rprefH );
    }

    if ( alignChilds && lastChildNode != null ) {
      // grab the node's y2
      if ( computedContentHeight > usedHeight ) {
        // we have extra space to distribute. So lets shift some boxes.
        final ElementAlignment valign = box.getNodeLayoutProperties().getVerticalAlignment();
        if ( ElementAlignment.BOTTOM.equals( valign ) ) {
          final long boxBottom = ( box.getCachedY() + computedContentHeight - insetBottom );
          final long delta = boxBottom - childY2;
          CacheBoxShifter.shiftBoxChilds( box, delta );
        } else if ( ElementAlignment.MIDDLE.equals( valign ) ) {
          final long extraHeight = computedContentHeight - usedHeight;
          final long boxTop = box.getCachedY() + insetTop + ( extraHeight / 2 );
          final long delta = boxTop - childY1;
          CacheBoxShifter.shiftBoxChilds( box, delta );
        }
      }
    }

    final long retval = Math.max( 0, computedContentHeight + insetTop + insetBottom );
    // For the water-mark area, this computation is different. The Watermark-area uses the known height of
    // the parent (=the page size)
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
      final WatermarkAreaBox watermarkAreaBox = (WatermarkAreaBox) box;
      final LogicalPageBox lpb = watermarkAreaBox.getLogicalPage();
      // set the page-height as watermark size.
      return Math.max( retval, Math.max( 0, lpb.getPageHeight() - insetTop - insetBottom ) );
    }
    return retval;
  }

  /**
   * We will do the alignment during the CanvasMajorAxisLayoutStep.
   *
   * @param box
   *          the box to be computed. Must be a box with row-layout
   * @param resolveSize
   *          the current height that makes 100%
   * @return the row's height.
   */
  private long computeRowHeight( final RenderBox box, final long resolveSize ) {
    // For the water-mark area, this computation is different. The Watermark-area uses the known height of
    // the parent (=the page size)
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_WATERMARK ) {
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

    // usedHeight already contains the insetsTop ..
    final long usedHeight;
    RenderNode child = box.getFirstChild();
    if ( child != null ) {
      long maxChildY2 = 0;
      while ( child != null ) {
        if ( child.isVisible() ) {
          final long childY2 = child.getCachedY() + child.getCachedHeight() + child.getEffectiveMarginBottom();
          maxChildY2 = Math.max( childY2, maxChildY2 );
        }
        child = child.getNext();
      }
      usedHeight = ( maxChildY2 - box.getCachedY() );
    } else {
      usedHeight = insetTop;
    }

    final long rminH = minimumHeight.resolve( resolveSize, 0 );
    final long rmaxH = maximumHeight.resolve( resolveSize, InfiniteMajorAxisLayoutStep.MAX_AUTO );

    final long computedHeight; // always the height of the content box
    if ( boxDefinition.isSizeSpecifiesBorderBox() ) {
      final long rprefH = preferredHeight.resolve( resolveSize, usedHeight + insetBottom );
      final long specifiedHeight = ProcessUtility.computeLength( rminH, rmaxH, rprefH );
      computedHeight = Math.max( 0, specifiedHeight - insetTop - insetBottom );
    } else {
      final long rprefH = preferredHeight.resolve( resolveSize, usedHeight - insetTop );
      computedHeight = Math.max( 0, ProcessUtility.computeLength( rminH, rmaxH, rprefH ) );
    }
    return Math.max( 0, computedHeight + insetTop + insetBottom );
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    if ( complexText ) {
      processBoxChilds( box );
    } else {
      // Process the direct childs of the paragraph
      // Each direct child represents a line ..

      RenderNode node = box.getFirstChild();
      while ( node != null ) {
        // all childs of the linebox container must be inline boxes. They
        // represent the lines in the paragraph. Any other element here is
        // a error that must be reported
        final ParagraphPoolBox inlineRenderBox = (ParagraphPoolBox) node;
        if ( startLine( inlineRenderBox ) ) {
          processBoxChilds( inlineRenderBox );
          finishLine( inlineRenderBox );
        }

        node = node.getNext();
      }
    }
  }

  private boolean startLine( final ParagraphPoolBox box ) {
    box.setCachedY( computeVerticalBlockPosition( box ) );

    if ( breakState.isActive() == false ) {
      return false;
    }

    if ( breakState.isSuspended() ) {
      return false;
    }

    breakState.openContext( box );
    return true;
  }

  private void finishLine( final ParagraphPoolBox inlineRenderBox ) {
    if ( breakState.isActive() == false || breakState.isSuspended() ) {
      return;
    }

    final BoxAlignContext boxAlignContext = breakState.closeContext();

    // This aligns all direct childs. Once that is finished, we have to
    // check, whether possibly existing inner-paragraphs are still valid
    // or whether moving them violated any of the inner-pagebreak constraints.

    final StaticBoxLayoutProperties blp = inlineRenderBox.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = inlineRenderBox.getBoxDefinition();
    final long insetTop = ( blp.getBorderTop() + bdef.getPaddingTop() );

    final long contentAreaY1 = inlineRenderBox.getCachedY() + insetTop;
    final long lineHeight = inlineRenderBox.getLineHeight();
    processor.align( boxAlignContext, contentAreaY1, lineHeight );
  }

  protected boolean startInlineLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    box.setCachedY( computeVerticalInlinePosition( box ) );
    computeBaselineInfo( box );

    if ( breakState == null ) {
      // ignore .. should not happen anyway ..
      return true;
    }

    if ( breakState.isSuspended() ) {
      return false;
    }

    final int nodeType = box.getLayoutNodeType();
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
      breakState.openContext( box );
      return true;
    } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      breakState.getCurrentLine().addChild( new ReplacedContentAlignContext( (RenderableReplacedContentBox) box, 0 ) );
      return false;
    }

    breakState.getCurrentLine().addChild( new InlineBlockAlignContext( box ) );
    breakState.setSuspendItem( box.getInstanceId() );
    return false;
  }

  private void computeBaselineInfo( final RenderBox box ) {
    if ( box.getBaselineInfo() == null ) {
      return;
    }

    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
        // grab the baseline info from there ...
        final RenderableText text = (RenderableText) node;
        box.setBaselineInfo( text.getBaselineInfo() );
        break;
      }

      node = node.getNext();
    }

    // If we have no baseline info here, ask the parent. If that one has none
    // either, then we cant do anything about it.
    if ( box.getBaselineInfo() == null ) {
      box.setBaselineInfo( box.getStaticBoxLayoutProperties().getNominalBaselineInfo() );
    }
  }

  protected void processInlineLevelNode( final RenderNode node ) {
    // compute the intial position.
    node.setCachedY( computeVerticalInlinePosition( node ) );
    // the height and the real position will be computed during the vertical-alignment computation.

    if ( breakState.isActive() == false || breakState.isSuspended() ) {
      return;
    }

    if ( complexText ) {
      return;
    }

    if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
      breakState.getCurrentLine().addChild( new TextElementAlignContext( (RenderableText) node ) );
    } else {
      breakState.getCurrentLine().addChild( new NodeAlignContext( node ) );
    }
  }

  protected void finishInlineLevelBox( final RenderBox box ) {
    // todo Arabic text

    if ( checkCacheValid( box ) ) {
      return;
    }

    // The height of an inline-level box will be computed when the vertical-alignemnt is done.

    if ( breakState.isActive() == false ) {
      return;
    }

    final int nodeType = box.getLayoutNodeType();
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
      breakState.closeContext();
      return;
    }

    final Object suspender = breakState.getSuspendItem();
    if ( box.getInstanceId() == suspender ) {
      breakState.setSuspendItem( null );
      return;
    }

    if ( suspender != null ) {
      return;
    }

    if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      throw new IllegalStateException( "This cannot be; Why is there a paragraph inside a inline context" );
    }
  }

  private long computeVerticalInlinePosition( final RenderNode node ) {
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

  protected boolean startCanvasLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    performStartTable( box );

    box.setCachedY( computeVerticalCanvasPosition( box ) );

    if ( breakState.isActive() == false ) {
      final int nodeType = box.getNodeType();
      if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
        // We cant cache that ... the shift operations later would misbehave
        // One way around would be to at least store the layouted offsets
        // (which should be immutable as long as the line did not change its
        // contents) and to reapply them on each run. This is cheaper than
        // having to compute the whole v-align for the whole line.
        breakState.init( paragraphBox );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
        final RenderableReplacedContentBox rpc = (RenderableReplacedContentBox) box;
        final long computedHeight = ReplacedContentUtil.computeHeight( rpc, 0, box.getCachedWidth() );
        box.setCachedHeight( computedHeight );
      }
      return true;
    }

    // No breakstate and not being suspended? Why this?
    if ( breakState.isSuspended() == false ) {
      throw new IllegalStateException( "This cannot be: No breakstate and not being suspended? Why this?" );
    }

    // this way or another - we are suspended now. So there is no need to look
    // at the children anymore ..
    return false;
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
   *          the box.
   */
  protected void finishCanvasLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return;
    }

    performFinishTable( box );

    final int layoutNodeType = box.getLayoutNodeType();
    if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      box.setCachedHeight( computeBlockHeightAndAlign( box ) );
    } else if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      box.setCachedHeight( computeRowHeight( box, 0 ) );
    } else if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      // ignored ...
    } else {
      box.setCachedHeight( computeCanvasHeight( box ) );
    }

    if ( breakState.isActive() ) {
      final Object suspender = breakState.getSuspendItem();
      if ( box.getInstanceId() == suspender ) {
        breakState.setSuspendItem( null );
        return;
      }
      if ( suspender != null ) {
        return;
      }

      if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        breakState.deinit();
      }
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

    final double posY = node.getNodeLayoutProperties().getPosY();
    if ( node.isSizeSpecifiesBorderBox() ) {
      return ( parentPosition + RenderLength.resolveLength( 0, posY ) );
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
      return ( parentPosition + RenderLength.resolveLength( 0, posY ) - insetsTop );
    }
  }

  private static long computeCanvasHeight( final RenderBox box ) {
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = box.getBoxDefinition();

    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final RenderLength minHeight = boxDefinition.getMinimumHeight();
    final RenderLength preferredHeight = boxDefinition.getPreferredHeight();
    final RenderLength maxHeight = boxDefinition.getMaximumHeight();

    final long insetsTop = ( blp.getBorderTop() + bdef.getPaddingTop() );
    final long insetsBottom = blp.getBorderBottom() + bdef.getPaddingBottom();
    final long insets = insetsTop + insetsBottom;

    // find the maximum of the used height (for all childs) and the specified min-height.
    final long minHeightResolved = minHeight.resolve( 0 );
    long consumedHeight;
    if ( box.isSizeSpecifiesBorderBox() ) {
      consumedHeight = Math.max( minHeightResolved, insets ) - insetsBottom;
    } else {
      consumedHeight = minHeightResolved + insetsTop;
    }

    final long boxY = box.getCachedY();

    RenderNode node = box.getFirstChild();
    while ( node != null ) {
      final long childY2 = ( node.getCachedY() + node.getCachedHeight() );
      final long childLocalY2 = childY2 - boxY;
      if ( childLocalY2 > consumedHeight ) {
        consumedHeight = childLocalY2;
      }
      node = node.getNext();
    }

    consumedHeight += insetsBottom;

    // The consumed height computed above specifies the size at the border-edge.
    // However, depending on the box-sizing property, we may have to resolve them against the
    // content-edge instead.

    final long maxHeightResolved = maxHeight.resolve( 0, InfiniteMajorAxisLayoutStep.MAX_AUTO );
    if ( box.isSizeSpecifiesBorderBox() ) {
      final long prefHeightResolved;
      if ( RenderLength.AUTO.equals( preferredHeight ) ) {
        prefHeightResolved = consumedHeight;
      } else if ( preferredHeight.isPercentage() == false ) {
        prefHeightResolved = preferredHeight.resolve( 0 );
      } else {
        prefHeightResolved = consumedHeight;
      }

      final long height = ProcessUtility.computeLength( minHeightResolved, maxHeightResolved, prefHeightResolved );
      return ( height );
    } else {
      consumedHeight = Math.max( 0, consumedHeight - insets );
      final long prefHeightResolved;
      if ( RenderLength.AUTO.equals( preferredHeight ) ) {
        prefHeightResolved = consumedHeight;
      } else if ( preferredHeight.isPercentage() == false ) {
        prefHeightResolved = preferredHeight.resolve( 0 );
      } else {
        prefHeightResolved = consumedHeight;
      }

      final long height = ProcessUtility.computeLength( minHeightResolved, maxHeightResolved, prefHeightResolved );
      return ( height + insets );
    }
  }

  protected boolean startRowLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return false;
    }

    performStartTable( box );

    // Compute the block-position of the box. The box is positioned relative to the previous sibling or
    // relative to the parent.
    box.setCachedY( computeVerticalRowPosition( box ) );

    if ( breakState.isActive() ) {
      // No breakstate and not being suspended? Why this?
      if ( breakState.isSuspended() == false ) {
        throw new IllegalStateException( "This cannot be." );
      }

      // this way or another - we are suspended now. So there is no need to look
      // at the children anymore ..

      // This code is only executed for inline-block elements. Inline-block elements are not part of
      // the 0.8.9 or 1.0 engine layouting.
      return false;
    }

    final int layoutNodeType = box.getLayoutNodeType();
    if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
      // We cant cache that ... the shift operations later would misbehave
      // One way around would be to at least store the layouted offsets
      // (which should be immutable as long as the line did not change its
      // contents) and to reapply them on each run. This is cheaper than
      // having to compute the whole v-align for the whole line.
      breakState.init( paragraphBox );
    } else if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      final RenderableReplacedContentBox rpc = (RenderableReplacedContentBox) box;
      box.setCachedHeight( ReplacedContentUtil.computeHeight( rpc, 0, box.getCachedWidth() ) );
    }

    return true;
  }

  protected void processRowLevelNode( final RenderNode node ) {
    node.setCachedY( computeVerticalRowPosition( node ) );

    final int nodeType = node.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      final FinishedRenderNode fnode = (FinishedRenderNode) node;
      node.setCachedHeight( fnode.getLayoutedHeight() );
    } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
      throw new IllegalStateException( "A Inline-Box must be contained in a paragraph." );
    }
  }

  protected void finishRowLevelBox( final RenderBox box ) {
    if ( checkCacheValid( box ) ) {
      return;
    }

    performFinishTable( box );

    final int layoutNodeType = box.getLayoutNodeType();
    if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
      box.setCachedHeight( computeBlockHeightAndAlign( box ) );
    } else if ( ( layoutNodeType & LayoutNodeTypes.MASK_BOX_ROW ) == LayoutNodeTypes.MASK_BOX_ROW ) {
      box.setCachedHeight( computeRowHeight( box, 0 ) );
    } else if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      // ignored ..
    } else {
      box.setCachedHeight( computeCanvasHeight( box ) );
    }

    if ( breakState.isActive() ) {
      final Object suspender = breakState.getSuspendItem();
      if ( box.getInstanceId() == suspender ) {
        breakState.setSuspendItem( null );
        return;
      }
      if ( suspender != null ) {
        return;
      }

      if ( layoutNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        breakState.deinit();
      }
    }
  }

  private long computeVerticalRowPosition( final RenderNode node ) {
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
    // table cells behave like block-level cells most of the time.
    return startBlockLevelBox( box );
  }

  protected void finishTableCellLevelBox( final RenderBox box ) {
    // table cells behave like block-level cells most of the time.
    finishBlockLevelBox( box );
  }

  protected boolean startTableRowLevelBox( final RenderBox box ) {
    box.setCachedY( computeVerticalRowPosition( box ) );
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
      getTableRowHeightStep().startTableCell( (TableCellRenderBox) box );
    } else {
      final long blockHeight = computeTableHeightAndAlign( box );
      box.setCachedHeight( blockHeight );
    }

    markAllChildsDirty( box );
    return true;
  }

  protected void finishTableRowLevelBox( final RenderBox box ) {
    clearAllChildsDirtyMarker( box );
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_CELL ) {
      final long blockHeight = computeTableHeightAndAlign( box );
      getTableRowHeightStep().finishTableCell( (TableCellRenderBox) box, blockHeight );
    } else {
      final long blockHeight = computeTableHeightAndAlign( box );
      box.setCachedHeight( blockHeight );
    }
  }

  protected boolean startTableLevelBox( final RenderBox box ) {
    box.setCachedY( computeVerticalBlockPosition( box ) );

    if ( box instanceof TableSectionRenderBox ) {
      getTableRowHeightStep().startTableSection( (TableSectionRenderBox) box );
    }
    return true;
  }

  protected void processTableLevelNode( final RenderNode node ) {
    processBlockLevelNode( node );
  }

  protected void finishTableLevelBox( final RenderBox box ) {
    box.setCachedHeight( computeTableHeightAndAlign( box ) );

    if ( box instanceof TableSectionRenderBox ) {
      getTableRowHeightStep().finishTableSection( (TableSectionRenderBox) box );
    }
  }

  protected boolean startTableSectionLevelBox( final RenderBox box ) {
    if ( box instanceof TableRowRenderBox ) {
      getTableRowHeightStep().startTableRow( (TableRowRenderBox) box );
    }

    box.setCachedY( computeVerticalBlockPosition( box ) );
    return true;
  }

  protected void processTableSectionLevelNode( final RenderNode node ) {
    processBlockLevelNode( node );
  }

  protected void finishTableSectionLevelBox( final RenderBox box ) {
    box.setCachedHeight( 0 );
  }
}
