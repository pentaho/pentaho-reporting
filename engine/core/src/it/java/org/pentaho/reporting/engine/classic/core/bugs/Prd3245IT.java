/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.compat.CompatibilityUpdater;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.PreviewDialog;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.*;
import java.net.URL;
import java.util.List;

public class Prd3245IT extends TestCase {
  public Prd3245IT() {
  }

  public Prd3245IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReport() throws ResourceException {
    final URL url = getClass().getResource( "Prd-3245.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    DebugReportRunner.execGraphics2D( report );

    if ( GraphicsEnvironment.isHeadless() ) {
      return;
    }

    final PreviewDialog previewDialog = new PreviewDialog( report );
    previewDialog.pack();
    previewDialog.setModal( true );
    previewDialog.setVisible( true );
  }

  public void testReportFlow() throws Exception {
    final URL url = getClass().getResource( "Prd-3245.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    DebugReportRunner.createXmlFlow( report );
  }

  public void testReportStream() throws Exception {
    final URL url = getClass().getResource( "Prd-3245.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();
    DebugReportRunner.createXmlStream( report );
  }

  public void testGoldenSample() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3245.prpt" );
    List<LogicalPageBox> pages = DebugReportRunner.layoutPages( report, 0, 1, 2, 3, 4, 5, 6, 7 );
    assertChildren( 1, pages.get( 0 ) );
    assertChildren( 2, pages.get( 1 ) );
    assertChildren( 1, pages.get( 2 ) );
    assertChildren( 2, pages.get( 3 ) );
    assertChildren( 1, pages.get( 4 ) );
    assertChildren( 2, pages.get( 5 ) );
    assertChildren( 1, pages.get( 6 ) );
    assertChildren( 2, pages.get( 7 ) );
  }

  public void testGoldenSampleSubReportHeightLegacy() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3245.prpt" );
    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode elementByName = MatchFactory.findElementByName( logicalPageBox, "Subreport 1.1.1" );
    assertTrue( "SubReport height of " + StrictGeomUtility.toExternalValue( elementByName.getHeight() )
        + " is greater than 600pt", elementByName.getHeight() > StrictGeomUtility.toInternalValue( 600 ) );
  }

  public void testGoldenSampleSubReportHeightMigrated() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3245.prpt" );

    // migrate to latest version
    final CompatibilityUpdater updater = new CompatibilityUpdater();
    updater.performUpdate( report );
    report.setAttribute( AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.COMAPTIBILITY_LEVEL, null );

    LogicalPageBox logicalPageBox = DebugReportRunner.layoutPage( report, 0 );
    RenderNode elementByName = MatchFactory.findElementByName( logicalPageBox, "Subreport 1.1.1" );
    assertTrue( "SubReport height of " + StrictGeomUtility.toExternalValue( elementByName.getHeight() )
        + " is greater than 600pt", elementByName.getHeight() > StrictGeomUtility.toInternalValue( 600 ) );
  }

  public void testBandedPageSubreport() throws Exception {
    final MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Prd-3245.prpt" );
    LogicalPageBox box = DebugReportRunner.layoutPage( report, 2 );
    assertNull( MatchFactory.findElementByName( box, "Subreport 1.1" ) );
  }

  private void assertChildren( final int expected, final LogicalPageBox box ) {
    final RenderBox headerContainer = (RenderBox) box.getHeaderArea().getFirstChild();
    RenderNode n = headerContainer.getFirstChild();
    int count = 0;
    while ( n != null ) {
      if ( n instanceof CanvasRenderBox ) {
        count += 1;
      }
      n = n.getNext();
    }
    assertEquals( expected, count );
  }
}
