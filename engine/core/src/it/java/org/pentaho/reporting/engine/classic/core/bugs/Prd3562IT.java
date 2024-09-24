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
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

/**
 * This test only acts as smoke-test, not a real content test. The content test is done via the golden sample test
 * instead.
 */
public class Prd3562IT extends TestCase {
  public Prd3562IT() {
  }

  public Prd3562IT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunReport() throws Exception {
    final URL url = getClass().getResource( "Prd-3562.prpt" );
    assertNotNull( url );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    PrintReportProcessor p = new PrintReportProcessor( report );
    p.paginate();
    assertTrue( p.isPaginated() );
    assertFalse( p.isError() );

    final PageDrawable pageDrawable = p.getPageDrawable( 1 );

    //
    // DebugReportRunner.createPDF(report);

  }
}
