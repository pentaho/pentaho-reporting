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


package org.pentaho.reporting.engine.classic.migration;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xml.XmlTableReportUtil;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;

import java.io.File;

public class AutoTableTest extends TestCase {
  public AutoTableTest() {
  }

  public AutoTableTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLegacyMode() throws Exception {
    File file = GoldTestBase.locateGoldenSampleReport( "2sql-subreport.prpt" );
    MasterReport masterReport = GoldTestBase.parseReport( file );
    masterReport.setCompatibilityLevel( ClassicEngineBoot.computeVersionId( 3, 8, 0 ) );
    //    XmlPageReportUtil.createXml(masterReport, new NoCloseOutputStream(System.out));
    //    XmlTableReportUtil.createFlowXML(masterReport, new NoCloseOutputStream(System.out));
    XmlTableReportUtil.createFlowXML( masterReport, new NullOutputStream() );
  }
}
