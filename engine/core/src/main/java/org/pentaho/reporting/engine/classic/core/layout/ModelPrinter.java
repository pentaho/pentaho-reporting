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

package org.pentaho.reporting.engine.classic.core.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.layout.model.BreakMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;

@SuppressWarnings( "HardCodedStringLiteral" )
public class ModelPrinter {
  public static final ModelPrinter INSTANCE = new ModelPrinter();
  private static final Log logger = LogFactory.getLog( ModelPrinter.class );
  private static final boolean PRINT_LINEBOX_CONTENTS = false;
  private static final boolean PRINT_TABLE_CELL_CONTENTS = true;

  public ModelPrinter() {
  }

  public static RenderBox getRoot( final RenderNode node ) {
    RenderBox parent = node.getParent();
    RenderBox retval = node.getParent();
    while ( parent != null ) {
      retval = parent;
      parent = parent.getParent();
    }
    return retval;
  }

  protected void print( final String s ) {
    logger.debug( s );
  }

  @SuppressWarnings( "UnusedDeclaration" )
  public void printParents( RenderNode box ) {
    int level = 0;
    while ( box != null ) {
      if ( box instanceof RenderBox ) {
        printBoxDetails( (RenderBox) box, level );
      } else {
        printNode( box, level );
      }
      level += 1;

      box = box.getParent();
    }
  }

  public void print( final RenderNode box ) {
    if ( !isPrintingEnabled() ) {
      return;
    }

    if ( box instanceof RenderBox ) {
      printBox( (RenderBox) box, 0 );
    } else {
      printNode( box, 0 );
    }
  }

  protected boolean isPrintingEnabled() {
    return logger.isDebugEnabled();
  }

  public void print( final RenderBox box ) {
    if ( !isPrintingEnabled() ) {
      return;
    }
    printBox( box, 0 );
  }

  protected void printBox( final RenderBox box, final int level ) {
    printBoxDetails( box, level );

    final StringBuilder b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    print( b.toString() );

    if ( box instanceof ParagraphRenderBox ) {
      if ( PRINT_LINEBOX_CONTENTS ) {
        final ParagraphRenderBox paraBox = (ParagraphRenderBox) box;
        print( "----------------  START PARAGRAPH POOL CONTAINER -------------------------------------" );
        printBox( paraBox.getPool(), level + 1 );
        print( "---------------- FINISH PARAGRAPH POOL CONTAINER -------------------------------------" );

        if ( paraBox.isComplexParagraph() ) {
          print( "----------------  START PARAGRAPH LINEBOX CONTAINER -------------------------------------" );
          printBox( paraBox.getLineboxContainer(), level + 1 );
          print( "---------------- FINISH PARAGRAPH LINEBOX CONTAINER -------------------------------------" );
        }
      }
    }

    if ( isPrintPageHeader() && box instanceof LogicalPageBox ) {
      final LogicalPageBox lbox = (LogicalPageBox) box;
      printBox( lbox.getHeaderArea(), level + 1 );
      printBox( lbox.getWatermarkArea(), level + 1 );
    }
    printChilds( box, level );
    if ( isPrintPageFooter() && box instanceof LogicalPageBox ) {
      final LogicalPageBox lbox = (LogicalPageBox) box;
      printBox( lbox.getRepeatFooterArea(), level + 1 );
      printBox( lbox.getFooterArea(), level + 1 );
    }
  }

  protected boolean isPrintPageHeader() {
    return true;
  }

  protected boolean isPrintPageFooter() {
    return true;
  }

