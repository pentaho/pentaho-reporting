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

package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Pir868Test extends TestCase {
  public Pir868Test() {
  }

  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSample()
    throws ResourceException, ReportProcessingException, ContentProcessingException {
    final URL reportURL = getClass().getResource( "Pir-868.prpt" );
    final ResourceManager mgr = new ResourceManager();
    final MasterReport report = (MasterReport) mgr.createDirectly( reportURL, MasterReport.class ).getResource();

    final LogicalPageBox pageDH = DebugReportRunner.layoutSingleBand( report, report.getDetailsHeader(), false, false );
    final LogicalPageBox pageIB = DebugReportRunner.layoutSingleBand( report, report.getItemBand(), false, false );

    final RenderNode[] dhRow = MatchFactory.findElementsByNodeType( pageDH, LayoutNodeTypes.TYPE_BOX_ROWBOX );
    final RenderNode[] ibRow = MatchFactory.findElementsByNodeType( pageIB, LayoutNodeTypes.TYPE_BOX_ROWBOX );
    ModelPrinter.INSTANCE.print( pageDH );
    ModelPrinter.INSTANCE.print( pageIB );

    assertEquals( 1, dhRow.length );
    assertEquals( 1, ibRow.length );
    assertEquals( dhRow[ 0 ].getWidth(), ibRow[ 0 ].getWidth() );
    assertEquals( dhRow[ 0 ].getX(), ibRow[ 0 ].getX() );
  }
}
