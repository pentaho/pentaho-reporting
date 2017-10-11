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

import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.text.Spacing;

public final class GlyphList {
  protected static final int EXTRA_GLYPH_INFO = 7;

  private static class VirtualGlyph implements Glyph {
    private int index;
    private int spacingIndex;
    private int extraGlyphCount;
    private int[] glyphData;
    private GlyphList parent;

    private VirtualGlyph( final GlyphList parent ) {
      this.parent = parent;
    }

    public void update( final int glyphDataIndex, final int spacingIndex ) {
      this.index = glyphDataIndex;
      this.spacingIndex = spacingIndex;
      this.glyphData = parent.getGlyphSequenceData();
      this.extraGlyphCount = glyphData[index];
    }

    public int getClassification() {
      return glyphData[index + 2];
    }

    public int[] getExtraChars() {
      if ( extraGlyphCount == 0 ) {
        return GlyphList.EMPTY_INTS;
      }
      final int[] retal = new int[extraGlyphCount];
      System.arraycopy( glyphData, index + GlyphList.EXTRA_GLYPH_INFO + 1, retal, 0, extraGlyphCount );
      return retal;
    }

    public int getBaseLine() {
      return glyphData[index + 5];
    }

    public int getCodepoint() {
      return glyphData[index + GlyphList.EXTRA_GLYPH_INFO];
    }

    public int getBreakWeight() {
      return glyphData[index + 1];
    }

    public Spacing getSpacing() {
      final Spacing[] spacings1 = parent.getSpacings();
      return spacings1[spacingIndex];
    }

    public int getWidth() {
      return glyphData[index + 3];
    }

    public int getHeight() {
      return glyphData[index + 4];
    }

    public int getKerning() {
      return glyphData[index + 6];
    }
  }

  protected static final int[] EMPTY_INTS = new int[0];

  private int[] glyphSequenceData;
  private Spacing[] spacings;
  private int[] glyphIndices;
  private int glyphSequenceFill;
  private int glyphIncrement;
  private int size;
  private int spacerIncrement;
  private VirtualGlyph virtualGlyph;
  private boolean locked;
  private StringBuilder stringBuilder;

  private GlyphList() {
    this.virtualGlyph = new VirtualGlyph( this );
  }

  public GlyphList( final int spacerIncrement ) {
    // this is a good enough default for all common unicode languages.
    this( spacerIncrement * ( GlyphList.EXTRA_GLYPH_INFO + 1 ), spacerIncrement );
  }

  public GlyphList( final int glyphIncrement, final int spacerIncrement ) {
    this.glyphIncrement = glyphIncrement;
    this.spacerIncrement = spacerIncrement;
    this.virtualGlyph = new VirtualGlyph( this );

    this.glyphIndices = new int[spacerIncrement];
    this.spacings = new Spacing[spacerIncrement];
    this.glyphSequenceData = new int[glyphIncrement];
  }

  protected int[] getGlyphSequenceData() {
    return glyphSequenceData;
  }

  protected Spacing[] getSpacings() {
    return spacings;
  }

  /**
   * Ensures, that the list backend can store at least <code>c</code> elements. This method does nothing, if the new
   * capacity is less than the current capacity.
   *
   * @param capacity
   *          the new capacity of the list.
   */
  private void ensureGlyphCapacity( final int capacity ) {
    if ( glyphSequenceData.length <= capacity ) {
      final int[] newData = new int[Math.max( glyphSequenceData.length + glyphIncrement, capacity + 1 )];
      System.arraycopy( glyphSequenceData, 0, newData, 0, glyphSequenceFill );
      glyphSequenceData = newData;
    }
  }

  private void ensureSpacerCapacity( final int capacity ) {
    if ( spacings.length <= capacity ) {
      final Spacing[] newData = new Spacing[Math.max( spacings.length + spacerIncrement, capacity + 1 )];
      System.arraycopy( spacings, 0, newData, 0, size );
      spacings = newData;

      final int[] newIndexData = new int[Math.max( glyphIndices.length + spacerIncrement, capacity + 1 )];
      System.arraycopy( glyphIndices, 0, newIndexData, 0, size );
      glyphIndices = newIndexData;
    }
  }

