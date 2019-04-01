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
 * Copyright (c) 2001 - 2019 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.base;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RevalidateTextEllipseProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;

/**
 * Creation-Date: 02.11.2007, 14:14:23
 *
 * @author Thomas Morgner
 */
public class DefaultTextExtractor extends IterateStructuralProcessStep {
  private StringBuffer text;
  private Object rawResult;
  private RenderNode rawSource;
  private StrictBounds paragraphBounds;
  private boolean overflowX;
  private boolean overflowY;
  private boolean textLineOverflow;
  private RevalidateTextEllipseProcessStep revalidateTextEllipseProcessStep;
  private CodePointBuffer codePointBuffer;
  private boolean manualBreak;
  // private long contentAreaX1;
  private long contentAreaX2;
  private boolean ellipseDrawn;
  private boolean clipOnWordBoundary;

  public DefaultTextExtractor( final OutputProcessorMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }

    codePointBuffer = new CodePointBuffer( 400 );
    text = new StringBuffer( 400 );
    paragraphBounds = new StrictBounds();
    revalidateTextEllipseProcessStep = new RevalidateTextEllipseProcessStep( metaData );
    this.clipOnWordBoundary =
        "true".equals( metaData.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.LastLineBreaksOnWordBoundary" ) );
  }

  protected CodePointBuffer getCodePointBuffer() {
    return codePointBuffer;
  }

  public Object compute( final RenderBox box ) {
    rawResult = null;
    rawSource = null;
    // initialize it once. It may be overriden later, if there is a real paragraph
    paragraphBounds.setRect( box.getX(), box.getY(), box.getWidth(), box.getHeight() );
    overflowX = box.isBoxOverflowX();
    overflowY = box.isBoxOverflowY();
    clearText();
    startProcessing( box );

    // A simple result. So there's no need to create a rich-text string.
    if ( rawResult != null ) {
      return rawResult;
    }
    return text.toString();
  }

  public String getFormattedtext() {
    return text.toString();
  }

  private long extractEllipseSize( final RenderNode node ) {
    if ( node == null ) {
      return 0;
    }
    final RenderBox parent = node.getParent();
    if ( parent == null ) {
      return 0;
    }
    final RenderBox textEllipseBox = parent.getTextEllipseBox();
    if ( textEllipseBox == null ) {
      return 0;
    }
    return textEllipseBox.getWidth();
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( isTextLineOverflow() ) {
      if ( handleOverflow( node ) ) {
        return;
      }
    }

    final int nodeType = node.getNodeType();
    if ( nodeType == LayoutNodeTypes.TYPE_NODE_TEXT ) {
      final RenderableText textNode = (RenderableText) node;
      processStandardText( textNode );
      if ( textNode.isForceLinebreak() ) {
        manualBreak = true;
      }
    } else if ( nodeType == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
      final RenderableComplexText textNode = (RenderableComplexText) node;
      if ( processComplexText( textNode ) ) {
        return;
      }
      if ( textNode.isForceLinebreak() ) {
        manualBreak = true;
      }
    } else if ( nodeType == LayoutNodeTypes.TYPE_NODE_SPACER ) {
      final SpacerRenderNode spacer = (SpacerRenderNode) node;
      final int count = Math.max( 1, spacer.getSpaceCount() );
      for ( int i = 0; i < count; i++ ) {
        this.text.append( ' ' );
      }
    }
  }

