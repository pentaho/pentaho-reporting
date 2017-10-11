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

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ShapeDrawable;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * The sheet layout is used to build the background map and to collect the x- and y-cell-borders.
 */
public class SheetLayout implements SlimSheetLayout, Cloneable {

  private static final SheetLayoutTableCellDefinition MARKER_DEFINITION = new SheetLayoutTableCellDefinition();
  /**
   * An internal flag indicating that the upper or left bounds should be used.
   */
  private static final boolean UPPER_BOUNDS = true;
  private static final boolean LOWER_BOUNDS = false;
  /**
   * A flag, defining whether to use strict layout mode.
   */
  private final boolean strict;
  /**
   * The XBounds, all vertical cell boundaries (as CoordinateMappings).
   */
  private TableCutList xBounds;
  /**
   * The YBounds, all vertical cell boundaries (as CoordinateMappings).
   */
  private TableCutList yBounds;
  /**
   * The right border of the grid. This is needed when not being in the strict mode.
   */
  private long xMaxBounds;
  private long yMaxBounds;
  private boolean ellipseAsRectangle;

  /**
   * Creates a new TableGrid-object. If strict mode is enabled, all cell bounds are used to create the table grid,
   * resulting in a more complex layout.
   *
   * @param strict
   *          the strict mode for the layout.
   * @param ellipseAsRectangle
   *          a flag that defines whether ellipse-objects are translated into rectangles and therefore create
   *          backgrounds.
   */
  public SheetLayout( final boolean strict, final boolean ellipseAsRectangle ) {
    this.ellipseAsRectangle = ellipseAsRectangle;
    this.xBounds = new TableCutList( 50, true );
    this.yBounds = new TableCutList( 2500, true );
    this.strict = strict;
    this.xMaxBounds = 0;
    this.yMaxBounds = 0;
    this.ensureXMapping( 0, Boolean.FALSE );
    this.ensureYMapping( 0, Boolean.FALSE );
  }

  public SheetLayout( OutputProcessorMetaData metaData ) {
    this( metaData.isFeatureSupported( AbstractTableOutputProcessor.STRICT_LAYOUT ), metaData
        .isFeatureSupported( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE ) );
  }

  public SheetLayout derive() {
    SheetLayout clone = clone();
    clone.clearVerticalInfo();
    return clone;
  }

