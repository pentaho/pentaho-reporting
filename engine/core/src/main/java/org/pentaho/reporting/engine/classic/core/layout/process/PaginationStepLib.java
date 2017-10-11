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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.process.util.BasePaginationTableState;
import org.pentaho.reporting.engine.classic.core.layout.process.util.PaginationShiftState;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * A helper class that contains generic methods that would distract me from the actual pagination logic.
 */
public final class PaginationStepLib {
  private static final Log logger = LogFactory.getLog( PaginationStepLib.class );

  private PaginationStepLib() {
  }

  public static void configureBreakUtility( final PageBreakPositionList breakUtility, final LogicalPageBox pageBox,
      final long[] allCurrentBreaks, final long reservedHeight, final long lastBreakLocal ) {
    final PageBreakPositionList allPreviousBreak = pageBox.getAllVerticalBreaks();
    breakUtility.copyFrom( allPreviousBreak );

    final long pageOffset = pageBox.getPageOffset();
    final long headerHeight = pageBox.getHeaderArea().getHeight();
    // Then add all new breaks (but take the header and footer-size into account) ..
    if ( allCurrentBreaks.length == 1 ) {
      breakUtility.addMajorBreak( pageOffset, headerHeight );
      breakUtility.addMajorBreak( ( lastBreakLocal - reservedHeight ) + pageOffset, headerHeight );
    } else { // more than one physical page; therefore header and footer are each on a separate canvas ..
      breakUtility.addMajorBreak( pageOffset, headerHeight );
      final int breakCount = allCurrentBreaks.length - 1;
      for ( int i = 1; i < breakCount; i++ ) {
        final long aBreak = allCurrentBreaks[i];
        breakUtility.addMinorBreak( pageOffset + ( aBreak - headerHeight ) );
      }
      breakUtility.addMajorBreak( pageOffset + ( lastBreakLocal - reservedHeight ), headerHeight );
    }
  }

  public static void assertProgress( final LogicalPageBox pageBox ) {
    final RenderNode lastChild = pageBox.getLastChild();
    if ( lastChild != null ) {
      final long lastChildY2 = lastChild.getY() + lastChild.getHeight();
      if ( lastChildY2 < pageBox.getHeight() ) {
        // ModelPrinter.print(pageBox);
        throw new IllegalStateException( "Assertation failed: Pagination did not proceed: " + lastChildY2 + " < "
            + pageBox.getHeight() );
      }
    }
  }

  public static long restrictPageAreaHeights( final LogicalPageBox pageBox, final long[] allCurrentBreaks ) {
    final BlockRenderBox headerArea = pageBox.getHeaderArea();
    final long headerHeight = Math.min( headerArea.getHeight(), allCurrentBreaks[0] );
    headerArea.setHeight( headerHeight );

    final BlockRenderBox footerArea = pageBox.getFooterArea();
    final BlockRenderBox repeatFooterArea = pageBox.getRepeatFooterArea();
    if ( allCurrentBreaks.length > 1 ) {
      final long lastBreakLocal = allCurrentBreaks[allCurrentBreaks.length - 1];
      final long lastPageHeight = lastBreakLocal - allCurrentBreaks[allCurrentBreaks.length - 2];
      final long footerHeight = Math.min( footerArea.getHeight(), lastPageHeight );
      footerArea.setHeight( footerHeight );

      final long repeatFooterHeight = Math.min( repeatFooterArea.getHeight(), lastPageHeight );
      repeatFooterArea.setHeight( repeatFooterHeight );
    }

    final long footerHeight = footerArea.getHeight();
    final long repeatFooterHeight = repeatFooterArea.getHeight();
    // Assertion: Make sure that we do not run into a infinite loop..
    return headerHeight + repeatFooterHeight + footerHeight;
  }

  public static void assertBlockPosition( final RenderBox box, final long shift ) {
    if ( box.getLayoutNodeType() == LayoutNodeTypes.TYPE_BOX_TABLE_SECTION ) {
      // no point in testing table-sections, as the header will be an out-of-order band.
      return;
    }

    final boolean error;
    final long expectedYPos;
    if ( box.getPrev() != null ) {
      error = true;
      expectedYPos = box.getPrev().getY() + box.getPrev().getHeight();
    } else {
      if ( box.getParent() != null ) {
        error = false;
        expectedYPos = box.getParent().getY();
        final Object parentVAlignment = box.getParent().getStyleSheet().getStyleProperty( ElementStyleKeys.VALIGNMENT );
        if ( parentVAlignment != null && ElementAlignment.TOP.equals( parentVAlignment ) == false ) {
          return;
        }
      } else {
        error = true;
        expectedYPos = 0;
      }
    }

    final long realY = box.getY() + shift;
    if ( realY != expectedYPos ) {
      final long additionalShift = expectedYPos - realY;
      final long realShift = shift + additionalShift;
      if ( error ) {
        ModelPrinter.INSTANCE.print( box );
        ModelPrinter.INSTANCE.print( ModelPrinter.getRoot( box ) );
        throw new InvalidReportStateException( String.format( "Assert: Shift is not as expected: "
            + "realY=%d != expectation=%d; Shift=%d; AdditionalShift=%d; RealShift=%d", realY, expectedYPos, shift,
            additionalShift, realShift ) );
      } else {
        if ( logger.isDebugEnabled() ) {
          logger.debug( String.format( "Assert: Shift is not as expected: realY=%d != expectation=%d; Shift=%d; "
              + "AdditionalShift=%d; RealShift=%d (False positive if block box has valign != TOP", realY, expectedYPos,
              shift, additionalShift, realShift ) );
        }
      }
    }
  }

