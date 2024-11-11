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
import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Looks-up the character on the given font.
 *
 * @author Thomas Morgner
 */
public class VariableFontSizeProducer implements FontSizeProducer {
  private FontMetrics fontMetrics;
  private int maxHeight;
  private int baseLine;


  public VariableFontSizeProducer( final FontMetrics fontMetrics ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException();
    }
    this.fontMetrics = fontMetrics;
    this.maxHeight = (int) ( 0x7FFFFFFF & fontMetrics.getMaxHeight() );
    this.baseLine = (int) ( 0x7FFFFFFF & ( fontMetrics.getMaxHeight() - fontMetrics.getMaxDescent() ) );
  }

  public GlyphMetrics getCharacterSize( final int codePoint,
                                        GlyphMetrics dimension ) {
    final int width;
    if ( codePoint == ClassificationProducer.START_OF_TEXT ||
      codePoint == ClassificationProducer.END_OF_TEXT ||
      codePoint == -1 ) {
      width = 0;
    } else {
      width = (int) ( 0x7FFFFFFF & fontMetrics.getCharWidth( codePoint ) );
    }

    if ( dimension == null ) {
      dimension = new GlyphMetrics();
    }

    dimension.setWidth( width );
    dimension.setHeight( maxHeight );
    dimension.setBaselinePosition( baseLine );
    return dimension;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