  private void printBoxDetails( final RenderBox box, final int level ) {
    StringBuilder b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( box.getClass().getName() );
    b.append( '[' );
    b.append( box.getElementType().getClass().getName() );
    // b.append(Integer.toHexString(System.identityHashCode(box)));
    b.append( ';' );
    b.append( box.getName() );
    b.append( ']' );
    b.append( "={stateKey=" );
    b.append( box.getStateKey() );
    b.append( ", pinned=" );
    b.append( box.getPinned() );
    b.append( '}' );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- layout x=" );
    b.append( box.getX() );
    b.append( ", y=" );
    b.append( box.getY() );
    b.append( ", width=" );
    b.append( box.getWidth() );
    b.append( ", height=" );
    b.append( box.getHeight() );
    b.append( ", min-chunk-width=" );
    b.append( box.getMinimumChunkWidth() );
    b.append( ", x2=" );
    b.append( box.getX() + box.getWidth() );
    b.append( ", y2=" );
    b.append( box.getY2() );
    b.append( ", y2-overflow=" );
    b.append( box.getY() + box.getOverflowAreaHeight() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- cached-layout cached-x=" );
    b.append( box.getCachedX() );
    b.append( ", cached-y=" );
    b.append( box.getCachedY() );
    b.append( ", cached-width=" );
    b.append( box.getCachedWidth() );
    b.append( ", cached-height=" );
    b.append( box.getCachedHeight() );
    b.append( ", content-area-x1=" );
    b.append( box.getContentAreaX1() );
    b.append( ", content-area-x2=" );
    b.append( box.getContentAreaX2() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- widow-size=" );
    b.append( box.getWidowConstraintSize() );
    b.append( "- widow-size-with-keep-together=" );
    b.append( box.getWidowConstraintSizeWithKeepTogether() );
    b.append( ", orphan-size=" );
    b.append( box.getOrphanConstraintSize() );
    b.append( ", widows=" );
    b.append( box.getStaticBoxLayoutProperties().getWidows() );
    b.append( ", orphans=" );
    b.append( box.getStaticBoxLayoutProperties().getOrphans() );
    b.append( ", keep-together=" );
    b.append( box.getStaticBoxLayoutProperties().isAvoidPagebreakInside() );
    b.append( ", widow-orphan-opt-out=" );
    b.append( box.getStaticBoxLayoutProperties().isWidowOrphanOptOut() );
    b.append( ", widows-box=" );
    b.append( box.isWidowBox() );
    b.append( ", orphan-restrict-finish=" );
    b.append( box.getRestrictFinishedClearOut() );
    b.append( ", invalid-widow-orphan-node=" );
    b.append( box.isInvalidWidowOrphanNode() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- boxDefinition=" );
    b.append( box.getBoxDefinition() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- nodeLayoutProperties=" );
    b.append( box.getNodeLayoutProperties() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- staticBoxLayoutProperties=" );
    b.append( box.getStaticBoxLayoutProperties() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    print( b.toString() );

    if ( box instanceof LogicalPageBox ) {
      final LogicalPageBox pageBox = (LogicalPageBox) box;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- PageBox={PageOffset=" );
      b.append( pageBox.getPageOffset() );
      b.append( ", PageHeight=" );
      b.append( pageBox.getPageHeight() );
      b.append( ", PageEnd=" );
      b.append( pageBox.getPageEnd() );
      b.append( ", PageWidth=" );
      b.append( pageBox.getPageWidth() );
      b.append( '}' );
      print( b.toString() );

      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- PageBreaks={" );
      b.append( pageBox.getAllVerticalBreaks() );
      b.append( '}' );
      print( b.toString() );
    }

    if ( box instanceof TableRenderBox ) {
      final TableRenderBox pageBox = (TableRenderBox) box;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- Layout: " );
      Object styleProperty = pageBox.getStyleSheet().getStyleProperty( BandStyleKeys.TABLE_LAYOUT );
      if ( TableLayout.auto.equals( styleProperty ) ) {
        b.append( TableLayout.auto );
      } else {
        b.append( TableLayout.fixed );
      }
      print( b.toString() );
    }

    if ( box instanceof TableSectionRenderBox ) {
      final TableSectionRenderBox pageBox = (TableSectionRenderBox) box;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- Role: " );
      b.append( pageBox.getDisplayRole() );
      print( b.toString() );
    }

    if ( box instanceof TableRowRenderBox ) {
      final TableRowRenderBox pageBox = (TableRowRenderBox) box;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- Row: " );
      b.append( pageBox.getRowIndex() );
      print( b.toString() );
    }

    if ( box instanceof TableCellRenderBox ) {
      final TableCellRenderBox pageBox = (TableCellRenderBox) box;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- Column-Index=" );
      b.append( pageBox.getColumnIndex() );
      b.append( ", ColSpan=" );
      b.append( pageBox.getColSpan() );
      b.append( ", RowSpan=" );
      b.append( pageBox.getRowSpan() );
      print( b.toString() );
    }

    if ( box instanceof BreakMarkerRenderBox ) {
      final BreakMarkerRenderBox pageBox = (BreakMarkerRenderBox) box;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- InstanceID=" );
      b.append( pageBox.getInstanceId() );
      b.append( ", validity-range=" );
      b.append( pageBox.getValidityRange() );
      print( b.toString() );
    }

    if ( box.isOpen() ) {
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- WARNING: THIS BOX IS STILL OPEN" );
      print( b.toString() );
    }

    if ( box.isFinishedTable() || box.isFinishedPaginate() ) {
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- INFO: THIS BOX IS FINISHED: " );
      if ( box.isFinishedTable() ) {
        b.append( "- TABLE " );
      }
      if ( box.isFinishedPaginate() ) {
        b.append( "- PAGE " );
      }
      print( b.toString() );
    }
    if ( box.isCommited() ) {
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- INFO: THIS BOX IS COMMITED" );
      print( b.toString() );
    }
  }

  private void printChilds( final RenderBox box, final int level ) {
    if ( PRINT_TABLE_CELL_CONTENTS == false && box instanceof TableCellRenderBox ) {
      return;
    }

    RenderNode childs = box.getFirstChild();
    while ( childs != null ) {
      if ( childs instanceof RenderBox ) {
        printBox( (RenderBox) childs, level + 1 );
      } else {
        printNode( childs, level + 1 );
      }
      childs = childs.getNext();
    }
  }

  private void printNode( final RenderNode node, final int level ) {
    StringBuilder b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( node.getClass().getName() );
    b.append( '[' );
    // b.append(Integer.toHexString(System.identityHashCode(node)));
    b.append( ']' );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- layout x=" );
    b.append( node.getX() );
    b.append( ", y=" );
    b.append( node.getY() );
    b.append( ", width=" );
    b.append( node.getWidth() );
    b.append( ", height=" );
    b.append( node.getHeight() );
    b.append( ", min-chunk-width=" );
    b.append( node.getMinimumChunkWidth() );
    b.append( ", x2=" );
    b.append( node.getX() + node.getWidth() );
    b.append( ", y2=" );
    b.append( node.getY() + node.getHeight() );
    print( b.toString() );

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- cached-layout cached-x=" );
    b.append( node.getCachedX() );
    b.append( ", cached-y=" );
    b.append( node.getCachedY() );
    b.append( ", cached-width=" );
    b.append( node.getCachedWidth() );
    b.append( ", cached-height=" );
    b.append( node.getCachedHeight() );
    print( b.toString() );

    if ( node instanceof FinishedRenderNode ) {
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      final FinishedRenderNode fn = (FinishedRenderNode) node;
      b.append( "layouted-y=" );
      b.append( fn.getLayoutedY() );
      b.append( ", layouted-width=" );
      b.append( fn.getLayoutedWidth() );
      b.append( ", layouted-height=" );
      b.append( fn.getLayoutedHeight() );
      b.append( ", orphan-leaf=" );
      b.append( fn.isOrphanLeaf() );
      print( b.toString() );
    }

    if ( node instanceof RenderableText ) {
      final RenderableText text = (RenderableText) node;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- text='" );
      b.append( text.getRawText() );
      b.append( "'" );
      print( b.toString() );
    }

    if ( node instanceof RenderableComplexText ) {
      final RenderableComplexText text = (RenderableComplexText) node;
      b = new StringBuilder();
      for ( int i = 0; i < level; i++ ) {
        b.append( "   " );
      }
      b.append( "- complex-text='" );
      b.append( text.getRawText() );
      b.append( "'" );
      print( b.toString() );
    }

    b = new StringBuilder();
    for ( int i = 0; i < level; i++ ) {
      b.append( "   " );
    }
    b.append( "- nodeLayoutProperties=" );
    b.append( node.getNodeLayoutProperties() );
    print( b.toString() );
    print( " " );
  }
}
