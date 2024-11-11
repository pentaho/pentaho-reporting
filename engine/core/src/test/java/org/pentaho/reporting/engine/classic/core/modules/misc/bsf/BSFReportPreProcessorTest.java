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


package org.pentaho.reporting.engine.classic.core.modules.misc.bsf;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.base.PreProcessorTestBase;

public class BSFReportPreProcessorTest extends PreProcessorTestBase {
  private static final String SCRIPT =
      "org.pentaho.reporting.engine.classic.core.modules.misc.bsf.BSFReportPreProcessorTest.run(definition, \"script\")"
          + "; return definition;";
  private static final String PRESCRIPT =
      "org.pentaho.reporting.engine.classic.core.modules.misc.bsf.BSFReportPreProcessorTest.run(definition, "
          + "\"pre-data-script\"); return definition;";

  public static void run( AbstractReportDefinition def, String value ) {
    def.setAttribute( "test", "test", value );
  }

  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  protected ReportPreProcessor create() {
    BSFReportPreProcessor p = new BSFReportPreProcessor();
    p.setLanguage( "beanshell" );
    p.setPreDataScript( PRESCRIPT );
    p.setScript( SCRIPT );
    return p;
  }

  @Test
  public void testUnconfiguredDoesNotCrash() throws ReportProcessingException {
    BSFReportPreProcessor p = new BSFReportPreProcessor();
    MasterReport preData = materializePreData( new MasterReport(), p );
    MasterReport postData = materialize( new MasterReport(), p );

    Assert.assertNull( preData.getAttribute( "test", "test" ) );
    Assert.assertNull( postData.getAttribute( "test", "test" ) );

  }

  @Test
  public void testReportIsConfigured() throws ReportProcessingException {
    ReportPreProcessor p = create();
    MasterReport preData = materializePreData( new MasterReport(), p );
    MasterReport postData = materialize( new MasterReport(), p );

    Assert.assertEquals( "pre-data-script", preData.getAttribute( "test", "test" ) );
    Assert.assertEquals( "script", postData.getAttribute( "test", "test" ) );

  }
}
