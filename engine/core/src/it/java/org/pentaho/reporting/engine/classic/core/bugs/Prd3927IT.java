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

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd3927IT extends TestCase {
  public Prd3927IT() {
  }

  public Prd3927IT( final String name ) {
    super( name );
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLayout() throws ResourceException, ReportProcessingException, ContentProcessingException {
    URL reportLocation = Prd3927IT.class.getResource( "Prd-3927.prpt" );

    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource resource = mgr.createDirectly( reportLocation, MasterReport.class );
    final MasterReport report = (MasterReport) resource.getResource();

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutSingleBand( report, report.getReportHeader() );
    // ModelPrinter.print(logicalPageBox);
    /*
     * final RenderNode elementByName2 = MatchFactory.findElementByName(logicalPageBox, "Push me too!");
     * assertEquals("Expected failure, this bug is still unresolved.", StrictGeomUtility.toInternalValue(75),
     * elementByName2.getHeight());
     * 
     * final RenderNode elementByName = MatchFactory.findElementByName(logicalPageBox, "shift me!");
     * assertEquals("Expected failure, this bug is still unresolved.", StrictGeomUtility.toInternalValue(75),
     * elementByName.getHeight());
     */
  }
}
