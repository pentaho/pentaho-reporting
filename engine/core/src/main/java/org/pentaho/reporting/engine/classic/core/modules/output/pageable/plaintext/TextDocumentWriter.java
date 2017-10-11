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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext;

import java.awt.font.TextLayout;
import java.awt.print.Paper;
import java.io.IOException;

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PhysicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableColumnGroupNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableRowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.PhysicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateStructuralProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RevalidateTextEllipseProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.PlainTextPage;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.driver.PrinterDriver;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;

/**
 * Creation-Date: 13.05.2007, 15:49:13
 *
 * @author Thomas Morgner
 */
public class TextDocumentWriter extends IterateStructuralProcessStep {
  private PrinterDriver driver;
  private String encoding;
  private PlainTextPage plainTextPage;
  private long characterWidthInMicroPoint;
  private long characterHeightInMicroPoint;
  private StrictBounds drawArea;
  private RevalidateTextEllipseProcessStep revalidateTextEllipseProcessStep;
  private long contentAreaX1;
  private long contentAreaX2;
  private boolean textLineOverflow;
  private CodePointBuffer codePointBuffer;
  private boolean ellipseDrawn;
  private boolean clipOnWordBoundary;
  private boolean watermarkOnTop;

  public TextDocumentWriter( final OutputProcessorMetaData metaData, final PrinterDriver driver, final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException();
    }
    if ( driver == null ) {
      throw new NullPointerException();
    }
    if ( metaData == null ) {
      throw new NullPointerException();
    }

    this.codePointBuffer = new CodePointBuffer( 400 );
    this.driver = driver;
    this.encoding = encoding;
    characterHeightInMicroPoint =
        StrictGeomUtility.toInternalValue( metaData.getNumericFeatureValue( TextOutputProcessorMetaData.CHAR_HEIGHT ) );
    characterWidthInMicroPoint =
        StrictGeomUtility.toInternalValue( metaData.getNumericFeatureValue( TextOutputProcessorMetaData.CHAR_WIDTH ) );

    if ( characterHeightInMicroPoint <= 0 || characterWidthInMicroPoint <= 0 ) {
      throw new IllegalStateException( "Invalid character box size. Cannot continue." );
    }
    revalidateTextEllipseProcessStep = new RevalidateTextEllipseProcessStep( metaData );
    this.clipOnWordBoundary =
        "true".equals( metaData.getConfiguration().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.LastLineBreaksOnWordBoundary" ) );
    this.watermarkOnTop = metaData.isFeatureSupported( OutputProcessorFeature.WATERMARK_PRINTED_ON_TOP );
  }

  @Deprecated
  public void close() {
  }

  @Deprecated
  public void open() {

  }

  public void processPhysicalPage( final PageGrid pageGrid, final LogicalPageBox logicalPage, final int row,
      final int col, final PhysicalPageKey pageKey ) throws IOException {
    final PhysicalPageBox page = pageGrid.getPage( row, col );
    final Paper paper = new Paper();
    paper.setSize( StrictGeomUtility.toExternalValue( page.getWidth() ), StrictGeomUtility.toExternalValue( page
        .getHeight() ) );
    paper.setImageableArea( StrictGeomUtility.toExternalValue( page.getImageableX() ), StrictGeomUtility
        .toExternalValue( page.getImageableY() ), StrictGeomUtility.toExternalValue( page.getImageableWidth() ),
        StrictGeomUtility.toExternalValue( page.getImageableHeight() ) );
    drawArea = new StrictBounds( page.getGlobalX(), page.getGlobalY(), page.getWidth(), page.getHeight() );
    plainTextPage = new PlainTextPage( paper, driver, encoding );
    processPageBox( logicalPage );
    plainTextPage.writePage();
  }

  public void processLogicalPage( final LogicalPageKey key, final LogicalPageBox logicalPage ) throws IOException {
    final Paper paper = new Paper();
    paper.setSize( StrictGeomUtility.toExternalValue( logicalPage.getPageWidth() ), StrictGeomUtility
        .toExternalValue( logicalPage.getPageHeight() ) );
    paper.setImageableArea( 0, 0, StrictGeomUtility.toExternalValue( logicalPage.getPageWidth() ), StrictGeomUtility
        .toExternalValue( logicalPage.getPageHeight() ) );
    paper.setSize( logicalPage.getPageWidth(), logicalPage.getPageHeight() );
    paper.setImageableArea( 0, 0, logicalPage.getPageWidth(), logicalPage.getPageHeight() );

    drawArea = new StrictBounds( 0, 0, logicalPage.getWidth(), logicalPage.getHeight() );
    plainTextPage = new PlainTextPage( paper, driver, encoding );
    processPageBox( logicalPage );
    plainTextPage.writePage();
  }

