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

package org.pentaho.reporting.engine.classic.core.layout.text;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableText;
import org.pentaho.reporting.engine.classic.core.layout.model.SpacerRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;
import org.pentaho.reporting.libraries.fonts.text.DefaultLanguageClassifier;
import org.pentaho.reporting.libraries.fonts.text.GraphemeClusterProducer;
import org.pentaho.reporting.libraries.fonts.text.LanguageClassifier;
import org.pentaho.reporting.libraries.fonts.text.Spacing;
import org.pentaho.reporting.libraries.fonts.text.SpacingProducer;
import org.pentaho.reporting.libraries.fonts.text.StaticSpacingProducer;
import org.pentaho.reporting.libraries.fonts.text.breaks.BreakOpportunityProducer;
import org.pentaho.reporting.libraries.fonts.text.breaks.LineBreakProducer;
import org.pentaho.reporting.libraries.fonts.text.breaks.WordBreakProducer;
import org.pentaho.reporting.libraries.fonts.text.classifier.GlyphClassificationProducer;
import org.pentaho.reporting.libraries.fonts.text.classifier.WhitespaceClassificationProducer;
import org.pentaho.reporting.libraries.fonts.text.font.FontSizeProducer;
import org.pentaho.reporting.libraries.fonts.text.font.GlyphMetrics;
import org.pentaho.reporting.libraries.fonts.text.font.KerningProducer;
import org.pentaho.reporting.libraries.fonts.text.font.NoKerningProducer;
import org.pentaho.reporting.libraries.fonts.text.font.VariableFontSizeProducer;
import org.pentaho.reporting.libraries.fonts.text.whitespace.CollapseWhiteSpaceFilter;
import org.pentaho.reporting.libraries.fonts.text.whitespace.DiscardWhiteSpaceFilter;
import org.pentaho.reporting.libraries.fonts.text.whitespace.PreserveBreaksWhiteSpaceFilter;
import org.pentaho.reporting.libraries.fonts.text.whitespace.PreserveWhiteSpaceFilter;
import org.pentaho.reporting.libraries.fonts.text.whitespace.WhiteSpaceFilter;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * Creation-Date: 03.04.2007, 16:43:48
 *
 * @author Thomas Morgner
 */
public final class DefaultRenderableTextFactory implements RenderableTextFactory {
  private static final RenderNode[] EMPTY_RENDER_NODE = new RenderNode[0];
  private static final RenderableText[] EMPTY_TEXT = new RenderableText[0];
  private static final GlyphList EMPTY_GLYPHS = new GlyphList( 1 ).lock();
  private static final int[] END_OF_TEXT = new int[] { ClassificationProducer.END_OF_TEXT };

  private GraphemeClusterProducer clusterProducer;
  private boolean startText;
  private FontSizeProducer fontSizeProducer;
  private KerningProducer kerningProducer;

  private SpacingProducer spacingProducer;
  private Spacing spacingProducerKey;

  private BreakOpportunityProducer breakOpportunityProducer;
  private WhiteSpaceFilter whitespaceFilter;
  private GlyphClassificationProducer classificationProducer;
  private StyleSheet layoutContext;
  private LanguageClassifier languageClassifier;

  private transient GlyphMetrics dims;

  private ArrayList<RenderNode> words;
  private GlyphList glyphList;
  private long leadingMargin;
  private int spaceCount;
  private int lastLanguage;

  private transient FontMetrics fontMetrics;
  private OutputProcessorMetaData metaData;

  // cached instance ..
  private NoKerningProducer noKerningProducer;

  private WhitespaceCollapse whitespaceFilterValue;
  private WhitespaceCollapse whitespaceCollapseValue;
  private TextWrap breakOpportunityValue;
  private long wordSpacing;
  private ReportAttributeMap<Object> attributeMap;
  private ElementType elementType;
  private ExtendedBaselineInfo uniformBaselineInfo;
  private InstanceID instanceId;

  public DefaultRenderableTextFactory( final OutputProcessorMetaData metaData ) {
    this.metaData = metaData;
    this.clusterProducer = new GraphemeClusterProducer();
    this.languageClassifier = new DefaultLanguageClassifier();
    this.startText = true;
    this.words = new ArrayList<RenderNode>( 20 );
    this.dims = new GlyphMetrics();
    this.noKerningProducer = new NoKerningProducer();
    this.spacingProducer = new StaticSpacingProducer( Spacing.EMPTY_SPACING );
    this.spacingProducerKey = Spacing.EMPTY_SPACING;
    this.glyphList = new GlyphList( 100 );
  }

