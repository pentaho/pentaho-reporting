/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.process.text;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.AbstractMinorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.CenterAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.JustifyAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.LeftAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.RightAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.TextAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.EndSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineNodeSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.ReplacedContentSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SpacerSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.TextSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContext;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisParagraphBreakState;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;

public class SimpleTextMinorAxisLayoutStep extends IterateSimpleStructureProcessStep implements TextMinorAxisLayoutStep {
  private static final Log logger = LogFactory.getLog( SimpleTextMinorAxisLayoutStep.class );

  private MinorAxisParagraphBreakState lineBreakState;
  private MinorAxisNodeContext nodeContext;
  private PageGrid pageGrid;
  private OutputProcessorMetaData metaData;
  private TextAlignmentProcessor centerProcessor;
  private TextAlignmentProcessor rightProcessor;
  private TextAlignmentProcessor leftProcessor;
  private TextAlignmentProcessor justifyProcessor;

  public SimpleTextMinorAxisLayoutStep( final OutputProcessorMetaData metaData ) {
    ArgumentNullException.validate( "metaData", metaData );

    this.metaData = metaData;
    this.lineBreakState = new MinorAxisParagraphBreakState();
  }

  public MinorAxisNodeContext getNodeContext() {
    return nodeContext;
  }

  public void process( final ParagraphRenderBox box, final MinorAxisNodeContext nodeContext, final PageGrid pageGrid ) {
    this.nodeContext = nodeContext;
    this.pageGrid = pageGrid;
    try {
      lineBreakState.init( box );

      processParagraphChildsNormal( box );

      lineBreakState.deinit();

    } finally {
      this.pageGrid = null;
    }
  }

  private void processParagraphChildsNormal( final ParagraphRenderBox box ) {
    final MinorAxisParagraphBreakState breakState = getLineBreakState();

    if ( box.isComplexParagraph() ) {
      final RenderBox lineboxContainer = box.getLineboxContainer();
      RenderNode node = lineboxContainer.getFirstChild();
      while ( node != null ) {
        // all childs of the linebox container must be inline boxes. They
        // represent the lines in the paragraph. Any other element here is
        // a error that must be reported
        if ( node.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
          throw new IllegalStateException( "Expected ParagraphPoolBox elements." );
        }

        final ParagraphPoolBox inlineRenderBox = (ParagraphPoolBox) node;
        if ( startLine( inlineRenderBox ) ) {
          processBoxChilds( inlineRenderBox );
          finishLine( inlineRenderBox, breakState );
        }

        node = node.getNext();
      }
    } else {
      final ParagraphPoolBox node = box.getPool();

      if ( node.getFirstChild() == null ) {
        return;
      }

      // all childs of the linebox container must be inline boxes. They
      // represent the lines in the paragraph. Any other element here is
      // a error that must be reported
      if ( startLine( node ) ) {
        processBoxChilds( node );
        finishLine( node, breakState );
      }
    }
  }

  private boolean startLine( final RenderBox inlineRenderBox ) {
    final MinorAxisParagraphBreakState breakState = getLineBreakState();
    if ( breakState.isInsideParagraph() == false ) {
      return false;
    }

    if ( breakState.isSuspended() ) {
      return false;
    }

    breakState.clear();
    breakState.add( StartSequenceElement.INSTANCE, inlineRenderBox );
    return true;
  }

