/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.text.font;

import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * Creates a monospaced font from any given font by always returning the maximum character width and height for that
 * font. Grapheme clusters have no effect on that font size producer.
 *
 * @author Thomas Morgner
 */
public class StaticFontSizeProducer implements FontSizeProducer {
  private int maxWidth;
  private int maxHeight;
  private int baseLine;
  // private FontMetrics fontMetrics;

  public StaticFontSizeProducer( final FontMetrics fontMetrics ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException();
    }
    //this.fontMetrics = fontMetrics;
    this.maxHeight = (int) ( 0x7FFFFFFF &
      FontStrictGeomUtility.toInternalValue( fontMetrics.getMaxHeight() ) );
    this.maxWidth = (int) ( 0x7FFFFFFF &
      FontStrictGeomUtility.toInternalValue( fontMetrics.getMaxCharAdvance() ) );
    this.baseLine = (int) ( 0x7FFFFFFF & FontStrictGeomUtility.toInternalValue
      ( fontMetrics.getMaxHeight() - fontMetrics.getMaxDescent() ) );
  }

  public StaticFontSizeProducer( final int maxWidth,
                                 final int maxHeight,
                                 final int baseLine ) {
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    this.baseLine = baseLine;
  }

  public GlyphMetrics getCharacterSize( final int codePoint,
                                        final GlyphMetrics dimension ) {
    if ( dimension == null ) {
      final GlyphMetrics retval = new GlyphMetrics();
      retval.setWidth( maxWidth );
      retval.setHeight( maxHeight );
      retval.setBaselinePosition( baseLine );
      return retval;
    }

    dimension.setWidth( maxWidth );
    dimension.setHeight( maxHeight );
    dimension.setBaselinePosition( baseLine );
    return dimension;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
