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

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.process.linebreak.EmptyLinebreaker;
import org.pentaho.reporting.engine.classic.core.layout.process.linebreak.FullLinebreaker;
import org.pentaho.reporting.engine.classic.core.layout.process.linebreak.ParagraphLinebreaker;
import org.pentaho.reporting.engine.classic.core.layout.process.linebreak.SimpleLinebreaker;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * This static computation step performs manual linebreaks on all paragraphs. This transforms the pool-collection into
 * the lines-collection.
 * <p/>
 * For now, we follow a very simple path: A paragraph cannot be validated, if it is not yet closed. The linebreaking, be
 * it the static one here or the dynamic one later, must be redone when the paragraph changes.
 * <p/>
 * Splitting for linebreaks happens only between inline-boxes. BlockBoxes that are contained in inline-boxes (like
 * 'inline-block' elements or 'inline-tables') are considered unbreakable according to the CSS specs. Linebreaking can
 * be suspended in these cases.
 * <p/>
 * As paragraphs itself are block elements, the linebreaks can be done iterative, using a simple stack to store the
 * context of possibly nested paragraphs. The paragraph's pool contains the elements that should be processed, and the
 * line-container will receive the pool's content (contained in an artificial inline element, as the linecontainer is a
 * block-level element).
 * <p/>
 * Change-tracking should take place on the paragraph's pool element instead of the paragraph itself. This way, only
 * structural changes are taken into account.
 *
 * @author Thomas Morgner
 */
public final class ParagraphLineBreakStep extends IterateStructuralProcessStep {
  private static final EmptyLinebreaker LEAF_BREAK_STATE = new EmptyLinebreaker();

  private FastStack<ParagraphLinebreaker> paragraphNesting;
  private ParagraphLinebreaker breakState;
  private SimpleLinebreaker reusableSimpleLinebreaker;

  public ParagraphLineBreakStep() {
    paragraphNesting = new FastStack<ParagraphLinebreaker>( 50 );
  }

  public void compute( final LogicalPageBox root ) {
    paragraphNesting.clear();
    try {
      startProcessing( root );
    } finally {
      paragraphNesting.clear();
      breakState = null;
    }
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    super.processParagraphChilds( box );
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    if ( breakState != null ) {
      breakState.addNode( box );
    }
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
      final long poolChangeTracker = paragraphBox.getPool().getChangeTracker();
      final boolean unchanged = poolChangeTracker == paragraphBox.getLineBoxAge();

      if ( unchanged || paragraphBox.getPool().getFirstChild() == null ) {
        // If the paragraph is unchanged (no new elements have been added to the pool) then we can take a
        // shortcut. The childs of this paragraph will also be unchanged (as any structural change would have increased
        // the change-tracker).
        //
        // We treat an empty paragraph (pool has no childs) as unchanged at any time.
        paragraphNesting.push( ParagraphLineBreakStep.LEAF_BREAK_STATE );
        breakState = ParagraphLineBreakStep.LEAF_BREAK_STATE;
        return false;
      }

      // When the paragraph has changed, this can only be caused by someone adding a new node to the paragraph
      // or to one of the childs.

      // Paragraphs can be nested whenever a Inline-Level element declares to be a Block-Layouter. (This is an
      // Inline-Block or Inline-Table case in CSS)

      // It is guaranteed, that if a child is changed, the parent is marked as changed as well.
      // So we have only two cases to deal with: (1) The child is unchanged (2) the child is changed.

      if ( breakState == null ) {
        final ParagraphPoolBox paragraphPoolBox = paragraphBox.getPool();
        final RenderNode firstChild = paragraphPoolBox.getFirstChild();
        if ( firstChild == null ) {
          paragraphBox.setPoolSize( 0 );
          paragraphBox.setLineBoxAge( paragraphPoolBox.getChangeTracker() );
          breakState = ParagraphLineBreakStep.LEAF_BREAK_STATE;
          return false;
        }
        if ( firstChild == paragraphPoolBox.getLastChild() ) {
          if ( ( firstChild.getLayoutNodeType() & LayoutNodeTypes.MASK_BOX ) != LayoutNodeTypes.MASK_BOX ) {
            // Optimize away: A single text-element or other content in a linebox. No need to dive deeper.
            paragraphBox.setPoolSize( 1 );
            paragraphBox.setLineBoxAge( paragraphPoolBox.getChangeTracker() );
            breakState = ParagraphLineBreakStep.LEAF_BREAK_STATE;
            return false;
          }
        }
        if ( paragraphBox.isComplexParagraph() ) {
          final ParagraphLinebreaker item = new FullLinebreaker( paragraphBox );
          paragraphNesting.push( item );
          breakState = item;
        } else {
          if ( reusableSimpleLinebreaker == null ) {
            reusableSimpleLinebreaker = new SimpleLinebreaker( paragraphBox );
          } else {
            reusableSimpleLinebreaker.recycle( paragraphBox );
          }
          paragraphNesting.push( reusableSimpleLinebreaker );
          breakState = reusableSimpleLinebreaker;
        }
        return true;
      }

      // The breakState indicates that there is a paragraph processing active at the moment. This means, the
      // paragraph-box we are dealing with right now is a nested box.

      if ( breakState.isWritable() == false ) {
        // OK, should not happen, but you never know. I'm good at hiding
        // bugs in the code ..
        throw new IllegalStateException( "A child cannot be dirty, if the parent is clean" );
      }

      // The paragraph is somehow nested in an other paragraph.
      // This cannot be handled by the simple implementation, as we will most likely start to deriveForAdvance childs
      // sooner
      // or later
      if ( breakState instanceof FullLinebreaker == false ) {
        // convert it ..
        final FullLinebreaker fullBreaker = breakState.startComplexLayout();
        paragraphNesting.pop();
        paragraphNesting.push( fullBreaker );
        breakState = fullBreaker;
      }

      final ParagraphLinebreaker subFlow = breakState.startParagraphBox( paragraphBox );
      paragraphNesting.push( subFlow );
      breakState = subFlow;
      return true;
    }