  private void finishLine( final RenderBox inlineRenderBox, final MinorAxisParagraphBreakState breakState ) {
    if ( breakState.isInsideParagraph() == false || breakState.isSuspended() ) {
      throw new IllegalStateException( "No active breakstate, finish-line cannot continue." );
    }

    final MinorAxisNodeContext nodeContext = getNodeContext();
    final PageGrid pageGrid = getPageGrid();
    final OutputProcessorMetaData metaData = getMetaData();
    breakState.add( EndSequenceElement.INSTANCE, inlineRenderBox );

    final ParagraphRenderBox paragraph = breakState.getParagraph();

    final ElementAlignment textAlignment = paragraph.getTextAlignment();
    final long textIndent = paragraph.getTextIndent();
    final long firstLineIndent = paragraph.getFirstLineIndent();
    // This aligns all direct childs. Once that is finished, we have to
    // check, whether possibly existing inner-paragraphs are still valid
    // or whether moving them violated any of the inner-pagebreak constraints.
    final TextAlignmentProcessor processor = create( textAlignment );

    final SequenceList sequence = breakState.getSequence();

    final long lineEnd;
    final boolean overflowX = paragraph.getStaticBoxLayoutProperties().isOverflowX();
    if ( overflowX ) {
      lineEnd = nodeContext.getX1() + AbstractMinorAxisLayoutStep.OVERFLOW_DUMMY_WIDTH;
    } else {
      lineEnd = nodeContext.getX2();
    }

    long lineStart = Math.min( lineEnd, nodeContext.getX1() + firstLineIndent );
    if ( lineEnd - lineStart <= 0 ) {
      final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
      processor.initialize( metaData, sequence, lineStart, lineStart + minimumChunkWidth, pageGrid, overflowX );
      nodeContext.updateX2( lineStart + minimumChunkWidth );
      logger.warn( "Auto-Corrected zero-width first-line on paragraph - " + paragraph.getName() );
    } else {
      processor.initialize( metaData, sequence, lineStart, lineEnd, pageGrid, overflowX );
      if ( overflowX == false ) {
        nodeContext.updateX2( lineEnd );
      }
    }

    while ( processor.hasNext() ) {
      final RenderNode linebox = processor.next();
      if ( linebox.getLayoutNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
        throw new IllegalStateException( "Line must not be null" );
      }

      paragraph.addGeneratedChild( linebox );

      if ( processor.hasNext() ) {
        lineStart = Math.min( lineEnd, nodeContext.getX1() + textIndent );

        if ( lineEnd - lineStart <= 0 ) {
          final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
          processor.updateLineSize( lineStart, lineStart + minimumChunkWidth );
          nodeContext.updateX2( lineStart + minimumChunkWidth );
          logger.warn( "Auto-Corrected zero-width text-line on paragraph continuation - " + paragraph.getName() );
        } else {
          processor.updateLineSize( lineStart, lineEnd );
          if ( overflowX == false ) {
            nodeContext.updateX2( lineEnd );
          }
        }

      }
    }

    processor.deinitialize();
  }

  protected boolean startBox( final RenderBox box ) {
    if ( lineBreakState.isInsideParagraph() == false ) {
      throw new InvalidReportStateException( "A inline-level box outside of a paragraph box is not allowed." );
    }

    final int nodeType = box.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      lineBreakState.add( ReplacedContentSequenceElement.INSTANCE, box );
      return false;
    }

    lineBreakState.add( StartSequenceElement.INSTANCE, box );
    return true;
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( lineBreakState.isInsideParagraph() == false ) {
      throw new InvalidReportStateException( "A inline-level box outside of a paragraph box is not allowed." );
    }

    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE ) {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      node.setCachedWidth( finNode.getLayoutedWidth() );
      return;
    }

    if ( nodeType == LayoutNodeTypes.TYPE_NODE_TEXT ) {
      lineBreakState.add( TextSequenceElement.INSTANCE, node );
    } else if ( nodeType == LayoutNodeTypes.TYPE_NODE_SPACER ) {
      final StyleSheet styleSheet = node.getStyleSheet();
      if ( WhitespaceCollapse.PRESERVE.equals( styleSheet.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE ) )
          && styleSheet.getBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT ) == false ) {
        // bug-alert: This condition could indicate a workaround for a logic-flaw in the text-processor
        lineBreakState.add( SpacerSequenceElement.INSTANCE, node );
      } else if ( lineBreakState.isContainsContent() ) {
        lineBreakState.add( SpacerSequenceElement.INSTANCE, node );
      }
    } else {
      lineBreakState.add( InlineNodeSequenceElement.INSTANCE, node );
    }
  }

  protected void finishBox( final RenderBox box ) {
    if ( lineBreakState.isInsideParagraph() == false ) {
      throw new InvalidReportStateException( "A inline-level box outside of a paragraph box is not allowed." );
    }

    final int nodeType = box.getLayoutNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
      return;
    }

    lineBreakState.add( EndSequenceElement.INSTANCE, box );
  }

  /**
   * Reuse the processors ..
   *
   * @param alignment
   * @return
   */
  protected TextAlignmentProcessor create( final ElementAlignment alignment ) {
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
      if ( justifyProcessor == null ) {
        justifyProcessor = new JustifyAlignmentProcessor();
      }
      return justifyProcessor;
    }

    if ( leftProcessor == null ) {
      leftProcessor = new LeftAlignmentProcessor();
    }
    return leftProcessor;
  }

  public PageGrid getPageGrid() {
    return pageGrid;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected MinorAxisParagraphBreakState getLineBreakState() {
    return lineBreakState;
  }

}
