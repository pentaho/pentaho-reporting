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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.testsupport.ReportWritingUtil;

public class Prd4912IT {
  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test( expected = BundleWriterException.class )
  public void testReportAPI() throws Exception {
    MasterReport report = new MasterReport();
    // by all means, this is illegal, root-level band should not appear directly inside other root-level bands
    report.getReportHeader().addElement( new PageHeader() );

    MasterReport elements = ReportWritingUtil.saveAndLoad( report );
    Assert.assertTrue( elements.getReportHeader().getElement( 0 ) instanceof Band );
    Assert.assertFalse( elements.getReportHeader().getElement( 0 ) instanceof RootLevelBand );
  }

  @Test( expected = BundleWriterException.class )
  public void testReportAPI2() throws Exception {
    MasterReport report = new MasterReport();
    // by all means, this is illegal, root-level band should not appear directly inside other root-level bands
    report.getReportHeader().addElement( new MasterReport() );

    MasterReport elements = ReportWritingUtil.saveAndLoad( report );
    Assert.assertTrue( elements.getReportHeader().getElement( 0 ) instanceof Band );
    Assert.assertFalse( elements.getReportHeader().getElement( 0 ) instanceof RootLevelBand );
  }
}
