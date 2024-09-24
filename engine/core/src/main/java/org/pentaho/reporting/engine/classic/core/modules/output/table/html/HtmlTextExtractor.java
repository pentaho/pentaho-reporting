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
 * Copyright (c) 2001 - 2020 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
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
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.text.RichTextSpec;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.DefaultTextExtractor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlOutputProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTagHelper;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTextExtractorHelper;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.HtmlTextExtractorState;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper.StyleBuilder;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.HtmlCharacterEntities;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;

/**
 * Creation-Date: 02.11.2007, 15:58:29
 *
 * @author Thomas Morgner
 */
public class HtmlTextExtractor extends DefaultTextExtractor {
  private static final String DIV_TAG = "div";
  private static final String BR_TAG = "br";

  private XmlWriter xmlWriter;
  private StyleBuilder styleBuilder;
  private CharacterEntityParser characterEntityParser;
  private boolean result;
  private HtmlTextExtractorState processStack;
  private HtmlTextExtractorHelper textExtractorHelper;

  public HtmlTextExtractor( final OutputProcessorMetaData metaData, final XmlWriter xmlWriter,
      final HtmlContentGenerator contentGenerator, final HtmlTagHelper tagHelper ) {
    super( metaData );
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( contentGenerator == null ) {
      throw new NullPointerException();
    }

    this.xmlWriter = xmlWriter;
    this.styleBuilder = tagHelper.getStyleBuilder();
    this.characterEntityParser = HtmlCharacterEntities.getEntityParser();
    this.textExtractorHelper = new HtmlTextExtractorHelper( tagHelper, xmlWriter, metaData, contentGenerator );
  }

  public boolean performOutput( final RenderBox content, final StyleBuilder.StyleCarrier[] cellStyle )
    throws IOException {
    return performOutput( content, cellStyle, true );
  }

  public boolean performOutput( final RenderBox content, final StyleBuilder.StyleCarrier[] cellStyle, boolean writeAttrs )
    throws IOException {
    styleBuilder.clear();
    clearText();
    setRawResult( null );
    result = false;
    processStack = new HtmlTextExtractorState( null, false, cellStyle );
    textExtractorHelper.setFirstElement( content.getInstanceId(), processStack );

    try {
      final int nodeType = content.getNodeType();
      if ( nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH ) {
        processInitialBox( (ParagraphRenderBox) content );
      } else if ( nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT ) {
        processRenderableContent( (RenderableReplacedContentBox) content, writeAttrs );
      } else {
        processBoxChilds( content );
      }
    } finally {
      processStack = null;
    }
    return result;
  }

  /**
   * Prints the contents of a canvas box. This can happen only once per cell, as every canvas box creates its own cell
   * at some point. If for some strange reason a canvas box appears in the middle of a box-structure, your layouter is
   * probably a mess and this method will treat the box as a generic content container.
   *
   * @param box
   *          the canvas box
   * @return true, if the child content will be processed, false otherwise.
   */
  protected boolean startCanvasBox( final CanvasRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return textExtractorHelper.startBox( box.getInstanceId(), box.getAttributes(), box.getStyleSheet(), box
        .getBoxDefinition(), false );
  }

  protected void finishCanvasBox( final CanvasRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return;
    }