  protected void processPageBox( LogicalPageBox box ) {
    if ( startBlockBox( box ) ) {
      if ( !watermarkOnTop ) {
        startProcessing( box.getWatermarkArea() );
      }

      startProcessing( box.getHeaderArea() );
      processBoxChilds( box );
      startProcessing( box.getRepeatFooterArea() );
      startProcessing( box.getFooterArea() );
      if ( watermarkOnTop ) {
        startProcessing( box.getWatermarkArea() );
      }
    }
    finishBlockBox( box );

  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    return startBox( box );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    return startBox( box );
  }

  public boolean startCanvasBox( final CanvasRenderBox box ) {
    return startBox( box );
  }

  protected boolean startRowBox( final RenderBox box ) {
    return startBox( box );
  }

  protected boolean startBox( final RenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    if ( box.isBoxVisible( drawArea ) == false ) {
      return false;
    }
    return true;
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
    return startBox( box );
  }

  protected boolean startTableBox( final TableRenderBox box ) {
    return startBox( box );
  }

  protected boolean startOtherBox( final RenderBox box ) {
    return startBox( box );
  }

  protected boolean startAutoBox( final RenderBox box ) {
    return startBox( box );
  }

  protected void drawText( final RenderableText renderableText ) {
    drawText( renderableText, renderableText.getX() + renderableText.getWidth() );
  }

  protected void drawText( final RenderableText text, final long contentX2 ) {
    if ( text.isNodeVisible( drawArea ) == false ) {
      return;
    }
    if ( text.getLength() == 0 ) {
      // This text is empty.
      return;
    }

    final GlyphList gs = text.getGlyphs();
    final int maxLength = text.computeMaximumTextSize( contentX2 );
    final String rawText = gs.getText( text.getOffset(), maxLength, codePointBuffer );

    final int x = PlainTextPage.correctedDivisionFloor( ( text.getX() - drawArea.getX() ), characterWidthInMicroPoint );
    final int y = PlainTextPage.correctedDivisionFloor( ( text.getY() - drawArea.getY() ), characterHeightInMicroPoint );
    int w = Math.min( maxLength, PlainTextPage.correctedDivisionFloor( text.getWidth(), characterWidthInMicroPoint ) );

    // filter out results that do not belong to the current physical page
    if ( x + w > plainTextPage.getWidth() ) {
      w = Math.max( 0, plainTextPage.getWidth() - x );
    }
    if ( w == 0 ) {
      return;
    }
    if ( y < 0 ) {
      return;
    }
    if ( y >= plainTextPage.getHeight() ) {
      return;
    }
    plainTextPage.addTextChunk( x, y, w, rawText, text.getStyleSheet() );
  }

  protected void drawComplexText( final RenderNode node ) {
    final RenderableComplexText renderableComplexText = (RenderableComplexText) node;

    // The text node that is printed will overlap with the ellipse we need to print.
    if ( renderableComplexText.isNodeVisible( drawArea ) == false ) {
      return;
    }
    if ( renderableComplexText.getRawText().length() == 0 ) {
      // This text is empty.
      return;
    }

    final String text;
    TextLayout textLayout = renderableComplexText.getTextLayout();
    String debugInfo = textLayout.toString();
    String startPos =
        debugInfo.substring( debugInfo.indexOf( "[start:" ), debugInfo.indexOf( ", len:" ) ).replace( "[start:", "" );
    int startPosIntValue = -1;

    try {
      startPosIntValue = Integer.parseInt( startPos );
    } catch ( NumberFormatException e ) {
      // do nothing
    }

    // workaround for line breaking (since the text cannot be extracted directly from textLayout as stream or String)
    // in order to avoid duplicates of same source raw text on multiple lines
    if ( ( renderableComplexText.getRawText().length() > textLayout.getCharacterCount() ) && startPosIntValue >= 0 ) {
      text =
          renderableComplexText.getRawText().substring( startPosIntValue,
              textLayout.getCharacterCount() + startPosIntValue );
    } else {
      text = renderableComplexText.getRawText();
    }

    final int x =
        PlainTextPage.correctedDivisionFloor( ( renderableComplexText.getX() - drawArea.getX() ),
            characterWidthInMicroPoint );
    final int y =
        PlainTextPage.correctedDivisionFloor( ( renderableComplexText.getY() - drawArea.getY() ),
            characterHeightInMicroPoint );
    int w = text.length();

    // filter out results that do not belong to the current physical page
    if ( x + w > plainTextPage.getWidth() ) {
      w = Math.max( 0, plainTextPage.getWidth() - x );
    }
    if ( w == 0 ) {
      return;
    }
    if ( y < 0 ) {
      return;
    }
    if ( y >= plainTextPage.getHeight() ) {
      return;
    }

    plainTextPage.addTextChunk( x, y, w, text, renderableComplexText.getStyleSheet() );
  }

