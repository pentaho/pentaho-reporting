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
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal.GraphicsOutputProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

/**
 * Creation-Date: 10.08.2007, 13:45:14
 *
 * @author Thomas Morgner
 */
public class Pre34IT extends TestCase {
  public Pre34IT() {
  }

  public Pre34IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testReportSizePRD4251() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }
    final URL url = getClass().getResource( "Pre-34.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();

    final PageableReportProcessor p =
        new PageableReportProcessor( resource, new GraphicsOutputProcessor( resource.getConfiguration() ) );
    p.paginate();

    // if you return 1, then your datasource is f'd up.
    assertTrue( p.getPhysicalPageCount() > 10 );
  }

  public void testSubReportDoesNotCrash() throws Exception {
    if ( DebugReportRunner.isSkipLongRunTest() ) {
      return;
    }

    final URL url = getClass().getResource( "Pre-34.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();

    DebugReportRunner.executeAll( resource );
  }

  public void testNames() throws Exception {
    final URL url = getClass().getResource( "Pre-34.xml" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();

    assertEquals( "ReportName", "Report", resource.getName() );
    assertEquals( "ReportName", "sub1", resource.getItemBand().getSubReport( 0 ).getName() );
  }
}
