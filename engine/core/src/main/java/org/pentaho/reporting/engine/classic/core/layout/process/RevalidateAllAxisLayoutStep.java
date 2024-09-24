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
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.CenterAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.JustifyAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.LastLineTextAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.LeftAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.RightAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.DefaultSequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.EndSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineBoxSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineNodeSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.ReplacedContentSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SpacerSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.TextSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.util.CacheBoxShifter;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.AlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.BoxAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.InlineBlockAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.NodeAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.ReplacedContentAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.TextElementAlignContext;
import org.pentaho.reporting.engine.classic.core.layout.process.valign.VerticalAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.FastStack;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

/**
 * This final processing step revalidates the text-layouting and the vertical alignment of block-level elements.
 * <p/>
 * At this point, the layout is almost finished, but non-dynamic text elements may contain more content on the last line
 * than actually needed. This step recomputes the vertical alignment and merges all extra lines into the last line.
 *
 * @author Thomas Morgner
 */
public final class RevalidateAllAxisLayoutStep { // extends IterateSimpleStructureProcessStep
  private static class MergeContext {
    private RenderBox readContext;
    private RenderBox writeContext;

    protected MergeContext( final RenderBox writeContext, final RenderBox readContext ) {
      this.readContext = readContext;
      this.writeContext = writeContext;
    }

    public RenderBox getReadContext() {
      return readContext;
    }

    public RenderBox getWriteContext() {
      return writeContext;
    }
  }

  private static final Log logger = LogFactory.getLog( RevalidateAllAxisLayoutStep.class );
  private static final long OVERFLOW_DUMMY_WIDTH = StrictGeomUtility.toInternalValue( 20000 );
  private LastLineTextAlignmentProcessor centerProcessor;
  private LastLineTextAlignmentProcessor leftProcessor;
  private LastLineTextAlignmentProcessor rightProcessor;
  private LastLineTextAlignmentProcessor justifiedProcessor;
  private PageGrid pageGrid;
  private OutputProcessorMetaData metaData;
  private VerticalAlignmentProcessor verticalAlignmentProcessor;
  private boolean complexText;
  private boolean strictTextProcessing;

  public RevalidateAllAxisLayoutStep() {
    this.verticalAlignmentProcessor = new VerticalAlignmentProcessor();
  }

  public void initialize( final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    complexText = metaData.isFeatureSupported( OutputProcessorFeature.COMPLEX_TEXT );
    strictTextProcessing = metaData.isFeatureSupported( OutputProcessorFeature.STRICT_TEXT_PROCESSING );
  }

  public void processBoxChilds( final ParagraphRenderBox box, final PageGrid pageGrid ) {
    try {
      this.pageGrid = pageGrid;
      if ( complexText ) {
        processComplexText( box );
      } else {
        processSimpleText( box );
      }
    } finally {
      this.pageGrid = null;
    }
  }

  private void performVerticalBlockAlignment( final RenderBox box ) {

    final RenderNode lastChildNode = box.getLastChild();

    if ( lastChildNode == null ) {
      return;
    }

    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final long insetBottom = blp.getBorderBottom() + boxDefinition.getPaddingBottom();
    final long insetTop = blp.getBorderTop() + boxDefinition.getPaddingTop();

    final long childY2 =
        lastChildNode.getCachedY() + lastChildNode.getCachedHeight() + lastChildNode.getEffectiveMarginBottom();
    final long childY1 = box.getFirstChild().getCachedY();
    final long usedHeight = ( childY2 - childY1 );

    final long computedHeight = box.getCachedHeight();
    if ( computedHeight > usedHeight ) {
      // we have extra space to distribute. So lets shift some boxes.
      final ElementAlignment valign = box.getNodeLayoutProperties().getVerticalAlignment();
      if ( ElementAlignment.BOTTOM.equals( valign ) ) {
        final long boxBottom = ( box.getCachedY() + box.getCachedHeight() - insetBottom );
        final long delta = boxBottom - childY2;
        CacheBoxShifter.shiftBoxChilds( box, delta );
      } else if ( ElementAlignment.MIDDLE.equals( valign ) ) {
        final long extraHeight = computedHeight - usedHeight;
        final long boxTop = box.getCachedY() + insetTop + ( extraHeight / 2 );
        final long delta = boxTop - childY1;
        CacheBoxShifter.shiftBoxChilds( box, delta );
      }
    }
  }

