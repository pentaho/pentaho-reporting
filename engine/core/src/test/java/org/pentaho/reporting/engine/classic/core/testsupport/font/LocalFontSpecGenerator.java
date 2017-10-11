/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport.font;

import org.pentaho.reporting.libraries.fonts.LibFontBoot;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontRegistry;
import org.pentaho.reporting.libraries.fonts.merge.CompoundFontRecord;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontContext;
import org.pentaho.reporting.libraries.fonts.registry.FontFamily;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.registry.FontMetricsFactory;
import org.pentaho.reporting.libraries.fonts.registry.FontRecord;
import org.pentaho.reporting.libraries.fonts.truetype.TrueTypeFontRecord;
import org.pentaho.reporting.libraries.fonts.truetype.TrueTypeFontRegistry;
import org.pentaho.reporting.libraries.xmlns.LibXmlBoot;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LocalFontSpecGenerator {
  public static void main( final String[] args ) throws IOException {
    LibFontBoot.getInstance().start();
    LibXmlBoot.getInstance().start();

    final TrueTypeFontRegistry registry = new TrueTypeFontRegistry();
    final ITextFontRegistry itextRegistry = new ITextFontRegistry();

    registry.initialize();
    itextRegistry.initialize();
    final DefaultTagDescription defaultTagDescription = new DefaultTagDescription();
    defaultTagDescription.setNamespaceHasCData( null, false );
    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( new FileOutputStream( "/Users/user/fonts.xml" ), "UTF-8" ),
            defaultTagDescription );
    writer.writeXmlDeclaration( "UTF-8" );
    writer.writeTag( null, "font-spec", "fallback-font", "Arial", false );

    final Map<String, FontRecord> records = new HashMap<String, FontRecord>();
    final String[] families = registry.getAllRegisteredFamilies();
    // final String[] families = new String[]{"Arial"};
    Arrays.sort( families );
    for ( int i = 0; i < families.length; i++ ) {
      final String family = families[i];
      writer.writeTag( null, "font-family", "name", family, false );
      final FontFamily fontFamily = registry.getFontFamily( family );
      writeRecord( writer, false, false, fontFamily.getFontRecord( false, false ), records );
      writeRecord( writer, true, false, fontFamily.getFontRecord( true, false ), records );
      writeRecord( writer, false, true, fontFamily.getFontRecord( false, true ), records );
      writeRecord( writer, true, true, fontFamily.getFontRecord( true, true ), records );
      writer.writeCloseTag();
    }

    final FontContext context = new DefaultFontContext( 1000, false, true, false, "Identity-H" );
    final FontMetricsFactory metricsFactory = itextRegistry.createMetricsFactory();
    final String[] sourceFiles = records.keySet().toArray( new String[records.size()] );
    Arrays.sort( sourceFiles );
    for ( int i = 0; i < sourceFiles.length; i++ ) {
      final String sourceFile = sourceFiles[i];
      final FontRecord fontRecord = records.get( sourceFile );
      final FontMetrics metrics = metricsFactory.createMetrics( fontRecord.getIdentifier(), context );

      writer.writeTag( null, "font-source", "source", sourceFile, false );
      /*
       * <global-metrics ascent="0" descent="0" italic-angle="0" leading="0" max-ascent="0" max-char-advance="0"
       * max-descent="0" max-height="0" overline-position="0" strike-through-position="0" underline-position="0"
       * x-height="0" uniform="false"/>
       */
      final AttributeList globalAttrs = new AttributeList();
      globalAttrs.setAttribute( null, "ascent", formatText( metrics.getAscent() ) );
      globalAttrs.setAttribute( null, "descent", formatText( metrics.getDescent() ) );
      globalAttrs.setAttribute( null, "italic-angle", formatText( metrics.getItalicAngle() ) );
      globalAttrs.setAttribute( null, "leading", formatText( metrics.getLeading() ) );
      globalAttrs.setAttribute( null, "max-ascent", formatText( metrics.getMaxAscent() ) );
      globalAttrs.setAttribute( null, "max-char-advance", formatText( metrics.getMaxCharAdvance() ) );
      globalAttrs.setAttribute( null, "max-descent", formatText( metrics.getMaxDescent() ) );
      globalAttrs.setAttribute( null, "max-height", formatText( metrics.getMaxHeight() ) );
      globalAttrs.setAttribute( null, "overline-position", formatText( metrics.getOverlinePosition() ) );
      globalAttrs.setAttribute( null, "strike-through-position", formatText( metrics.getStrikeThroughPosition() ) );
      globalAttrs.setAttribute( null, "underline-position", formatText( metrics.getUnderlinePosition() ) );
      globalAttrs.setAttribute( null, "x-height", formatText( metrics.getXHeight() ) );
      globalAttrs.setAttribute( null, "uniform", String.valueOf( metrics.isUniformFontMetrics() ) );
      writer.writeTag( null, "global-metrics", globalAttrs, true );

      for ( int c = 0; c < 65536; c++ ) {
        if ( c >= 0xD800 && c <= 0xDFFF ) {
          // surrogate range is not valid in unicode
          continue;
        }
        final long width = metrics.getCharWidth( c );
        if ( width == 0 ) {
          continue;
        }
        final AttributeList cwList = new AttributeList();
        cwList.setAttribute( null, "codepoint", String.valueOf( c ) );
        cwList.setAttribute( null, "value", String.valueOf( width ) );
        writer.writeTag( null, "char-width", cwList, true );
      }

      for ( int c = 0; c < 65536; c++ ) {
        if ( c >= 0xD800 && c <= 0xDFFF ) {
          // surrogate range is not valid in unicode
          continue;
        }
        final long width = metrics.getCharWidth( c );
        if ( width == 0 ) {
          // if there is no metrics, there is no kerning info
          continue;
        }

        for ( int cPrev = 0; cPrev < 65536; cPrev++ ) {
          if ( cPrev >= 0xD800 && cPrev <= 0xDFFF ) {
            // surrogate range is not valid in unicode
            continue;
          }
          final long kerning = metrics.getKerning( cPrev, c );
          if ( kerning == 0 ) {
            continue;
          }
          final AttributeList cwList = new AttributeList();
          cwList.setAttribute( null, "codepoint", String.valueOf( c ) );
          cwList.setAttribute( null, "prev", String.valueOf( cPrev ) );
          cwList.setAttribute( null, "value", String.valueOf( kerning ) );
          writer.writeTag( null, "kerning", cwList, true );
        }
      }

      writer.writeCloseTag();
    }

    writer.writeCloseTag();
    writer.flush();
    writer.close();
  }

  private static String formatText( final double number ) {
    final DecimalFormat format = new DecimalFormat( "#0", new DecimalFormatSymbols( Locale.US ) );
    return format.format( number );
  }

  private static void writeRecord( final XmlWriter writer, final boolean bold, final boolean italics,
      FontRecord record, final Map<String, FontRecord> records ) throws IOException {
    final AttributeList attrList = new AttributeList();
    attrList.setAttribute( null, "bold", String.valueOf( bold ) );
    attrList.setAttribute( null, "italics", String.valueOf( italics ) );
    if ( record instanceof CompoundFontRecord ) {
      final CompoundFontRecord cfr = (CompoundFontRecord) record;
      record = cfr.getBase();
    }
    if ( record instanceof TrueTypeFontRecord ) {
      final TrueTypeFontRecord trueTypeFontRecord = (TrueTypeFontRecord) record;
      attrList.setAttribute( null, "source", String.valueOf( trueTypeFontRecord.getFontSource() ) );
      writer.writeTag( null, "font-record", attrList, true );

      records.put( trueTypeFontRecord.getFontSource(), record );
    }
  }
}
