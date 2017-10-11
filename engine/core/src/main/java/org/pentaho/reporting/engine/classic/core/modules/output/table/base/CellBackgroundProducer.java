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

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.ProcessUtility;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.ShapeDrawable;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class CellBackgroundProducer extends IterateStructuralProcessStep {
  private static final int BACKGROUND_NONE = 0;
  private static final int BACKGROUND_AREA = 1;
  private static final int BACKGROUND_TOP = 2;
  private static final int BACKGROUND_LEFT = 4;
  private static final int BACKGROUND_BOTTOM = 8;
  private static final int BACKGROUND_RIGHT = 16;

  private TableRectangle lookupRectangle;
  private int gridX;
  private int gridY;
  private boolean collectAttributes;
  private SheetLayout sheetLayout;
  private int gridX2;
  private int gridY2;
  private CellBackground retval;
  private long resolvedX;
  private long resolvedY;
  private boolean ellipseAsRectangle;
  private boolean unalignedPagebands;
  private long contentShift;
  private FastStack<RenderBox> parents;
  private boolean fastCellBackgroundProducerMode;

  public CellBackgroundProducer( final boolean ellipseAsRectangle, final boolean unalignedPagebands ) {
    this.ellipseAsRectangle = ellipseAsRectangle;
    this.unalignedPagebands = unalignedPagebands;
    this.lookupRectangle = new TableRectangle();
    this.fastCellBackgroundProducerMode =
        "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.base.FastCellBackgroundProducer" ) );
  }

  public CellBackground getBackgroundAt( final LogicalPageBox pageBox, final SheetLayout sheetLayout, final int gridX,
      final int gridY, final boolean computeAttributes, final CellMarker.SectionType sectionType ) {
    return computeBackground( pageBox, sheetLayout, gridX, gridY, 1, 1, computeAttributes, sectionType );
  }

  private CellBackground computeBackground( final LogicalPageBox logicalPageBox, final SheetLayout sheetLayout,
      final int gridX, final int gridY, final int gridWidth, final int gridHeight, final boolean collectAttributes,
      final CellMarker.SectionType sectionType ) {
    if ( logicalPageBox == null ) {
      throw new NullPointerException();
    }
    if ( sheetLayout == null ) {
      throw new NullPointerException();
    }

    this.sheetLayout = sheetLayout;
    this.collectAttributes = collectAttributes;
    this.retval = null;

    initFromPosition( gridX, gridY, gridWidth, gridHeight );
    final BlockRenderBox headerArea = logicalPageBox.getHeaderArea();
    if ( unalignedPagebands == false ) {
      contentShift = 0;
      startProcessing( headerArea );
      startProcessing( logicalPageBox );
      startProcessing( logicalPageBox.getRepeatFooterArea() );
      startProcessing( logicalPageBox.getFooterArea() );
      return retval;
    }

    switch ( sectionType ) {
      case TYPE_HEADER: {
        contentShift = 0;
        startProcessing( headerArea );
        return retval;
      }
      case TYPE_NORMALFLOW: {
        final BlockRenderBox contentArea = logicalPageBox.getContentArea();
        final long contentStart = headerArea.getHeight() + contentArea.getY();
        contentShift = contentStart - logicalPageBox.getPageOffset();
        startProcessing( contentArea );
        return retval;
      }
      case TYPE_REPEAT_FOOTER: {
        final BlockRenderBox contentArea = logicalPageBox.getContentArea();
        final long contentStart = headerArea.getHeight() + contentArea.getY();
        final BlockRenderBox footerArea = logicalPageBox.getRepeatFooterArea();
        contentShift = contentStart + contentArea.getHeight();
        startProcessing( footerArea );
        return retval;
      }
      case TYPE_FOOTER: {
        final BlockRenderBox contentArea = logicalPageBox.getContentArea();
        final long contentStart = headerArea.getHeight() + contentArea.getY();
        final BlockRenderBox repeatFooterArea = logicalPageBox.getRepeatFooterArea();
        final BlockRenderBox footerArea = logicalPageBox.getFooterArea();
        contentShift = contentStart + contentArea.getHeight() + repeatFooterArea.getHeight();
        startProcessing( footerArea );
        return retval;
      }
      case TYPE_INVALID:
        return null;
      default: {
        throw new IllegalStateException();
      }
    }
  }

  public CellBackground getBackgroundForBox( final LogicalPageBox logicalPageBox, final SheetLayout sheetLayout,
      final int gridX, final int gridY, final int gridWidth, final int gridHeight, final boolean collectAttributes,
      final CellMarker.SectionType sectionType, final RenderBox contentBox ) {
    if ( fastCellBackgroundProducerMode == false ) {
      return computeBackground( logicalPageBox, sheetLayout, gridX, gridY, gridWidth, gridHeight, collectAttributes,
          sectionType );
    }
    if ( logicalPageBox == null ) {
      throw new NullPointerException();
    }
    if ( sheetLayout == null ) {
      throw new NullPointerException();
    }

    this.sheetLayout = sheetLayout;
    this.collectAttributes = collectAttributes;
    this.retval = null;

    initFromPosition( gridX, gridY, gridWidth, gridHeight );
    final BlockRenderBox headerArea = logicalPageBox.getHeaderArea();
    if ( unalignedPagebands == false ) {
      contentShift = 0;
    } else {
      switch ( sectionType ) {
        case TYPE_HEADER: {
          contentShift = 0;
          break;
        }
        case TYPE_NORMALFLOW: {
          final BlockRenderBox contentArea = logicalPageBox.getContentArea();
          final long contentStart = headerArea.getHeight() + contentArea.getY();
          contentShift = contentStart - logicalPageBox.getPageOffset();
          break;
        }
        case TYPE_REPEAT_FOOTER: {
          final BlockRenderBox contentArea = logicalPageBox.getContentArea();
          final long contentStart = headerArea.getHeight() + contentArea.getY();
          contentShift = contentStart + contentArea.getHeight();
          break;
        }
        case TYPE_FOOTER: {
          final BlockRenderBox contentArea = logicalPageBox.getContentArea();
          final long contentStart = headerArea.getHeight() + contentArea.getY();
          final BlockRenderBox repeatFooterArea = logicalPageBox.getRepeatFooterArea();
          contentShift = contentStart + contentArea.getHeight() + repeatFooterArea.getHeight();
          break;
        }
        case TYPE_INVALID:
          return null;
        default: {
          throw new IllegalStateException();
        }
      }
    }

    if ( parents == null ) {
      parents = new FastStack<RenderBox>();
    } else {
      parents.clear();
    }

    RenderBox p = contentBox;
    boolean seenSectionBox = false;
    RenderBox lastProcessed = null;

    while ( p != null ) {
      if ( p.getStaticBoxLayoutProperties().isSectionContext() ) {
        seenSectionBox = true;
      }
      if ( seenSectionBox ) {
        parents.push( p );
      } else {
        lastProcessed = p;
      }
      p = p.getParent();
    }

    boolean receivedNoBackground = false;
    while ( parents.isEmpty() == false ) {
      final RenderBox p2 = parents.pop();
      if ( startBox( p2 ) == false ) {
        receivedNoBackground = true;
        break;
      }
    }

    if ( receivedNoBackground == false && lastProcessed != null ) {
      startProcessing( lastProcessed );
    }
    return retval;
  }

  private void initFromPosition( final int gridX, final int gridY, final int gridWidth, final int gridHeight ) {
    this.resolvedX = sheetLayout.getXPosition( gridX );
    this.resolvedY = sheetLayout.getYPosition( gridY );
    this.gridX = gridX;
    this.gridY = gridY;
    this.gridX2 = gridX + gridWidth;
    this.gridY2 = gridY + gridHeight;
  }

  private int computeBackground( final RenderBox node ) {
    final Object state = node.getTableExportState();
    final TableExportRenderBoxState renderBoxState;
    if ( state instanceof TableExportRenderBoxState ) {
      renderBoxState = (TableExportRenderBoxState) state;
    } else {
      renderBoxState = new TableExportRenderBoxState();
      node.setTableExportState( renderBoxState );
    }
    TableRectangle hint = renderBoxState.getCellBackgroundHint();
    if ( hint == null ) {
      hint =
          sheetLayout.getTableBoundsWithCache( node.getX(), node.getY() + contentShift, node.getWidth(), node
              .getHeight(), new TableRectangle( -1, -1, -1, -1 ) );
    } else {
      if ( renderBoxState.getBackgroundDefinitionAge() != node.getCachedAge() ) {
        hint =
            sheetLayout.getTableBoundsWithCache( node.getX(), node.getY() + contentShift, node.getWidth(), node
                .getHeight(), hint );
      }
    }

    final int x1 = hint.getX1();
    final int y1 = hint.getY1();
    final int x2 = hint.getX2();
    final int y2 = hint.getY2();
    final int retval = computeBackgroundHint( x1, y1, x2, y2 );
    renderBoxState.setCellBackgroundHint( hint, node.getCachedAge() );
    return retval;
  }

  private int computeBackground( final long x, final long y, final long width, final long height ) {
    lookupRectangle = sheetLayout.getTableBounds( x, y + contentShift, width, height, lookupRectangle );
    final int x1 = lookupRectangle.getX1();
    final int y1 = lookupRectangle.getY1();
    final int x2 = lookupRectangle.getX2();
    final int y2 = lookupRectangle.getY2();
    return computeBackgroundHint( x1, y1, x2, y2 );
  }

  private int computeBackgroundHint( final int x1, final int y1, final int x2, final int y2 ) {
    // a node is a background if it fully covers the given grid-area. A node that only covers the area
    // partially is not a background.
    // for lines: At least the full width or full height must be covered.
    // for background colors: The full area must be covered.
    int retval = BACKGROUND_NONE;
    if ( x1 <= gridX && x2 >= gridX2 ) {
      // covers full width ..
      // could be a horizontal line or a full area background.

      if ( y1 <= gridY && y2 >= gridY2 ) {
        // full area background
        retval |= BACKGROUND_AREA;
      }

      if ( y1 == gridY ) {
        retval |= BACKGROUND_TOP;
      }
      if ( y2 == gridY2 ) {
        retval |= BACKGROUND_BOTTOM;
      }
    }
    if ( y1 <= gridY && y2 >= gridY2 ) {
      if ( x1 == gridX ) {
        retval |= BACKGROUND_LEFT;
      }
      if ( x2 == gridX2 ) {
        retval |= BACKGROUND_RIGHT;
      }
    }

    return retval;
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    final int backgroundHint = computeBackground( box );
    if ( backgroundHint == BACKGROUND_NONE ) {
      return;
    }
    retval = applyBorder( box, retval, backgroundHint );
    if ( ( backgroundHint & BACKGROUND_AREA ) == BACKGROUND_AREA ) {
      retval = applyBackground( box, retval );
      retval = applyAnchor( box, contentShift, resolvedX, resolvedY, retval );
      retval = applyElementType( box, contentShift, resolvedX, resolvedY, retval );
      if ( collectAttributes ) {
        retval = applyAttributes( box, contentShift, resolvedX, resolvedY, retval );
      }
    }

    computeLegacyBackground( box );
  }

  private void computeLegacyBackground( final RenderableReplacedContentBox node ) {
    final BoxDefinition sblp = node.getBoxDefinition();
    final long nodeX = node.getX() + sblp.getPaddingLeft();
    final long nodeY = node.getY() + sblp.getPaddingTop();
    final long nodeWidth = node.getWidth() - sblp.getPaddingLeft() - sblp.getPaddingRight();
    final long nodeHeight = node.getHeight() - sblp.getPaddingTop() - sblp.getPaddingBottom();
    final int backgroundHint = computeBackground( nodeX, nodeY, nodeWidth, nodeHeight );
    if ( backgroundHint == BACKGROUND_NONE ) {
      return;
    }

    final RenderableReplacedContent rpc = node.getContent();
    final Object rawContentObject = rpc.getRawObject();
    if ( rawContentObject instanceof DrawableWrapper == false ) {
      return;
    }
    final DrawableWrapper wrapper = (DrawableWrapper) rawContentObject;
    final Object rawbackend = wrapper.getBackend();
    if ( rawbackend instanceof ShapeDrawable == false ) {
      return;
    }
    final ShapeDrawable drawable = (ShapeDrawable) rawbackend;
    final Shape shape = drawable.getShape();

    final StyleSheet styleSheet = node.getStyleSheet();
    final boolean draw = styleSheet.getBooleanStyleProperty( ElementStyleKeys.DRAW_SHAPE );
    if ( draw && shape instanceof Line2D ) {
      final Line2D line = (Line2D) shape;

      final boolean vertical = line.getX1() == line.getX2();
      final boolean horizontal = line.getY1() == line.getY2();
      if ( vertical && horizontal ) {
        // not a valid line ..
        return;
      }
      if ( vertical == false && horizontal == false ) {
        // not a valid line ..
        return;
      }
      if ( retval == null ) {
        retval = new CellBackground();
      }
      final BorderEdge edge = ProcessUtility.produceBorderEdge( styleSheet );
      if ( edge == null ) {
        return;
      }
      if ( vertical ) {
        if ( line.getX1() == 0 ) {
          if ( ( backgroundHint & BACKGROUND_LEFT ) == BACKGROUND_LEFT ) {
            retval.setLeft( edge );
          } else if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
            final RenderBox nodeParent = node.getParent();
            if ( nodeParent != null && ( nodeParent.getX() + nodeParent.getWidth() ) == ( nodeX + nodeWidth ) ) {
              retval.setRight( edge );
            }
          }
        } else {
          if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
            retval.setRight( edge );
          }
        }
      } else {
        if ( line.getY1() == 0 ) {
          if ( ( backgroundHint & BACKGROUND_TOP ) == BACKGROUND_TOP ) {
            retval.setTop( edge );
          } else if ( ( backgroundHint & BACKGROUND_BOTTOM ) == BACKGROUND_BOTTOM ) {
            final RenderBox nodeParent = node.getParent();
            if ( nodeParent != null && ( nodeParent.getY() + nodeParent.getHeight() ) == ( nodeY + nodeHeight ) ) {
              retval.setBottom( edge );
            }
          }
        } else {
          if ( ( backgroundHint & BACKGROUND_BOTTOM ) == BACKGROUND_BOTTOM ) {
            retval.setBottom( edge );
          }
        }
      }
      return;
    }

    final boolean fill = styleSheet.getBooleanStyleProperty( ElementStyleKeys.FILL_SHAPE );
    if ( draw == false && fill == false ) {
      return;
    }

    if ( shape instanceof Rectangle2D || ( ellipseAsRectangle && shape instanceof Ellipse2D ) ) {
      if ( retval == null ) {
        retval = new CellBackground();
      }

      if ( draw ) {
        // the beast has a border ..
        final BorderEdge edge = ProcessUtility.produceBorderEdge( styleSheet );
        if ( edge != null ) {
          if ( ( backgroundHint & BACKGROUND_TOP ) == BACKGROUND_TOP ) {
            retval.setTop( edge );
          }
          if ( ( backgroundHint & BACKGROUND_LEFT ) == BACKGROUND_LEFT ) {
            retval.setLeft( edge );
          }
          if ( ( backgroundHint & BACKGROUND_BOTTOM ) == BACKGROUND_BOTTOM ) {
            retval.setBottom( edge );
          }
          if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
            retval.setRight( edge );
          }
        }
      }
      if ( fill && ( ( backgroundHint & BACKGROUND_AREA ) == BACKGROUND_AREA ) ) {

        final Color color = (Color) styleSheet.getStyleProperty( ElementStyleKeys.FILL_COLOR );
        if ( color != null ) {
          retval.addBackground( color );
        } else {
          retval.addBackground( (Color) styleSheet.getStyleProperty( ElementStyleKeys.PAINT ) );
        }
      }
      return;
    }

    if ( shape instanceof RoundRectangle2D ) {
      final RoundRectangle2D rr = (RoundRectangle2D) shape;
      if ( retval == null ) {
        retval = new CellBackground();
      }

      if ( draw ) {
        // the beast has a border ..
        final BorderEdge edge = ProcessUtility.produceBorderEdge( styleSheet );
        if ( edge != null ) {
          if ( ( backgroundHint & BACKGROUND_TOP ) == BACKGROUND_TOP ) {
            retval.setTop( edge );
          }
          if ( ( backgroundHint & BACKGROUND_LEFT ) == BACKGROUND_LEFT ) {
            retval.setLeft( edge );
          }
          if ( ( backgroundHint & BACKGROUND_BOTTOM ) == BACKGROUND_BOTTOM ) {
            retval.setBottom( edge );
          }
          if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
            retval.setRight( edge );
          }
        }
      }
      if ( fill && ( ( backgroundHint & BACKGROUND_AREA ) == BACKGROUND_AREA ) ) {
        final Color color = (Color) styleSheet.getStyleProperty( ElementStyleKeys.FILL_COLOR );
        if ( color != null ) {
          retval.addBackground( color );
        } else {
          retval.addBackground( (Color) styleSheet.getStyleProperty( ElementStyleKeys.PAINT ) );
        }
      }

      final long arcHeight = StrictGeomUtility.toInternalValue( rr.getArcHeight() );
      final long arcWidth = StrictGeomUtility.toInternalValue( rr.getArcWidth() );
      if ( arcHeight > 0 && arcWidth > 0 ) {
        final BorderCorner bc = new BorderCorner( arcWidth, arcHeight );
        if ( ( backgroundHint & BACKGROUND_TOP ) == BACKGROUND_TOP ) {
          if ( ( backgroundHint & BACKGROUND_LEFT ) == BACKGROUND_LEFT ) {
            retval.setTopLeft( bc );
          }
          if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
            retval.setTopRight( bc );
          }
        }
        if ( ( backgroundHint & BACKGROUND_BOTTOM ) == BACKGROUND_BOTTOM ) {
          if ( ( backgroundHint & BACKGROUND_LEFT ) == BACKGROUND_LEFT ) {
            retval.setBottomLeft( bc );
          }
          if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
            retval.setBottomRight( bc );
          }
        }
      }
    }
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return startBox( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return startBox( box );
  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    return startBox( box );
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return startBox( box );
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    return startBox( box );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    return startBox( box );
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    return startBox( box );
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    return true;
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    return startBox( box );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return true;
  }

  private boolean startBox( final RenderBox box ) {
    final int backgroundHint = computeBackground( box );
    if ( backgroundHint == BACKGROUND_NONE ) {
      return false;
    }

    retval = applyBorder( box, retval, backgroundHint );
    if ( ( backgroundHint & BACKGROUND_AREA ) == BACKGROUND_AREA ) {
      retval = applyBackground( box, retval );
      retval = applyAnchor( box, contentShift, resolvedX, resolvedY, retval );
      retval = applyElementType( box, contentShift, resolvedX, resolvedY, retval );
      if ( collectAttributes ) {
        retval = applyAttributes( box, contentShift, resolvedX, resolvedY, retval );
      }
      return true;
    }
    return true;
  }

  protected boolean startRowBox( final RenderBox box ) {
    return startBox( box );
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    // no need for that. Paragraph contents cannot form a cell-background.
  }

  private static CellBackground applyAnchor( final RenderBox content, final long contentShift, final long x,
      final long y, CellBackground retval ) {
    if ( content.getY() + contentShift != y || content.getX() != x ) {
      return retval;
    }

    final String anchor = (String) content.getStyleSheet().getStyleProperty( ElementStyleKeys.ANCHOR_NAME );
    if ( anchor != null ) {
      if ( retval == null ) {
        retval = new CellBackground();
      }
      retval.addAnchor( anchor );
    }
    return retval;
  }

  private static CellBackground applyElementType( final RenderBox content, final long contentShift, final long x,
      final long y, CellBackground retval ) {
    if ( content.getY() + contentShift != y || content.getX() != x ) {
      return retval;
    }

    final ElementType anchor = content.getElementType();
    if ( anchor != null ) {
      if ( retval == null ) {
        retval = new CellBackground();
      }
      retval.addElementType( anchor );
    }
    return retval;
  }

  private static CellBackground applyAttributes( final RenderBox content, final long contentShift, final long x,
      final long y, CellBackground retval ) {
    if ( content.getY() + contentShift != y || content.getX() != x ) {
      return retval;
    }

    if ( retval == null ) {
      retval = new CellBackground();
    }
    retval.addAttributes( content.getAttributes() );
    return retval;
  }

  private static CellBackground applyBorder( final RenderBox content, CellBackground retval, final int backgroundHint ) {
    final Border border = content.getBoxDefinition().getBorder();
    if ( border.isEmpty() ) {
      return retval;
    }

    if ( ( backgroundHint & BACKGROUND_TOP ) == BACKGROUND_TOP ) {
      final BorderEdge borderEdgeTop = border.getTop();
      if ( borderEdgeTop.isEmpty() == false ) {
        if ( retval == null ) {
          retval = new CellBackground();
        }
        retval.setTop( borderEdgeTop );
      }
    }

    if ( ( backgroundHint & BACKGROUND_LEFT ) == BACKGROUND_LEFT ) {
      final BorderEdge borderEdgeLeft = border.getLeft();
      if ( borderEdgeLeft.isEmpty() == false ) {
        if ( retval == null ) {
          retval = new CellBackground();
        }
        retval.setLeft( borderEdgeLeft );
      }
    }

    if ( ( backgroundHint & BACKGROUND_BOTTOM ) == BACKGROUND_BOTTOM ) {
      final BorderEdge borderEdgeBottom = border.getBottom();
      if ( borderEdgeBottom.isEmpty() == false ) {
        if ( retval == null ) {
          retval = new CellBackground();
        }
        retval.setBottom( borderEdgeBottom );
      }
    }

    if ( ( backgroundHint & BACKGROUND_RIGHT ) == BACKGROUND_RIGHT ) {
      final BorderEdge borderEdgeRight = border.getRight();
      if ( borderEdgeRight.isEmpty() == false ) {
        if ( retval == null ) {
          retval = new CellBackground();
        }
        retval.setRight( borderEdgeRight );
      }
    }
    return retval;
  }

  private static CellBackground applyBackground( final RenderBox content, CellBackground retval ) {
    final Color backgroundColor = (Color) content.getStyleSheet().getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    if ( backgroundColor != null && backgroundColor.getAlpha() > 0 ) {
      if ( retval == null ) {
        retval = new CellBackground();
      }
      retval.addBackground( backgroundColor );
    }
    return retval;
  }
}
