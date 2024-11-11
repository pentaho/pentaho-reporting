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


package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityUpdater;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.SectionRenderBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;

public class ExtraFooterIT extends TestCase {
  public ExtraFooterIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testExtraFooterOn38() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Prd-2974-2.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);
    SectionRenderBox srb = (SectionRenderBox) logicalPageBox.getFooterArea().getFirstChild();
    assertTrue( srb.getFirstChild() instanceof ProgressMarkerRenderBox );
  }

  public void testExtraFooterOnMigration() throws Exception {
    final File file = GoldTestBase.locateGoldenSampleReport( "Prd-2974-2.prpt" );
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource directly = mgr.createDirectly( file, MasterReport.class );
    final MasterReport report = tuneForMigrationMode( (MasterReport) directly.getResource() );

    final LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    // ModelPrinter.print(logicalPageBox);
    final RenderBox srb = (RenderBox) logicalPageBox.getFooterArea().getFirstChild();
    assertTrue( srb.getFirstChild() instanceof ProgressMarkerRenderBox );
  }

  protected MasterReport tuneForMigrationMode( final MasterReport report ) {
    final CompatibilityUpdater updater = new CompatibilityUpdater();
    updater.performUpdate( report );
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null );
    return report;
  }

}