  public void addGlyphData( final int[] rawCodepoints, final int rawCodePointOffset, final int rawCodePointLength,
      final int breakWeight, final int classification, final Spacing spacing, final int width, final int height,
      final int baseLine, final int kerning ) {
    if ( locked ) {
      throw new IllegalStateException();
    }
    ensureGlyphCapacity( glyphSequenceFill + rawCodePointLength + GlyphList.EXTRA_GLYPH_INFO );
    ensureSpacerCapacity( size + 1 );

    final int glyphSequenceFill = this.glyphSequenceFill;
    glyphSequenceData[glyphSequenceFill] = rawCodePointLength - 1;
    glyphSequenceData[glyphSequenceFill + 1] = breakWeight;
    glyphSequenceData[glyphSequenceFill + 2] = classification;
    glyphSequenceData[glyphSequenceFill + 3] = width;
    glyphSequenceData[glyphSequenceFill + 4] = height;
    glyphSequenceData[glyphSequenceFill + 5] = baseLine;
    glyphSequenceData[glyphSequenceFill + 6] = kerning;
    if ( rawCodePointLength == 1 ) {
      glyphSequenceData[glyphSequenceFill + GlyphList.EXTRA_GLYPH_INFO] = rawCodepoints[rawCodePointOffset];
    } else {
      System.arraycopy( rawCodepoints, rawCodePointOffset, glyphSequenceData, glyphSequenceFill
          + GlyphList.EXTRA_GLYPH_INFO, rawCodePointLength );
    }
    this.glyphSequenceFill = glyphSequenceFill + GlyphList.EXTRA_GLYPH_INFO + rawCodePointLength;
    glyphIndices[size] = glyphSequenceFill;
    spacings[size] = spacing;
    size += 1;
  }

  public Glyph getGlyph( final int index ) {
    if ( index >= size ) {
      throw new IndexOutOfBoundsException();
    }
    if ( index < 0 ) {
      throw new IndexOutOfBoundsException();
    }
    virtualGlyph.update( glyphIndices[index], index );
    return virtualGlyph;
  }

  public int getSize() {
    return size;
  }

  public GlyphList lock() {
    final GlyphList retval = new GlyphList();
    retval.spacerIncrement = 0;
    retval.glyphIncrement = 0;
    retval.locked = true;
    retval.glyphSequenceFill = glyphSequenceFill;
    retval.glyphSequenceData = new int[glyphSequenceFill];
    System.arraycopy( glyphSequenceData, 0, retval.glyphSequenceData, 0, glyphSequenceFill );

    retval.size = size;
    retval.spacings = new Spacing[size];
    retval.glyphIndices = new int[size];
    System.arraycopy( spacings, 0, retval.spacings, 0, size );
    System.arraycopy( glyphIndices, 0, retval.glyphIndices, 0, size );
    return retval;
  }

  public void clear() {
    size = 0;
    glyphSequenceFill = 0;
  }

  public void ensureSize( final int size ) {
    ensureSpacerCapacity( size );
    ensureGlyphCapacity( size * 8 );
  }

  public boolean isEmpty() {
    return size == 0;
  }

  public String getText( final int offset, final int length, final CodePointBuffer codePointBuffer ) {
    if ( length == 0 ) {
      return "";
    }

    codePointBuffer.setCursor( 0 );

    final StringBuilder cps = create();
    final int maxPos = offset + length;
    for ( int i = offset; i < maxPos; i++ ) {
      final int glyphIndex = glyphIndices[i];
      final int glyphDataStart = glyphIndex + GlyphList.EXTRA_GLYPH_INFO;
      final int glyphDataEnd = glyphDataStart + glyphSequenceData[glyphIndex] + 1;
      for ( int g = glyphDataStart; g < glyphDataEnd; g++ ) {
        encodeStringIncrementally( cps, glyphSequenceData[g] );
      }
    }
    final String retval = cps.toString();
    cps.delete( 0, cps.length() );
    return retval;
  }

  private StringBuilder create() {
    if ( stringBuilder == null ) {
      stringBuilder = new StringBuilder();
    }
    stringBuilder.delete( 0, stringBuilder.length() );
    return stringBuilder;
  }

  private void encodeStringIncrementally( StringBuilder stringBuffer, int codePoint ) {
    if ( codePoint < 0x10000 ) {
      stringBuffer.append( (char) codePoint );
    } else {
      // oh, no, we have to decode ...
      // compute the weird replacement mode chars ..
      final int derivedSourceItem = codePoint - 0x10000;
      final int highWord = 0xD800 | ( ( derivedSourceItem & 0xFFC00 ) >> 10 );
      final int lowWord = 0xDC00 | ( derivedSourceItem & 0x3FF );
      stringBuffer.append( (char) highWord );
      stringBuffer.append( (char) lowWord );
    }
  }

  public String getGlyphAsString( final int index, final CodePointBuffer codePointBuffer ) {
    codePointBuffer.setCursor( 0 );

    final StringBuilder cps = create();
    final int glyphIndex = glyphIndices[index];
    final int glyphDataStart = glyphIndex + GlyphList.EXTRA_GLYPH_INFO;
    final int glyphDataEnd = glyphDataStart + glyphSequenceData[glyphIndex] + 1;
    for ( int g = glyphDataStart; g < glyphDataEnd; g++ ) {
      encodeStringIncrementally( cps, glyphSequenceData[g] );
    }
    final String retval = cps.toString();
    cps.delete( 0, cps.length() );
    return retval;
  }

  public String toString() {
    return "GlyphList={text='" + getText( 0, size, new CodePointBuffer( size ) ) + "'}";
  }
}
