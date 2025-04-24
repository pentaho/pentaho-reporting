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


package org.pentaho.reporting.engine.classic.core.modules.output.fast;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.xls.FastExcelReportUtil;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class ReportStructureValidatorIT {
  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testReport() throws ResourceException, ReportProcessingException, IOException {
    URL url = getClass().getResource( "Prd-5129.prpt" );
    MasterReport report = (MasterReport) new ResourceManager().createDirectly( url, MasterReport.class ).getResource();
    ReportStructureValidator v = new ReportStructureValidator();
    Assert.assertTrue( v.isValidForFastProcessing( report ) );
    FastExcelReportUtil.processXls( report, new NullOutputStream() );
  }
}
