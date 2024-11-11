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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;
import java.net.URL;

public class PageableHtmlOutputIT extends TestCase {
  public PageableHtmlOutputIT() {
  }

  public PageableHtmlOutputIT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPagination() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final URL target = PageableHtmlOutputIT.class.getResource( "pageable-html-output-report.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    report.setDataFactory( new TableDataFactory( "default", new DefaultTableModel( 1, 1 ) ) );
    DebugReportRunner.createPageableHTML( report );
  }
}