  private boolean processComplexText( final RenderableComplexText textNode ) {
    if ( isTextLineOverflow() ) {
      if ( textNode.isNodeVisible( paragraphBounds, overflowX, overflowY ) == false ) {
        return true;
      }

      final long ellipseSize = extractEllipseSize( textNode );
      final long x1 = textNode.getX();
      final long x2 = x1 + textNode.getWidth();
      final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );
      if ( x2 <= effectiveAreaX2 ) {
        // the text will be fully visible.
        drawComplexText( textNode );
      } else if ( x1 < contentAreaX2 ) {
        // The text node that is printed will overlap with the ellipse we need to print.
        drawComplexText( textNode );

        final RenderBox parent = textNode.getParent();
        if ( parent != null ) {
          final RenderBox textEllipseBox = parent.getTextEllipseBox();
          if ( textEllipseBox != null ) {
            processBoxChilds( textEllipseBox );
          }
        }
      }
    } else {
      drawComplexText( textNode );
    }
    return false;
  }

  private void processStandardText( final RenderableText textNode ) {
    if ( isTextLineOverflow() ) {
      if ( textNode.isNodeVisible( paragraphBounds, overflowX, overflowY ) == false ) {
        return;
      }

      final long ellipseSize = extractEllipseSize( textNode );
      final long x1 = textNode.getX();
      final long x2 = x1 + textNode.getWidth();
      final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );
      if ( x2 <= effectiveAreaX2 ) {
        // the text will be fully visible.
        drawText( textNode, x2 );
      } else if ( x1 < contentAreaX2 ) {
        // The text node that is printed will overlap with the ellipse we need to print.
        drawText( textNode, effectiveAreaX2 );
        final RenderBox parent = textNode.getParent();
        if ( parent != null ) {
          final RenderBox textEllipseBox = parent.getTextEllipseBox();
          if ( textEllipseBox != null ) {
            processBoxChilds( textEllipseBox );
          }
        }
      }
    } else {
      drawText( textNode, textNode.getX() + textNode.getWidth() );
    }
  }

  private boolean handleOverflow( final RenderNode node ) {
    if ( node.isNodeVisible( paragraphBounds, overflowX, overflowY ) == false ) {
      return true;
    }

    if ( node.isVirtualNode() ) {
      if ( ellipseDrawn ) {
        return true;
      }
      ellipseDrawn = true;

      final int nodeType = node.getNodeType();
      if ( clipOnWordBoundary == false && nodeType == LayoutNodeTypes.TYPE_NODE_TEXT ) {
        final RenderableText text = (RenderableText) node;
        final long ellipseSize = extractEllipseSize( node );
        final long x1 = text.getX();
        final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );

        if ( x1 < contentAreaX2 ) {
          // The text node that is printed will overlap with the ellipse we need to print.
          drawText( text, effectiveAreaX2 );
        }
      } else if ( clipOnWordBoundary == false && nodeType == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
        final RenderableComplexText text = (RenderableComplexText) node;
        final long x1 = text.getX();

        if ( x1 < contentAreaX2 ) {
          // The text node that is printed will overlap with the ellipse we need to print.
          drawComplexText( text );
        }
      }

      final RenderBox parent = node.getParent();
      if ( parent != null ) {
        final RenderBox textEllipseBox = parent.getTextEllipseBox();
        if ( textEllipseBox != null ) {
          processBoxChilds( textEllipseBox );
        }
      }
      return true;
    }
    return false;
  }

  /**
   * Renders the glyphs stored in the text node.
   *
   * @param renderableText
   *          the text node that should be rendered.
   * @param contentX2
   */
  protected void drawText( final RenderableText renderableText, final long contentX2 ) {
    if ( renderableText.getLength() == 0 ) {
      // This text is empty.
      return;
    }

    final GlyphList gs = renderableText.getGlyphs();
    final int maxLength = renderableText.computeMaximumTextSize( contentX2 );
    this.text.append( gs.getText( renderableText.getOffset(), maxLength, codePointBuffer ) );
  }

  protected void drawComplexText( final RenderableComplexText renderableComplexText ) {
    String text = renderableComplexText.getRawText();
    if ( text.length() == 0 ) {
      // This text is empty.
      return;
    }

    this.text.append( text );
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return false;
  }

  protected boolean isContentField( final RenderBox box ) {
    return ( box.getNodeType() == LayoutNodeTypes.TYPE_BOX_CONTENT );
  }

  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    return true;
  }

  protected void processRenderableContent( final RenderableReplacedContentBox box ) {
    final RenderableReplacedContent rpc = box.getContent();
    this.rawResult = rpc.getRawObject();
    this.rawSource = box;
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }
    return true;
  }

  protected boolean startRowBox( final RenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }
    return true;
  }

  public RenderNode getRawSource() {
    return rawSource;
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected boolean startTableCellBox( final TableCellRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected boolean startTableRowBox( final TableRowRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected boolean startTableSectionBox( final TableSectionRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected boolean startTableColumnGroupBox( final TableColumnGroupNode box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected boolean startAutoBox( final RenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return true;
  }

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    rawResult = box.getRawValue();
    paragraphBounds.setRect( box.getX(), box.getY(), box.getWidth(), box.getHeight() );
    overflowX = box.isBoxOverflowX();
    overflowY = box.isBoxOverflowY();
    // final long y2 = box.getY() + box.getHeight();
    final long contentAreaX1 = box.getContentAreaX1();
    contentAreaX2 = box.getContentAreaX2();

    RenderBox lineBox = (RenderBox) box.getFirstChild();
    while ( lineBox != null ) {
      manualBreak = false;
      processTextLine( lineBox, contentAreaX1, contentAreaX2 );
      if ( manualBreak ) {
        addLinebreak();
      } else if ( lineBox.getNext() != null ) {
        if ( lineBox.getStaticBoxLayoutProperties().isPreserveSpace() == false ) {
          addSoftBreak();
        } else {

          RenderNode deepestLastChild = getDeepestLastChild( lineBox );
          if ( deepestLastChild != null && deepestLastChild instanceof RenderableText ) {
            RenderableText renderableText = (RenderableText) deepestLastChild;
            int wordSize = renderableText.getGlyphs().getSize();
            int currentOffset = renderableText.getOffset();
            int currentLength = renderableText.getLength();

            if ( wordSize == currentOffset + currentLength ) {
              addSoftBreak();
            } else {
              addEmptyBreak();
            }
          } else {
            addSoftBreak();
          }
        }
      }
      lineBox = (RenderBox) lineBox.getNext();
    }
  }

  protected RenderNode getDeepestLastChild( RenderBox lineBox ) {
    if ( lineBox.getLastChild() != null && lineBox.getLastChild() instanceof RenderBox ) {
      return getDeepestLastChild( (RenderBox) lineBox.getLastChild() );
    }
    return lineBox.getLastChild();
  }

  protected void addEmptyBreak() {
    text.append( ' ' );
  }

  protected void addSoftBreak() {
    text.append( ' ' );
  }

  protected void addLinebreak() {
    text.append( '\n' );
  }

  protected void processTextLine( final RenderBox lineBox, final long contentAreaX1, final long contentAreaX2 ) {
    if ( lineBox.isNodeVisible( paragraphBounds, overflowX, overflowY ) == false ) {
      return;
    }
    ellipseDrawn = false;

    final boolean overflowProperty = lineBox.getParent().getStaticBoxLayoutProperties().isOverflowX();

    this.textLineOverflow = ( ( lineBox.getX() + lineBox.getWidth() ) > contentAreaX2 ) && overflowProperty == false;

    if ( textLineOverflow ) {
      revalidateTextEllipseProcessStep.compute( lineBox, contentAreaX1, contentAreaX2 );
    }

    startProcessing( lineBox );
  }

  public Object getRawResult() {
    return rawResult;
  }

  protected void setRawResult( final Object rawResult ) {
    this.rawResult = rawResult;
  }

  public String getText() {
    return text.toString();
  }

  public int getTextLength() {
    return text.length();
  }

  protected void clearText() {
    text.delete( 0, text.length() );
  }

  protected StrictBounds getParagraphBounds() {
    return paragraphBounds;
  }

  public boolean isTextLineOverflow() {
    return textLineOverflow;
  }

  public boolean isOverflowX() {
    return overflowX;
  }

  public boolean isOverflowY() {
    return overflowY;
  }
}
