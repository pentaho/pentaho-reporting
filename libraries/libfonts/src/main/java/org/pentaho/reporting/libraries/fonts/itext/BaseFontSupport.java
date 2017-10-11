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

import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.FontMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.merge.CompoundFontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.registry.FontSource;
import org.pentaho.reporting.libraries.fonts.truetype.TrueTypeFontRecord;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * iText font support.
 *
 * @author Thomas Morgner
 */
public class BaseFontSupport implements FontMapper {
  private static final Log logger = LogFactory.getLog( BaseFontSupport.class );
  /**
   * Storage for BaseFont objects created.
   */
  private final Map baseFonts;

  private String defaultEncoding;

  private boolean useGlobalCache;
  private boolean embedFonts;
  private ITextFontRegistry registry;

  /**
   * Creates a new support instance.
   */
  public BaseFontSupport( final ITextFontRegistry registry ) {
    this( registry, "UTF-8" );
  }

  public BaseFontSupport( final ITextFontRegistry registry,
                          final String defaultEncoding ) {
    this.baseFonts = new HashMap();
    this.registry = registry;
    this.defaultEncoding = defaultEncoding;
    final ExtendedConfiguration extendedConfig = LibFontBoot.getInstance().getExtendedConfig();
    this.useGlobalCache =
      extendedConfig.getBoolProperty( "org.pentaho.reporting.libraries.fonts.itext.UseGlobalFontCache" );
  }

  public String getDefaultEncoding() {
    return defaultEncoding;
  }

  public void setDefaultEncoding( final String defaultEncoding ) {
    if ( defaultEncoding == null ) {
      throw new NullPointerException( "DefaultEncoding is null." );
    }
    this.defaultEncoding = defaultEncoding;
  }

  public boolean isEmbedFonts() {
    return embedFonts;
  }

  public void setEmbedFonts( final boolean embedFonts ) {
    this.embedFonts = embedFonts;
  }

  /**
   * Close the font support.
   */
  public void close() {
    this.baseFonts.clear();
  }

  /**
   * Creates a iText-BaseFont for an font.  If no basefont could be created, an BaseFontCreateException is thrown.
   *
   * @param logicalName the name of the font (null not permitted).
   * @param bold        a flag indicating whether the font is rendered as bold font.
   * @param italic      a flag indicating whether the font is rendered as italic or cursive font.
   * @param encoding    the encoding.
   * @param embedded    a flag indicating whether to embed the font glyphs in the generated documents.
   * @return the base font record.
   * @throws BaseFontCreateException if there was a problem setting the font for the target.
   */
  public BaseFont createBaseFont( final String logicalName,
                                  final boolean bold,
                                  final boolean italic,
                                  final String encoding,
                                  final boolean embedded )
    throws BaseFontCreateException {
    return createBaseFontRecord( logicalName, bold, italic, encoding, embedded ).getBaseFont();
  }

