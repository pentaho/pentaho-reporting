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
import org.pentaho.reporting.libraries.fonts.cache.FontCache;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

import java.util.HashMap;

/**
 * Creation-Date: 22.07.2007, 17:56:28
 *
 * @author Thomas Morgner
 */
public class ITextBuiltInFontRegistry implements FontRegistry {
  private HashMap families;
  private String[] familyNames;

  public ITextBuiltInFontRegistry() {
    families = new HashMap();
    families.put( "Symbol", createSymbolFamily() );
    families.put( "ZapfDingbats", createZapfDingbatsFamily() );
    families.put( "Times", createTimesFamily() );
    families.put( "Courier", createCourierFamily() );
    families.put( "Helvetica", createHelveticaFamily() );

    familyNames = new String[] { "Symbol", "ZapfDingbats", "Times", "Courier", "Helvetica" };
  }

  private FontFamily createSymbolFamily() {
    final DefaultFontFamily fontFamily = new DefaultFontFamily( "Symbol" );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.SYMBOL, false, false, false ) );
    return fontFamily;
  }

  private FontFamily createZapfDingbatsFamily() {
    final DefaultFontFamily fontFamily = new DefaultFontFamily( "ZapfDingbats" );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.ZAPFDINGBATS, false, false, false ) );
    return fontFamily;
  }

  private FontFamily createTimesFamily() {
    final DefaultFontFamily fontFamily = new DefaultFontFamily( "Times" );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.TIMES_ROMAN, false, false, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.TIMES_BOLD, true, false, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.TIMES_ITALIC, false, true, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.TIMES_BOLDITALIC, true, true, false ) );
    return fontFamily;
  }

  private FontFamily createCourierFamily() {
    final DefaultFontFamily fontFamily = new DefaultFontFamily( "Courier" );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.COURIER, false, false, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.COURIER_BOLD, true, false, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.COURIER_OBLIQUE, false, true, true ) );
    fontFamily
      .addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.COURIER_BOLDOBLIQUE, true, true, true ) );
    return fontFamily;
  }

  private FontFamily createHelveticaFamily() {
    final DefaultFontFamily fontFamily = new DefaultFontFamily( "Helvetica" );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.HELVETICA, false, false, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.HELVETICA_BOLD, true, false, false ) );
    fontFamily.addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.HELVETICA_OBLIQUE, false, true, true ) );
    fontFamily
      .addFontRecord( new ITextBuiltInFontRecord( fontFamily, BaseFont.HELVETICA_BOLDOBLIQUE, true, true, true ) );
    return fontFamily;
  }

  public void initialize() {

  }

  public FontFamily getFontFamily( final String name ) {
    return (FontFamily) families.get( name );
  }

  public String[] getRegisteredFamilies() {
    return (String[]) familyNames.clone();
  }

  public String[] getAllRegisteredFamilies() {
    return (String[]) familyNames.clone();
  }

  public FontMetricsFactory createMetricsFactory() {
    throw new UnsupportedOperationException();
  }

  public FontCache getSecondLevelCache() {
    throw new UnsupportedOperationException();
  }
}
