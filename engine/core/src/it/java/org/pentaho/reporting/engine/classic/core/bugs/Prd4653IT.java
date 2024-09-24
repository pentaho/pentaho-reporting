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
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.base.LibBaseBoot;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

@SuppressWarnings( "HardCodedStringLiteral" )
public class Prd4653IT extends TestCase {
  public Prd4653IT() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testBugExists() throws Exception {
    final URL resource = getClass().getResource( "Prd-4653.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource parsed = mgr.createDirectly( resource, MasterReport.class );
    final MasterReport report = (MasterReport) parsed.getResource();
    report.getRootGroup().getElement( 0 ).setName( "master-group-header" );

    final LogicalPageBox page = DebugReportRunner.layoutPage( report, 1 );
    final RenderNode[] elementsByName = MatchFactory.findElementsByName( page, "master-group-header" );

    assertEquals( 1, elementsByName.length );
  }

  public void testLogging() {
    LibBaseBoot.getInstance().start();
    System.out.println( getClass().getResource( "/simplelog.properties" ) );
  }

  public void testSubReportFlow() throws Exception {
    MasterReport report = new MasterReport();
    report.getReportHeader().addSubReport( new SubReport() );
    report.getReportHeader().addElement( new SubReport() );

    final LogicalPageBox page = DebugReportRunner.layoutPage( report, 0 );

  }

}
