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

import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;

public class MinorAxisLayoutStepUtil {

  public static final RenderLength FULL_WIDTH_LENGTH = RenderLength.createFromRaw( -100 );

  private MinorAxisLayoutStepUtil() {
  }

  public static long
    resolveNodeWidthOnStart( final RenderBox box, final MinorAxisNodeContext nodeContext, final long x ) {
    final long width = resolveNodeWidthOnStartInternal( box, nodeContext );
    return correctForRoundingErrors( nodeContext, x, width );
  }

  private static long correctForRoundingErrors( final MinorAxisNodeContext nodeContext, final long x, final long width ) {
    final long parentX2 = nodeContext.getParentX2();
    if ( parentX2 == 0 ) {
      return width;
    }

    final long currentX2 = width + x;
    final long delta = Math.abs( parentX2 - currentX2 );
    if ( delta != 0 && delta < 10 ) {
      // prefer the parent's edge over the calculated edge. We only do that when the edges are close by,
      // usually after a rounding error.
      return parentX2 - x;
    }
    return width;
  }

  /**
   * Resolves the current element against the parent's content-area.
   *
   * @param box
   * @return
   */
  private static long resolveNodeWidthOnStartInternal( final RenderBox box, final MinorAxisNodeContext nodeContext ) {
    final long minChunkWidth = 0;
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength minLength = boxDef.getMinimumWidth();
    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long bcw = nodeContext.getBlockContextWidth();
    final long min = minLength.resolve( bcw, 0 );
    final long max = maxLength.resolve( bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO );
    if ( box.getBoxDefinition().isSizeSpecifiesBorderBox() ) {
      final long parentSize = nodeContext.getResolvedPreferredSize();
      // We are assuming that any size specified by the user already includes the padding and borders.
      // min-chunk-width must take insets into account. We will not add the insets to the computed width.
      final long pref = prefLength.resolve( bcw, Math.max( parentSize, minChunkWidth ) );
      return ProcessUtility.computeLength( min, max, pref );
    } else {
      // We are assuming that any size specified by the user does not include padding or border.
      // min-chunk-width is used without borders. We will add the insets unconditionally later.
      final long parentSize = nodeContext.getResolvedPreferredSize() - box.getInsets();
      final long pref = prefLength.resolve( bcw, Math.max( parentSize, minChunkWidth ) );
      return ProcessUtility.computeLength( min, max, pref ) + box.getInsets();
    }
  }

  public static long resolveNodeWidthOnStartForCanvasLegacy( final RenderBox box,
      final MinorAxisNodeContext nodeContext, final long x ) {
    final long width = resolveNodeWidthOnStartForCanvasLegacyInternal( box, nodeContext );
    return correctForRoundingErrors( nodeContext, x, width );
  }

  /**
   * Resolves the current element against the parent's content-area.
   *
   * @param box
   * @return
   */
  private static long resolveNodeWidthOnStartForCanvasLegacyInternal( final RenderBox box,
      final MinorAxisNodeContext nodeContext ) {
    final long minChunkWidth = 0;
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength definedMinLength = boxDef.getMinimumWidth();

    final RenderLength minLength;
    if ( definedMinLength.getValue() == 0 ) {
      // PRD-3857 - Auto-correcting min-size to 100% for zero-defined boxes that are not canvas
      // blows up the min-chunk-width test
      minLength = FULL_WIDTH_LENGTH;
    } else {
      minLength = definedMinLength;
    }

    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long bcw = nodeContext.getBlockContextWidth();
    final long min = minLength.resolve( bcw, 0 );
    final long max = maxLength.resolve( bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO );
    if ( box.getBoxDefinition().isSizeSpecifiesBorderBox() ) {
      final long parentSize = nodeContext.getResolvedPreferredSize();
      // We are assuming that any size specified by the user already includes the padding and borders.
      // min-chunk-width must take insets into account. We will not add the insets to the computed width.
      final long pref = prefLength.resolve( bcw, Math.max( parentSize, minChunkWidth ) );
      return ProcessUtility.computeLength( min, max, pref );
    } else {
      // We are assuming that any size specified by the user does not include padding or border.
      // min-chunk-width is used without borders. We will add the insets unconditionally later.
      final long parentSize = nodeContext.getResolvedPreferredSize() - box.getInsets();
      final long pref = prefLength.resolve( bcw, Math.max( parentSize, minChunkWidth ) );
      return ProcessUtility.computeLength( min, max, pref ) + box.getInsets();
    }
  }

