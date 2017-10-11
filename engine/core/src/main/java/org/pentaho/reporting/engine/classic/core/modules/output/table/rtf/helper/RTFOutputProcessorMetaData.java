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

package org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.helper;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.text.LegacyFontMetrics;
import org.pentaho.reporting.engine.classic.core.modules.output.support.itext.BaseFontModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.AbstractTableOutputProcessor;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.itext.BaseFontFontMetrics;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;

public class RTFOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final int PAGINATION_NONE = 0;
  public static final int PAGINATION_MANUAL = 1;
  public static final int PAGINATION_FULL = 2;

  public static final OutputProcessorFeature.BooleanOutputProcessorFeature IMAGES_ENABLED =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "RTF.EnableImages" );
  private int paginationMode;

  public RTFOutputProcessorMetaData( final int paginationMode ) {
    super( new ITextFontStorage( BaseFontModule.getFontRegistry() ) );
    this.paginationMode = paginationMode;
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.EnableImages" ) ) ) {
      addFeature( RTFOutputProcessorMetaData.IMAGES_ENABLED );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.StrictLayout" ) ) ) {
      addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.StrictLayout" ) ) ) {
      addFeature( AbstractTableOutputProcessor.STRICT_LAYOUT );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.UsePageBands" ) ) ) {
      addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.base.TreatEllipseAsRectangle" ) ) ) {
      addFeature( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.TreatEllipseAsRectangle" ) ) ) {
      addFeature( AbstractTableOutputProcessor.TREAT_ELLIPSE_AS_RECTANGLE );
    }

    if ( paginationMode == RTFOutputProcessorMetaData.PAGINATION_FULL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
    } else if ( paginationMode == RTFOutputProcessorMetaData.PAGINATION_MANUAL ) {
      addFeature( OutputProcessorFeature.PAGEBREAKS );
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    } else {
      addFeature( OutputProcessorFeature.ITERATIVE_RENDERING );
      addFeature( OutputProcessorFeature.UNALIGNED_PAGEBANDS );
    }

    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );
    final double deviceResolution =
        extendedConfig.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.DeviceResolution", 0 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    }

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.rtf.ShapeAsContent" ) ) ) {
      addFeature( AbstractTableOutputProcessor.SHAPES_CONTENT );
    }

  }

  /**
   * The export descriptor is a string that describes the output characteristics. For libLayout outputs, it should start
   * with the output class (one of 'pageable', 'flow' or 'stream'), followed by '/liblayout/' and finally followed by
   * the output type (ie. PDF, Print, etc).
   *
   * @return the export descriptor.
   */
  public String getExportDescriptor() {
    return "table/rtf";
  }

  public String getNormalizedFontFamilyName( final String name ) {
    final String mappedName = super.getNormalizedFontFamilyName( name );
    if ( FontMappingUtility.isSerif( mappedName ) ) {
      return "Times";
    }
    if ( FontMappingUtility.isSansSerif( mappedName ) ) {
      return "Helvetica";
    }
    if ( FontMappingUtility.isCourier( mappedName ) ) {
      return "Courier";
    }
    if ( FontMappingUtility.isSymbol( mappedName ) ) {
      return "Symbol";
    }
    return mappedName;
  }

  public ITextFontStorage getITextFontStorage() {
    return (ITextFontStorage) getFontStorage();
  }

  /**
   * This method goes away as soon as we no longer rely on iText for RTF export.
   *
   * @param fontFamily
   * @param fontSize
   * @param bold
   * @param italics
   * @param encoding
   * @param embedded
   * @param antiAliasing
   * @return
   * @throws IllegalArgumentException
   */
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
      throw new IllegalArgumentException( "Ups, I did it again! ClassCastException in RTFOutputProcessorMetaData" );
    }
  }

}
