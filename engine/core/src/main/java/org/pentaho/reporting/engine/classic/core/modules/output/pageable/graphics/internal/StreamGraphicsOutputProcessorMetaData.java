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


package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

public class StreamGraphicsOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public StreamGraphicsOutputProcessorMetaData() {
    this( new DefaultFontStorage( new AWTFontRegistry() ) );
  }

  public StreamGraphicsOutputProcessorMetaData( final FontStorage storage ) {
    super( storage );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );

    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.WatermarkPrinted" ) ) ) {
      addFeature( OutputProcessorFeature.WATERMARK_SECTION );
    }
    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper( configuration );
    final double deviceResolution =
        extendedConfig.getIntProperty(
            "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.DeviceResolution", 0 );
    if ( deviceResolution > 0 ) {
      setNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution );
    }

    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.AssumeOverflowX" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_X );
    }
    if ( "true"
        .equals( configuration
            .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.AssumeOverflowY" ) ) ) {
      addFeature( OutputProcessorFeature.ASSUME_OVERFLOW_Y );
    }

  }

  public String getExportDescriptor() {
    return "pageable/X-AWT-Graphics";
  }
}