  protected void processComplexText( final ParagraphRenderBox paragraph ) {
    if ( paragraph.getStaticBoxLayoutProperties().isOverflowY() == true ) {
      performVerticalBlockAlignment( paragraph );
      return;
    }

    final RenderNode lastLine = paragraph.getLastChild();
    if ( lastLine == null ) {
      // Empty paragraph, no need to do anything ...
      return;
    }

    // Process the direct childs of the paragraph
    // Each direct child represents a line ..
    final long paragraphBottom = paragraph.getCachedY() + paragraph.getCachedHeight();
    if ( ( lastLine.getCachedY() + lastLine.getCachedHeight() ) <= paragraphBottom ) {
      // Already perfectly aligned.
      return;
    }

    RenderNode node = paragraph.getFirstChild();
    ParagraphPoolBox prev = null;
    while ( node != null ) {
      // all childs of the linebox container must be inline boxes. They
      // represent the lines in the paragraph. Any other element here is
      // a error that must be reported
      if ( node.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
        throw new IllegalStateException( "Encountered " + node.getClass() );
      }

      // Process the current line.
      final long y = node.getCachedY();
      final long height = node.getCachedHeight();
      if ( y + height <= paragraphBottom ) {

        // Node will fit, so we can allow it ..
        prev = (ParagraphPoolBox) node;
        node = node.getNext();
        continue;
      }

      // Encountered a line that will not fully fit into the paragraph.
      // Merge it with the previous line-paragraph.
      if ( prev == null ) {
        // none of the lines fits fully, so get the first one at least
        rebuildLastLineComplex( (ParagraphPoolBox) node, (RenderBox) node.getNext() );
        node = node.getNext();
      } else {
        rebuildLastLineComplex( prev, (ParagraphPoolBox) node );
      }

      // now remove all pending lineboxes (they should be empty anyway).
      while ( node != null ) {
        final RenderNode oldNode = node;
        node = node.getNext();
        paragraph.remove( oldNode );
      }
      return;
    }
  }

  protected void processSimpleText( final ParagraphRenderBox paragraph ) {
    if ( paragraph.getStaticBoxLayoutProperties().isOverflowY() == true ) {
      performVerticalBlockAlignment( paragraph );
      return;
    }

    final RenderNode lastLine = paragraph.getLastChild();
    if ( lastLine == null ) {
      // Empty paragraph, no need to do anything ...
      return;
    }

    // Process the direct childs of the paragraph
    // Each direct child represents a line ..
    final long paragraphBottom = paragraph.getCachedY() + paragraph.getCachedHeight();
    if ( ( lastLine.getCachedY() + lastLine.getCachedHeight() ) <= paragraphBottom ) {
      // Already perfectly aligned.
      return;
    }

    final boolean overflowX = paragraph.getStaticBoxLayoutProperties().isOverflowX();
    RenderNode node = paragraph.getFirstChild();
    ParagraphPoolBox prev = null;
    boolean first =
        metaData
            .isFeatureSupported( OutputProcessorFeature.BooleanOutputProcessorFeature.ALWAYS_PRINT_FIRST_LINE_OF_TEXT );
    while ( node != null ) {
      // all childs of the linebox container must be inline boxes. They
      // represent the lines in the paragraph. Any other element here is
      // a error that must be reported
      if ( node.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
        throw new IllegalStateException( "Encountered " + node.getClass() );
      }

      final ParagraphPoolBox inlineRenderBox = (ParagraphPoolBox) node;
      // Process the current line.
      final long y = inlineRenderBox.getCachedY();
      final long height = inlineRenderBox.getCachedHeight();
      if ( first || y + height <= paragraphBottom ) {
        // Node will fit, so we can allow it ..
        prev = inlineRenderBox;
        node = node.getNext();
        first = false;
        continue;
      }

      // Encountered a line that will not fully fit into the paragraph.
      // Merge it with the previous line-paragraph.
      final ParagraphPoolBox mergedLine = rebuildLastLine( prev, inlineRenderBox );

      // now remove all pending lineboxes (they should be empty anyway).
      while ( node != null ) {
        final RenderNode oldNode = node;
        node = node.getNext();
        paragraph.remove( oldNode );
      }

      if ( mergedLine == null ) {
        return;
      }

      final ElementAlignment textAlignment = paragraph.getLastLineAlignment();
      final LastLineTextAlignmentProcessor proc = create( textAlignment );

      // Now Build the sequence list that holds all nodes for the horizontal alignment computation.
      // The last line will get a special "last-line" horizontal alignment. This is quite usefull if
      // we are working with justified text and want the last line to be left-aligned.
      final SequenceList sequenceList = createHorizontalSequenceList( mergedLine );
      final long lineStart = paragraph.getContentAreaX1();
      final long lineEnd;

      if ( overflowX ) {
        lineEnd = OVERFLOW_DUMMY_WIDTH;
      } else {
        lineEnd = paragraph.getContentAreaX2();
      }

      if ( lineEnd - lineStart <= 0 ) {
        final long minimumChunkWidth = paragraph.getMinimumChunkWidth();
        proc.initialize( metaData, sequenceList, lineStart, lineStart + minimumChunkWidth, pageGrid, overflowX );
        logger.warn( "Auto-Corrected zero-width linebox." ); // NON-NLS
      } else {
        proc.initialize( metaData, sequenceList, lineStart, lineEnd, pageGrid, overflowX );
      }
      proc.performLastLineAlignment();
      proc.deinitialize();

      // Now Perform the vertical layout for the last line of the paragraph.
      final BoxAlignContext valignContext = createVerticalAlignContext( mergedLine );
      final StaticBoxLayoutProperties blp = mergedLine.getStaticBoxLayoutProperties();
      final BoxDefinition bdef = mergedLine.getBoxDefinition();
      final long insetTop = ( blp.getBorderTop() + bdef.getPaddingTop() );

      final long contentAreaY1 = mergedLine.getCachedY() + insetTop;
      final long lineHeight = mergedLine.getLineHeight();
      verticalAlignmentProcessor.align( valignContext, contentAreaY1, lineHeight );

      // And finally make sure that the paragraph box itself obeys to the defined vertical box alignment.
      performVerticalBlockAlignment( paragraph );
      return;
    }
  }