  public SheetLayout clone() {
    try {
      SheetLayout clone = (SheetLayout) super.clone();
      clone.xBounds = xBounds.clone();
      clone.yBounds = yBounds.clone();
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  private SheetLayoutTableCellDefinition createBackground( final RenderBox box ) {
    if ( box.getBoxDefinition().getBorder().isEmpty() == false ) {
      return MARKER_DEFINITION;
    }

    final StyleSheet styleSheet = box.getStyleSheet();
    if ( styleSheet.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR ) != null ) {
      return MARKER_DEFINITION;
    }

    if ( styleSheet.getStyleProperty( ElementStyleKeys.ANCHOR_NAME ) != null ) {
      return MARKER_DEFINITION;
    }
    return null;
  }

  private SheetLayoutTableCellDefinition computeLegacyBackground( final RenderBox box, final long shift ) {

    // For legacy reasons: A single ReplacedContent in a canvas means, we may have a old-style border and
    // background definition.
    if ( box.getNodeType() != LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      return null;
    }
    final RenderableReplacedContentBox contentBox = (RenderableReplacedContentBox) box;

    final StyleSheet styleSheet = box.getStyleSheet();
    final RenderableReplacedContent rpc = contentBox.getContent();
    final Object rawContentObject = rpc.getRawObject();
    if ( rawContentObject instanceof DrawableWrapper == false ) {
      return null;
    }
    final DrawableWrapper wrapper = (DrawableWrapper) rawContentObject;
    final Object rawbackend = wrapper.getBackend();
    if ( rawbackend instanceof ShapeDrawable == false ) {
      return null;
    }
    final ShapeDrawable drawable = (ShapeDrawable) rawbackend;
    final Shape rawObject = drawable.getShape();

    final boolean draw = styleSheet.getBooleanStyleProperty( ElementStyleKeys.DRAW_SHAPE );
    if ( draw && rawObject instanceof Line2D ) {
      final int lineType;
      final long coordinate;
      final Line2D line = (Line2D) rawObject;

      final boolean vertical = line.getX1() == line.getX2();
      final boolean horizontal = line.getY1() == line.getY2();
      if ( vertical && horizontal ) {
        return null;
      }
      if ( vertical ) {
        lineType = SheetLayoutTableCellDefinition.LINE_HINT_VERTICAL;
        coordinate = StrictGeomUtility.toInternalValue( line.getX1() ) + box.getX();
      } else if ( horizontal ) {
        lineType = SheetLayoutTableCellDefinition.LINE_HINT_HORIZONTAL;
        coordinate = StrictGeomUtility.toInternalValue( line.getY1() ) + box.getY() + shift;
      } else {
        return null;
      }

      return new SheetLayoutTableCellDefinition( lineType, coordinate );
    }

    if ( rawObject instanceof Rectangle2D || ( ellipseAsRectangle && rawObject instanceof Ellipse2D ) ) {
      if ( draw ) {
        final BorderEdge edge = ProcessUtility.produceBorderEdge( box.getStyleSheet() );
        if ( edge != null ) {
          return MARKER_DEFINITION;
        }
      }
      if ( styleSheet.getBooleanStyleProperty( ElementStyleKeys.FILL_SHAPE ) ) {
        return MARKER_DEFINITION;
      }

      return null;
    }

    if ( rawObject instanceof RoundRectangle2D ) {
      if ( draw ) {
        // the beast has a border ..
        final BorderEdge edge = ProcessUtility.produceBorderEdge( box.getStyleSheet() );
        if ( edge != null ) {
          return MARKER_DEFINITION;
        }
      }
      if ( styleSheet.getBooleanStyleProperty( ElementStyleKeys.FILL_SHAPE ) ) {
        return MARKER_DEFINITION;
      }

      return null;
    }

    return null;
  }

  /**
   * Adds the bounds of the given TableCellData to the grid. The bounds given must be the same as the bounds of the
   * element, or the layouting might produce surprising results.
   * <p/>
   * This method will do nothing, if the element has a width and height of zero and does not define any anchors.
   *
   * @param element
   *          the position that should be added to the grid (might be null).
   * @param offset
   *          the vertical shift which adjusts the visual position of the content.
   * @param headerSize
   *          the vertical shift which adjusts the visual position of the content.
   * @return true, if the box has not changed and can be safely skipped.
   * @throws NullPointerException
   *           if the bounds are null
   */
  public boolean add( final RenderBox element, final long offset, final long headerSize, final long maxHeight ) {
    final long shift = headerSize - offset;
    final long shiftedY = element.getY() + shift;
    final long elementY;
    if ( shiftedY < 0 ) {
      if ( ( shiftedY + element.getHeight() ) < 0 ) {
        // The box will not be visible at all. (Should not happen in a sane environment ..)
        return true;
      }

      elementY = 0;
    } else {
      elementY = shiftedY;
    }

    final Object state = element.getTableExportState();
    final TableExportRenderBoxState tablestate;
    final SheetLayoutTableCellDefinition background;
    final boolean unmodified;
    if ( state != null ) {
      tablestate = (TableExportRenderBoxState) state;
      if ( tablestate.getBackgroundDefinitionAge() == element.getChangeTracker() ) {
        background = tablestate.getBackgroundDefinition();
        unmodified = true;
      } else {
        background = createBackground( element );
        tablestate.setBackgroundDefinition( background, element.getChangeTracker() );
        unmodified = false;
      }
    } else {
      tablestate = new TableExportRenderBoxState();
      element.setTableExportState( tablestate );
      background = createBackground( element );
      tablestate.setBackgroundDefinition( background, element.getChangeTracker() );
      unmodified = false;
    }

    if ( addLine( element, background, elementY, shiftedY, unmodified, headerSize ) ) {
      return unmodified;
    }

    final long elementX = element.getX();
    final long elementRightX = ( element.getWidth() + elementX );
    final long elementBottomY = element.getHeight() + shiftedY;

    // collect the bounds and add them to the xBounds and yBounds collection
    // if necessary...
    if ( unmodified == false ) {
      ensureXMapping( elementX, Boolean.FALSE );
    }
    if ( elementY >= headerSize ) {
      ensureYMapping( elementY, Boolean.FALSE );
    }

    // an end cut is auxilary, if it is not a background and the layout is not strict
    final Boolean aux;
    if ( ( background == null ) && ( isStrict() == false ) ) {
      aux = Boolean.TRUE;
    } else {
      aux = Boolean.FALSE;
    }
    ensureXMapping( elementRightX, aux );
    if ( elementBottomY >= headerSize && elementBottomY <= maxHeight ) {
      ensureYMapping( elementBottomY, aux );
    }

    // update the collected maximums
    return unmodified;
  }

  public void addRenderableContent( final RenderableReplacedContentBox element, final long offset,
      final long headerSize, final long maxHeight ) {
    final long shift = headerSize - offset;
    final long shiftedY = element.getY() + shift;
    final long elementY;
    if ( shiftedY < 0 ) {
      if ( ( shiftedY + element.getHeight() ) < 0 ) {
        // The box will not be visible at all. (Should not happen in a sane environment ..)
        return;
      }

      elementY = 0;
    } else {
      elementY = shiftedY;
    }

    final SheetLayoutTableCellDefinition background = computeLegacyBackground( element, shift );
    if ( addLine( element, background, elementY, shiftedY, false, headerSize ) ) {
      return;
    }

    final long elementX = element.getX();
    final long elementRightX = ( element.getWidth() + elementX );
    final long elementBottomY = element.getHeight() + shiftedY;

    ensureXMapping( elementX, Boolean.FALSE );
    if ( elementY >= headerSize ) {
      ensureYMapping( elementY, Boolean.FALSE );
    }
    ensureXMapping( elementRightX, Boolean.FALSE );
    if ( elementBottomY >= headerSize && elementBottomY <= maxHeight ) {
      ensureYMapping( elementBottomY, Boolean.FALSE );
      if ( yMaxBounds < elementBottomY ) {
        yMaxBounds = elementBottomY;
      }
    }
  }

  private boolean addLine( final RenderBox element, final SheetLayoutTableCellDefinition background,
      final long elementY, final long shiftedY, final boolean unmodified, final long headerSize ) {

    // This method handles several special cases. If the element is a non-area box with borderss,
    // it mapps the borders into a equivalent line-definition.
    final long width = element.getWidth();
    final long height = element.getHeight();
    if ( width == 0 && height == 0 ) {
      if ( background != null ) {
        // Elements that define anchors are an exception. We add it ..
        return false;
      }
      // this element will be invisible. We do not add it to the layout; signal that the element has been handled ..
      return true;
    }

    if ( width != 0 && height != 0 ) {
      return false;
    }
    if ( background == null ) {
      return false;
    }
    if ( background.getLineType() == SheetLayoutTableCellDefinition.LINE_HINT_NONE ) {
      return false;
    }
    if ( elementY < headerSize ) {
      return false;
    }

    final long elementX = element.getX();
    final long elementRightX = ( element.getWidth() + elementX );
    final long elementBottomY = element.getHeight() + shiftedY;

    // Begin the mapping ..
    if ( background.getLineType() == SheetLayoutTableCellDefinition.LINE_HINT_HORIZONTAL ) {
      if ( unmodified == false ) {
        ensureXMapping( elementX, Boolean.FALSE );
      }
      ensureXMapping( elementRightX, Boolean.FALSE );

      if ( background.getCoordinate() == 0 ) {
        ensureYMapping( elementY, Boolean.FALSE );
      } else {
        ensureYMapping( elementBottomY, Boolean.FALSE );
      }
    } else {
      ensureYMapping( elementY, Boolean.FALSE );
      ensureYMapping( elementBottomY, Boolean.FALSE );

      if ( background.getCoordinate() == 0 ) {
        if ( unmodified == false ) {
          ensureXMapping( elementX, Boolean.FALSE );
        }
      } else {
        ensureXMapping( elementRightX, Boolean.FALSE );
      }
    }

    // update the collected maximums

    return true;
  }

  public void ensureXMapping( final long coordinate, final Boolean aux ) {
    xBounds.put( coordinate, aux );
    if ( xMaxBounds < coordinate ) {
      xMaxBounds = coordinate;
    }
  }

  public void ensureYMapping( final long coordinate, final Boolean aux ) {
    yBounds.put( coordinate, aux );
    if ( yMaxBounds < coordinate ) {
      yMaxBounds = coordinate;
    }
  }

  /**
   * Gets the strict mode flag.
   *
   * @return true, if strict mode is enabled, false otherwise.
   */
  public boolean isStrict() {
    return strict;
  }

  public boolean isEmpty() {
    return yMaxBounds == 0 && xMaxBounds == 0;
  }

  /**
   * Returns the position of the given element within the table. The TableRectangle contains row and cell indices, no
   * layout coordinates.
   *
   * @param x
   *          the element bounds for which the table bounds should be found.
   * @param y
   *          the element bounds for which the table bounds should be found.
   * @param width
   *          the element bounds for which the table bounds should be found.
   * @param height
   *          the element bounds for which the table bounds should be found.
   * @param rect
   *          the returned rectangle or null, if a new instance should be created
   * @return the filled table rectangle.
   */
  public TableRectangle getTableBounds( final long x, final long y, final long width, final long height,
      TableRectangle rect ) {
    if ( rect == null ) {
      rect = new TableRectangle();
    }
    final int x1 = xBounds.findKeyPosition( x, SheetLayout.LOWER_BOUNDS );
    final int y1 = yBounds.findKeyPosition( y, SheetLayout.LOWER_BOUNDS );
    final int x2 = xBounds.findKeyPosition( x + width, SheetLayout.UPPER_BOUNDS );
    final int y2 = yBounds.findKeyPosition( y + height, SheetLayout.UPPER_BOUNDS );
    rect.setRect( x1, y1, x2, y2 );
    return rect;
  }

  public TableRectangle getTableBoundsWithCache( final long x, final long y, final long width, final long height,
      final TableRectangle rect ) {
    if ( rect == null ) {
      throw new NullPointerException();
    }
    final int x1 = xBounds.findKeyPosition( x, SheetLayout.LOWER_BOUNDS, rect.getX1() );
    final int y1 = yBounds.findKeyPosition( y, SheetLayout.LOWER_BOUNDS, rect.getY1() );
    final int x2 = xBounds.findKeyPosition( x + width, SheetLayout.UPPER_BOUNDS, rect.getX2() );
    final int y2 = yBounds.findKeyPosition( y + height, SheetLayout.UPPER_BOUNDS, rect.getY2() );
    rect.setRect( x1, y1, x2, y2 );
    return rect;
  }

  public int getColSpan( final int x1, final long endPosition ) {
    final int x2 = xBounds.findKeyPosition( endPosition, SheetLayout.UPPER_BOUNDS );
    return x2 - x1;
  }

  public int getRowSpan( final int y1, final long endPosition ) {
    final int y2 = yBounds.findKeyPosition( endPosition, SheetLayout.UPPER_BOUNDS );
    return y2 - y1;
  }

  /**
   * Returns the position of the given element within the table. The TableRectangle contains row and cell indices, no
   * layout coordinates.
   *
   * @param bounds
   *          the element bounds for which the table bounds should be found.
   * @param rect
   *          the returned rectangle or null, if a new instance should be created
   * @return the filled table rectangle.
   */
  public TableRectangle getTableBounds( final StrictBounds bounds, TableRectangle rect ) {
    if ( bounds == null ) {
      throw new NullPointerException();
    }
    if ( rect == null ) {
      rect = new TableRectangle();
    }
    final long xCoord = bounds.getX();
    final long yCoord = bounds.getY();
    final int x1 = xBounds.findKeyPosition( xCoord, SheetLayout.LOWER_BOUNDS );
    final int y1 = yBounds.findKeyPosition( yCoord, SheetLayout.LOWER_BOUNDS );
    final int x2 = xBounds.findKeyPosition( xCoord + bounds.getWidth(), SheetLayout.UPPER_BOUNDS );
    final int y2 = yBounds.findKeyPosition( yCoord + bounds.getHeight(), SheetLayout.UPPER_BOUNDS );
    rect.setRect( x1, y1, x2, y2 );
    return rect;
  }

  /**
   * A Callback method to inform the sheet layout, that the current page is complete, and no more content will be added.
   */
  public void pageCompleted() {
    removeAuxilaryBounds();
  }

  protected void removeAuxilaryBounds() {
    ensureXMapping( this.xMaxBounds, Boolean.FALSE );
    ensureYMapping( this.yMaxBounds, Boolean.FALSE );

    // Log.debug("Size: " + getRowCount() + ", " + getColumnCount());

    final long[] removedXCuts = new long[xBounds.size()];
    final Boolean[] xEntries = xBounds.getRawEntries();
    final int xEntrySize = xBounds.size();
    int arrayIdx = 0;
    for ( int i = 0; i < xEntrySize; i++ ) {
      final Boolean cut = xEntries[i];
      if ( Boolean.TRUE.equals( cut ) ) {
        removedXCuts[arrayIdx] = xBounds.getKeyAt( i );
        arrayIdx += 1;
      }
    }

    xBounds.removeAll( removedXCuts, arrayIdx );

    arrayIdx = 0;
    final long[] removedYCuts = new long[yBounds.size()];
    final Boolean[] yEntries = yBounds.getRawEntries();
    final int ySize = yBounds.size();
    for ( int i = 0; i < ySize; i++ ) {
      final Boolean cut = yEntries[i];
      if ( Boolean.TRUE.equals( cut ) ) {
        removedYCuts[arrayIdx] = yBounds.getKeyAt( i );
        arrayIdx += 1;
      }
    }
    yBounds.removeAll( removedYCuts, arrayIdx );
  }

  /**
   * Computes the height of the given row.
   *
   * @param row
   *          the row, for which the height should be computed.
   * @return the height of the row.
   * @throws IndexOutOfBoundsException
   *           if the row is invalid.
   */
  public long getRowHeight( final int row ) {
    final int rowCount = yBounds.size();
    if ( row >= rowCount ) {
      throw new IndexOutOfBoundsException( "Row " + row + " is invalid. Max valid row is " + ( rowCount - 1 ) );
    }

    final long bottomBorder;
    if ( ( row + 1 ) < rowCount ) {
      bottomBorder = yBounds.getKeyAt( row + 1 );
    } else {
      bottomBorder = yMaxBounds;
    }

    return bottomBorder - yBounds.getKeyAt( row );
  }

  public long getMaxHeight() {
    return yMaxBounds;
  }

  public long getMaxWidth() {
    return xMaxBounds;
  }

  public long getCellWidth( final int startCell ) {
    return getCellWidth( startCell, startCell + 1 );
  }

  /**
   * Computes the height of the given row.
   *
   * @param startCell
   *          the first cell in the range
   * @param endCell
   *          the last cell included in the cell range
   * @return the height of the row.
   * @throws IndexOutOfBoundsException
   *           if the row is invalid.
   */
  public long getCellWidth( final int startCell, final int endCell ) {
    if ( startCell < 0 ) {
      throw new IndexOutOfBoundsException( "Start-Cell must not be negative" );
    }
    if ( endCell < 0 ) {
      throw new IndexOutOfBoundsException( "End-Cell must not be negative" );
    }
    if ( endCell < startCell ) {
      throw new IndexOutOfBoundsException( "End-Cell must not smaller than end-cell" );
    }

    final long rightBorder;
    if ( endCell >= xBounds.size() ) {
      rightBorder = xMaxBounds;
    } else {
      rightBorder = xBounds.getKeyAt( endCell );
    }
    return rightBorder - xBounds.getKeyAt( startCell );
  }

  public long getRowHeight( final int startRow, final int endRow ) {
    if ( startRow < 0 ) {
      throw new IndexOutOfBoundsException( "Start-Cell must not be negative" );
    }
    if ( endRow < 0 ) {
      throw new IndexOutOfBoundsException( "End-Cell must not be negative" );
    }
    if ( endRow < startRow ) {
      throw new IndexOutOfBoundsException( "End-Cell must not smaller than end-cell" );
    }

    final long bottomBorder;
    if ( endRow >= yBounds.size() ) {
      bottomBorder = yMaxBounds;
    } else {
      bottomBorder = yBounds.getKeyAt( endRow );
    }
    return bottomBorder - yBounds.getKeyAt( startRow );
  }

  /**
   * The current number of columns. Of course, this value begins to be reliable, once the number of columns is known
   * (that is at the end of the layouting process).
   *
   * @return the number columns.
   */
  public int getColumnCount() {
    return Math.max( xBounds.size() - 1, 0 );
  }

  /**
   * The current number of rows. Of course, this value begins to be reliable, once the number of rows is known (that is
   * at the end of the layouting process).
   *
   * @return the number columns.
   */
  public int getRowCount() {
    return Math.max( yBounds.size() - 1, 0 );
  }

  public long getXPosition( final int col ) {
    return xBounds.getKeyAt( col );
  }

  public long getYPosition( final int row ) {
    return yBounds.getKeyAt( row );
  }

  public void clear() {
    xBounds.clear();
    xMaxBounds = 0;

    yBounds.clear();
    yMaxBounds = 0;
  }

  public void clearVerticalInfo() {
    yBounds.clear();
    yMaxBounds = 0;
  }
}
