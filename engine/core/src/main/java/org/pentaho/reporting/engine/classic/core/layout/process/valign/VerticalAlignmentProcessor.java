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

package org.pentaho.reporting.engine.classic.core.layout.process.valign;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.process.InfiniteMajorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;

/**
 * There's only one alignment processor for the vertical layouting. The processor is non-iterative, it receives a single
 * primary sequence (which represents a line) and processes that fully.
 * <p/>
 * As result, this processor generates a list of offsets and heights; the offset of the outermost element is always zero
 * and the height is equal to the height of the whole line.
 *
 * @author Thomas Morgner
 */
public final class VerticalAlignmentProcessor {
  // private long lineHeight;
  private long minTopPos;
  private long maxBottomPos;
  private BoxAlignContext rootContext;
  private long sourcePosition;
  private InfiniteMajorAxisLayoutStep majorAxisLayoutStep;

  public VerticalAlignmentProcessor() {
  }

  private InfiniteMajorAxisLayoutStep getMajorAxisLayoutStep() {
    if ( majorAxisLayoutStep == null ) {
      majorAxisLayoutStep = new InfiniteMajorAxisLayoutStep();
    }
    return majorAxisLayoutStep;
  }

  // 7% of our time is spent here ..
  public void align( final BoxAlignContext alignStructure, final long y1, final long lineHeight ) {
    this.minTopPos = Long.MAX_VALUE;
    this.maxBottomPos = Long.MIN_VALUE;
    // this.lineHeight = lineHeight;
    this.rootContext = alignStructure;
    this.sourcePosition = y1;

    performAlignment( alignStructure );
    if ( alignStructure.isSimpleNode() == false ) {
      performExtendedAlignment( alignStructure, alignStructure );
    }
    normalizeAlignment( alignStructure );

    alignStructure.setAfterEdge( Math.max( maxBottomPos, lineHeight ) );
    alignStructure.shift( -minTopPos + y1 );
    apply( alignStructure );

    this.rootContext = null;
  }

  private void performAlignment( final BoxAlignContext box ) {
    // We have a valid align structure here.
    AlignContext child = box.getFirstChild();
    while ( child != null ) {
      if ( child instanceof InlineBlockAlignContext ) {
        final InlineBlockAlignContext context = (InlineBlockAlignContext) child;
        final InfiniteMajorAxisLayoutStep majorAxisLayoutStep = getMajorAxisLayoutStep();
        majorAxisLayoutStep.continueComputation( (RenderBox) context.getNode() );

        // todo: Allow to select other than the first baseline ..
      }

      BoxAlignContext parent = box;
      final VerticalTextAlign verticalAlignment = child.getNode().getVerticalTextAlignment();
      if ( VerticalTextAlign.TOP.equals( verticalAlignment ) || VerticalTextAlign.BOTTOM.equals( verticalAlignment ) ) {
        // Those alignments ignore the normal alignment rules and all boxes
        // align themself on the extended linebox.
        // I'm quite sure that the definition itself is unclean ..
        // continue;
        parent = rootContext;
      }

      // Now lets assume we have a valid structure...
      // All childs have been aligned. Now check how this box is positioned
      // in relation to its parent.
      final long shiftDistance = computeShift( child, parent );
      // The alignment baseline defines to which baseline of the parent we
      // will align this element
      final int alignmentBaseline = child.getDominantBaseline();

      // The alignment adjust defines, where the alignment point of this
      // child will be. The alignment adjust is relative to the child's
      // line-height. In the normal case, this will be zero to indicate, that
      // the alignment point is equal to the child's dominant baseline.
      final long childAlignmentPoint = computeAlignmentAdjust( child, alignmentBaseline );
      final long childAscent = child.getBaselineDistance( ExtendedBaselineInfo.BEFORE_EDGE );
      final long childPosition = ( -childAscent + childAlignmentPoint ) + child.getBeforeEdge();

      // If zero, the parent's alignment point is on the parent's dominant
      // baseline.
      final long parentAlignmentPoint = parent.getBaselineDistance( alignmentBaseline );
      final long parentAscent = parent.getBaselineDistance( ExtendedBaselineInfo.BEFORE_EDGE );

      final long parentPosition = ( -parentAscent + parentAlignmentPoint ) + parent.getBeforeEdge();

      final long alignment = parentPosition - childPosition;
      final long offset = shiftDistance + alignment;
      child.shift( offset );

      if ( rootContext.getBeforeEdge() > child.getBeforeEdge() ) {
        rootContext.setBeforeEdge( child.getBeforeEdge() );
      }

      if ( rootContext.getAfterEdge() < child.getAfterEdge() ) {
        rootContext.setAfterEdge( child.getAfterEdge() );
      }

      if ( child instanceof BoxAlignContext ) {
        performAlignment( (BoxAlignContext) child );
      }

      child = child.getNext();
    }
  }

