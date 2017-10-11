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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.layout.text.Glyph;
import org.pentaho.reporting.engine.classic.core.layout.text.GlyphList;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.text.Spacing;
import org.pentaho.reporting.libraries.fonts.text.breaks.BreakOpportunityProducer;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * The renderable text is a text chunk, enriched with layouting information, such as break opportunities, character
 * sizes, kerning information and spacing information.
 * <p/>
 * Text is given as codepoints. Break opportunities are given as integer values, where zero forbids breaking, and higher
 * values denote better breaks. Spacing and glyph sizes and kerning is given in micro-points; Spacing is the 'added'
 * space between codepoints if text-justification is enabled.
 * <p/>
 * The text is computed as grapheme clusters; this means that several unicode codepoints may result in a single
 * /virtual/ glyph/codepoint/character. (Example: 'A' + accent symbols). If the font supports Lithurges, these lithurges
 * may also be represented as a single grapheme cluster (and thus behave unbreakable).
 * <p/>
 * Grapheme clusters with more than one unicode char have the size of that char added to the first codepoint, all
 * subsequence codepoints of the same cluster have a size/kerning/etc of zero and are unbreakable.
 * <p/>
 * This text chunk is perfectly suitable for horizontal text, going either from left-to-right or right-to-left.
 * (Breaking mixed text is up to the textfactory).
 *
 * @author Thomas Morgner
 */
public final class RenderableText extends RenderNode implements SplittableRenderNode {
  private static long conversionFactor;

  static {
    final long value = StrictGeomUtility.toInternalValue( 1 );
    conversionFactor = value / FontStrictGeomUtility.toInternalValue( 1 );
  }

  private GlyphList glyphs;
  private int offset;
  private int length;
  private int script;

  private long minimumWidth;
  private long preferredWidth;
  private boolean forceLinebreak;
  private ExtendedBaselineInfo baselineInfo;
  private boolean normalTextSpacing;

  public RenderableText( final StyleSheet layoutContext, final ElementType elementType, final InstanceID instanceID,
      final ReportAttributeMap<Object> attributes, final ExtendedBaselineInfo baselineInfo, final GlyphList glyphs,
      final int offset, final int length, final int script, final boolean forceLinebreak ) {
    super( new NodeLayoutProperties( layoutContext, attributes, instanceID, elementType ) );
    initialize( glyphs, offset, length, baselineInfo, script, forceLinebreak );
  }

  protected void initialize( final GlyphList glyphs, final int offset, final int length,
      final ExtendedBaselineInfo baselineInfo, final int script, final boolean forceLinebreak ) {
    if ( glyphs == null ) {
      throw new NullPointerException();
    }
    if ( forceLinebreak == false && length == 0 ) {
      throw new IllegalArgumentException( "Do not create zero-length renderable text!" );
    }
    if ( glyphs.getSize() < ( offset + length ) ) {
      throw new IllegalArgumentException();
    }

    this.baselineInfo = baselineInfo;
    this.script = script;

    this.glyphs = glyphs;
    this.offset = offset;
    this.length = length;
    this.forceLinebreak = forceLinebreak;

    normalTextSpacing = true;
    long wordMinChunkWidth = 0;

    // long heightAbove = 0;
    // long heightBelow = 0;
    long minimumChunkWidth = 0;

    long realCharTotal = 0;
    long spacerMin = 0;
    long spacerMax = 0;
    long spacerOpt = 0;

    final int lastPos = Math.min( glyphs.getSize(), offset + length );
    for ( int i = offset; i < lastPos; i++ ) {
      final Glyph glyph = glyphs.getGlyph( i );
      // heightAbove = Math.max(glyph.getBaseLine(), heightAbove);
      // heightBelow = Math.max(glyph.getHeight() - glyph.getBaseLine(), heightBelow);
      final int kerning = glyph.getKerning();
      final int width = glyph.getWidth();
      final long realCharSpace = convert( width - kerning );
      realCharTotal += realCharSpace;
      wordMinChunkWidth += realCharSpace;
      if ( i != ( lastPos - 1 ) ) {
        final Spacing spacing = glyph.getSpacing();
        spacerMax += spacing.getMaximum();
        spacerMin += spacing.getMinimum();
        spacerOpt += spacing.getOptimum();
        if ( normalTextSpacing == true && Spacing.EMPTY_SPACING.equals( spacing ) == false ) {
          normalTextSpacing = false;
        }

        wordMinChunkWidth += spacing.getMinimum();
      }

      if ( glyph.getBreakWeight() > BreakOpportunityProducer.BREAK_CHAR ) {
        minimumChunkWidth = Math.max( minimumChunkWidth, wordMinChunkWidth );
        wordMinChunkWidth = 0;

        // Paranoid sanity checks: The word- and linebreaks should have been
        // replaced by other definitions in the text factory.
        if ( glyph.getBreakWeight() == BreakOpportunityProducer.BREAK_LINE ) {
          throw new IllegalStateException( "A renderable text cannot and must " + "not contain linebreaks." );
        }
      }
    }

    final long wordMinWidth = spacerMin + realCharTotal;
    final long wordPrefWidth = spacerOpt + realCharTotal;
    final long wordMaxWidth = spacerMax + realCharTotal;

    minimumChunkWidth = Math.max( minimumChunkWidth, wordMinChunkWidth );
    minimumWidth = wordMinWidth;
    preferredWidth = wordPrefWidth;

    setMaximumBoxWidth( wordMaxWidth );
    setMinimumChunkWidth( minimumChunkWidth );
  }

