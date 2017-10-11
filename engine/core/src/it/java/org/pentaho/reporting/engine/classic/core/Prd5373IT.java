/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