  protected void processOtherNode( final RenderNode node ) {
    if ( ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) == false
        && ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) == false ) {
      return;
    }

    if ( isTextLineOverflow() ) {
      if ( node.isNodeVisible( drawArea ) == false ) {
        return;
      }

      if ( clipOnWordBoundary == false ) {
        if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
          final RenderableText text = (RenderableText) node;
          final long ellipseSize = extractEllipseSize( node );
          final long x1 = text.getX();
          final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );

          if ( x1 < contentAreaX2 ) {
            // The text node that is printed will overlap with the ellipse we need to print.
            drawText( text, effectiveAreaX2 );
          }
        } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
          final RenderableComplexText text = (RenderableComplexText) node;
          final long x1 = text.getX();

          if ( x1 < contentAreaX2 ) {
            drawComplexText( node );
          }
        }
      }

      if ( node.isVirtualNode() ) {
        if ( ellipseDrawn ) {
          return;
        }
        ellipseDrawn = true;

        final RenderBox parent = node.getParent();
        if ( parent != null ) {
          final RenderBox textEllipseBox = parent.getTextEllipseBox();
          if ( textEllipseBox != null ) {
            processBoxChilds( textEllipseBox );
          }
        }
        return;
      }
    }

    if ( isTextLineOverflow() ) {
      if ( node.isNodeVisible( drawArea ) == false ) {
        return;
      }

      final long ellipseSize = extractEllipseSize( node );
      final long x1 = node.getX();
      final long x2 = x1 + node.getWidth();
      final long effectiveAreaX2 = ( contentAreaX2 - ellipseSize );
      if ( x2 <= effectiveAreaX2 ) {
        // the text will be fully visible.
        if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
          drawText( (RenderableText) node );
        } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
          drawComplexText( node );
        }
      } else if ( x1 < contentAreaX2 ) {
        // The text node that is printed will overlap with the ellipse we need to print.
        if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
          drawText( (RenderableText) node, effectiveAreaX2 );
        } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
          drawComplexText( node );
        }

        final RenderBox parent = node.getParent();
        if ( parent != null ) {
          final RenderBox textEllipseBox = parent.getTextEllipseBox();
          if ( textEllipseBox != null ) {
            processBoxChilds( textEllipseBox );
          }
        }
      }

    } else {
      if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_TEXT ) {
        drawText( (RenderableText) node );
      } else if ( node.getNodeType() == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
        drawComplexText( node );
      }
    }
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

  protected void processParagraphChilds( final ParagraphRenderBox box ) {
    this.contentAreaX1 = box.getContentAreaX1();
    this.contentAreaX2 = box.getContentAreaX2();

    RenderBox lineBox = (RenderBox) box.getFirstChild();
    while ( lineBox != null ) {
      processTextLine( lineBox, contentAreaX1, contentAreaX2 );
      lineBox = (RenderBox) lineBox.getNext();
    }
  }

  protected void processTextLine( final RenderBox lineBox, final long contentAreaX1, final long contentAreaX2 ) {
    final boolean overflowProperty = lineBox.getParent().getStaticBoxLayoutProperties().isOverflowX();
    this.textLineOverflow = ( ( lineBox.getX() + lineBox.getWidth() ) > contentAreaX2 ) && overflowProperty == false;

    this.ellipseDrawn = false;
    if ( textLineOverflow ) {
      revalidateTextEllipseProcessStep.compute( lineBox, contentAreaX1, contentAreaX2 );
    }

    startProcessing( lineBox );
  }

  public long getContentAreaX2() {
    return contentAreaX2;
  }

  public void setContentAreaX2( final long contentAreaX2 ) {
    this.contentAreaX2 = contentAreaX2;
  }

  public long getContentAreaX1() {
    return contentAreaX1;
  }

  public void setContentAreaX1( final long contentAreaX1 ) {
    this.contentAreaX1 = contentAreaX1;
  }

  public boolean isTextLineOverflow() {
    return textLineOverflow;
  }

  public void setTextLineOverflow( final boolean textLineOverflow ) {
    this.textLineOverflow = textLineOverflow;
  }
}
