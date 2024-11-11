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


package org.pentaho.reporting.engine.classic.core.layout.basic;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class LayoutAutoParagraphIT extends TestCase {
  public LayoutAutoParagraphIT() {
  }

  public LayoutAutoParagraphIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmpty() {

  }

  public void testLayout1() throws Exception {
    final URL url = getClass().getResource( "layout-auto-paragraph.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( resource, resource.getReportHeader() );
    // ModelPrinter.print(logicalPageBox);
    // XmlPageReportUtil.createXml(resource, new NoCloseOutputStream(System.out));
  }

  public void testLayout2() throws Exception {
    final URL url = getClass().getResource( "layout-auto-paragraph-2.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();
    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( resource, resource.getReportHeader() );
    // ModelPrinter.print(logicalPageBox);
    // XmlPageReportUtil.createXml(resource, new NoCloseOutputStream(System.out));
  }
}