  /**
   * The text is given as CodePoints.
   *
   * @param text
   * @return
   */
  public RenderNode[] createText( final int[] text, final int offset, final int length, final StyleSheet layoutContext,
      final ElementType elementType, final InstanceID instanceId, final ReportAttributeMap<Object> attributeMap ) {
    this.instanceId = instanceId;
    if ( layoutContext == null ) {
      throw new NullPointerException();
    }
    if ( attributeMap == null ) {
      throw new NullPointerException();
    }
    if ( elementType == null ) {
      throw new NullPointerException();
    }
    if ( text == null ) {
      throw new NullPointerException();
    }
    this.layoutContext = layoutContext;
    // this.parentLayoutContext = new NodeLayoutProperties(majorAxis, minorAxis, layoutContext);
    this.elementType = elementType;
    this.attributeMap = attributeMap;
    this.fontMetrics = metaData.getFontMetrics( layoutContext );
    this.uniformBaselineInfo = null;
    kerningProducer = createKerningProducer( layoutContext );
    fontSizeProducer = createFontSizeProducer( layoutContext );
    spacingProducer = createSpacingProducer( layoutContext );
    breakOpportunityProducer = createBreakProducer( layoutContext );
    whitespaceFilter = createWhitespaceFilter( layoutContext );
    classificationProducer = createGlyphClassifier( layoutContext );
    this.layoutContext = layoutContext;

    if ( metaData.isFeatureSupported( OutputProcessorFeature.SPACING_SUPPORTED ) ) {
      this.wordSpacing =
          FontStrictGeomUtility.toInternalValue( layoutContext.getDoubleStyleProperty( TextStyleKeys.WORD_SPACING, 0 ) );
    } else {
      this.wordSpacing = 0;
    }

    if ( startText ) {
      whitespaceFilter.filter( ClassificationProducer.START_OF_TEXT );
      breakOpportunityProducer.createBreakOpportunity( ClassificationProducer.START_OF_TEXT );
      kerningProducer.getKerning( ClassificationProducer.START_OF_TEXT );
      startText = false;
    }

    return processText( text, offset, length );
  }

  protected RenderNode[] processText( final int[] text, final int offset, final int length ) {
    final int maxLen = Math.min( length + offset, text.length );
    int clusterStartIdx = offset < maxLen ? 0 : -1;
    for ( int i = offset; i < maxLen; i++ ) {
      final int codePoint = text[i];
      final boolean clusterStarted = this.clusterProducer.createGraphemeCluster( codePoint );
      // ignore the first cluster start; we need to see the whole cluster.
      if ( clusterStarted ) {
        if ( i > offset ) {
          final int extraCharLength = i - clusterStartIdx - 1;
          addGlyph( text, clusterStartIdx, extraCharLength );
        }

        clusterStartIdx = i;
      }
    }

    // Process the last cluster ...
    if ( clusterStartIdx >= offset ) {
      final int extraCharLength = maxLen - clusterStartIdx - 1;
      addGlyph( text, clusterStartIdx, extraCharLength );
    }

    if ( words.isEmpty() == false ) {
      final RenderNode[] renderableTexts = words.toArray( new RenderNode[words.size()] );
      words.clear();
      return renderableTexts;
    } else {
      // we did not produce any text.
      return DefaultRenderableTextFactory.EMPTY_RENDER_NODE;
    }
  }

