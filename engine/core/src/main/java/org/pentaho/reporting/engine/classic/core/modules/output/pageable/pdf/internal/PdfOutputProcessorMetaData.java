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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.MasterReportType;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.text.LegacyFontMetrics;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.itext.BaseFontFontMetrics;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

public class PdfOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  private LFUMap<String, String> normalizedFontNameCache;

  public PdfOutputProcessorMetaData( final ITextFontStorage fontStorage ) {
    super( fontStorage );
    setFamilyMapping( null, "Helvetica" );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.PAGEBREAKS );

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.WatermarkPrinted" ) ) ) {
      addFeature( OutputProcessorFeature.WATERMARK_SECTION );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.EmbedFonts" ) ) ) {
      addFeature( OutputProcessorFeature.EMBED_ALL_FONTS );
    }

    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );
    final double deviceResolution =
        extendedConfig.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.DeviceResolution", 0 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }

    if ( isFeatureSupported( OutputProcessorFeature.COMPLEX_TEXT ) ) {
      addFeature( OutputProcessorFeature.STRICT_TEXT_PROCESSING );
    }

    String defaultEncoding =
        configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.Encoding" );
    if ( !StringUtils.isEmpty( defaultEncoding ) ) {
      getITextFontStorage().setDefaultEncoding( defaultEncoding );
    }
    normalizedFontNameCache = new LFUMap<String, String>( 500 );
  }

  public String getNormalizedFontFamilyName( final String name ) {
    if ( name != null ) {
      final Object fromCache = normalizedFontNameCache.get( name );
      if ( fromCache != null ) {
        return (String) fromCache;
      }
    }

    String mappedName = super.getNormalizedFontFamilyName( name );
    if ( FontMappingUtility.isSerif( mappedName ) ) {
      mappedName = "Times";
    } else if ( FontMappingUtility.isSansSerif( mappedName ) ) {
      mappedName = "Helvetica";
    } else if ( FontMappingUtility.isCourier( mappedName ) ) {
      mappedName = "Courier";
    } else if ( FontMappingUtility.isSymbol( mappedName ) ) {
      mappedName = "Symbol";
    }
    if ( name != null ) {
      normalizedFontNameCache.put( name, mappedName );
    }
    return mappedName;
  }

  public String getExportDescriptor() {
    return "pageable/pdf";
  }

  public ITextFontStorage getITextFontStorage() {
    return (ITextFontStorage) getFontStorage();
  }

  public BaseFontFontMetrics getBaseFontFontMetrics( final String fontFamily, final double fontSize,
      final boolean bold, final boolean italics, final String encoding, final boolean embedded,
      final boolean antiAliasing ) throws IllegalArgumentException {
    try {
      final FontMetrics metrics =
          super.getFontMetrics( fontFamily, fontSize, bold, italics, encoding, embedded, antiAliasing );
      if ( metrics instanceof LegacyFontMetrics ) {
        final LegacyFontMetrics lm = (LegacyFontMetrics) metrics;
        return (BaseFontFontMetrics) lm.getParent();
      }
      return (BaseFontFontMetrics) metrics;
    } catch ( ClassCastException ce ) {
      throw new IllegalArgumentException( "Ups, I did it again! ClassCastException in PdfOutputProcessorMetaData" );
    }
  }

  /**
   * Checks whether this element provides some extra content that is not part of the visible layout structure. This can
   * be embedded scripts, anchors etc.
   *
   * @param style
   * @param attributes
   * @return
   */
  public boolean isExtraContentElement( final StyleSheet style, final ReportAttributeMap attributes ) {
    if ( isFeatureSupported( OutputProcessorFeature.DETECT_EXTRA_CONTENT ) == false ) {
      return false;
    }

    final Object o = attributes.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.ELEMENT_TYPE );
    if ( o instanceof MasterReportType ) {
      return false;
    }

    if ( super.isExtraContentElement( style, attributes ) ) {
      return true;
    }

    if ( StringUtils.isEmpty( (String) attributes.getAttribute( AttributeNames.Pdf.NAMESPACE,
        AttributeNames.Pdf.SCRIPT_ACTION ) ) == false ) {
      return true;
    }
    return false;
  }
}
