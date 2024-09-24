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

package org.pentaho.reporting.designer.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.report.layouting.ReportLayouter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.ItemBandType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.selector.MatchFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4109Test extends TestCase {
  public Prd4109Test() {
  }

  public Prd4109Test( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ReportDesignerBoot.getInstance().start();
  }

  public void testItemBandLayout() throws ResourceException, ReportProcessingException, ContentProcessingException {
    final URL url = ReportLayouterTest.class.getResource( "Prd-4109.prpt" );
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource resource = resourceManager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) resource.getResource();

    final ReportLayouter l = new ReportLayouter
      ( new ReportRenderContext( report, report, null, new GlobalAuthenticationStore() ) );
    final LogicalPageBox layout = l.layout();

    ModelPrinter.INSTANCE.print( layout );
    assertNotNull( MatchFactory.findElementsByElementType( layout, ItemBandType.INSTANCE ) );
    assertNotNull( MatchFactory.findElementsByElementType( layout, SubReportType.INSTANCE ) );

  }
}
