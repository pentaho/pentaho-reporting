/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.internal;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontRegistry;

public class XmlPageOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public static final OutputProcessorFeature.BooleanOutputProcessorFeature WRITE_RESOURCEKEYS =
      new OutputProcessorFeature.BooleanOutputProcessorFeature( "xml.write-resourcekeys" );

  public XmlPageOutputProcessorMetaData() {
    this( new AWTFontRegistry() );
  }

  public XmlPageOutputProcessorMetaData( final FontRegistry registry ) {
    super( new DefaultFontStorage( registry ) );
    setFamilyMapping( null, "Helvetica" );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.PAGEBREAKS );
    addFeature( OutputProcessorFeature.WATERMARK_SECTION );

    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true".equals( configuration
        .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.xml.WriteResourceKeys" ) ) ) {
      addFeature( WRITE_RESOURCEKEYS );
    }
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

  public String getExportDescriptor() {
    return "pageable/xml";
  }
}