  /**
   * Creates a BaseFontRecord for an font.  If no basefont could be created, an BaseFontCreateException is thrown.
   *
   * @param logicalName the name of the font (null not permitted).
   * @param bold        a flag indicating whether the font is rendered as bold font.
   * @param italic      a flag indicating whether the font is rendered as italic or cursive font.
   * @param encoding    the encoding.
   * @param embedded    a flag indicating whether to embed the font glyphs in the generated documents.
   * @return the base font record.
   * @throws BaseFontCreateException if there was a problem setting the font for the target.
   */
  public BaseFontRecord createBaseFontRecord( final String logicalName,
                                              final boolean bold,
                                              final boolean italic,
                                              String encoding,
                                              final boolean embedded )
    throws BaseFontCreateException {
    if ( logicalName == null ) {
      throw new NullPointerException( "Font definition is null." );
    }
    if ( encoding == null ) {
      encoding = getDefaultEncoding();
    }

    // use the Java logical font name to map to a predefined iText font.

    final String fontKey;
    if ( FontMappingUtility.isCourier( logicalName ) ) {
      fontKey = "Courier";
    } else if ( FontMappingUtility.isSymbol( logicalName ) ) {
      fontKey = "Symbol";
    } else if ( FontMappingUtility.isSerif( logicalName ) ) {
      fontKey = "Times";
    } else if ( FontMappingUtility.isSansSerif( logicalName ) ) {
      // default, this catches Dialog and SansSerif
      fontKey = "Helvetica";
    } else {
      fontKey = logicalName;
    }

    // iText uses some weird mapping between IDENTY-H/V and java supported encoding, IDENTITY-H/V is
    // used to recognize TrueType fonts, but the real JavaEncoding is used to encode Type1 fonts
    final String stringEncoding;
    if ( "utf-8".equalsIgnoreCase( encoding ) ) {
      stringEncoding = "utf-8";
      encoding = BaseFont.IDENTITY_H;
    } else if ( "utf-16".equalsIgnoreCase( encoding ) ) {
      stringEncoding = "utf-16";
      encoding = BaseFont.IDENTITY_H;
    } else {
      // Correct the encoding for truetype fonts
      // iText will crash if IDENTITY_H is used to create a base font ...
      if ( encoding.equalsIgnoreCase( BaseFont.IDENTITY_H ) ||
        encoding.equalsIgnoreCase( BaseFont.IDENTITY_V ) ) {
        //changed to UTF to support all unicode characters ..
        stringEncoding = "utf-8";
      } else {
        stringEncoding = encoding;
      }
    }

    try {
      final FontFamily registryFontFamily = registry.getFontFamily( fontKey );
      FontRecord registryFontRecord = null;
      if ( registryFontFamily != null ) {
        registryFontRecord = registryFontFamily.getFontRecord( bold, italic );

        if ( registryFontRecord instanceof CompoundFontRecord ) {
          final CompoundFontRecord cfr = (CompoundFontRecord) registryFontRecord;
          registryFontRecord = cfr.getBase();
        }
      }

      if ( registryFontRecord != null ) {
        // Check, whether this is an built-in font. If not, then the record points to a file.
        if ( ( registryFontRecord instanceof ITextBuiltInFontRecord ) == false ) {

          boolean embeddedOverride = embedded;
          if ( embedded == true && registryFontRecord instanceof FontSource ) {
            final FontSource source = (FontSource) registryFontRecord;
            if ( source.isEmbeddable() == false ) {
              logger.warn( "License of font forbids embedded usage for font: " + fontKey );
              // strict mode here?
              embeddedOverride = false;
            }
          }

          final BaseFontRecord fontRecord = createFontFromTTF
            ( registryFontRecord, bold, italic, encoding, stringEncoding, embeddedOverride );
          if ( fontRecord != null ) {
            return fontRecord;
          }
        } else {
          final ITextBuiltInFontRecord buildInFontRecord = (ITextBuiltInFontRecord) registryFontRecord;
          // So this is one of the built-in records.
          final String fontName = buildInFontRecord.getFullName();

          // Alternative: No Registered TrueType font was found. OK; don't panic,
          // we try to create a font anyway..

          BaseFontRecord fontRecord = getFromCache( fontName, encoding, embedded );
          if ( fontRecord != null ) {
            return fontRecord;
          }
          fontRecord = getFromCache( fontName, stringEncoding, embedded );
          if ( fontRecord != null ) {
            return fontRecord;
          }

          // filename is null, so no ttf file registered for the fontname, maybe this is
          // one of the internal fonts ...
          final BaseFont f = BaseFont.createFont( fontName, stringEncoding, embedded,
            useGlobalCache, null, null );
          if ( f != null ) {
            fontRecord = new BaseFontRecord( fontName, false, embedded, f, bold, italic );
            putToCache( fontRecord );
            return fontRecord;
          }
        }
      }

      // If we got to this point, then the font was not recognized as any known font. We will fall back
      // to Helvetica instead ..
    } catch ( Exception e ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "BaseFont.createFont failed. Key = " + fontKey + ": " + e.getMessage(), e );
      } else if ( logger.isWarnEnabled() ) {
        logger.warn( "BaseFont.createFont failed. Key = " + fontKey + ": " + e.getMessage(), e );
      }
    }
    // fallback .. use BaseFont.HELVETICA as default
    try {
      // check, whether HELVETICA is already created - yes, then return cached instance instead
      BaseFontRecord fontRecord = getFromCache( BaseFont.HELVETICA, stringEncoding, embedded );
      if ( fontRecord != null ) {
        // map all font references of the invalid font to the default font..
        // this might be not very nice, but at least the report can go on..
        putToCache( new BaseFontRecordKey( fontKey, encoding, embedded ), fontRecord );
        return fontRecord;
      }

      // no helvetica created, so do this now ...
      final BaseFont f = BaseFont.createFont( BaseFont.HELVETICA, stringEncoding, embedded,
        useGlobalCache, null, null );
      if ( f != null ) {
        fontRecord = new BaseFontRecord
          ( BaseFont.HELVETICA, false, embedded, f, bold, italic );
        putToCache( fontRecord );
        putToCache( new BaseFontRecordKey( fontKey, encoding, embedded ), fontRecord );
        return fontRecord;
      }
    } catch ( Exception e ) {
      logger.warn( "BaseFont.createFont for FALLBACK failed.", e );
      throw new BaseFontCreateException( "Null font = " + fontKey );
    }
    throw new BaseFontCreateException( "BaseFont creation failed, null font: " + fontKey );
  }
  //
  //  /**
  //   *
  //   * @param fileName
  //   * @param fontName iTexts idea of mixing font meta data with filenames
  //   * @param encoding
  //   * @param embedded
  //   * @return
  //   */
  //  private BaseFont loadFromLibLoader (final String fileName,
  //                                      final String fontName,
  //                                      final String encoding,
  //                                      final boolean embedded)
  //  {
  //    final HashMap map = new HashMap();
  //    map.put(BaseFontResourceFactory.FONTNAME, fontName);
  //    map.put(BaseFontResourceFactory.ENCODING, encoding);
  //    map.put(BaseFontResourceFactory.EMBEDDED, new Boolean(embedded));
  //    map.put(ResourceKey.CONTENT_KEY, new File (fileName));
  //
  //    try
  //    {
  //      final Resource res =
  //              getResourceManager().createDirectly(map, BaseFont.class);
  //      return (BaseFont) res.getResource();
  //    }
  //    catch (ResourceException e)
  //    {
  //      return null;
  //    }
  //  }

  /**
   * Creates a PDF font record from a true type font.
   *
   * @param encoding       the encoding.
   * @param stringEncoding the string encoding.
   * @param embedded       a flag indicating whether to embed the font glyphs in the generated documents.
   * @return the PDF font record.
   * @throws com.lowagie.text.DocumentException if the BaseFont could not be created.
   */
  private BaseFontRecord createFontFromTTF( final FontRecord fontRecord,
                                            final boolean bold,
                                            final boolean italic,
                                            final String encoding,
                                            final String stringEncoding,
                                            final boolean embedded )
    throws DocumentException {
    // check if this font is in the cache ...
    //Log.warn ("TrueTypeFontKey : " + fontKey + " Font: " + font.isItalic() + " Encoding: "
    //          + encoding);
    final String rawFilename;
    if ( fontRecord instanceof TrueTypeFontRecord ) {
      final TrueTypeFontRecord ttfRecord = (TrueTypeFontRecord) fontRecord;
      if ( ttfRecord.getCollectionIndex() >= 0 ) {
        rawFilename = ttfRecord.getFontSource() + ',' + ttfRecord.getCollectionIndex();
      } else {
        rawFilename = ttfRecord.getFontSource();
      }
    } else if ( fontRecord instanceof FontSource ) {
      final FontSource source = (FontSource) fontRecord;
      rawFilename = source.getFontSource();
    } else {
      return null;
    }

    final String filename;
    // check, whether the the physical font does not provide some of the
    // required styles. We have to synthesize them, if neccessary
    if ( ( fontRecord.isBold() == false && bold ) &&
      ( fontRecord.isItalic() == false && italic ) ) {
      filename = rawFilename + ",BoldItalic";
    } else if ( fontRecord.isBold() == false && bold ) {
      filename = rawFilename + ",Bold";
    } else if ( fontRecord.isItalic() == false && italic ) {
      filename = rawFilename + ",Italic";
    } else {
      filename = rawFilename;
    }

    final BaseFontRecord fontRec = getFromCache( filename, encoding, embedded );
    if ( fontRec != null ) {
      return fontRec;
    }

    BaseFont f;
    try {
      try {
        f = BaseFont.createFont( filename, encoding, embedded, false, null, null );
      } catch ( DocumentException e ) {
        f = BaseFont.createFont( filename, stringEncoding, embedded, false, null, null );
      }
    } catch ( IOException ioe ) {
      throw new DocumentException( "Failed to read the font: " + ioe );
    }

    // no, we have to create a new instance
    final BaseFontRecord record = new BaseFontRecord
      ( filename, true, embedded, f, fontRecord.isBold(), fontRecord.isItalic() );
    putToCache( record );
    return record;
  }

  /**
   * Stores a record in the cache.
   *
   * @param record the record.
   */
  private void putToCache( final BaseFontRecord record ) {
    final BaseFontRecordKey key = record.createKey();
    putToCache( key, record );
  }

  private void putToCache( final BaseFontRecordKey key, final BaseFontRecord record ) {
    baseFonts.put( key, record );
  }

  /**
   * Retrieves a record from the cache.
   *
   * @param fileName the physical filename name of the font file.
   * @param encoding the encoding; never null.
   * @return the PDF font record or null, if not found.
   */
  private BaseFontRecord getFromCache( final String fileName,
                                       final String encoding,
                                       final boolean embedded ) {
    final Object key = new BaseFontRecordKey( fileName, encoding, embedded );
    final BaseFontRecord r = (BaseFontRecord) baseFonts.get( key );
    if ( r != null ) {
      return r;
    }
    return null;
  }

  /**
   * Returns a BaseFont which can be used to represent the given AWT Font
   *
   * @param font the font to be converted
   * @return a BaseFont which has similar properties to the provided Font
   */

  public BaseFont awtToPdf( final Font font ) {
    // this has to be defined in the element, an has to set as a default...
    final boolean embed = isEmbedFonts();
    final String encoding = getDefaultEncoding();
    try {
      return createBaseFont( font.getName(), font.isBold(), font.isItalic(), encoding, embed );
    } catch ( Exception e ) {
      // unable to handle font creation exceptions properly, all we can
      // do is throw a runtime exception and hope the best ..
      throw new BaseFontCreateException( "Unable to create font: " + font, e );
    }
  }

  /**
   * Returns an AWT Font which can be used to represent the given BaseFont
   *
   * @param font the font to be converted
   * @param size the desired point size of the resulting font
   * @return a Font which has similar properties to the provided BaseFont
   */

  public Font pdfToAwt( final BaseFont font, final int size ) {
    final String logicalName = getFontName( font );
    boolean bold = false;
    boolean italic = false;

    if ( StringUtils.endsWithIgnoreCase( logicalName, "bolditalic" ) ) {
      bold = true;
      italic = true;
    } else if ( StringUtils.endsWithIgnoreCase( logicalName, "bold" ) ) {
      bold = true;
    } else if ( StringUtils.endsWithIgnoreCase( logicalName, "italic" ) ) {
      italic = true;
    }

    int style = Font.PLAIN;
    if ( bold ) {
      style |= Font.BOLD;
    }
    if ( italic ) {
      style |= Font.ITALIC;
    }

    return new Font( logicalName, style, size );
  }

  private String getFontName( final BaseFont font ) {
    final String[][] names = font.getFullFontName();
    final int nameCount = names.length;
    if ( nameCount == 1 ) {
      return names[ 0 ][ 3 ];
    }

    String nameExtr = null;
    for ( int k = 0; k < nameCount; ++k ) {
      final String[] name = names[ k ];
      // Macintosh language english
      if ( "1".equals( name[ 0 ] ) && "0".equals( name[ 1 ] ) ) {
        nameExtr = name[ 3 ];
      }
      // Microsoft language code for US-English ...
      else if ( "1033".equals( name[ 2 ] ) ) {
        nameExtr = name[ 3 ];
        break;
      }
    }

    if ( nameExtr != null ) {
      return nameExtr;
    }
    return names[ 0 ][ 3 ];
  }
}