  private BoxAlignContext createVerticalAlignContext( final InlineRenderBox box ) {
    BoxAlignContext alignContext = new BoxAlignContext( box );
    final FastStack<RenderBox> contextStack = new FastStack<RenderBox>( 50 );
    final FastStack<AlignContext> alignContextStack = new FastStack<AlignContext>( 50 );
    RenderNode next = box.getFirstChild();
    RenderBox context = box;

    while ( next != null ) {
      // process next
      final int nodeType = next.getLayoutNodeType();
      if ( ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
        final RenderBox nBox = (RenderBox) next;
        final RenderNode firstChild = nBox.getFirstChild();
        if ( firstChild != null ) {
          // Open a non-empty box context
          contextStack.push( context );
          alignContextStack.push( alignContext );

          next = firstChild;

          final BoxAlignContext childBoxContext = new BoxAlignContext( nBox );
          alignContext.addChild( childBoxContext );
          context = nBox;
          alignContext = childBoxContext;
        } else {
          // Process an empty box.
          final BoxAlignContext childBoxContext = new BoxAlignContext( nBox );
          alignContext.addChild( childBoxContext );
          next = nBox.getNext();
        }
      } else {
        // Process an ordinary node.
        if ( nodeType == LayoutNodeTypes.TYPE_NODE_TEXT ) {
          alignContext.addChild( new TextElementAlignContext( (RenderableText) next ) );
        } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
          alignContext.addChild( new ReplacedContentAlignContext( (RenderableReplacedContentBox) next, 0 ) );
        } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
          alignContext.addChild( new InlineBlockAlignContext( (RenderBox) next ) );
        } else {
          alignContext.addChild( new NodeAlignContext( next ) );
        }
        next = next.getNext();
      }

