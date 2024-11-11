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


package org.pentaho.reporting.engine.classic.core.testsupport;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontRegistry;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.monospace.MonospaceFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

public class DebugOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  private static LocalFontRegistry localFontRegistry;

  public DebugOutputProcessorMetaData() {
    this( getLocalFontStorage() );
  }

  public DebugOutputProcessorMetaData( final FontStorage fontStorage ) {
    super( fontStorage );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.PAGEBREAKS );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.WATERMARK_SECTION );
    if ( getFontRegistry() instanceof MonospaceFontRegistry ) {
      removeFeature( OutputProcessorFeature.LEGACY_LINEHEIGHT_CALC );
    }
  }

  public void setDesignTime( boolean designTime ) {
    if ( designTime ) {
      addFeature( OutputProcessorFeature.DESIGNTIME );
    } else {
      removeFeature( OutputProcessorFeature.DESIGNTIME );
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
    return "pageable/debug";
  }

  public static synchronized FontStorage getLocalFontStorage() {
    if ( localFontRegistry == null ) {
      localFontRegistry = new LocalFontRegistry();
      localFontRegistry.initialize();
    }
    return new DefaultFontStorage( localFontRegistry );
  }
}
