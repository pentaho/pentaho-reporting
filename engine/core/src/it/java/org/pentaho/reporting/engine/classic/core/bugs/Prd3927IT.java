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