  /**
   * Computes the height that will be required on this page to display at least some parts of the box.
   *
   * @param box
   *          the box for which the height is computed
   * @return the height in micro-points.
   */
  public static long computeNonBreakableBoxHeight( final RenderBox box, final PaginationShiftState shiftState,
      final BasePaginationTableState tableState ) {
    // must return the reserved space starting from box's y position.
    final long widowSize = getWidowConstraint( box, shiftState, tableState );

    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    if ( sblp.isAvoidPagebreakInside()
        && box.getRestrictFinishedClearOut() != RenderBox.RestrictFinishClearOut.RESTRICTED ) {
      return Math.max( widowSize, box.getHeight() );
    }

    final int nodeType = box.getLayoutNodeType();
    if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) != LayoutNodeTypes.MASK_BOX_BLOCK ) {
      // Canvas, row or inline boxes have no notion of lines, and therefore they cannot have orphans and widows.
      return widowSize;
    }

    if ( isOrphanConstraintNeeded( box, shiftState, tableState ) == false ) {
      return widowSize;
    }

    final long orphanHeight = box.getOrphanConstraintSize();
    if ( widowSize + orphanHeight > box.getHeight() ) {
      // if the widows and orphan areas overlap, then the box becomes non-breakable.
      return Math.max( widowSize, box.getHeight() );
    }

    return Math.max( orphanHeight, widowSize );
  }

  /**
   * Widow constraint evaluation is skipped if
   * <p/>
   * (a) the box has a widow/orphan-parent defining that sits on the current page's page-offset and (b) this parent's
   * orphan-restricted pagebreak-exclusion zone includes this box.
   *
   * @param box
   * @param shiftState
   * @param tableState
   * @return
   */
  private static boolean isOrphanConstraintNeeded( final RenderBox box, final PaginationShiftState shiftState,
      final BasePaginationTableState tableState ) {
    final long boxY = box.getY() + shiftState.getShiftForNextChild();
    final long pageOffset = tableState.getPageOffset( boxY );
    if ( pageOffset == boxY ) {
      // no need to set a widow constraint, we are not shifting anyway ..
      return false;
    }
    if ( box.getRestrictFinishedClearOut() != RenderBox.RestrictFinishClearOut.RESTRICTED ) {
      return false;
    }

    if ( isBoxInsideParentOrphanZoneOnThisPage( box, pageOffset, boxY ) ) {
      return false;
    }
    return true;
  }

  private static boolean isBoxInsideParentOrphanZoneOnThisPage( final RenderBox box, final long pageOffset,
      final long boxYShifted ) {
    RenderBox parent = box.getParent();
    while ( parent != null ) {
      if ( parent.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED ) {
        break;
      }

      if ( parent.getY() < pageOffset ) {
        // once the parent is sitting on the previous page, we no longer need to ask it ..
        break;
      }

      if ( parent.getOrphanConstraintSize() == 0 ) {
        parent = parent.getParent();
        continue;
      }

      final long constraintBoundary = parent.getY() + parent.getOrphanConstraintSize();
      if ( constraintBoundary > boxYShifted ) {
        // this parent is not relevant.
        return true;
      }

      parent = parent.getParent();
    }
    return false;
  }

  public static long getWidowConstraint( final RenderBox box, final PaginationShiftState shiftState,
      final BasePaginationTableState tableState ) {
    if ( box.isWidowBox() == false ) {
      return 0;
    }

    final long boxY = box.getY() + shiftState.getShiftForNextChild();
    long retval = 0;
    RenderBox parent = box.getParent();

    final long pageOffset = tableState.getPageOffset();
    if ( pageOffset == boxY ) {
      // no need to set a widow constraint, we are not shifting anyway ..
      return 0;
    }

    while ( parent != null ) {
      if ( parent.getRestrictFinishedClearOut() == RenderBox.RestrictFinishClearOut.UNRESTRICTED ) {
        break;
      }

      if ( parent.getWidowConstraintSize() == 0 ) {
        parent = parent.getParent();
        continue;
      }

      final long y2 = parent.getY2();
      final long constraintBoundary;
      if ( parent.getY() < pageOffset ) {
        constraintBoundary = y2 - Math.max( 0, parent.getWidowConstraintSize() );
      } else {
        constraintBoundary = y2 - Math.max( 0, parent.getWidowConstraintSizeWithKeepTogether() );
      }
      if ( constraintBoundary == boxY ) {
        retval = Math.max( retval, y2 - boxY );
      }

      parent = parent.getParent();
    }
    return retval;
  }
}