  public int getNodeType() {
    return LayoutNodeTypes.TYPE_NODE_TEXT;
  }

  public boolean isNormalTextSpacing() {
    return normalTextSpacing;
  }

  public boolean isForceLinebreak() {
    return forceLinebreak;
  }

  public GlyphList getGlyphs() {
    return glyphs;
  }

  public int getOffset() {
    return offset;
  }

  public int getLength() {
    return length;
  }

  public String getRawText() {
    final GlyphList gs = getGlyphs();
    return gs.getText( offset, length, new CodePointBuffer( length ) );
  }

  public boolean isEmpty() {
    return length == 0 && forceLinebreak == false;
  }

  public boolean isDiscardable() {
    if ( forceLinebreak ) {
      return false;
    }

    return glyphs.getSize() == 0;
  }

  /**
   * Returns the baseline info for the given node. This can be null, if the node does not have any baseline info.
   *
   * @return
   */
  public ExtendedBaselineInfo getBaselineInfo() {
    return baselineInfo;
  }

  public int getScript() {
    return script;
  }

  @Override
  public long getMinimumWidth() {
    return minimumWidth;
  }

  public long getPreferredWidth() {
    return preferredWidth;
  }

  public String toString() {
    return "RenderableText={glyphs=" + glyphs + "'}";
  }

  public static long convert( final long fontMetricsValue ) {
    return fontMetricsValue * conversionFactor;
  }

  public int computeMaximumTextSize( final long contentX2 ) {
    final int length = getLength();
    final long x = getX();
    if ( contentX2 >= ( x + getWidth() ) ) {
      return length;
    }

    final GlyphList gs = getGlyphs();
    long runningPos = x;
    final int offset = getOffset();
    final int maxPos = offset + length;

    for ( int i = offset; i < maxPos; i++ ) {
      final Glyph g = gs.getGlyph( i );
      runningPos += RenderableText.convert( g.getWidth() );
      if ( i != offset ) {
        runningPos += g.getSpacing().getMinimum();
      }
      if ( runningPos > contentX2 ) {
        return Math.max( 0, i - offset );
      }
    }
    return length;
  }

  /**
   * {@inheritDoc}
   * <p/>
   * <b>Important!</b> The separation is allowed only if
   * {@linkplain org.pentaho.reporting.engine.classic.core.style .TextStyleKeys#WORDBREAK TextStyleKeys.WORDBREAK}
   * property is {@code true}
   *
   * @throws IllegalArgumentException
   *           if {@code widthOfFirst <= 0}
   * @throws IllegalStateException
   *           if {@code widthOfFirst >= getMinimumWidth()}
   */
  @Override
  public RenderableText[] splitBy( long widthOfFirst ) {
    if ( widthOfFirst <= 0 ) {
      throw new IllegalArgumentException( String.format(
          "Illegal width: %d. Only text nodes with non-zero width are not allowed!", widthOfFirst ) );
    }

    if ( widthOfFirst >= getMinimumWidth() ) {
      throw new IllegalStateException( String.format(
          "Split width (%d) should be less than the component's minimum width (%d)!", widthOfFirst, getMinimumWidth() ) );
    }

    if ( !getStyleSheet().getBooleanStyleProperty( TextStyleKeys.WORDBREAK ) ) {
      // word-breaking should be allowed
      return null;
    }

    // length cannot be 0 - see guard check in initialize()
    final int last = offset + length;
    final GlyphList glyphs = getGlyphs();
    int index = offset;
    long currentWidth = 0;
    while ( index < last && currentWidth <= widthOfFirst ) {
      currentWidth += convert( glyphs.getGlyph( index ).getWidth() );
      index++;
    }
    index--;

    if ( index == offset ) {
      // the first element's width exceeds widthOfFirst
      return null;
    }

    RenderableText first = (RenderableText) derive( true );
    int firstLength = index - offset;
    first.initialize( glyphs, offset, firstLength, baselineInfo, script, forceLinebreak );

    RenderableText rest = (RenderableText) derive( true );
    rest.initialize( glyphs, index, length - firstLength, baselineInfo, script, forceLinebreak );

    RenderableText[] pair = { first, rest };
    RenderBox parent = getParent();
    if ( parent != null ) {
      parent.replaceChilds( this, pair );
    }

    return pair;
  }
}
