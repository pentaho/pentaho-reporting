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
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableComplexText;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.RenderUtility;
import org.pentaho.reporting.engine.classic.core.layout.process.AbstractMinorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.IterateSimpleStructureProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.util.MinorAxisNodeContext;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;

public class ComplexTextMinorAxisLayoutStep extends IterateSimpleStructureProcessStep implements
    TextMinorAxisLayoutStep {

  private static final Log logger = LogFactory.getLog( ComplexTextMinorAxisLayoutStep.class );
  private final boolean strictCompatibility;
  private final OutputProcessorMetaData metaData;
  private final ResourceManager resourceManager;

  private MinorAxisNodeContext nodeContext;

  public ComplexTextMinorAxisLayoutStep( final OutputProcessorMetaData metaData, final ResourceManager resourceManager ) {
    this.resourceManager = resourceManager;
    ArgumentNullException.validate( "metaData", metaData );

    this.metaData = metaData;
    this.strictCompatibility = getMetaData().isFeatureSupported( OutputProcessorFeature.STRICT_COMPATIBILITY );
  }

  public void process( final ParagraphRenderBox box, final MinorAxisNodeContext nodeContext, final PageGrid pageGrid ) {
    this.nodeContext = nodeContext;
    processParagraphChildsComplex( box );
  }

  public MinorAxisNodeContext getNodeContext() {
    return nodeContext;
  }

  public OutputProcessorMetaData getMetaData() {
    return metaData;
  }

  protected void processParagraphChildsComplex( final ParagraphRenderBox box ) {
    // Clear the paragraph to throw away previously layouted nodes. This leaves the
    // paragraph's pool (where your original text is stored) untouched.
    box.clearLayout();

    if ( box.isComplexParagraph() ) {
      final RenderBox lineBoxContainer = box.getLineboxContainer();
      final StyleSheet layoutContext = box.getStyleSheet();

      RenderNode paragraphContainer = lineBoxContainer.getFirstChild();
      while ( paragraphContainer != null ) {
        if ( paragraphContainer.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX ) {
          throw new IllegalStateException( "Expected ParagraphPoolBox elements." );
        }

        final ParagraphPoolBox paragraph = (ParagraphPoolBox) paragraphContainer;
        addGeneratedComplexTextLines( box, paragraph, layoutContext );

        paragraphContainer = paragraphContainer.getNext();
      }
    } else {
      final ParagraphPoolBox lineBoxContainer = (ParagraphPoolBox) box.getEffectiveLineboxContainer();
      final StyleSheet layoutContext = box.getStyleSheet();

      addGeneratedComplexTextLines( box, lineBoxContainer, layoutContext );
    }
  }

  private FontRenderContext createFontRenderContext( final StyleSheet layoutContext ) {
    final boolean antiAliasing = RenderUtility.isFontSmooth( layoutContext, getMetaData() );
    return new FontRenderContext( null, antiAliasing, true );
  }

  private void addGeneratedComplexTextLines( final ParagraphRenderBox box, final ParagraphPoolBox lineBoxContainer,
      final StyleSheet layoutContext ) {
    updateNodeContextWidth( box );

    // Determine if anti-aliasing is required or not
    if ( TextWrap.NONE.equals( box.getStyleSheet().getStyleProperty( TextStyleKeys.TEXT_WRAP ) ) ) {
      addCompleteLine( box, lineBoxContainer, layoutContext );
      return;
    }

    // Create a LineBreakMeasurer to break down that string into lines.
    final RichTextSpec richText = RichTextSpecProducer.compute( lineBoxContainer, metaData, resourceManager );
    final LineBreakIterator lineIterator = createLineBreakIterator( box, layoutContext, richText );

    ArrayList<RenderableComplexText> lines = new ArrayList<RenderableComplexText>();
    ParagraphFontMetricsImpl metrics = new ParagraphFontMetricsImpl();
    while ( lineIterator.hasNext() ) {
      final LineBreakIteratorState state = lineIterator.next();

      final RenderableComplexText text = richText.create( lineBoxContainer, state.getStart(), state.getEnd() );
      text.setTextLayout( state.getTextLayout() );
      metrics.update( state.getTextLayout() );
      // and finally add the line to the paragraph
      lines.add( text );
    }

    final double height = metrics.getLineHeight();
    for ( RenderableComplexText text : lines ) {
      final RenderBox line = generateLine( box, lineBoxContainer, text, height, metrics );
      box.addGeneratedChild( line );
    }
  }

  private LineBreakIterator createLineBreakIterator( final ParagraphRenderBox box, final StyleSheet layoutContext,
      final RichTextSpec richText ) {
    final AttributedCharacterIterator ci = richText.createAttributedCharacterIterator();
    final FontRenderContext fontRenderContext = createFontRenderContext( layoutContext );
    final boolean breakOnWordBoundary =
        strictCompatibility || layoutContext.getBooleanStyleProperty( TextStyleKeys.WORDBREAK );

    if ( breakOnWordBoundary ) {
      return new WordBreakingLineIterator( box, fontRenderContext, ci, richText.getText() );
    } else {
      return new LineBreakIterator( box, fontRenderContext, ci );
    }
  }

  private void addCompleteLine( final ParagraphRenderBox box, final ParagraphPoolBox lineBoxContainer,
      final StyleSheet layoutContext ) {
    RichTextSpec richText = RichTextSpecProducer.compute( lineBoxContainer, metaData, resourceManager );

    final FontRenderContext fontRenderContext = createFontRenderContext( layoutContext );
    final TextLayout textLayout = new TextLayout( richText.createAttributedCharacterIterator(), fontRenderContext );
    double height = textLayout.getAscent() + textLayout.getDescent() + textLayout.getLeading();

    final RenderableComplexText text = richText.create( lineBoxContainer );
    text.setTextLayout( textLayout );
    ParagraphFontMetricsImpl metrics = new ParagraphFontMetricsImpl();
    metrics.update( textLayout );
    final RenderBox line = generateLine( box, lineBoxContainer, text, height, metrics );
    // and finally add the line to the paragraph
    getNodeContext().updateX2( line.getCachedX2() );
    box.addGeneratedChild( line );
  }

  private RenderBox generateLine( final ParagraphRenderBox paragraph, final ParagraphPoolBox lineBoxContainer,
      final RenderableComplexText text, final double height, final ParagraphFontMetricsImpl metrics ) {
    // derive a new RenderableComplexText object representing the line, that holds on to the TextLayout class.
    TextLayout textLayout = text.getTextLayout();

    // Store the height and width, so that the other parts of the layouter have access to the information
    // text.setCachedHeight();
    text.setCachedHeight( Math.max( StrictGeomUtility.toInternalValue( height ), lineBoxContainer.getLineHeight() ) );
    text.setCachedWidth( StrictGeomUtility.toInternalValue( textLayout.getAdvance() ) );
    text.setParagraphFontMetrics( metrics );

    MinorAxisNodeContext nodeContext = getNodeContext();
    final long alignmentX =
        RenderUtility.computeHorizontalAlignment( paragraph.getTextAlignment(), nodeContext.getContentAreaWidth(),
            StrictGeomUtility.toInternalValue( textLayout.getAdvance() ) );
    text.setCachedX( alignmentX + nodeContext.getX() );

    // Create a shallow copy of the paragraph-pool to act as a line container.
    final RenderBox line = (RenderBox) paragraph.getPool().deriveFrozen( false );
    line.addGeneratedChild( text );

    // Align the line inside the paragraph. (Adjust the cachedX position depending on whether the line is left,
    // centred or right aligned)
    line.setCachedX( alignmentX + nodeContext.getX() );
    line.setCachedWidth( nodeContext.getContentAreaWidth() );
    return line;
  }

  private void updateNodeContextWidth( final ParagraphRenderBox paragraph ) {
    MinorAxisNodeContext nodeContext = getNodeContext();
    final long lineEnd;
    final boolean overflowX = paragraph.getStaticBoxLayoutProperties().isOverflowX();
    if ( overflowX ) {
      lineEnd = nodeContext.getX1() + AbstractMinorAxisLayoutStep.OVERFLOW_DUMMY_WIDTH;
    } else {
      lineEnd = nodeContext.getX2();
    }

    long firstLineIndent = 0; // todo
    long lineStart = Math.min( lineEnd, nodeContext.getX1() + firstLineIndent );
    if ( lineEnd - lineStart <= 0 ) {
      final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
      nodeContext.updateX2( lineStart + minimumChunkWidth );
      logger.warn( "Auto-Corrected zero-width first-line on paragraph - " + paragraph.getName() );
    } else {
      if ( overflowX == false ) {
        nodeContext.updateX2( lineEnd );
      }
    }
  }

}