    textExtractorHelper.finishBox( box.getInstanceId(), box.getAttributes() );
  }

  /**
   * Prints a paragraph cell. This is a special entry point used by the processContent method and is never called from
   * elsewhere. This method assumes that the attributes of the paragraph have been processed as part of the table-cell
   * processing.
   *
   * @param box
   *          the paragraph box
   * @throws IOException
   *           if an IO error occured.
   */
  protected void processInitialBox( final ParagraphRenderBox box ) throws IOException {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return;
    }

    final StyleSheet styleSheet = box.getStyleSheet();
    final String target = (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TARGET );
    if ( target != null ) {
      textExtractorHelper.handleLinkOnElement( styleSheet, target );
      processStack = new HtmlTextExtractorState( processStack, true );
    } else {
      processStack = new HtmlTextExtractorState( processStack, false );
    }

    if ( Boolean.TRUE.equals( box.getAttributes().getAttribute( AttributeNames.Html.NAMESPACE,
        AttributeNames.Html.SUPPRESS_CONTENT ) ) == false ) {
      processParagraphChilds( box );
    }

    if ( processStack.isWrittenTag() ) {
      xmlWriter.writeCloseTag();
    }
    processStack = processStack.getParent();

  }

  protected void addEmptyBreak() {
    try {
      xmlWriter.writeText( "\u200B" );
    } catch ( final IOException e ) {
      throw new HtmlOutputProcessingException( "Failed to perform IO", e );
    }
  }

  protected void addSoftBreak() {
    try {
      xmlWriter.writeText( " " );
    } catch ( final IOException e ) {
      throw new HtmlOutputProcessingException( "Failed to perform IO", e );
    }
  }

  protected void addLinebreak() {
    try {
      result = true;
      xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, BR_TAG, XmlWriterSupport.CLOSE );
    } catch ( final IOException e ) {
      throw new HtmlOutputProcessingException( "Failed to perform IO", e );
    }
  }

  protected boolean startBlockBox( final BlockRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return textExtractorHelper.startBox( box.getInstanceId(), box.getAttributes(), box.getStyleSheet(), box
        .getBoxDefinition(), true );
  }

  protected void finishBlockBox( final BlockRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return;
    }

    textExtractorHelper.finishBox( box.getInstanceId(), box.getAttributes() );
  }

  /**
   * Like a canvas box, a row-box should be split into several cells already. Therefore we treat it as a generic content
   * container instead.
   */
  protected boolean startRowBox( final RenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }

    return textExtractorHelper.startBox( box.getInstanceId(), box.getAttributes(), box.getStyleSheet(), box
        .getBoxDefinition(), true );
  }

  protected void finishRowBox( final RenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return;
    }

    textExtractorHelper.finishBox( box.getInstanceId(), box.getAttributes() );
  }

  protected boolean startInlineBox( final InlineRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return false;
    }
    return textExtractorHelper.startInlineBox( box.getInstanceId(), box.getAttributes(), box.getStyleSheet(), box
        .getBoxDefinition() );
  }

  protected void finishInlineBox( final InlineRenderBox box ) {
    if ( box.getStaticBoxLayoutProperties().isVisible() == false ) {
      return;
    }

    textExtractorHelper.finishBox( box.getInstanceId(), box.getAttributes() );
  }

  protected void processOtherNode( final RenderNode node ) {
    try {
      final int nodeType = node.getNodeType();
      if ( nodeType == LayoutNodeTypes.TYPE_NODE_TEXT || nodeType == LayoutNodeTypes.TYPE_NODE_COMPLEX_TEXT ) {
        super.processOtherNode( node );
        return;
      }

      if ( node.isVirtualNode() ) {
        return;
      }

      if ( nodeType == LayoutNodeTypes.TYPE_NODE_SPACER ) {
        final SpacerRenderNode spacer = (SpacerRenderNode) node;
        final int count = Math.max( 1, spacer.getSpaceCount() );
        for ( int i = 0; i < count; i++ ) {
          xmlWriter.writeText( " " );
        }
      }
    } catch ( final IOException e ) {
      throw new RuntimeException( "Failed", e );
    }
  }

  protected void processRenderableContent( final RenderableReplacedContentBox node ) {
    processRenderableContent( node, true );
  }

  protected void processRenderableContent( final RenderableReplacedContentBox node, boolean writeAttrs ) {
    try {
      final ReportAttributeMap map = node.getAttributes();
      final AttributeList attrs = new AttributeList();
      HtmlTagHelper.applyHtmlAttributes( map, attrs );
      if ( !attrs.isEmpty() ) {
        if ( writeAttrs ) {
          xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, attrs, XmlWriterSupport.OPEN );
        } else {
          xmlWriter.writeTag( HtmlPrinter.XHTML_NAMESPACE, DIV_TAG, new AttributeList(), XmlWriterSupport.OPEN );
        }
      }

      textExtractorHelper.writeLocalAnchor( node.getStyleSheet() );

      final StyleSheet styleSheet = node.getStyleSheet();
      final String target = (String) styleSheet.getStyleProperty( ElementStyleKeys.HREF_TARGET );
      if ( target != null ) {
        textExtractorHelper.handleLinkOnElement( styleSheet, target );
      }

      processReplacedContent( node );

      if ( target != null ) {
        xmlWriter.writeCloseTag();
      }
      if ( attrs.isEmpty() == false ) {
        xmlWriter.writeCloseTag();
      }
    } catch ( final IOException e ) {
      throw new RuntimeException( "Failed", e );
    } catch ( final ContentIOException e ) {
      throw new RuntimeException( "Failed", e );
    }
  }

  /**
   * @noinspection StringConcatenation
   */
  private void processReplacedContent( final RenderableReplacedContentBox node ) throws IOException, ContentIOException {

    final RenderableReplacedContent rc = node.getContent();
    final ReportAttributeMap attrs = node.getAttributes();
    final long width = node.getWidth();
    final long height = node.getHeight();
    final long contentWidth = rc.getContentWidth();
    final long contentHeight = rc.getContentHeight();
    final StyleSheet styleSheet = node.getStyleSheet();

    final Object rawObject = rc.getRawObject();
    // We have to do three things here. First, we have to check what kind
    // of content we deal with.
    if ( textExtractorHelper.processRenderableReplacedContent( attrs, styleSheet, width, height, contentWidth,
        contentHeight, rawObject ) ) {
      result = true;
    }
  }

  protected void drawText( final RenderableText renderableText, final long contentX2 ) {
    try {

      if ( renderableText.getLength() == 0 ) {
        // This text is empty.
        return;
      }
      if ( renderableText.isNodeVisible( getParagraphBounds(), isOverflowX(), isOverflowY() ) == false ) {
        return;
      }

      final String text;
      final GlyphList gs = renderableText.getGlyphs();
      final int maxLength = renderableText.computeMaximumTextSize( contentX2 );
      text = gs.getText( renderableText.getOffset(), maxLength, getCodePointBuffer() );

      if ( text.length() > 0 ) {
        xmlWriter.writeText( characterEntityParser.encodeEntities( text ) );
        if ( text.trim().length() > 0 ) {
          result = true;
        }
        clearText();
      }
    } catch ( final IOException ioe ) {
      throw new InvalidReportStateException( "Failed to write text", ioe );
    }
  }

  protected void drawComplexText( final RenderableComplexText renderableComplexText ) {
    try {

      if ( renderableComplexText.getRichText().isEmpty() ) {
        // This text is empty.
        return;
      }
      if ( renderableComplexText.isNodeVisible( getParagraphBounds(), isOverflowX(), isOverflowY() ) == false ) {
        return;
      }

      // iterate through all inline elements
      for ( final RichTextSpec.StyledChunk styledChunk : renderableComplexText.getRichText().getStyleChunks() ) {
        RenderNode node = styledChunk.getOriginatingTextNode();
        InstanceID dummy = node.getInstanceId();
        textExtractorHelper.startInlineBox( dummy, styledChunk.getOriginalAttributes(), styledChunk.getStyleSheet(),
            BoxDefinition.EMPTY );
        if ( node instanceof RenderableReplacedContentBox ) {
          processRenderableContent( (RenderableReplacedContentBox) node );
          result = true;
        } else {
          String text = styledChunk.getText();
          xmlWriter.writeText( characterEntityParser.encodeEntities( text ) );
          if ( text.trim().length() > 0 ) {
            result = true;
          }
        }
        textExtractorHelper.finishBox( dummy, styledChunk.getOriginalAttributes() );
        clearText();

      }
    } catch ( final IOException ioe ) {
      throw new InvalidReportStateException( "Failed to write text", ioe );
    }

  }
}
