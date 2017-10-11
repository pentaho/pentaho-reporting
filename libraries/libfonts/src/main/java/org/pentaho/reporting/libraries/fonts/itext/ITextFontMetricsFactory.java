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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

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
