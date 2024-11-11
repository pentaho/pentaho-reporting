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


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;

public class DesignerOutputProcessorMetaData extends AbstractOutputProcessorMetaData {
  public DesignerOutputProcessorMetaData() {
    super( new DefaultFontStorage( new AWTFontRegistry() ) );
    addFeature( OutputProcessorFeature.DESIGNTIME );
  }

  public void initialize( final Configuration configuration ) {
    super.initialize( configuration );
    addFeature( OutputProcessorFeature.FAST_FONTRENDERING );
    addFeature( OutputProcessorFeature.BACKGROUND_IMAGE );
    addFeature( OutputProcessorFeature.SPACING_SUPPORTED );
    addFeature( OutputProcessorFeature.WATERMARK_SECTION );
    addFeature( OutputProcessorFeature.DESIGNTIME );
    addFeature( OutputProcessorFeature.PAGE_SECTIONS );
    addFeature( OutputProcessorFeature.DIRECT_RICHTEXT_RENDERING );
  }

  public String getExportDescriptor() {
    return "pageable/report-designer"; // NON-NLS
  }
}
