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


package org.pentaho.reporting.engine.classic.core;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal.PdfOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.support.itext.BaseFontModule;
import org.pentaho.reporting.engine.classic.core.states.process.ProcessState;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;

public class Prd5373IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testDataConfigurationDefault() {
    String conf =
        ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
            "org.pentaho.reporting.engine.classic.core.WatermarkPrintedOnTopOfContent", "not-defined" );
    Assert.assertEquals( conf, "false" );
  }

  @Test
  public void testMetaDataHandling() {
    HierarchicalConfiguration hc = new HierarchicalConfiguration( ClassicEngineBoot.getInstance().getGlobalConfig() );
    hc.setConfigProperty( "org.pentaho.reporting.engine.classic.core.WatermarkPrintedOnTopOfContent", "true" );

    final ITextFontStorage fontStorage = new ITextFontStorage( BaseFontModule.getFontRegistry() );
    PdfOutputProcessorMetaData md = new PdfOutputProcessorMetaData( fontStorage );
    md.initialize( hc );

    Assert.assertTrue( md.isFeatureSupported( OutputProcessorFeature.WATERMARK_PRINTED_ON_TOP ) );
  }

  @Test
  public void testStateSetup() throws ReportProcessingException {
    MasterReport r = new MasterReport();
    r.getWatermark().setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.WATERMARK_PRINTED_ON_TOP, true );

    DefaultProcessingContext pc = new DefaultProcessingContext( r );

    ProcessState state = new ProcessState();
    state.initializeForMasterReport( r, pc, new DefaultOutputFunction() );
    OutputProcessorMetaData md = state.getFlowController().getReportContext().getOutputProcessorMetaData();
    Assert.assertTrue( md.isFeatureSupported( OutputProcessorFeature.WATERMARK_PRINTED_ON_TOP ) );
  }
}
