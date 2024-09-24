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

package org.pentaho.reporting.libraries.fonts.itext;

import com.lowagie.text.pdf.BaseFont;
import org.pentaho.reporting.libraries.fonts.merge.CompoundFontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontNativeContext;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontIdentifier;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.truetype.TrueTypeFontIdentifier;

/**
 * Todo: Document me!
 *
 * @author : Thomas Morgner
 */
public class ITextFontMetricsFactory implements FontMetricsFactory {
  private BaseFontSupport baseFontSupport;

  public ITextFontMetricsFactory( final ITextFontRegistry registry ) {
    this.baseFontSupport = new BaseFontSupport( registry );
  }

  public void close() {
    this.baseFontSupport.close();
  }

  public FontMetrics createMetrics( final FontIdentifier identifier,
                                    final FontContext context ) {
    final CompoundFontIdentifier compoundFontIdentifier;
    final FontIdentifier record;
    if ( identifier instanceof CompoundFontIdentifier ) {
      compoundFontIdentifier = (CompoundFontIdentifier) identifier;
      record = compoundFontIdentifier.getIdentifier();
    } else {
      record = identifier;
      compoundFontIdentifier = null;
    }

    final String fontName;
    final boolean bold;
    final boolean italic;
    if ( record instanceof FontRecord ) {
      final FontRecord fontRecord = (FontRecord) record;
      fontName = fontRecord.getFamily().getFamilyName();
      if ( compoundFontIdentifier != null ) {
        bold = compoundFontIdentifier.isBoldSpecified();
        italic = compoundFontIdentifier.isItalicsSpecified();
      } else {
        bold = fontRecord.isBold();
        italic = fontRecord.isItalic();
      }
    } else if ( record instanceof TrueTypeFontIdentifier ) {
      final TrueTypeFontIdentifier ttfFontRecord = (TrueTypeFontIdentifier) record;
      fontName = ttfFontRecord.getFontName();
      if ( compoundFontIdentifier != null ) {
        bold = compoundFontIdentifier.isBoldSpecified();
        italic = compoundFontIdentifier.isItalicsSpecified();
      } else {
        bold = false;
        italic = false;
      }
    } else {
      throw new IllegalArgumentException( "Unknown font-identifier type encountered." );
    }

    final BaseFont baseFont = baseFontSupport.createBaseFont
      ( fontName, bold, italic, context.getEncoding(), context.isEmbedded() );

    return new BaseFontFontMetrics
      ( new DefaultFontNativeContext( bold, italic ), baseFont, (float) context.getFontSize() );
  }
}