  /**
   * This simply searches the maximum shift that we have to do to normalize the element.
   *
   * @param box
   * @return
   */
  private void normalizeAlignment( final BoxAlignContext box ) {
    minTopPos = Math.min( minTopPos, box.getBeforeEdge() );
    maxBottomPos = Math.max( maxBottomPos, box.getAfterEdge() );

    if ( box.isSimpleNode() ) {
      return;
    }

    AlignContext child = box.getFirstChild();
    while ( child != null ) {
      if ( child instanceof BoxAlignContext ) {
        normalizeAlignment( (BoxAlignContext) child );
      }
      child = child.getNext();
    }
  }

  private long computeShift( final AlignContext child, final BoxAlignContext box ) {
    // for now, we do not perform any advanced layouting. Maybe later ..
    return 0;
  }

  private long computeAlignmentAdjust( final AlignContext context, final int defaultBaseLine ) {
    // for now, we do not perform any advanced layouting. Maybe later ..
    return context.getBaselineDistance( defaultBaseLine );
  }

  private void apply( final BoxAlignContext box ) {
    final RenderNode node = box.getNode();
    final long beforeEdge = box.getBeforeEdge();
    node.setCachedY( beforeEdge );
    node.setCachedHeight( box.getAfterEdge() - beforeEdge );

    if ( box.isSimpleNode() ) {
      AlignContext child = box.getFirstChild();
      while ( child != null ) {
        if ( child instanceof BoxAlignContext ) {
          apply( (BoxAlignContext) child );
        } else {
          final RenderNode childNode = child.getNode();
          final long childBeforeEdge = child.getBeforeEdge();
          childNode.setCachedY( childBeforeEdge );
          childNode.setCachedHeight( child.getAfterEdge() - childBeforeEdge );
        }
        child = child.getNext();
      }
    } else {
      AlignContext child = box.getFirstChild();
      while ( child != null ) {
        if ( child instanceof BoxAlignContext ) {
          apply( (BoxAlignContext) child );
        } else if ( child instanceof InlineBlockAlignContext ) {
          // Luckily the layoutmodel does not yet specify inline-boxes. Need to be fixed in the flow-engine.
          // also shift all the childs.
          final long shift = child.getBeforeEdge() - sourcePosition;
          CacheBoxShifter.shiftBox( child.getNode(), shift );
        } else {
          final RenderNode childNode = child.getNode();
          final long childBeforeEdge = child.getBeforeEdge();
          childNode.setCachedY( childBeforeEdge );
          childNode.setCachedHeight( child.getAfterEdge() - childBeforeEdge );
        }

        child = child.getNext();
      }
    }
  }

  // protected static void print (final BoxAlignContext alignContext, final int level)
  // {
  // Log.debug ("Box: L:" + level + " Y1:" + alignContext.getBeforeEdge() +
  // " Y2:" + alignContext.getAfterEdge() +
  // " H:" + (alignContext.getAfterEdge() - alignContext.getBeforeEdge())
  // );
  // // We have a valid align structure here.
  // AlignContext child = alignContext.getFirstChild();
  // while (child != null)
  // {
  // if (child instanceof BoxAlignContext)
  // {
  // print((BoxAlignContext) child, level + 1);
  // }
  // else
  // {
  // Log.debug ("...: L:" + level + " Y1:" + child.getBeforeEdge() +
  // " Y2:" + (child.getAfterEdge()) +
  // " H:" + (child.getAfterEdge() - child.getBeforeEdge()));
  // }
  // child = child.getNext();
  // }
  // }
  //

  /**
   * Verify all elements with alignment top or bottom. This step is required, as the extended linebox is allowed to
   * change its height during the ordinary alignment. Argh, I hate that specificiation.
   *
   * @param box
   */
  private void performExtendedAlignment( final BoxAlignContext box, final BoxAlignContext lineBox ) {
    // Aligns elements with vertical-align TOP and vertical-align BOTTOM
    AlignContext child = box.getFirstChild();
    while ( child != null ) {
      final ElementAlignment verticalAlignment = child.getNode().getNodeLayoutProperties().getVerticalAlignment();
      if ( ElementAlignment.TOP.equals( verticalAlignment ) ) {
        final long childTopEdge = child.getBeforeEdge();
        final long parentTopEdge = lineBox.getBeforeEdge();
        child.shift( parentTopEdge - childTopEdge );
      } else if ( ElementAlignment.BOTTOM.equals( verticalAlignment ) ) {
        // Align the childs after-edge with the parent's after-edge
        final long childBottomEdge = child.getAfterEdge();
        final long parentBottomEdge = lineBox.getAfterEdge();
        child.shift( parentBottomEdge - childBottomEdge );
      }

      if ( child instanceof BoxAlignContext ) {
        performExtendedAlignment( (BoxAlignContext) child, lineBox );
      }

      child = child.getNext();
    }
  }
}