  protected void addGlyph( final int[] text, final int offset, final int extraCharCount ) {
    // Log.debug ("Processing " + rawCodePoint);
    final int rawCodePoint = text[offset];
    if ( rawCodePoint == ClassificationProducer.END_OF_TEXT ) {
      whitespaceFilter.filter( rawCodePoint );
      classificationProducer.getClassification( rawCodePoint );
      kerningProducer.getKerning( rawCodePoint );
      breakOpportunityProducer.createBreakOpportunity( rawCodePoint );
      spacingProducer.createSpacing( rawCodePoint );
      fontSizeProducer.getCharacterSize( rawCodePoint, dims );

      if ( leadingMargin > 0 || glyphList.getSize() != 0 ) {
        addWord( false );
      } else {
        // finish up ..
        glyphList.clear();
        leadingMargin = 0;
        spaceCount = 0;
      }
      return;
    }

    int codePoint = whitespaceFilter.filter( rawCodePoint );

    // No matter whether we will ignore the result, we have to keep our
    // factories up and running. These beasts need to see all data, no
    // matter what get printed later.
    if ( codePoint == WhiteSpaceFilter.STRIP_WHITESPACE ) {
      // if we dont have extra-chars, ignore the thing ..
      if ( extraCharCount == 0 ) {
        return;
      } else {
        // convert it into a space. This might be invalid, but will work for now.
        codePoint = DiscardWhiteSpaceFilter.ZERO_WIDTH;
      }
    }

    int glyphClassification = classificationProducer.getClassification( codePoint );
    final long kerning = kerningProducer.getKerning( codePoint );
    int breakweight = breakOpportunityProducer.createBreakOpportunity( codePoint );
    final Spacing spacing = spacingProducer.createSpacing( codePoint );
    dims = fontSizeProducer.getCharacterSize( codePoint, dims );
    int width = dims.getWidth();
    int height = dims.getHeight();
    lastLanguage = languageClassifier.getScript( codePoint );

    for ( int i = 0; i < extraCharCount; i++ ) {
      final int extraChar = text[offset + i + 1];
      dims = fontSizeProducer.getCharacterSize( extraChar, dims );
      width = Math.max( width, ( dims.getWidth() & 0x7FFFFFFF ) );
      height = Math.max( height, ( dims.getHeight() & 0x7FFFFFFF ) );
      breakweight = breakOpportunityProducer.createBreakOpportunity( extraChar );
      glyphClassification = classificationProducer.getClassification( extraChar );
    }

    if ( ( Glyph.SPACE_CHAR == glyphClassification ) && isWordBreak( breakweight ) ) {

      // Finish the current word ...
      final boolean forceLinebreak = breakweight == BreakOpportunityProducer.BREAK_LINE;
      if ( glyphList.isEmpty() == false || forceLinebreak ) {
        addWord( forceLinebreak );
        if ( forceLinebreak ) {
          return;
        }
      }

      // This character can be stripped. We increase the leading margin of the
      // next word by the character's width.
      leadingMargin += width + wordSpacing;
      spaceCount += 1;
      // Log.debug ("Increasing Margin");
      return;
    }

    // final Glyph glyph = new DefaultGlyph(codePoint, breakweight, glyphClassification, spacing, width, height,
    // dims.getBaselinePosition(), (int) kerning, extraChars);
    glyphList.addGlyphData( text, offset, extraCharCount + 1, breakweight, glyphClassification, spacing, width, height,
        dims.getBaselinePosition(), (int) kerning );
    // Log.debug ("Adding Glyph");

    // does this finish a word? Check it!
    if ( isWordBreak( breakweight ) ) {
      final boolean forceLinebreak = breakweight == BreakOpportunityProducer.BREAK_LINE;
      addWord( forceLinebreak );
    }
  }

  private ExtendedBaselineInfo getBaselineInfo( final int character ) {
    if ( uniformBaselineInfo != null ) {
      return uniformBaselineInfo;
    }

    final ExtendedBaselineInfo baselineInfo = metaData.getBaselineInfo( character, layoutContext );
    if ( fontMetrics.isUniformFontMetrics() ) {
      uniformBaselineInfo = baselineInfo;
    }
    return baselineInfo;
  }

  protected void addWord( final boolean forceLinebreak ) {
    if ( glyphList.isEmpty() ) {
      // This is a forced linebreak, caused by a \n somewhere at the beginning of the text or after a whitespace.
      // If there is a preservable whitespace, the leading margin will be non-zero.
      if ( leadingMargin > 0 ) {
        final SpacerRenderNode spacer =
            new SpacerRenderNode( RenderableText.convert( leadingMargin ), 0, true, spaceCount );
        words.add( spacer );
      }
      if ( forceLinebreak ) {
        final ExtendedBaselineInfo info = getBaselineInfo( '\n' );
        // / TextUtility.createBaselineInfo('\n', fontMetrics, baselineInfo);
        final RenderableText text =
            new RenderableText( layoutContext, elementType, instanceId, attributeMap, info,
                DefaultRenderableTextFactory.EMPTY_GLYPHS, 0, 0, lastLanguage, true );
        words.add( text );
      }
      leadingMargin = 0;
      spaceCount = 0;
      return;
    }

    // final DefaultGlyph[] glyphs = (DefaultGlyph[]) glyphList.toArray(new DefaultGlyph[glyphList.size()]);
    if ( leadingMargin > 0 ) {
      final SpacerRenderNode spacer =
          new SpacerRenderNode( RenderableText.convert( leadingMargin ), 0, true, spaceCount );
      words.add( spacer );
    }

    // Compute a suitable text-metrics object for this text. We simply assume that the first character is representive
    // for all characters of the text chunk. This may be a wrong assumption in complex-text environments but will work
    // for now.
    final int codePoint = glyphList.getGlyph( 0 ).getCodepoint();

    final ExtendedBaselineInfo baselineInfo = getBaselineInfo( codePoint );
    // final ExtendedBaselineInfo baselineInfo = TextUtility.createBaselineInfo(codePoint, fontMetrics, this
    // .baselineInfo);
    final RenderableText text =
        new RenderableText( layoutContext, elementType, instanceId, attributeMap, baselineInfo, glyphList.lock(), 0,
            glyphList.getSize(), lastLanguage, forceLinebreak );
    words.add( text );

    glyphList.clear();
    leadingMargin = 0;
    spaceCount = 0;
  }