    // some other block box ..
    if ( breakState == null ) {
      if ( box.isLinebreakCacheValid() ) {
        return false;
      }
      // Not nested in a paragraph, thats easy ..
      return true;
    }

    if ( breakState.isWritable() == false ) {
      throw new IllegalStateException( "This cannot be: There is an active break-state, but the box is not writable." );
    }

    breakState.startBlockBox( box );
    return true;
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    box.setLinebreakAge( box.getChangeTracker() );

    if ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
      if ( breakState == ParagraphLineBreakStep.LEAF_BREAK_STATE && paragraphNesting.isEmpty() ) {
        // this is a non-nested simple paragraph; no need for clean up ..
        breakState = null;
        return;
      }
      // do the linebreak jiggle ...
      // This is the first test case whether it is possible to avoid
      // composition-recursion on such computations. I'd prefer to have
      // an iterator pattern here ...

      // finally update the change tracker ..
      breakState.finish();
      paragraphNesting.pop();
      if ( paragraphNesting.isEmpty() ) {
        if ( reusableSimpleLinebreaker != null ) {
          reusableSimpleLinebreaker.dispose();
        }
        breakState = null;
      } else {
        breakState = paragraphNesting.peek();
        breakState.finishParagraphBox( (ParagraphRenderBox) box );
      }
      return;
    }

    if ( breakState == null ) {
      return;
    }

    if ( breakState.isWritable() == false ) {
      throw new IllegalStateException( "A child cannot be dirty, if the parent is clean" );
    }

    breakState.finishBlockBox( box );
  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    return startBox( box );
  }

  protected void finishCanvasBox( final CanvasRenderBox box ) {
    finishBox( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( breakState == null || breakState.isWritable() == false ) {
      if ( box.isLinebreakCacheValid() ) {
        return false;
      }
      return true;
    }

    breakState.startInlineBox( box );
    return true;
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    box.setLinebreakAge( box.getChangeTracker() );

    if ( breakState == null || breakState.isWritable() == false ) {
      return;
    }

    breakState.finishInlineBox( box );
    if ( breakState.isBreakRequested() && isEndOfLine( box ) == false ) {
      performBreak();
    }
  }

  protected boolean startRowBox( final RenderBox box ) {
    return startBox( box );
  }

  protected void finishRowBox( final RenderBox box ) {
    finishBox( box );
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    return startBox( box );
  }

  protected void finishTableRowBox( final TableRowRenderBox box ) {
    finishBox( box );
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    return startBox( box );
  }

  protected void finishTableSectionBox( final TableSectionRenderBox box ) {
    finishBox( box );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return startBox( box );
  }

  protected void finishAutoBox( final RenderBox box ) {
    finishBox( box );
  }

  private void finishBox( final RenderBox box ) {
    box.setLinebreakAge( box.getChangeTracker() );

    if ( breakState == null ) {
      return;
    }

    if ( breakState.isWritable() == false ) {
      throw new IllegalStateException( "A child cannot be dirty, if the parent is clean" );
    }

    breakState.finishBlockBox( box );
  }

  private boolean startBox( final RenderBox box ) {
    if ( breakState == null ) {
      if ( box.isLinebreakCacheValid() ) {
        return false;
      }

      return true;
    }

    // some other block box .. suspend.
    if ( breakState.isWritable() == false ) {
      throw new IllegalStateException( "A child cannot be dirty, if the parent is clean" );
    }

    breakState.startBlockBox( box );
    return true;
  }

  private void processText( final RenderableText text ) {
    breakState.addNode( text );
    if ( text.isForceLinebreak() == false ) {
      return;
    }

    if ( breakState.isBreakRequested() ) {
      performBreak();
    }

    // OK, someone requested a manual linebreak.
    // Fill a stack with the current context ..
    // Check if we are at the end of the line
    if ( text.getNext() == null ) {
      // OK, if we are at the end of the line (for all contexts), so we
      // dont have to perform a break. The text will end anyway ..
      if ( isEndOfLine( text ) ) {
        return;
      }

      // as soon as we are no longer the last element - break!
      // According to the flow rules, that will happen in one of the next
      // finishInlineBox events ..
      breakState.setBreakRequested( true );
      return;
    }

    performBreak();
  }

  private void processText( final RenderableComplexText text ) {
    breakState.addNode( text );
    if ( text.isForceLinebreak() == false ) {
      return;
    }

    if ( breakState.isBreakRequested() ) {
      performBreak();
    }

    // OK, someone requested a manual linebreak.
    // Fill a stack with the current context ..
    // Check if we are at the end of the line
    if ( text.getNext() == null ) {
      // OK, if we are at the end of the line (for all contexts), so we
      // dont have to perform a break. The text will end anyway ..
      if ( isEndOfLine( text ) ) {
        return;
      }

      // as soon as we are no longer the last element - break!
      // According to the flow rules, that will happen in one of the next
      // finishInlineBox events ..
      breakState.setBreakRequested( true );
      return;
    }

    performBreak();
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( breakState == null || breakState.isWritable() == false ) {
      return;
    }

    if ( breakState.isSuspended() ) {
      breakState.addNode( node );
    } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
      final RenderableText text = (RenderableText) node;
      processText( text );
    } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
      final RenderableComplexText text = (RenderableComplexText) node;
      processText( text );
    } else {
      breakState.addNode( node );
    }

  }

  private boolean isEndOfLine( RenderNode node ) {
    while ( node != null ) {
      final int nodeType = node.getLayoutNodeType();
      if ( ( nodeType & LayoutNodeTypes.MASK_BOX ) == LayoutNodeTypes.MASK_BOX
          && ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) != LayoutNodeTypes.MASK_BOX_INLINE ) {
        return true;
      }
      if ( node.getNext() != null ) {
        return false;
      }
      node = node.getParent();
    }
    return true;
  }

  private void performBreak() {
    if ( breakState instanceof FullLinebreaker == false ) {
      final FullLinebreaker fullBreaker = breakState.startComplexLayout();
      paragraphNesting.pop();
      paragraphNesting.push( fullBreaker );
      breakState = fullBreaker;

      fullBreaker.performBreak();
    } else {
      final FullLinebreaker fullBreaker = (FullLinebreaker) breakState;
      fullBreaker.performBreak();
    }
  }

  protected boolean startOtherBox( final RenderBox box ) {
    if ( breakState == null ) {
      if ( box.isLinebreakCacheValid() ) {
        return false;
      }

      return true;
    }

    if ( breakState.isWritable() == false ) {
      return false;
    }

    breakState.startBlockBox( box );
    return true;
  }

  protected void finishOtherBox( final RenderBox box ) {
    box.setLinebreakAge( box.getChangeTracker() );

    if ( breakState != null && breakState.isWritable() ) {
      breakState.finishBlockBox( box );
    }
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    if ( breakState == null ) {
      if ( box.isLinebreakCacheValid() ) {
        return false;
      }

      return true;
    }

    if ( breakState.isWritable() == false ) {
      return false;
    }

    breakState.startBlockBox( box );
    return true;
  }

  protected void finishTableCellBox( final TableCellRenderBox box ) {
    box.setLinebreakAge( box.getChangeTracker() );

    if ( breakState != null && breakState.isWritable() ) {
      breakState.finishBlockBox( box );
    }
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    return false;
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    if ( breakState == null ) {
      if ( box.isLinebreakCacheValid() ) {
        return false;
      }

      return true;
    }

    if ( breakState.isWritable() == false ) {
      return false;
    }

    breakState.startBlockBox( box );
    return true;
  }

  protected void finishTableBox( final TableRenderBox box ) {
    box.setLinebreakAge( box.getChangeTracker() );

    if ( breakState != null && breakState.isWritable() ) {
      breakState.finishBlockBox( box );
    }
  }
}
