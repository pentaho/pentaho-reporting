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

package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class ReportLayouterTest extends TestCase {
  public ReportLayouterTest() {
  }

  public ReportLayouterTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testLayoutEmptyBand() throws Exception {
    final URL url = ReportLayouterTest.class.getResource( "report-layouter-01.prpt" );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource resource = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) resource.getResource();

    final ReportLayouter l = new ReportLayouter
      ( new ReportRenderContext( report, report, null, new GlobalAuthenticationStore() ) );
    final LogicalPageBox layout = l.layout();
    //  ModelPrinter.print(layout);

  }
}