  private boolean isWordBreak( final int breakOp ) {
    if ( BreakOpportunityProducer.BREAK_WORD == breakOp || BreakOpportunityProducer.BREAK_LINE == breakOp ) {
      return true;
    }
    return false;
  }

  protected WhiteSpaceFilter createWhitespaceFilter( final StyleSheet layoutContext ) {
    final WhitespaceCollapse wsColl =
        (WhitespaceCollapse) layoutContext.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE );

    if ( whitespaceFilter != null ) {
      if ( ObjectUtilities.equal( whitespaceFilterValue, wsColl ) ) {
        whitespaceFilter.reset();
        return whitespaceFilter;
      }
    }

    whitespaceFilterValue = wsColl;

    if ( WhitespaceCollapse.DISCARD.equals( wsColl ) ) {
      return new DiscardWhiteSpaceFilter();
    }
    if ( WhitespaceCollapse.PRESERVE.equals( wsColl ) ) {
      return new PreserveWhiteSpaceFilter();
    }
    if ( WhitespaceCollapse.PRESERVE_BREAKS.equals( wsColl ) ) {
      return new PreserveBreaksWhiteSpaceFilter();
    }
    return new CollapseWhiteSpaceFilter();
  }

  protected GlyphClassificationProducer createGlyphClassifier( final StyleSheet layoutContext ) {
    final WhitespaceCollapse wsColl =
        (WhitespaceCollapse) layoutContext.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE );
    if ( classificationProducer != null ) {
      if ( ObjectUtilities.equal( whitespaceCollapseValue, wsColl ) ) {
        classificationProducer.reset();
        return classificationProducer;
      }
    }

    whitespaceCollapseValue = wsColl;

    // if (WhitespaceCollapse.PRESERVE_BREAKS.equals(wsColl))
    // {
    // return new LinebreakClassificationProducer();
    // }
    classificationProducer = new WhitespaceClassificationProducer();
    return classificationProducer;
  }

  protected BreakOpportunityProducer createBreakProducer( final StyleSheet layoutContext ) {
    final TextWrap wordBreak = (TextWrap) layoutContext.getStyleProperty( TextStyleKeys.TEXT_WRAP );
    if ( breakOpportunityProducer != null ) {
      if ( ObjectUtilities.equal( breakOpportunityValue, wordBreak ) ) {
        breakOpportunityProducer.reset();
        return breakOpportunityProducer;
      }
    }

    breakOpportunityValue = wordBreak;

    if ( TextWrap.NONE.equals( wordBreak ) ) {
      // suppress all but the linebreaks. This equals the 'pre' mode of HTML
      breakOpportunityProducer = new LineBreakProducer();
    } else {
      // allow other breaks as well. The wordbreak producer does not perform
      // advanced break-detection (like syllable based breaks).
      breakOpportunityProducer = new WordBreakProducer();
    }
    return breakOpportunityProducer;
  }

  protected SpacingProducer createSpacingProducer( final StyleSheet layoutContext ) {
    final Spacing spacing;
    if ( metaData.isFeatureSupported( OutputProcessorFeature.SPACING_SUPPORTED ) ) {
      final double minValue = layoutContext.getDoubleStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 0 );
      final double optValue = layoutContext.getDoubleStyleProperty( TextStyleKeys.X_OPTIMUM_LETTER_SPACING, 0 );
      final double maxValue = layoutContext.getDoubleStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, 0 );

      final int minIntVal = (int) StrictGeomUtility.toInternalValue( minValue );
      final int optIntVal = (int) StrictGeomUtility.toInternalValue( optValue );
      final int maxIntVal = (int) StrictGeomUtility.toInternalValue( maxValue );

      spacing = new Spacing( minIntVal, optIntVal, maxIntVal );
      return new StaticSpacingProducer( spacing );
    }
    spacing = ( Spacing.EMPTY_SPACING );
    if ( spacingProducer != null && ObjectUtilities.equal( spacing, spacingProducerKey ) ) {
      return spacingProducer;
    }

    spacingProducer = new StaticSpacingProducer( spacing );
    spacingProducerKey = spacing;
    return spacingProducer;
  }

  protected FontSizeProducer createFontSizeProducer( final StyleSheet layoutContext ) {
    return new VariableFontSizeProducer( fontMetrics );
  }

  protected KerningProducer createKerningProducer( final StyleSheet layoutContext ) {
    // for now, do nothing ..
    return noKerningProducer;
  }

  public RenderNode[] finishText() {
    if ( layoutContext == null ) {
      return DefaultRenderableTextFactory.EMPTY_TEXT;
    }

    final RenderNode[] text = processText( DefaultRenderableTextFactory.END_OF_TEXT, 0, 1 );
    layoutContext = null;
    fontSizeProducer = null;
    this.uniformBaselineInfo = null;

    return text;
  }

  public void startText() {
    startText = true;
  }
}