  public static long resolveNodeWidthOnFinish( final RenderBox box, final MinorAxisNodeContext nodeContext,
      final boolean strictLegacyMode ) {
    final long width = resolveNodeWidthOnFinishInternal( box, nodeContext, strictLegacyMode );
    return correctForRoundingErrors( nodeContext, nodeContext.getX(), width );
  }

  /**
   * If the element has no preferred size, apply the current element's constraints against the box children's used area.
   *
   * @param box
   * @return
   */
  private static long resolveNodeWidthOnFinishInternal( final RenderBox box, final MinorAxisNodeContext nodeContext,
      final boolean strictLegacyMode ) {
    final BoxDefinition boxDef = box.getBoxDefinition();
    if ( RenderLength.AUTO.equals( boxDef.getPreferredWidth() ) == false ) {
      return nodeContext.getWidth();
    }

    final long minChunkWidth;
    final RenderLength minLength;
    if ( strictLegacyMode == false || box.useMinimumChunkWidth() ) {
      minChunkWidth = nodeContext.getMaxChildX2() - nodeContext.getX1();
      minLength = boxDef.getMinimumWidth();
    } else {
      minChunkWidth = nodeContext.getX2() - nodeContext.getX1();
      if ( boxDef.getMinimumWidth().getValue() == 0 ) {
        minLength = FULL_WIDTH_LENGTH;
      } else {
        minLength = boxDef.getMinimumWidth();
      }
    }

    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long bcw = nodeContext.getBlockContextWidth();
    final long min = minLength.resolve( bcw, 0 );
    final long max = maxLength.resolve( bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO );
    if ( box.getBoxDefinition().isSizeSpecifiesBorderBox() ) {
      final long parentSize = nodeContext.getResolvedPreferredSize();
      // We are assuming that any size specified by the user already includes the padding and borders.
      // min-chunk-width must take insets into account. We will not add the insets to the computed width.
      final long pref = Math.max( parentSize, minChunkWidth + box.getInsets() );
      return ProcessUtility.computeLength( min, max, pref );
    } else {
      // We are assuming that any size specified by the user does not include padding or border.
      // min-chunk-width is used without borders. We will add the insets unconditionally later.
      final long parentSize = nodeContext.getResolvedPreferredSize() - box.getInsets();
      final long pref = Math.max( parentSize, minChunkWidth );
      return ProcessUtility.computeLength( min, max, pref ) + box.getInsets();
    }
  }

  /**
   * Calculates the minimum area a element will consume. The returned value references the border-box, the area that
   * includes border, padding and content-box.
   *
   * @param box
   * @return
   */
  public static long resolveNodeWidthForMinChunkCalculation( final RenderBox box ) {
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength minLength = boxDef.getMinimumWidth();
    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long min = minLength.resolve( 0, 0 );
    final long max = maxLength.resolve( 0, ComputeStaticPropertiesProcessStep.MAX_AUTO );
    if ( box.getBoxDefinition().isSizeSpecifiesBorderBox() ) {
      // We are assuming that any size specified by the user already includes the padding and borders.
      // min-chunk-width must take insets into account. We will not add the insets to the computed width.
      final long pref = prefLength.resolve( 0, box.getInsets() );
      return ProcessUtility.computeLength( min, max, pref );
    } else {
      // We are assuming that any size specified by the user does not include padding or border.
      // min-chunk-width is used without borders. We will add the insets unconditionally later.
      final long pref = prefLength.resolve( 0, 0 );
      return ProcessUtility.computeLength( min, max, pref ) + box.getInsets();
    }

  }
}