      while ( next == null && contextStack.isEmpty() == false ) {
        // Finish the current box context, if needed
        next = context.getNext();
        context = contextStack.pop();
        alignContext.validate();
        alignContext = (BoxAlignContext) alignContextStack.pop();
      }
    }
    return alignContext;
  }

  private SequenceList createHorizontalSequenceList( final InlineRenderBox box ) {
    final SequenceList sequenceList = new DefaultSequenceList();
    sequenceList.add( StartSequenceElement.INSTANCE, box );

    RenderNode next = box.getFirstChild();
    RenderBox context = box;

    final FastStack<RenderBox> contextStack = new FastStack<RenderBox>( 50 );
    boolean containsContent = false;

    while ( next != null ) {
      // process next
      final int nodeType = next.getLayoutNodeType();
      if ( ( nodeType & LayoutNodeTypes.MASK_BOX_INLINE ) == LayoutNodeTypes.MASK_BOX_INLINE ) {
        final RenderBox nBox = (RenderBox) next;
        final RenderNode firstChild = nBox.getFirstChild();
        if ( firstChild != null ) {
          // Open a non-empty box context
          contextStack.push( context );
          next = firstChild;

          sequenceList.add( StartSequenceElement.INSTANCE, nBox );
          context = nBox;
        } else {
          // Process an empty box.
          sequenceList.add( StartSequenceElement.INSTANCE, nBox );
          sequenceList.add( EndSequenceElement.INSTANCE, nBox );
          next = nBox.getNext();
        }
      } else {
        // Process an ordinary node.
        if ( nodeType == LayoutNodeTypes.TYPE_NODE_TEXT ) {
          sequenceList.add( TextSequenceElement.INSTANCE, next );
          containsContent = true;
        } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
          sequenceList.add( ReplacedContentSequenceElement.INSTANCE, next );
          containsContent = true;
        } else if ( nodeType == LayoutNodeTypes.TYPE_NODE_SPACER ) {
          if ( containsContent ) {
            sequenceList.add( SpacerSequenceElement.INSTANCE, next );
          }
        } else if ( ( nodeType & LayoutNodeTypes.MASK_BOX_BLOCK ) == LayoutNodeTypes.MASK_BOX_BLOCK ) {
          containsContent = true;
          sequenceList.add( InlineBoxSequenceElement.INSTANCE, next );
        } else {
          containsContent = true;
          sequenceList.add( InlineNodeSequenceElement.INSTANCE, next );
        }
        next = next.getNext();
      }

      while ( next == null && contextStack.isEmpty() == false ) {
        // Finish the current box context, if needed
        sequenceList.add( EndSequenceElement.INSTANCE, context );
        next = context.getNext();
        context = contextStack.pop();
      }
    }

    sequenceList.add( EndSequenceElement.INSTANCE, box );
    return sequenceList;
  }

  private LastLineTextAlignmentProcessor create( final ElementAlignment alignment ) {
    if ( ElementAlignment.CENTER.equals( alignment ) ) {
      if ( centerProcessor == null ) {
        centerProcessor = new CenterAlignmentProcessor();
      }
      return centerProcessor;
    } else if ( ElementAlignment.RIGHT.equals( alignment ) ) {
      if ( rightProcessor == null ) {
        rightProcessor = new RightAlignmentProcessor();
      }
      return rightProcessor;
    } else if ( ElementAlignment.JUSTIFY.equals( alignment ) ) {
      if ( justifiedProcessor == null ) {
        justifiedProcessor = new JustifyAlignmentProcessor();
      }
      return justifiedProcessor;
    }

    if ( leftProcessor == null ) {
      leftProcessor = new LeftAlignmentProcessor();
    }
    return leftProcessor;
  }

  private ParagraphPoolBox rebuildLastLine( final ParagraphPoolBox lineBox, final ParagraphPoolBox nextBox ) {
    if ( lineBox == null ) {
      if ( nextBox == null ) {
        throw new NullPointerException( "Both Line- and Next-Line are null." );
      }

      return rebuildLastLine( nextBox, (ParagraphPoolBox) nextBox.getNext() );
    }

    if ( nextBox == null || strictTextProcessing ) {
      // Linebox is finished, no need to do any merging anymore..
      return lineBox;
    }

    boolean needToAddSpacing = true;

    // do the merging ..
    final FastStack<MergeContext> contextStack = new FastStack<MergeContext>( 50 );
    RenderNode next = nextBox.getFirstChild();
    MergeContext context = new MergeContext( lineBox, nextBox );
    while ( next != null ) {
      // process next
      final RenderBox writeContext = context.getWriteContext();
      final StaticBoxLayoutProperties staticBoxLayoutProperties = writeContext.getStaticBoxLayoutProperties();
      long spaceWidth = staticBoxLayoutProperties.getSpaceWidth();
      if ( spaceWidth == 0 ) {
        // Space has not been computed yet.
        final FontMetrics fontMetrics = metaData.getFontMetrics( writeContext.getStyleSheet() );
        spaceWidth = StrictGeomUtility.fromFontMetricsValue( fontMetrics.getCharWidth( ' ' ) );
        staticBoxLayoutProperties.setSpaceWidth( spaceWidth );
      }

      if ( next.isRenderBox() ) {
        final RenderBox nBox = (RenderBox) next;
        final RenderNode firstChild = nBox.getFirstChild();
        if ( firstChild != null ) {
          contextStack.push( context );
          next = firstChild;

          final RenderNode writeContextLastChild = writeContext.getLastChild();
          if ( writeContextLastChild.isRenderBox() ) {
            if ( writeContextLastChild.getInstanceId() == nBox.getInstanceId() ) {
              context = new MergeContext( (RenderBox) writeContextLastChild, nBox );
            } else {
              if ( needToAddSpacing ) {
                if ( spaceWidth > 0 ) {
                  // docmark: Used zero as new height
                  final SpacerRenderNode spacer = new SpacerRenderNode( spaceWidth, 0, false, 1 );
                  spacer.setVirtualNode( true );
                  writeContext.addGeneratedChild( spacer );
                }
                needToAddSpacing = false;
              }
              final RenderBox newWriter = (RenderBox) nBox.derive( false );
              newWriter.setVirtualNode( true );
              writeContext.addGeneratedChild( newWriter );
              context = new MergeContext( newWriter, nBox );
            }
          } else {
            if ( needToAddSpacing ) {
              if ( spaceWidth > 0 ) {
                // docmark: Used zero as new height
                final SpacerRenderNode spacer = new SpacerRenderNode( spaceWidth, 0, false, 1 );
                spacer.setVirtualNode( true );
                writeContext.addGeneratedChild( spacer );
              }
              needToAddSpacing = false;
            }

            final RenderBox newWriter = (RenderBox) nBox.derive( false );
            newWriter.setVirtualNode( true );
            writeContext.addGeneratedChild( newWriter );
            context = new MergeContext( newWriter, nBox );
          }
        } else {
          if ( needToAddSpacing ) {
            if ( spaceWidth > 0 ) {
              // docmark: Used zero as new height
              final SpacerRenderNode spacer = new SpacerRenderNode( spaceWidth, 0, false, 1 );
              spacer.setVirtualNode( true );
              writeContext.addGeneratedChild( spacer );
            }
            needToAddSpacing = false;
          }

          final RenderNode box = nBox.derive( true );
          box.setVirtualNode( true );
          writeContext.addGeneratedChild( box );
          next = nBox.getNext();
        }
      } else {
        if ( needToAddSpacing ) {
          final RenderNode lastChild = writeContext.getLastChild();
          if ( spaceWidth > 0 && lastChild != null && ( lastChild.getNodeType() != LayoutNodeTypes.TYPE_NODE_SPACER ) ) {
            // docmark: Used zero as new height
            final SpacerRenderNode spacer = new SpacerRenderNode( spaceWidth, 0, false, 1 );
            spacer.setVirtualNode( true );
            writeContext.addGeneratedChild( spacer );
          }
          needToAddSpacing = false;
        }

        final RenderNode child = next.derive( true );
        child.setVirtualNode( true );
        writeContext.addGeneratedChild( child );
        next = next.getNext();
      }

      while ( next == null && contextStack.isEmpty() == false ) {
        // Log.debug ("FINISH " + context.getReadContext());
        next = context.getReadContext().getNext();
        context = contextStack.pop();
      }
    }

    return rebuildLastLine( lineBox, (ParagraphPoolBox) nextBox.getNext() );
  }

  private RenderBox rebuildLastLineComplex( final RenderBox lineBox, final RenderBox nextBox ) {
    if ( lineBox == null ) {
      throw new NullPointerException();
    }
    if ( nextBox == null ) {
      return lineBox;
    }

    RenderNode child = nextBox.getFirstChild();
    while ( child != null ) {
      if ( child.isRenderBox() ) {
        if ( lineBox.getLastChild().isRenderBox() && lineBox.getLastChild().getInstanceId() == child.getInstanceId() ) {
          rebuildLastLineComplex( (RenderBox) lineBox.getLastChild(), (RenderBox) child );
        } else {
          RenderBox lineBoxChild = (RenderBox) child.derive( false );
          rebuildLastLineComplex( lineBoxChild, (RenderBox) child );
          lineBoxChild.close();
          lineBox.addGeneratedChild( lineBoxChild );
        }
      } else if ( child instanceof RenderableComplexText ) {
        RenderableComplexText childAsText = (RenderableComplexText) child;
        RenderNode n = lineBox.getLastChild();
        if ( n instanceof RenderableComplexText ) {
          RenderableComplexText lastLine = (RenderableComplexText) n;
          if ( lastLine.isSameSource( childAsText ) ) {
            lineBox.replaceChild( n, lastLine.merge( childAsText ) );
          } else {
            lineBox.addGeneratedChild( child );
          }
        } else {
          lineBox.addGeneratedChild( child );
        }
      } else {
        lineBox.addGeneratedChild( child );
      }

      child = child.getNext();
    }
    return null;
  }
}
